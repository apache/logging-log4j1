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

import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.LogLog;
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
  File file;
  TriggeringPolicy triggeringPolicy;
  RollingPolicy rollingPolicy;

  /**
   * The default constructor simply calls its {@link
   * FileAppender#FileAppender parents constructor}.
   * */
  public RollingFileAppender() {
    super();
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
  
      rollingPolicy.rollover(file);

      file = rollingPolicy.getLogFile(fileName);

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false, bufferedIO, bufferSize);
    } catch (IOException e) {
      LogLog.error("setFile(" + fileName + ", false) call failed.", e);
    }
  }

  public synchronized void setFile(
    String fileName, boolean append, boolean bufferedIO, int bufferSize)
    throws IOException {
    super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    file = new File(fileName);
  }

  /**
     This method differentiates RollingFileAppender from its super
     class.
  */
  protected void subAppend(LoggingEvent event) {
    super.subAppend(event);

    boolean trigger;

    if (triggeringPolicy.isSizeSensitive()) {
      trigger = triggeringPolicy.isTriggeringEvent(file.length());
    } else {
      trigger = triggeringPolicy.isTriggeringEvent();
    }

    if (trigger) {
      rollover();
    }
  }
}
