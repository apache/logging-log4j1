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
import org.apache.log4j.rolling.helpers.FileNamePattern;
import org.apache.log4j.rolling.helpers.Util;
import org.apache.log4j.spi.OptionHandler;

import java.io.File;


/**
 * The SlidingWindowRollingPolicy rolls over files
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 * */
public class SlidingWindowRollingPolicy implements RollingPolicy,
  OptionHandler {
    
  static Logger logger = Logger.getLogger(SlidingWindowRollingPolicy.class);
  int maxIndex;
  int minIndex;
  FileNamePattern fileNamePattern;
  String activeFileName;

  public SlidingWindowRollingPolicy() {
    minIndex = 1;
    maxIndex = 7;
    activeFileName = null;
  }

  public void rollover() {
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
        // TODO: compress the currently active file into minIndex
        Util.rename(activeFileName, fileNamePattern.convert(minIndex));
      } else {
        // TODO: compress the currently active file (minIndex) into minIndex+1
        //Util.rename(
          //fileNamePattern.convert(minIndex),
          //fileNamePattern.convert(minIndex + 1));
      }
    }
  }

  public void activateOptions() {
    if (maxIndex < minIndex) {
      logger.warn(
        "maxIndex (" + maxIndex + ") cannot be smaller than minIndex ("
        + minIndex + ").");
      logger.warn("Setting maxIndex to equal minIndex.");
      maxIndex = minIndex;
    }
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
    if (activeFileName == null) {
      return fileNamePattern.convert(minIndex);
    } else {
      return activeFileName;
    }
  }

  public String getFileNamePattern() {
    return fileNamePattern.toString();
  }

  public int getMaxIndex() {
    return maxIndex;
  }

  public int getMinIndex() {
    return minIndex;
  }

  public void setFileNamePattern(String fnp) {
    fileNamePattern = new FileNamePattern(fnp);
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
