/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j;

import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.TracerPrintWriter;

// Contibutors: Jens Uwe Pipka <jens.pipka@gmx.de>

/**
   WriterAppender appends log events to a {@link java.io.Writer} or an
   {@link java.io.OutputStream} depending on the user's choice.

   @author Ceki G&uuml;lc&uuml;
   @since 1.1 */
public class WriterAppender extends AppenderSkeleton {

  /**
     A string constant used in naming the option for immediate
     flushing of the output stream at the end of each append
     operation. Current value of this string constant is
     <b>ImmediateFlush</b>.

     <p>Note that all option keys are case sensitive.     
  */
  public static final String IMMEDIATE_FLUSH_OPTION = "ImmediateFlush";

  /**
     Immediate flush means that the undelying writer or output stream
     will be flushed at the end of each append operation. Immediate
     flush is slower but ensures that each append request is actually
     written. If <code>immediateFlush</code> is set to
     <code>false</code>, then there is a good chance that the last few
     logs events are not actually written to persistent media if and
     when the application crashes.

     <p>The <code>immediateFlush</code> variable is set to
     <code>true</code> by default.

  */
  protected boolean immediateFlush = true;

  /**
     This is the {@link QuietWriter quietWriter} where we will write
     to. 
  */
  protected QuietWriter qw;

  /**
     {@link TracerPrintWriter} is specialized in optimized printing
     of stack traces (obtained from throwables) to a Writer. 
  */
  protected TracerPrintWriter tp;
  
  
  /**
     This default constructor does nothing.  */
  public
  WriterAppender() {
  }

  /**
     Instantiate a WriterAppender and set the output destination to a
     new {@link OutputStreamWriter} initialized with <code>os</code>
     as its {@link OutputStream}.  */
  public
  WriterAppender(Layout layout, OutputStream os) {
    this(layout, new OutputStreamWriter(os));
  }
  
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


  /**
     Does nothing.
  */
  public
  void activateOptions() {    
  }


  /**
     This method called by {@link AppenderSkeleton#doAppend}
     method. 

     <p>If the output stream exists an is writable then write a log
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
    subAppend(event);
   } 

  /**
     This method determines if there is a sense in attempting to append.
     
     <p>It checks whether there is a set output target and also if
     there is a set layout. If these checks fail, then the boolean
     value <code>false</code> is returned. */
  protected
  boolean checkEntryConditions() {
    if(this.closed) {
      LogLog.warn("Not allowed to write to a closed appender.");
      return false;
    }

    if(this.qw == null) {
      errorHandler.error("No output target set for appender named \""+ 
			name+"\".");
      return false;
    }
    
    if(this.layout == null) {
      errorHandler.error("No layout set for appender named \""+ name+"\".");
      return false;
    }
    return true;
  }


  /**
     Will close the stream opened by a previos {@link #setFile}
     call. If the writer is owned by the user it remains untouched.

     @see #setFile
     @see #setWriter
     @since 0.8.4
  */
  public
  synchronized
  void close() {
    if(this.closed)
      return;
    this.closed = true;
    writeFooter();
    reset();
  }

  public
  String getOption(String key) {
    if (key.equalsIgnoreCase(IMMEDIATE_FLUSH_OPTION)) {
      return immediateFlush ? "true" : "false";
    } else {
      return super.getOption(key);
    }
  }


  /**
     Retuns the option names for this component, namely the string
     array {{@link #FILE_OPTION}, {@link #APPEND_OPTION}} in addition
     to the options of its super class {@link AppenderSkeleton}.  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
           new String[] {IMMEDIATE_FLUSH_OPTION});
  }



  /**
     Set the {@link ErrorHandler} for this FileAppender and also the
     undelying {@link QuietWriter} if any. */
  public
  synchronized 
  void setErrorHandler(ErrorHandler eh) {
    if(eh == null) {
      LogLog.warn("You have tried to set a null error-handler.");
    } else {
      this.errorHandler = eh;
      if(this.qw != null) {
	this.qw.setErrorHandler(eh);
      }
    }    
  }
  
  
  /**
     Set WriterAppender specific options.
          
     The <b>ImmediateFlush</b> option is recognized on top of options
     for the super class {@link AppenderSkeleton}.
  */
  public
  void setOption(String key, String value) {
    if(value == null) return;
    super.setOption(key, value);
    
    if (key.equalsIgnoreCase(IMMEDIATE_FLUSH_OPTION)) {
      immediateFlush = OptionConverter.toBoolean(value, immediateFlush);
    }
  }

  
  /**
    <p>Sets the Writer where the log output will go. The
    specified Writer must be opened by the user and be
    writable.


    <p>If there was already an opened stream opened through the {@link
    #setFile setFile} method, then the previous stream is closed
    first. If the stream was opened by the user and passed to this
    method, then the previous stream remains untouched. It is the
    user's responsability to close it.

    <p><b>WARNING:</b> Logging to an unopened Writer will fail.
    <p>  
    @param Writer An already opened Writer.
    @return Writer The previously attached Writer.
  */
  public
  synchronized
  void setWriter(Writer writer) {
    reset();
    this.qw = new QuietWriter(writer, errorHandler);
    this.tp = new TracerPrintWriter(qw);
    writeHeader();
  }


  /**
     Actual writing occurs here.

     <p>Most sub-classes of <code>FileAppender</code> will need to
     override this method.

     @since 0.9.0 */
  protected
  void subAppend(LoggingEvent event) {
    this.qw.write(this.layout.format(event));

    if(layout.ignoresThrowable()) {
      if(event.throwable != null) {
	event.throwable.printStackTrace(this.tp);
      }
      // in case we received this event from a remote client    
      else {
	String tInfo = event.getThrowableInformation();
	if (tInfo != null) 
	  this.qw.write(tInfo);
      }
    }
 
    if(this.immediateFlush) {
      this.qw.flush();
    } 
  }



  /**
     The WriterAppender requires a layout. Hence, this method returns
     <code>true</code>.
  */
  public
  boolean requiresLayout() {
    return true;
  }

  /**
     Clerar internal references to the writer and other variables.

     Sub-classes can override this method for an alternate closing
     behaviour.  */
  protected
  void reset() {
     this.qw = null;
     this.tp = null;    
  }  

  protected
  void writeFooter() {
    if(layout != null) {
      String f = layout.getFooter();
      if(f != null && this.qw != null)
	this.qw.write(f);
    }
  }

  protected 
  void writeHeader() {
    if(layout != null) {
      String h = layout.getHeader();
      if(h != null && this.qw != null)
	this.qw.write(h);
    }
  }
}
