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

import org.apache.log4j.Logger;
import org.apache.log4j.rolling.helpers.Compress;
import org.apache.log4j.rolling.helpers.FileNamePattern;
import org.apache.log4j.rolling.helpers.Util;


import java.io.File;


/**
 * The SlidingWindowRollingPolicy rolls over files
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 * */
public class SlidingWindowRollingPolicy extends RollingPolicySkeleton {
    
  static Logger logger = Logger.getLogger(SlidingWindowRollingPolicy.class);
  int maxIndex;
  int minIndex;
  FileNamePattern fileNamePattern;
  String  fileNamePatternStr;
  String activeFileName;
 
  public SlidingWindowRollingPolicy() {
    minIndex = 1;
    maxIndex = 7;
    activeFileName = null;
  }

  public void rollover() throws RolloverFailure {
    // Inside this method it is guaranteed that the hereto active log fil is closed.
    // If maxIndex <= 0, then there is no file renaming to be done.
    if (maxIndex >= 0) {
      // Delete the oldest file, to keep Windows happy.
      File file = new File(fileNamePattern.convert(maxIndex));

      if (file.exists()) {
        file.delete();
      }

      // Map {(maxIndex - 1), ..., minIndex} to {maxIndex, ..., minIndex+1}
      for (int i = maxIndex - 1; i >= minIndex; i--) {
        Util.rename(
          fileNamePattern.convert(i), fileNamePattern.convert(i + 1));
      }

      if (activeFileName != null) {
        //move active file name to min
        switch(compressionMode) {
          case Compress.NONE: 
            Util.rename(activeFileName, fileNamePattern.convert(minIndex));
            break;
            case Compress.GZ: 
              Compress.GZCompress(activeFileName, fileNamePattern.convert(minIndex));
              break;
        }
      }
    }
  }

  public void activateOptions() {
    if(activeFileName == null) {
      logger.warn("The active file name option must be set before using this rolling policy.");
      throw new IllegalStateException("The activeFileName option must be set.");
    }
    
    if (maxIndex < minIndex) {
      logger.warn(
        "maxIndex (" + maxIndex + ") cannot be smaller than minIndex ("
        + minIndex + ").");
      logger.warn("Setting maxIndex to equal minIndex.");
      maxIndex = minIndex;
    }
    
    switch(compressionMode) {
      case Compress.GZ: 
      if(!fileNamePatternStr.endsWith(".gz")) {    
        fileNamePatternStr = fileNamePatternStr + ".gz";
      } 
      break;
    }      
    fileNamePattern = new FileNamePattern(fileNamePatternStr);
  }

  /**
   *
   * If the <b>ActiveFileName</b> option is set, then this method simply returns the
   * value of the option. Otherwise, it returns the value of <b>FileNamePattern</b>
   * for <b>MaxIndex</b>. For example, if <b>ActiveFileName</b> is not set and
   * <b>FileNamePattern</b> is set to "mylogfile.%i" and <b>MaxIndex</b> is set to 0,
   * then this method will return "mylogfile.0".
   *
   */
  public String getActiveLogFileName() {
    return activeFileName;
  }

  public String getFileNamePattern() {
    return fileNamePatternStr;
  }

  public int getMaxIndex() {
    return maxIndex;
  }

  public int getMinIndex() {
    return minIndex;
  }

  public void setFileNamePattern(String fnp) {
    fileNamePatternStr = fnp;
  }

  public void setMaxIndex(int maxIndex) {
    this.maxIndex = maxIndex;
  }

  public void setMinIndex(int minIndex) {
    this.minIndex = minIndex;
  }


  public String getActiveFileName() {
    return activeFileName;
  }

  public void setActiveFileName(String afn) {
    activeFileName = afn;
  }

}
