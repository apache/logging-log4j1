/*
 * Copyright 1999,2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import org.apache.log4j.helpers.OptionConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;


// Contibutors: Jens Uwe Pipka <jens.pipka@gmx.de>
//              Ben Sandee

/**
 *  FileAppender appends log events to a file.
 *
 *  <p>Support for <code>java.io.Writer</code> and console appending
 *  has been deprecated and then removed. See the replacement
 *  solutions: {@link WriterAppender} and {@link ConsoleAppender}.
 *
 * @author Ceki G&uuml;lc&uuml;
 * */
public class FileAppender extends WriterAppender {

  /**
   * The default size of the IO buffer. 
   */
  private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
  /** 
   * Controls whether to append to or truncate an existing file. 
   * The default value for this variable is 
   * <code>true</code>, meaning that by default a <code>FileAppender</code> will
   *  append to an existing file and not truncate it. 
   * 
   * <p>This option is meaningful only if the FileAppender opens the file.
  */
  protected boolean fileAppend = true;

  /**
     The name of the log file. */
  protected String fileName = null;

  /**
     Do we do bufferedIO? */
  protected boolean bufferedIO = false;

  /**
     The size of the IO buffer. Default is 8K. */
  protected int bufferSize = DEFAULT_BUFFER_SIZE;

  /**
     The default constructor does not do anything.
  */
  public FileAppender() {
  }

  /**
    Instantiate a <code>FileAppender</code> and open the file
    designated by <code>filename</code>. The opened filename will
    become the output destination for this appender.

    <p>If the <code>append</code> parameter is true, the file will be
    appended to. Otherwise, the file designated by
    <code>filename</code> will be truncated before being opened.

    <p>If the <code>bufferedIO</code> parameter is <code>true</code>,
    then buffered IO will be used to write to the output file.

  */
  public FileAppender(
    Layout layout, String filename, boolean append, boolean bufferedIO,
    int bufferSize) throws IOException {
    this.layout = layout;
    this.setFile(filename, append, bufferedIO, bufferSize);
    activateOptions();
  }

  /**
    Instantiate a FileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the output
    destination for this appender.

    <p>If the <code>append</code> parameter is true, the file will be
    appended to. Otherwise, the file designated by
    <code>filename</code> will be truncated before being opened.
  */
  public FileAppender(Layout layout, String filename, boolean append)
    throws IOException {
    this.layout = layout;
    this.setFile(filename, append, false, bufferSize);
    activateOptions();
  }

  /**
     Instantiate a FileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the output
    destination for this appender.

    <p>The file will be appended to.  */
  public FileAppender(Layout layout, String filename) throws IOException {
    this(layout, filename, true);
    activateOptions();
  }

  /**
     The <b>File</b> property takes a string value which should be the
     name of the file to append to.

     <p><font color="#DD0044"><b>Note that the special values
     "System.out" or "System.err" are no longer honored.</b></font>

     <p>Note: Actual opening of the file is made when {@link
     #activateOptions} is called, not when the options are set.  */
  public void setFile(String file) {
    // Trim spaces from both ends. The users probably does not want
    // trailing spaces in file names.
    String val = file.trim();
    fileName = OptionConverter.stripDuplicateBackslashes(val);
  }

  /**
      Returns the value of the <b>Append</b> option.
   */
  public boolean getAppend() {
    return fileAppend;
  }

  /** Returns the value of the <b>File</b> option. */
  public String getFile() {
    return fileName;
  }

  /**
     If the value of <b>File</b> is not <code>null</code>, then {@link
     #setFile} is called with the values of <b>File</b>  and
     <b>Append</b> properties.

     @since 0.8.1 */
  public void activateOptions() {
    if (fileName != null) {
      try {
        setFile(fileName, fileAppend, bufferedIO, bufferSize);
        super.activateOptions();
      } catch (java.io.IOException e) {
        getLogger().error(
          "setFile(" + fileName + "," + fileAppend + ") call failed.", e);
      }
    } else {
      getLogger().error("File option not set for appender [{}].", name);
      getLogger().warn("Are you using FileAppender instead of ConsoleAppender?");
    }
  }

  /**
   * Closes the previously opened file.
   * 
   * @deprecated Use the super class' {@link #closeWriter} method instead.
   */
  protected void closeFile() {
    closeWriter();
  }

  /**
     Get the value of the <b>BufferedIO</b> option.

     <p>BufferedIO will significantly increase performance on heavily
     loaded systems.

  */
  public boolean getBufferedIO() {
    return this.bufferedIO;
  }

  /**
     Get the size of the IO buffer.
  */
  public int getBufferSize() {
    return this.bufferSize;
  }

  /**
     The <b>Append</b> option takes a boolean value. It is set to
     <code>true</code> by default. If true, then <code>File</code>
     will be opened in append mode by {@link #setFile setFile} (see
     above). Otherwise, {@link #setFile setFile} will open
     <code>File</code> in truncate mode.

     <p>Note: Actual opening of the file is made when {@link
     #activateOptions} is called, not when the options are set.
   */
  public void setAppend(boolean flag) {
    fileAppend = flag;
  }

  /**
     The <b>BufferedIO</b> option takes a boolean value. It is set to
     <code>false</code> by default. If true, then <code>File</code>
     will be opened and the resulting {@link java.io.Writer} wrapped
     around a {@link BufferedWriter}.

     BufferedIO will significantly increase performance on heavily
     loaded systems.

  */
  public void setBufferedIO(boolean bufferedIO) {
    this.bufferedIO = bufferedIO;

    if (bufferedIO) {
      immediateFlush = false;
    }
  }

  /**
     Set the size of the IO buffer.
  */
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  /**
    <p>Sets and <i>opens</i> the file where the log output will
    go. The specified file must be writable.

    <p>If there was already an opened file, then the previous file
    is closed first.

    <p><b>Do not use this method directly. To configure a FileAppender
    or one of its subclasses, set its properties one by one and then
    call activateOptions.</b>

    @param filename The path to the log file.
    @param append   If true will append to fileName. Otherwise will
        truncate fileName.
    @param bufferedIO
    @param bufferSize
    
    @throws IOException
        
   */
  public synchronized void setFile(
    String filename, boolean append, boolean bufferedIO, int bufferSize)
    throws IOException {
    getLogger().debug("setFile called: {}, {}", fileName, append?"true":"false");

    // It does not make sense to have immediate flush and bufferedIO.
    if (bufferedIO) {
      setImmediateFlush(false);
    }

    closeWriter();

    FileOutputStream ostream = null;
    try {
        //
        //   attempt to create file
        //
        ostream = new FileOutputStream(filename, append);
    } catch(FileNotFoundException ex) {
        //
        //   if parent directory does not exist then
        //      attempt to create it and try to create file
        //      see bug 9150
        //
        File parentDir = new File(new File(filename).getParent());
        if(!parentDir.exists() && parentDir.mkdirs()) {
            ostream = new FileOutputStream(filename, append);
        } else {
            throw ex;
        }
    }
    Writer fw = createWriter(ostream);

    if (bufferedIO) {
      fw = new BufferedWriter(fw, bufferSize);
    }
    setQWForFiles(fw);

    this.fileAppend = append;
    this.bufferedIO = bufferedIO;
    this.fileName = filename;
    this.bufferSize = bufferSize;
    writeHeader();
    getLogger().debug("setFile ended");
  }

    /**
       Sets the quiet writer being used.

       This method is overriden by {@link RollingFileAppender}.
     */
    protected
    void setQWForFiles(final Writer writer) {
       this.qw = createQuietWriter(writer);
    }


    /**
       Close any previously opened file and call the parent's
       <code>reset</code>.
     @deprecated
     */
    protected
    void reset() {
      closeFile();
      this.fileName = null;
      super.reset();
    }


}
