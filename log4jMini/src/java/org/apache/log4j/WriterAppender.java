/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;

// Contibutors: Jens Uwe Pipka <jens.pipka@gmx.de>

/**
   WriterAppender appends log events to a {@link java.io.Writer} or an
   {@link java.io.OutputStream} depending on the user's choice.

   @author Ceki G&uuml;lc&uuml;
   @since 1.1 */
public class WriterAppender extends AppenderSkeleton {

  /**
     This is the {@link QuietWriter quietWriter} where we will write
     to. 
  */
  protected Writer w;

  
  /**
     This default constructor does nothing.  */
  public
  WriterAppender() {
  }

  /**
     Instantiate a WriterAppender and set the output destination to a
     new {@link OutputStreamWriter} initialized with <code>os</code>
     as its {@link OutputStream}.  */
  //public
  //WriterAppender(Layout layout, OutputStream os) {
  //this(layout, new OutputStreamWriter(os));
  //}
  
  /**
     Instantiate a WriterAppender and set the output destination to
     <code>writer</code>.

     <p>The <code>writer</code> must have been previously opened by
     the user.  */
  public
  WriterAppender(Layout layout, Writer writer) {
    this.layout = layout;
    this.setWriter(writer);
  }

  public
  void write(String string) {
    try {
      w.write(string);
    } catch(IOException e) {
      error("Failed to writing", e);
    }
  }

  /**
     Does nothing.
  */
  public
  void activateOptions() {    
  }


  /**
     This method is called by the {@link AppenderSkeleton#doAppend}
     method. 

     <p>If the output stream exists and is writable then write a log
     statement to the output stream. Otherwise, write a single warning
     message to <code>System.err</code>.

     <p>The format of the output will depend on this appender's
     layout.
     
  */
  public
  void append(LoggingEvent event) {

    // Reminder: the nesting of calls is:
    //
    //    doAppend()
    //      - check threshold
    //      - filter
    //      - append();
    //        - checkEntryConditions();
    //        - subAppend();

    if(!checkEntryConditions()) {
      return;
    }
    write(this.layout.format(event));

    if(layout.ignoresThrowable()) {
      String s = event.getThrowableStr();
      if (s != null) {
	write(s);
      }
    }

    try {
      w.flush();
    } catch(IOException e) {
      error("Failed to flush", e);
    }
   } 

  /**
     This method determines if there is a sense in attempting to append.
     
     <p>It checks whether there is a set output target and also if
     there is a set layout. If these checks fail, then the boolean
     value <code>false</code> is returned. */
  protected
  boolean checkEntryConditions() {
    if(this.closed) {
      return false;
    }

    if(this.w == null) {
      error("No Writer for ["+name+"]");
      return false;
    }
    
    if(this.layout == null) {
      error("No layout for ["+ name+"]");
      return false;
    }
    return true;
  }


  /**
     Close this appender instance. The underlying stream or writer is
     also closed.
     
     <p>Closed appenders cannot be reused.

     @see #setWriter
     @since 0.8.4 */
  public
  synchronized
  void close() {
    if(this.closed)
      return;
    this.closed = true;
    closeWriter();
    this.w = null;
  }

  /**
     Close the underlying {@link java.io.Writer}.
  */
  protected 
  void closeWriter() {
    if(w != null) {
      try {
	w.close();
      } catch(IOException e) {
	LogLog.error("Failed closing", e); 
      }
    }
  }

 
  /**
    <p>Sets the Writer where the log output will go. The
    specified Writer must be opened by the user and be
    writable.
    
    <p>The <code>java.io.Writer</code> will be closed when the
    appender instance is closed.

    
    <p><b>WARNING:</b> Logging to an unopened Writer will fail.
    <p>  
    @param Writer An already opened Writer.  */
  public
  synchronized
  void setWriter(Writer writer) {
    closeWriter();
    this.w = null;
    this.w = writer;
  }

  /**
     Clear internal references to the writer and other variables.

     Subclasses can override this method for an alternate closing
     behavior.  */
  //protected
  //void reset() {
  //closeWriter();
  //this.w = null;
  //}
}
