/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

import org.apache.log4j.pattern.IntegerPatternConverter;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.rolling.helper.FileRenameAction;
import org.apache.log4j.rolling.helper.GZCompressAction;
import org.apache.log4j.rolling.helper.ZipCompressAction;

import java.io.File;
import java.io.IOException;

import java.util.List;


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
  /**
   * Error message.
   */
  private static final String FNP_NOT_SET =
    "The FileNamePattern option must be set before using FixedWindowRollingPolicy. ";

  /**
   * Link for error message.
   */
  private static final String SEE_FNP_NOT_SET =
    "See also http://logging.apache.org/log4j/codes.html#tbr_fnp_not_set";

  /**
   * It's almost always a bad idea to have a large window size, say over 12.
   */
  private static final int MAX_WINDOW_SIZE = 12;

  /**
   * Index for oldest retained log file.
   */
  private int maxIndex;

  /**
   * Index for most recent log file.
   */
  private int minIndex;

  /**
   * Constructs a new instance.
   */
  public FixedWindowRollingPolicy() {
    minIndex = 1;
    maxIndex = 7;
    activeFileName = null;
  }

  /**
   * {@inheritDoc}
   */
  public void activateOptions() {
    if (fileNamePatternStr != null) {
      parseFileNamePattern();
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

    if ((maxIndex - minIndex) > MAX_WINDOW_SIZE) {
      getLogger().warn("Large window sizes are not allowed.");
      maxIndex = minIndex + MAX_WINDOW_SIZE;
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

  /**
   * {@inheritDoc}
   */
  public boolean rollover(
    final StringBuffer activeFile, List synchronousActions,
    List asynchronousActions) throws IOException {
    if (maxIndex >= 0) {
      // Delete the oldest file, to keep Windows happy.
      StringBuffer buf = new StringBuffer();
      formatFileName(new Integer(maxIndex), buf);

      String higherFileName = buf.toString();
      File higherFile = new File(higherFileName);

      if (higherFile.exists()) {
        if (!higherFile.delete()) {
          throw new IOException("Unable to delete " + higherFileName);
        }
      }

      int suffixLength = 0;

      if (higherFileName.endsWith(".gz")) {
        suffixLength = 3;
      } else if (higherFileName.endsWith(".zip")) {
        suffixLength = 4;
      }

      String higherBaseName = higherFileName;

      if (suffixLength > 0) {
        higherBaseName =
          higherFileName.substring(0, higherFileName.length() - suffixLength);

        File baseFile = new File(higherBaseName);

        if (baseFile.exists()) {
          if (!baseFile.delete()) {
            throw new IOException("Unable to delete " + higherBaseName);
          }
        }
      }

      // Map {(maxIndex - 1), ..., minIndex} to {maxIndex, ..., minIndex+1}
      for (int i = maxIndex - 1; i >= minIndex; i--) {
        buf.setLength(0);
        formatFileName(new Integer(i), buf);

        String lowerFileName = buf.toString();
        File toRename = new File(lowerFileName);

        // no point in trying to rename an non-existent file
        if (toRename.exists()) {
          if (!toRename.renameTo(new File(higherFileName))) {
            throw new IOException("Unable to rename " + lowerFileName);
          }
        }

        if (suffixLength > 0) {
          String lowerBaseName =
            lowerFileName.substring(0, lowerFileName.length() - suffixLength);
          File baseFile = new File(lowerBaseName);

          if (baseFile.exists()) {
            if (!baseFile.renameTo(new File(higherBaseName))) {
              throw new IOException("Unable to rename " + lowerBaseName);
            }
          }

          higherBaseName = lowerBaseName;
        } else {
          higherBaseName = lowerFileName;
        }

        higherFileName = lowerFileName;
      }

      activeFile.setLength(0);
      activeFile.append(activeFileName);

      File currentFile = new File(activeFileName);

      if (currentFile.exists()) {
        //
        //    add renaming of active file as something to be done
        //       after closing active file
        //
        synchronousActions.add(
          new FileRenameAction(
            new File(activeFileName), new File(higherBaseName), false));

        if (suffixLength == 3) {
          asynchronousActions.add(
            new GZCompressAction(
              new File(higherBaseName), new File(higherFileName), true,
              getLogger()));
        }

        if (suffixLength == 4) {
          asynchronousActions.add(
            new ZipCompressAction(
              new File(higherBaseName), new File(higherFileName), true,
              getLogger()));
        }
      }

      return true;
    }

    return false;
  }

  /**
   * Get index of oldest log file to be retained.
   * @return index of oldest log file.
   */
  public int getMaxIndex() {
    return maxIndex;
  }

  /**
   * Get index of most recent log file.
   * @return index of oldest log file.
   */
  public int getMinIndex() {
    return minIndex;
  }

  /**
   * Set index of oldest log file to be retained.
   * @param maxIndex index of oldest log file to be retained.
   */
  public void setMaxIndex(int maxIndex) {
    this.maxIndex = maxIndex;
  }

  /**
   * Set index of most recent log file.
   * @param minIndex Index of most recent log file.
   */
  public void setMinIndex(int minIndex) {
    this.minIndex = minIndex;
  }
}
