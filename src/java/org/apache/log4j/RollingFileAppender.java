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
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.spi.LoggingEvent;

/**
   RollingFileAppender extends FileAppender to backup the log files when 
   they reach a certain size. 

   @author <a HREF="mailto:heinz.richter@ecmwf.int">Heinz Richter</a>
   @author Ceki G&uuml;lc&uuml;
   
*/
public class RollingFileAppender extends FileAppender {

  /**
     A string constant used in naming the option for setting the
     maximum size of the log file. Current value of this string constant is
     <b>MaxFileSize</b>.
   */
  static final public String MAX_FILE_SIZE_OPTION = "MaxFileSize";
  
   /**
     A string constant used in naming the option for setting the the
     number of backup files to retain. Current value of this string
     constant is <b>MaxBackupIndex</b>.  */
  static final public String MAX_BACKUP_INDEX_OPTION = "MaxBackupIndex";  

  /**
     The default maximum file size is 10MB. 
  */
  protected long maxFileSize = 10*1024*1024; 

  /**
     There is one backup file by default.
   */
  protected int  maxBackupIndex  = 1;    

  /**
     The default constructor simply calls its {@link
     FileAppender#FileAppender parents constructor}.  */
  public
  RollingFileAppender() {
    super();
  }

 /**
    Instantiate a RollingFileAppender and set the output destination to a
     new {@link OutputStreamWriter} initialized with <code>os</code>
     as its {@link OutputStream}.  
     
     @deprecated This constructor does not allow to roll files and
     will disappear in the near future.  */
  public
  RollingFileAppender(Layout layout, OutputStream os) {
    super(layout, os);
  }
  
  /**
     Instantiate a RollingFileAppender and set the output destination
     to <code>writer</code>.

     <p>The <code>writer</code> must have been opened by the user.  

     @deprecated This constructor does not allow to roll files and will
     disappear in the near future.  */
  public
  RollingFileAppender(Layout layout, Writer writer) {
    super(layout, writer);
  }                    


  /**
    Instantiate a RollingFileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the ouput
    destination for this appender.

    <p>If the <code>append</code> parameter is true, the file will be
    appended to. Otherwise, the file desginated by
    <code>filename</code> will be truncated before being opened.
  */
  public
  RollingFileAppender(Layout layout, String filename, boolean append)
                                      throws IOException {
    super(layout, filename, append);
  }

  /**
     Instantiate a FileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the output
    destination for this appender.

    <p>The file will be appended to.  */
  public
  RollingFileAppender(Layout layout, String filename) throws IOException {
    super(layout, filename);
  }
  
  /**
     Retuns the option names for this component, namely {@link
     #MAX_FILE_SIZE_OPTION} and {@link #MAX_BACKUP_INDEX_OPTION} in
     addition to the options of {@link FileAppender#getOptionStrings
     FileAppender}.  */
  public
  String[] getOptionStrings() {

    return OptionConverter.concatanateArrays(super.getOptionStrings(),
		 new String[] {MAX_FILE_SIZE_OPTION, MAX_BACKUP_INDEX_OPTION});
  }

  public
  synchronized
  void setFile(String fileName, boolean append) throws IOException {
    super.setFile(fileName, append);
    if(append) {
      File f = new File(fileName);
      ((CountingQuietWriter) qw).setCount(f.length());
    }
  }

  /**
     Implemetns the usual roll over behaviour.

     <p>If <code>MaxBackupIndex</code> is positive, then files
     {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
     are renamed to {<code>File.2</code>, ..., 
     <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
     renamed <code>File.1</code> and closed. A new <code>File</code> is
     created to receive further log output.

     <p>If <code>MaxBackupIndex</code> is equal to zero, then the
     <code>File</code> is truncated with no backup files created.
     
   */
  public
  synchronized
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
	  file.renameTo(target);
	}
      }

      // Rename fileName to fileName.1
      target = new File(fileName + "." + 1);
      this.closeWriterIfOurs(); // keep windows happy. 
      file = new File(fileName);
      file.renameTo(target);
    }
    
    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false);
    }
    catch(IOException e) {
      System.err.println("setFile("+fileName+", false) call failed.");
      e.printStackTrace();
    }
  }

  /**
     Set the maximum number of backup files to keep around.
     
   */
  public
  void setMaxBackupIndex(int maxBackups) {
    this.maxBackupIndex = maxBackups;    
  } 

  /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over.     
   */
  public
  void setMaxFileSize(long maxFileSize) {
    this.maxFileSize = maxFileSize;    
  }

   /**
     Set RollingFileAppender specific options.

     In addition to {@link FileAppender#setOption FileAppender
     options} RollingFileAppender recognizes the options
     <b>MaxFileSize</b> and <b>MaxBackupIndex</b>.

     
     <p>The <b>MaxFileSize</b> determines the size of log file
     before it is rolled over to backup files. This option takes an
     long integer in the range 0 - 2^63. You can specify the value
     with the suffixes "KB", "MB" or "GB" so that the integer is
     interpreted being expressed respectively in kilobytes, megabytes
     or gigabytes. For example, the value "10KB" will be interpreted
     as 10240.
     
     <p>The <b>MaxBackupIndex</b> option determines how many backup
     files are kept before the oldest being erased. This option takes
     a positive integer value. If set to zero, then there will be no
     backup files and the log file will be truncated when it reaches
     <code>MaxFileSize</code>.

   */
  public
  void setOption(String key, String value) {
    super.setOption(key, value);    
    if(key.equalsIgnoreCase(MAX_FILE_SIZE_OPTION)) {
      maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
    }
    else if(key.equalsIgnoreCase(MAX_BACKUP_INDEX_OPTION)) {
      maxBackupIndex = OptionConverter.toInt(value, maxBackupIndex);
    }
  }
  
  protected
  void setQWForFiles(Writer writer) {
     this.qw = new CountingQuietWriter(writer, errorHandler);
  }


  /**
     This method differentiates RollingFileAppender from its super
     class.  

     @since 0.9.0
  */
  protected
  void subAppend(LoggingEvent event) {
    super.subAppend(event);
    if((fileName != null) &&
                     ((CountingQuietWriter) qw).getCount() >= maxFileSize) 
      this.rollOver();
   } 

} 
