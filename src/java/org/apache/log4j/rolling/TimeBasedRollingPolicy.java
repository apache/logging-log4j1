/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
