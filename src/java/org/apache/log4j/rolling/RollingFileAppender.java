/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */



package org.apache.log4j.rolling;

import java.io.IOException;
import java.io.Writer;
import java.io.File;

import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * RollingFileAppender extends FileAppender to backup the log files
 * depending on rotation policy.
 *
 * @author Heinz Richter
 * @author Ceki G&uuml;lc&uuml; 
 * */
public class RollingFileAppender extends FileAppender {


  File file;

  TriggeringPolicy triggeringPolicy;
  CopyingPolicy copyingPolicy;

  int maxBackupIndex;
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
  public // synchronization not necessary since doAppend is already synched
  void rollOver() {
    File target;
    File file;

    // If maxBackups <= 0, then there is no file renaming to be done.
    if(maxBackupIndex > 0) {
      // Delete the oldest file, to keep Windows happy.
      file = new File(fileName + '.' + maxBackupIndex);
      if (file.exists())
       file.delete();

      // Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3, 2}
      for (int i = maxBackupIndex - 1; i >= 1; i--) {
	file = new File(fileName + "." + i);
	if (file.exists()) {
	  target = new File(fileName + '.' + (i + 1));
	  LogLog.debug("Renaming file " + file + " to " + target);
	  file.renameTo(target);
	}
      }

      // Rename fileName to fileName.1
      target = new File(fileName + "." + 1);

      this.closeFile(); // keep windows happy.

      file = new File(fileName);
      LogLog.debug("Renaming file " + file + " to " + target);
      file.renameTo(target);
    }

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false, bufferedIO, bufferSize);
    }
    catch(IOException e) {
      LogLog.error("setFile("+fileName+", false) call failed.", e);
    }
  }

  public synchronized void setFile(String fileName, boolean append, 
				   boolean bufferedIO, int bufferSize) 
                                                            throws IOException {
    super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    file = new File(fileName);
  }


  /**
     This method differentiates RollingFileAppender from its super
     class.

     @since 0.9.0
  */
  protected
  void subAppend(LoggingEvent event) {
    super.subAppend(event);
    
    boolean trigger;
    if(triggeringPolicy.isSizeSensitive()) {
      trigger = triggeringPolicy.isTriggeringEvent(file.length());
    } else {
      trigger = triggeringPolicy.isTriggeringEvent();
    }

    if(trigger) {
      closeFile();
      copyingPolicy.copy(file);
      file = new File(fileName);
    }
  }
}
