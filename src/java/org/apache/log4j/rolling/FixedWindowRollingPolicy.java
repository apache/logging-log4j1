/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.rolling;

import org.apache.log4j.rolling.helper.Compress;
import org.apache.log4j.pattern.IntegerPatternConverter;
import org.apache.log4j.rolling.helper.Util;
import org.apache.log4j.pattern.PatternConverter;

import java.io.File;


/**
 * When rolling over, <code>FixedWindowRollingPolicy</code> renames files 
 * according to a fixed window algorithm as described below. 
 * 
 * <p>The <b>ActiveFileName</b> property, which is required, represents the name 
 * of the file where current logging output will be written. 
 * The <b>FileNamePattern</b>  option represents the file name pattern for the 
 * archived (rolled over) log files. If present, the <b>FileNamePattern</b> 
 * option must include an integer token, that is the string "%i" somewhere 
 * within the pattern.
 * 
 * <p>Let <em>max</em> and <em>min</em> represent the values of respectively 
 * the <b>MaxIndex</b> and <b>MinIndex</b> options. Let "foo.log" be the value
 * of the <b>ActiveFile</b> option and "foo.%i.log" the value of 
 * <b>FileNamePattern</b>. Then, when rolling over, the file 
 * <code>foo.<em>max</em>.log</code> will be deleted, the file 
 * <code>foo.<em>max-1</em>.log</code> will be renamed as 
 * <code>foo.<em>max</em>.log</code>, the file <code>foo.<em>max-2</em>.log</code> 
 * renamed as <code>foo.<em>max-1</em>.log</code>, and so on, 
 * the file <code>foo.<em>min+1</em>.log</code> renamed as 
 * <code>foo.<em>min+2</em>.log</code>. Lastly, the active file <code>foo.log</code>
 * will be renamed as <code>foo.<em>min</em>.log</code> and a new active file name
 * <code>foo.log</code> will be created.
 * 
 * <p>Given that this rollover algorithm requires as many file renaming 
 * operations as the window size, large window sizes are discouraged. The
 * current implementation will automatically reduce the window size to 12 when
 * larger values are specified by the user.
 * 
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 * */
public final class FixedWindowRollingPolicy extends RollingPolicyBase {
  static private final String FNP_NOT_SET =
    "The FileNamePattern option must be set before using FixedWindowRollingPolicy. ";
  static private final String SEE_FNP_NOT_SET =
    "See also http://logging.apache.org/log4j/codes.html#tbr_fnp_not_set";
  private int maxIndex;
  private int minIndex;
  private final Util util = new Util();
  private final Compress compress = new Compress();

  /**
   * It's almost always a bad idea to have a large window size, say over 12. 
   */
  private static int MAX_WINDOW_SIZE = 12;
  
  public FixedWindowRollingPolicy() {
    minIndex = 1;
    maxIndex = 7;
    activeFileName = null;
  }

  public void activateOptions() {
    // set the LR for our utility object
    util.setLoggerRepository(this.repository);
    compress.setLoggerRepository(this.repository);
    
    if (fileNamePatternStr != null) {
      parseFileNamePattern();
      determineCompressionMode();
    } else {
      getLogger().warn(FNP_NOT_SET);
      getLogger().warn(SEE_FNP_NOT_SET);
      throw new IllegalStateException(FNP_NOT_SET + SEE_FNP_NOT_SET);
    }
    if (activeFileName == null) {
      getLogger().warn(
        "The ActiveFile name option must be set before using this rolling policy.");
      throw new IllegalStateException(
        "The ActiveFileName option must be set.");
    }

    if (maxIndex < minIndex) {
      getLogger().warn(
        "MaxIndex (" + maxIndex + ") cannot be smaller than MinIndex ("
        + minIndex + ").");
      getLogger().warn("Setting maxIndex to equal minIndex.");
      maxIndex = minIndex;
    }

    if((maxIndex-minIndex) > MAX_WINDOW_SIZE) {
      getLogger().warn("Large window sizes are not allowed.");
      maxIndex = minIndex +  MAX_WINDOW_SIZE;
      getLogger().warn("MaxIndex reduced to {}.", new Integer(maxIndex));
    }

    PatternConverter itc = null;
    for (int i = 0; i < patternConverters.length; i++) {
        if (patternConverters[i] instanceof IntegerPatternConverter) {
            itc = patternConverters[i];
            break;
        }
    }

    if (itc == null) {
      throw new IllegalStateException(
        "FileNamePattern [" + fileNamePatternStr
        + "] does not contain a valid integer format specifier");
    }
  }

  public void rollover() throws RolloverFailure {
    // Inside this method it is guaranteed that the hereto active log fil is closed.
    // If maxIndex <= 0, then there is no file renaming to be done.
    if (maxIndex >= 0) {
      // Delete the oldest file, to keep Windows happy.
      StringBuffer buf = new StringBuffer();
      formatFileName(new Integer(maxIndex), buf);
      File file = new File(buf.toString());

      if (file.exists()) {
        file.delete();
      }

      // Map {(maxIndex - 1), ..., minIndex} to {maxIndex, ..., minIndex+1}
      for (int i = maxIndex - 1; i >= minIndex; i--) {
          buf.setLength(0);
          formatFileName(new Integer(i), buf);
	  String toRenameStr = buf.toString();
	  File toRename = new File(toRenameStr);
	  // no point in trying to rename an inexistent file
	  if(toRename.exists()) {
          buf.setLength(0);
          formatFileName(new Integer(i + 1), buf);
	      util.rename(toRenameStr, buf.toString());
	  } else {
	      getLogger().info("Skipping rollover for non-existent file {}", toRenameStr); 
          }
      }

      buf.setLength(0);
      formatFileName(new Integer(minIndex), buf);

      //move active file name to min
      switch (compressionMode) {
      case Compress.NONE:
          util.rename(activeFileName, buf.toString());
          break;
      case Compress.GZ:
          compress.GZCompress(activeFileName, buf.toString());
          break;	  
      case Compress.ZIP:
	  compress.ZIPCompress(activeFileName, buf.toString());
	  break;
      }
    }
  }

  /**
   * Return the value of the <b>ActiveFile</b> option.
   * 
   * @see {@link setActiveFileName}.
  */
  public String getActiveFileName() {
    // TODO This is clearly bogus.
    return activeFileName;
  }

  public int getMaxIndex() {
    return maxIndex;
  }

  public int getMinIndex() {
    return minIndex;
  }

  public void setMaxIndex(int maxIndex) {
    this.maxIndex = maxIndex;
  }

  public void setMinIndex(int minIndex) {
    this.minIndex = minIndex;
  }

}
