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
import org.apache.log4j.rolling.helpers.DateTokenConverter;
import org.apache.log4j.rolling.helpers.FileNamePattern;
import org.apache.log4j.rolling.helpers.RollingCalendar;
import org.apache.log4j.rolling.helpers.Util;

import java.io.File;

import java.util.Date;


/**
 *
 *
 * If configuring programatically, do not forget to call {@link #activateOptions}
 * method before using this policy.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class TimeBasedRollingPolicy extends RollingPolicySkeleton
  implements TriggeringPolicy {
    
  static final Logger logger = Logger.getLogger(TimeBasedRollingPolicy.class);
  
  FileNamePattern fileNamePattern;
  String fileNamePatternStr;
  RollingCalendar rc;
  long nextCheck;
  Date now = new Date();
  String currentFileName = null;
  String activeFileName;

  public void activateOptions() {
    // find out period from the filename pattern
    if (fileNamePatternStr != null) {
      int len = fileNamePatternStr.length();
      
      if (fileNamePatternStr.endsWith(".gz")) {
          logger.debug("Will use gz compression");
          fileNamePattern = new FileNamePattern(fileNamePatternStr.substring(0, len -3));
          compressionMode = Compress.GZ;
        } else if (fileNamePatternStr.endsWith(".zip")) {
          logger.debug("Will use zip compression");
          fileNamePattern = new FileNamePattern(fileNamePatternStr.substring(0, len -4));
          compressionMode = Compress.GZ;
        } else {
          fileNamePattern = new FileNamePattern(fileNamePatternStr);
          compressionMode = Compress.NONE;
        }
      }
      
      DateTokenConverter dtc = fileNamePattern.getDateTokenConverter();

      if (dtc == null) {
        throw new IllegalStateException(
          "FileNamePattern [" + fileNamePattern.getPattern()
          + "] does not contain a valid DateToken");
      }

      rc = new RollingCalendar();
      rc.init(dtc.getDatePattern());
      logger.debug(
        "The date pattern is [" + dtc.getDatePattern()
        + "] from file name pattern [" + fileNamePattern.getPattern() + "].");
      rc.printPeriodicity();

      long n = System.currentTimeMillis();
      now.setTime(n);
      nextCheck = rc.getNextCheckMillis(now);

      //Date nc = new Date();
      //nc.setTime(nextCheck);
      //logger.debug("Next check set to: " + nc);  
  }


  /**
   * 
   * The active log file is determined by the value of the activeFileName 
   * option if it is set. However, in case the activeFileName is left blank, 
   * then, the active log file equals the file name for the current period
   * as computed by the fileNamePattern.
   *  
   */
  public String getActiveLogFileName() {
    logger.debug("getActiveLogFileName called");
    if (activeFileName == null) {
      return fileNamePattern.convert(now);
    } else {
      return activeFileName;
    }
  }

  public void rollover() {
    logger.debug("rollover called");
    logger.debug("compressionMode: " + compressionMode);


    // if active file name is not set, then the active logging 
    // file is given by the value of currentFileName variable.
    if (activeFileName == null) {
      if (currentFileName != null) {
        //logger.debug("currentFileName != null");

        switch (compressionMode) {
        case Compress.NONE:

          // nothing to do;
          break;

        case Compress.GZ:
          logger.debug("Compressing [" + currentFileName + "]");
          Compress.GZCompress(currentFileName);

          break;
        }
      }
    } else { // if activeFileName != null, then the value of the
      // active logging is given by activeFileName

      switch (compressionMode) {
      case Compress.NONE:
        Util.rename(activeFileName, currentFileName);

        break;

      case Compress.GZ:
        logger.debug("Compressing [" + currentFileName + "]");
        Compress.GZCompress(activeFileName, currentFileName);

        break;
      }
    }
  }

  public void setFileNamePattern(String fnp) {
    fileNamePatternStr = fnp;
  }

  public boolean isTriggeringEvent(File file) {
    //logger.debug("Is triggering event called");
    long n = System.currentTimeMillis();

    if (n >= nextCheck) {
      logger.debug("Time to trigger rollover");

      // We set the oldFileName before we set the 'now' variable
      // The currentFileName is the currently active file name when
      // the activeFileName is not set specifically by the user.
      currentFileName = fileNamePattern.convert(now);

      now.setTime(n);
      //logger.debug("ActiveLogFileName will return " + getActiveLogFileName());
      nextCheck = rc.getNextCheckMillis(now);

      //logger.debug("nextCheck is :"+nextCheck);
      Date x = new Date();
      x.setTime(nextCheck);
      logger.debug("Next check: " + x);

      return true;
    } else {
      return false;
    }
  }

  public String getActiveFileName() {
    return activeFileName;
  }

  public void setActiveFileName(String string) {
    activeFileName = string;
  }
}
