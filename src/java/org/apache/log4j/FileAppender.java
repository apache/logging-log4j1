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
     
     @deprecated We now use JavaBeans introspection to configure
     components. Options strings are no longer needed.
  */
  public static final String FILE_OPTION = "File";


  /**
     A string constant used in naming the option that determines whether 
     the output file will be truncated or appended to. Current value
     of this string constant is <b>Append</b>.

     <p>Note that all option keys are case sensitive.

     @deprecated We now use JavaBeans introspection to configure
     components. Options strings are no longer needed.     
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

     @deprecated <b>The functionality of this constructor form has
     been replaced by the {@link WriterAppender}. This constructor
     will be removed in the <em>near</em> term.</b>

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
    appended to. Otherwise, the file designated by
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
     The <b>File</b> option takes a string value which should be
     the name of the file to append to. Special values "System.out" or
     "System.err" are interpreted as the standard out and standard
     error streams.

     <p><font color="#DD0044"><b>Note that the "System.out" or "System.err"
     options are deprecated. Use {@link ConsoleAppender}
     instead.</b></font>

     <p>If the option is set to "System.out" or "System.err" the
     output will go to the corresponding stream. Otherwise, if the
     option is set to the name of a file, then the file will be opened
     and output will go there.
     
     <p>Note: Actual opening of the file is made when {@link
     #activateOptions} is called, not when the options are set.
   */
  public void setFile(String file) {
    // Trim spaces from both ends. The users probably does not want 
    // trailing spaces in file names.
    String val = file.trim();
    if(val.equalsIgnoreCase("System.out")) {
      setWriter(new OutputStreamWriter(System.out));
    } else if(val.equalsIgnoreCase("System.err")) {
      setWriter(new OutputStreamWriter(System.err));
    } else {
      fileName = val;
    }
  }

  /** 
      Returns the value of the <b>Append</b> option. 
   */
  public
  boolean getAppend() {
    return fileAppend;
  }

  
  /** Returns the value of the <b>File</b> option. */
  public
  String getFile() {
    return fileName;
  }
  
  /**
     Returns the option names for this component, namely the string
     array {@link #FILE_OPTION}, {@link #APPEND_OPTION}} in addition
     to the options of its super class {@link WriterAppender}.  

     @deprecated We now use JavaBeans introspection to configure
     components. Options strings are no longer needed.

  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {FILE_OPTION, APPEND_OPTION});
  }


  /**
     <The <b>Append</b> option takes a boolean value. It is set to
     <code>true</code> by default. If true, then <code>File</code>
     will be opened in append mode by {@link #setFile setFile} (see
     above). Otherwise, {@link #setFile setFile} will open
     <code>File</code> in truncate mode.

     <p>Note: Actual opening of the file is made when {@link
     #activateOptions} is called, not when the options are set.
   */
  public
  void setAppend(boolean flag) {
    fileAppend = flag;
  }
  

  /**
     If the value of {@link #FILE_OPTION} is not <code>null</code>, then {@link
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
      //LogLog.error("File option not set for appender ["+name+"].");
      LogLog.warn("File option not set for appender ["+name+"].");
      LogLog.warn("Are you using FileAppender instead of ConsoleAppender?");
    }
  }

 /**
     Closes the previously opened file.
  */
  protected
  void closeFile() {
    // FIXME (remove qwIsOurs)
    if(this.qw != null && this.qwIsOurs) {
      try {
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
    <p>Sets and <i>opens</i> the file where the log output will
    go. The specified file must be writable. 

    <p>If there was already an opened file, then the previous file
    is closed first. 

    @param fileName The path to the log file.
    @param append   If true will append to fileName. Otherwise will
        truncate fileName.  */
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
     @deprecated Use the setter method for the option directly instead
     of the generic <code>setOption</code> method.  */
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
    if(qwIsOurs) {
      super.reset();    
    } else {
      this.qw = null;
      this.tp = null;    
    }
  }  
}

