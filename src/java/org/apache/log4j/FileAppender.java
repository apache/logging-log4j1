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
   FileAppender appends log events to the console, to a file, to a
   {@link java.io.Writer} or an {@link java.io.OutputStream} depending
   on the user's choice.

   @author Ceki G&uuml;lc&uuml;
   */
public class FileAppender extends AppenderSkeleton {

  /**
     A string constant used in naming the option for setting the
     output file. Current value of this string constant is
     <b>File</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String FILE_OPTION = "File";

  /**
     A string constant used in naming the option for immediate
     flushing of the output stream at the end of each append
     operation. Current value of this string constant is
     <b>ImmediateFlush</b>.

     <p>Note that all option keys are case sensitive.
     
     @since 0.9.1
  */
  public static final String IMMEDIATE_FLUSH_OPTION = "ImmediateFlush";


  /**
     A string constant used in naming the option that determines whether 
     the output file will be truncated or appended to. Current value
     of this string constant is <b>Append</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String APPEND_OPTION = "Append";

  
  /** Append to or truncate the file? The default value for this
      variable is <code>true</code>, meaning that by default a
      <code>FileAppender</code> will append to an existing file and
      not truncate it.

      <p>This option is meaningful only if the FileAppender opens the
      file.      
  */
  protected boolean fileAppend = true;


  /**
     Immediate flush means that the undelying writer or stream will be
     flushed at the end of each append operation. Immediate flush is
     slower but ensures that each append request is actually
     written. If <code>immediateFlush</code> is set to
     <code>false</code>, then there is a good chance that the last few
     logs events are not actually written to persistent media when the
     application crashes.

     <p>The <code>immediateFlush</code> variable is set to
     <code>true</code> by default.

     @since 0.9.1 */
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
     The name of the log file. */
  protected String fileName = null;


  /**
     Is the QuietWriter ours or was it created and passed by the user?
  */
  protected boolean qwIsOurs = false;
  
  /**
     The default constructor does no longer set a default layout nor a
     default output target.  */
  public
  FileAppender() {
  }

  /**
     Instantiate a FileAppender and set the output destination to a
     new {@link OutputStreamWriter} initialized with <code>os</code>
     as its {@link OutputStream}.  */
  public
  FileAppender(Layout layout, OutputStream os) {
    this(layout, new OutputStreamWriter(os));
  }
  
  /**
     Instantiate a FileAppender and set the output destination to
     <code>writer</code>.

     <p>The <code>writer</code> must have been opened by the user.  */
  public
  FileAppender(Layout layout, Writer writer) {
    this.layout = layout;
    this.setWriter(writer);
  }                    


  /**
    Instantiate a FileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the ouput
    destination for this appender.

    <p>If the <code>append</code> parameter is true, the file will be
    appended to. Otherwise, the file desginated by
    <code>filename</code> will be truncated before being opened.
  */
  public
  FileAppender(Layout layout, String filename, boolean append)
                                      throws IOException {
    this.layout = layout;
    this.setFile(filename, append);
  }

  /**
     Instantiate a FileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the output
    destination for this appender.

    <p>The file will be appended to.  */
  public
  FileAppender(Layout layout, String filename) throws IOException {
    this(layout, filename, true);
  }

  /**
     If the a value for {@link #FILE_OPTION} is non-null, then {@link
     #setFile} is called with the values of {@link #FILE_OPTION} and
     {@link #APPEND_OPTION}.

     @since 0.8.1 */
  public
  void activateOptions() {    
    if(fileName != null) {
      try {
	setFile(fileName, fileAppend);
      }
      catch(java.io.IOException e) {
	errorHandler.error("setFile("+fileName+","+fileAppend+") call failed.",
			   e, ErrorCode.FILE_OPEN_FAILURE);
      }
    }
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
  void close() {
    this.closed = true;
    reset();
  }


  
  /**
     Close this.writer if opened by setFile or FileAppend(filename..)
  */
  protected
  void closeWriterIfOurs() {
    if(this.qwIsOurs && this.qw != null) {
      try {
	this.qw.close();
      }
      catch(java.io.IOException e) {
	LogLog.error("Could not close output stream " + qw, e);
      }
    }      
  }

  /**
     Retuns the option names for this component, namely the string
     array {{@link #FILE_OPTION}, {@link #APPEND_OPTION}} in addition
     to the options of its super class {@link AppenderSkeleton}.  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {FILE_OPTION, APPEND_OPTION, IMMEDIATE_FLUSH_OPTION});
  }


  /**
     Set the {@link ErrorHandler} for this FileAppender and also the
     undelying {@link QuietWriter} if any. */
  public
  synchronized 
  void setErrorHandler(ErrorHandler eh) {
    this.errorHandler = eh;
    if(this.qwIsOurs && this.qw != null) {
      this.qw.setErrorHandler(eh);
    }    
  }

  
  /**
    <p>Sets and <i>opens</i> the file where the log output will
    go. The specified file must be writable. 

    <p>If there was already an opened stream opened through this
    method, then the previous stream is closed first. If the stream
    was opened by the user and passed to {@link #setWriter
    setWriter}, then the previous stream remains
    untouched. It is the users responsability to close it.

    @param fileName The path to the log file.
    @param boolean If true will append to fileName. Otherwise will
    truncate fileName.  */
  public
  synchronized
  void setFile(String fileName, boolean append) throws IOException {
    reset();
    this.setQWForFiles(new FileWriter(fileName, append));
    this.tp = new TracerPrintWriter(qw);
    this.fileName = fileName;
    this.qwIsOurs = true;
  }

  /**
    <p>Sets and <i>opens</i> the file where the log output will
    go. The specified file must be writable.

    <p>The open mode (append/truncate) will depend on the value of
    FileAppend option. If undefined, append mode is used.

    @param fileName The name of the log file.
  */
  public
  void setFile(String fileName) throws IOException {    
    this.setFile(fileName, fileAppend);
  }
  
  /**
     Set FileAppender specific options.
          
     The recognized options are <b>File</b> and <b>Append</b>,
     i.e. the values of the string constants {@link #FILE_OPTION} and
     respectively {@link #APPEND_OPTION}. The options of the super
     class {@link AppenderSkeleton} are also recognized.

     <p>The <b>File</b> option takes a string value which should be
     one of the strings "System.out" or "System.err" or the name of a
     file.

     <p>If the option is set to "System.out" or "System.err" the
     output will go to the corresponding stream. Otherwise, if the
     option is set to the name of a file, then the file will be opened
     and output will go there.
     
     <p>The <b>Append</b> option takes a boolean value. It is set to
     <code>true</code> by default. If true, then <code>File</code>
     will be opened in append mode by {@link #setFile setFile} (see
     above). Otherwise, {@link #setFile setFile} will open
     <code>File</code> in truncate mode.

     <p>Note: Actual opening of the file is made when {@link
     #activateOptions} is called, not when the options are set.
     
     @since 0.8.1 */
  public
  void setOption(String key, String value) {
    if(value == null) return;
    super.setOption(key, value);
    
    if(key.equalsIgnoreCase(FILE_OPTION)) {
      // Trim spaces from both ends. The users probably does not want 
      // trailing spaces in file names.
      value = value.trim();
      if(value.equalsIgnoreCase("System.out"))
	setWriter(new OutputStreamWriter(System.out));
      else if(value.equalsIgnoreCase("System.err"))
	setWriter(new OutputStreamWriter(System.err));
      else {
	fileName = value;
      } 
    }
    else if (key.equalsIgnoreCase(APPEND_OPTION)) {
      fileAppend = OptionConverter.toBoolean(value, fileAppend);
    }
    else if (key.equalsIgnoreCase(IMMEDIATE_FLUSH_OPTION)) {
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
    this.qwIsOurs = false;    
  }

  protected
  void setQWForFiles(Writer writer) {
     this.qw = new QuietWriter(writer, errorHandler);
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
      else if (event.throwableInformation != null) { 
	this.qw.write(event.throwableInformation);
      }
    }
 
    if(this.immediateFlush) {
      this.qw.flush();
    } 
  }

  /**
     The FileAppender requires a layout. Hence, this method returns
     <code>true</code>.

     @since 0.8.4 */
  public
  boolean requiresLayout() {
    return true;
  }

  protected
  void reset() {
     closeWriterIfOurs();
     this.fileName = null;
     this.qw = null;
     this.tp = null;    
  }
  
}
