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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;


/**
 * RollingFileAppender extends FileAppender to backup the log files
 * depending on rotation policy.
 *
 * @author Heinz Richter
 * @author Ceki G&uuml;lc&uuml;
 * @since  1.3
 * */
public class RollingFileAppender extends FileAppender {
  Logger logger = Logger.getLogger(RollingFileAppender.class);
  File activeFile;
  TriggeringPolicy triggeringPolicy;
  RollingPolicy rollingPolicy;

  /**
   * The default constructor simply calls its {@link
   * FileAppender#FileAppender parents constructor}.
   * */
  public RollingFileAppender() {
    super();
  }

  public void activateOptions() {
    if (triggeringPolicy == null) {
      logger.warn("Please set a TriggeringPolicy for ");

      return;
    }

    if (rollingPolicy != null) {
      rollingPolicy.activateOptions();
      String afn = rollingPolicy.getActiveLogFileName();
      activeFile = new File(afn);
      logger.debug("Active log file name: "+afn);
      setFile(afn);
      
      // the activeFile variable is used by the triggeringPolicy.isTriggeringEvent method
      activeFile = new File(afn);
      super.activateOptions();
    } else {
      logger.warn("Please set a rolling policy");
    }
  }

  /**
     Implements the usual roll over behaviour.

     <p>If <code>MaxBackupIndex</code> is positive, then files
     {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
     are renamed to {<code>File.2</code>, ...,
     <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
     renamed <code>File.1</code> and closed. A new <code>File</code> is
     created to receive further log output.

     <p>If <code>MaxBackupIndex</code> is equal to zero, then the
     <code>File</code> is truncated with no backup files created.

   */
  public void rollover() {
    // Note: synchronization not necessary since doAppend is already synched
    // make sure to close the hereto active log file!!
    this.closeWriter();

    rollingPolicy.rollover();

    // Although not certain, the active file name may change after roll over.
    fileName = rollingPolicy.getActiveLogFileName();
    logger.debug("Active file name is now ["+fileName+"].");

    // the activeFile variable is used by the triggeringPolicy.isTriggeringEvent method
    activeFile = new File(fileName);

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false, bufferedIO, bufferSize);
    } catch (IOException e) {
      errorHandler.error(
        "setFile(" + fileName + ", false) call failed.", e,
        ErrorCode.FILE_OPEN_FAILURE);
    }
  }

  /**
     This method differentiates RollingFileAppender from its super
     class.
  */
  protected void subAppend(LoggingEvent event) {
    // The rollover check must precede actual writing. This is the 
    // only correct behavior for time driven triggers. 
    if (triggeringPolicy.isTriggeringEvent(activeFile)) {
      logger.debug("About to rollover");
      rollover();
    }
      
    super.subAppend(event);
  }

  public RollingPolicy getRollingPolicy() {
    return rollingPolicy;
  }

  public TriggeringPolicy getTriggeringPolicy() {
    return triggeringPolicy;
  }

  public void setRollingPolicy(RollingPolicy policy) {
    rollingPolicy = policy;
    if(rollingPolicy instanceof TriggeringPolicy) {
      triggeringPolicy = (TriggeringPolicy) policy;
    }
    
  }

  public void setTriggeringPolicy(TriggeringPolicy policy) {
    triggeringPolicy = policy;
    if(policy instanceof RollingPolicy) {
      rollingPolicy = (RollingPolicy) policy;
    }
  }
}
