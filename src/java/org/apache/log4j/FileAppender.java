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
   FileAppender appends log events to a file. 
   
   <b>Support for java.io.Writer and console appending has been
   deprecated and will be removed in the near future.</b> You are
   stongly encouraged to use the replacement solutions: {@link
   WriterAppender} and {@link ConsoleAppender}.
   

   @author Ceki G&uuml;lc&uuml; */
public class FileAppender extends WriterAppender {

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

  /**
     Is the QuietWriter ours or was it created and passed by the user?     

     @deprecated FileAppender will not support streams passed by the
     user in the future. */
  protected boolean qwIsOurs = false;

  /**
     The default constructor does not do anything. 
  */
  public
  FileAppender() {
  }


  /**
     Instantiate a FileAppender and set the output destination to a
     new {@link OutputStreamWriter} initialized with <code>os</code>
     as its {@link OutputStream}.  

     @deprecated <b>The functionality of constructor form has been
     replaced by the {@link WriterAppender}. This constructor will be
     removed in the <em>near</em> term.</b>

  */
  public
  FileAppender(Layout layout, OutputStream os) {
    super(layout, os);
  }
  
  /**
     Instantiate a FileAppender and set the output destination to
     <code>writer</code>.

     <p>The <code>writer</code> must have been opened by the user.  

     @deprecated <b>The functionality of constructor form has been
     replaced by the {@link WriterAppender}. This constructor will be
     removed in the <em>near</em> term.</b>
  */
  public
  FileAppender(Layout layout, Writer writer) {
    super(layout, writer);
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
    } else {
      LogLog.error("Filename option not set for appender ["+name+"].");
    }
  }

 /**
     Close the file opened previously.
  */
  protected
  void closeFile() {
    // FIXME (remove qwIsOurs)
    if(this.qw != null && this.qwIsOurs) {
      try {
	LogLog.debug("////////////////////////////////////////////////");
	this.qw.close();
      }
      catch(java.io.IOException e) {
	// Exceptionally, it does not make sense to delegate to an
	// ErrorHandler. Since a closed appender is basically dead.
	LogLog.error("Could not close " + qw, e);
      }
    }
  }

  /**
     Return an option by name.     
   */
  public
  String getOption(String key) {
    if (key.equalsIgnoreCase(FILE_OPTION)) {
      return fileName;
    } else if (key.equalsIgnoreCase(APPEND_OPTION)) {
      return fileAppend ? "true" : "false";
    } else {
      return super.getOption(key);
    }
  }

 
  /**
     Retuns the option names for this component, namely the string
     array {{@link #FILE_OPTION}, {@link #APPEND_OPTION}} in addition
     to the options of its super class {@link WriterAppender}.  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {FILE_OPTION, APPEND_OPTION});
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
    @param append   If true will append to fileName. Otherwise will
        truncate fileName.
  */
  public
  synchronized
  void setFile(String fileName, boolean append) throws IOException {
    reset();
    this.setQWForFiles(new FileWriter(fileName, append));
    this.tp = new TracerPrintWriter(qw);
    this.fileName = fileName;
    this.fileAppend = append;
    this.qwIsOurs = true;
    writeHeader();
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
     class {@link WriterAppender} are also recognized.

     <p>The <b>File</b> option takes a string value which should be
     one of the strings "System.out" or "System.err" or the name of a
     file. 

     <font color="#FF0044"><b>Note that the "System.out" or "System.err"
     options are deprecated. Use {@link ConsoleAppender}
     instead.</b></font>

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
      String val = value.trim();
      if(val.equalsIgnoreCase("System.out")) {
	setWriter(new OutputStreamWriter(System.out));
      } else if(val.equalsIgnoreCase("System.err")) {
	setWriter(new OutputStreamWriter(System.err));
      } else {
	fileName = val;
      }
    }
    else if (key.equalsIgnoreCase(APPEND_OPTION)) {
      fileAppend = OptionConverter.toBoolean(value, fileAppend);
    }
  }

  /**
     Sets the quiet writer being used.
     
     This method is overriden by {@link RollingFileAppender}.
   */
  protected
  void setQWForFiles(Writer writer) {
     this.qw = new QuietWriter(writer, errorHandler);
  }


  /**
     Close any previously opened file and call the parent's
     <code>reset</code>.  */
  protected
  void reset() {
    closeFile();
    this.fileName = null;
    super.reset();    
  }  
}

