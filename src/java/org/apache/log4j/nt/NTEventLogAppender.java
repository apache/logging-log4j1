/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.nt;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Priority;
import java.io.*;


/**
   Append to the NT event log system. 

   <p><b>WARNING</b> This appender can only be installed and used on a
   Windows system.

   <p>Do not forget to place the file NTEventLogAppender.dll in a
   directory that is on the PATH of the Windows system. Otherwise, you
   will get a java.lang.UnsatisfiedLinkError.

   @author <a href="mailto:cstaylor@pacbell.net">Chris Taylor</a>
   @author <a href="mailto:jim_cakalic@na.biomerieux.com">Jim Cakalic</a> */
public class NTEventLogAppender extends AppenderSkeleton {
  private int _handle = 0;

  private static final int FATAL  = Priority.FATAL.toInt();
  private static final int ERROR  = Priority.ERROR.toInt();
  private static final int WARN   = Priority.WARN.toInt();
  private static final int INFO   = Priority.INFO.toInt();
  private static final int DEBUG  = Priority.DEBUG.toInt();
  
  public NTEventLogAppender() {
    this(null, null, null);
  }
  
  public NTEventLogAppender(String source) {
    this(null, source, null);
  }
  
  public NTEventLogAppender(String server, String source) {
    this(server, source, null);
  }
  
  public NTEventLogAppender(Layout layout) {
    this(null, null, layout);
  }
  
  public NTEventLogAppender(String source, Layout layout) {
    this(null, source, layout);
  }
  
  public NTEventLogAppender(String server, String source, Layout layout) {
    if (source == null) {
      source = "Log4j";
    }
    if (layout == null) {
      this.layout = new TTCCLayout();
    } else {
      this.layout = layout;
    }
    
    try {
      _handle = registerEventSource(server, source);
    } catch (Exception e) {
      e.printStackTrace();
      _handle = 0;
    }
  }

  public
  void close() {
    // unregister ...
  }
  
  public void append(LoggingEvent event) {
    // First, format the log string so we can send it to NT.
    StringWriter sw_writer = new StringWriter();
    PrintWriter pw_writer = new PrintWriter(sw_writer);
    pw_writer.print(layout.format(event));
    
    // And append a throwable if supplied
    if (event.throwable != null) 
      event.throwable.printStackTrace(pw_writer);
    
    pw_writer.close();
    
    // Normalize the log message priority into the supported categories
    int nt_category = event.priority.toInt();
    if (nt_category < FATAL || nt_category > DEBUG) {
      nt_category = INFO;
    }
    reportEvent(_handle, sw_writer.toString(), nt_category);
  }
  
  
  public 
  void finalize() {
    deregisterEventSource(_handle);
    _handle = 0;
  }

/**
     The <code>NTEventLogAppender</code> requires a layout. Hence,
     this method always returns <code>true</code>. */
  public
  boolean requiresLayout() {
    return true;
  }
  
  native private int registerEventSource(String server, String source);
  native private void reportEvent(int handle, String message, int priority);
  native private void deregisterEventSource(int handle);
  
  static {
    System.loadLibrary("NTEventLogAppender");
  }
}   
