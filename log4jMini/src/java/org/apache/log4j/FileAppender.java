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

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;

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
     The name of the log file. */
  protected String fileName = null;

  protected Writer w;

  /**
     Is the QuietWriter ours or was it created and passed by the user?
  */
  protected boolean wIsOurs = false;
  
  /**
     The default constructor does no longer set a default layout nor a
     default output target.  */
  public
  FileAppender() {
  }

  /**
     Instantiate a FileAppender and set the output destination to
     <code>writer</code>.

     <p>The <code>writer</code> must have been opened by the user.  */
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
	error("setFile("+fileName+","+fileAppend+") call failed.", e);
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
    if(this.w == null) {
      error("No output target set for \""+ name+"\".");
      return false;
    }
    
    if(this.layout == null) {
      error("No layout set for \""+ name+"\".");
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
    if(this.wIsOurs && this.w != null) {
      try {
	this.w.close();
      }
      catch(java.io.IOException e) {
	LogLog.error("Could not close output stream " + w, e);
      }
    }      
  }

  /**
     Retuns the option names for this component, namely the string
     array {{@link #FILE_OPTION}, {@link #APPEND_OPTION}} in addition
     to the options of its super class {@link AppenderSkeleton}.  */
  public
  String[] getOptionStrings() {
    return new String[] {THRESHOLD_OPTION, FILE_OPTION, APPEND_OPTION};
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
    this.w = new FileWriter(fileName, append);
    this.fileName = fileName;
    this.wIsOurs = true;
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
    } else if (key.equalsIgnoreCase(APPEND_OPTION)) {
      fileAppend = OptionConverter.toBoolean(value, fileAppend);
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
    this.w = writer;
    this.wIsOurs = false;    
  }

  /**
     Actual writing occurs here.

     <p>Most sub-classes of <code>FileAppender</code> will need to
     override this method.

     @since 0.9.0 */
  protected
  void subAppend(LoggingEvent event) {
    this.write(this.layout.format(event));


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
     this.w = null;
  }

  public
  void write(String string) {
    try {
      w.write(string);
    } catch(IOException e) {
      error("Failed to writing", e);
    }
  }
  
}
