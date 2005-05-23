/*
 * Copyright 1999,2005 The Apache Software Foundation.
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
import org.apache.log4j.rolling.FixedWindowRollingPolicy;
import org.apache.log4j.rolling.SizeBasedTriggeringPolicy;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

import java.io.IOException;


/**
 * org.apache.log4j.RollingFileAppender emulates earlier implementations
 * by delegating to general purpose org.apache.log4j.rollling.RollingFileAppender
 * introduced in log4j 1.3.  This class is provided for compatibility with
 * existing code and should not be used except when compatibility with version
 * of log4j prior to 1.3 is a concern.
 *
 * @author Curt Arnold
 * @deprecated Replaced by {@link org.apache.log4j.rolling.RollingFileAppender}
*/
public class RollingFileAppender implements Appender, OptionHandler {
  /**
     The default maximum file size is 10MB.
  */
  private long maxFileSize = 10 * 1024 * 1024;

  /**
     There is one backup file by default.
   */
  private int maxBackupIndex = 1;

  /**
   *  Nested new rolling file appender.
   */
  private final org.apache.log4j.rolling.RollingFileAppender rfa =
    new org.apache.log4j.rolling.RollingFileAppender();

  /**
     The default constructor simply calls its {@link
     FileAppender#FileAppender parents constructor}.  */
  public RollingFileAppender() {
  }

  /**
    Instantiate a RollingFileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the ouput
    destination for this appender.

    <p>If the <code>append</code> parameter is true, the file will be
    appended to. Otherwise, the file desginated by
    <code>filename</code> will be truncated before being opened.
  */
  public RollingFileAppender(
    final Layout layout, final String filename, final boolean append)
    throws IOException {
    rfa.setLayout(layout);
    rfa.setFile(filename);
    rfa.setAppend(append);
    activateOptions();
  }

  /**
     Instantiate a FileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the output
    destination for this appender.

    <p>The file will be appended to.  */
  public RollingFileAppender(final Layout layout, final String filename)
    throws IOException {
    rfa.setLayout(layout);
    rfa.setFile(filename);
    activateOptions();
  }

  /**
     Returns the value of the <b>MaxBackupIndex</b> option.
   */
  public int getMaxBackupIndex() {
    return maxBackupIndex;
  }

  /**
     Get the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     @since 1.1
  */
  public long getMaximumFileSize() {
    return maxFileSize;
  }

  /**
     Set the maximum number of backup files to keep around.

     <p>The <b>MaxBackupIndex</b> option determines how many backup
     files are kept before the oldest is erased. This option takes
     a positive integer value. If set to zero, then there will be no
     backup files and the log file will be truncated when it reaches
     <code>MaxFileSize</code>.
   */
  public void setMaxBackupIndex(int maxBackups) {
    this.maxBackupIndex = maxBackups;
  }

  /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     <p>This method is equivalent to {@link #setMaxFileSize} except
     that it is required for differentiating the setter taking a
     <code>long</code> argument from the setter taking a
     <code>String</code> argument by the JavaBeans {@link
     java.beans.Introspector Introspector}.

     @see #setMaxFileSize(String)
  */
  public void setMaximumFileSize(long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  /**
     Set the maximum size that the output file is allowed to reach
     before being rolled over to backup files.

     <p>In configuration files, the <b>MaxFileSize</b> option takes an
     long integer in the range 0 - 2^63. You can specify the value
     with the suffixes "KB", "MB" or "GB" so that the integer is
     interpreted being expressed respectively in kilobytes, megabytes
     or gigabytes. For example, the value "10KB" will be interpreted
     as 10240.
   */
  public void setMaxFileSize(String value) {
    maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
  }

  /**
   * Prepares RollingFileAppender for use.
   */
  public void activateOptions() {
    SizeBasedTriggeringPolicy trigger = new SizeBasedTriggeringPolicy();
    trigger.setMaxFileSize(maxFileSize);
    trigger.activateOptions();
    rfa.setTriggeringPolicy(trigger);

    FixedWindowRollingPolicy rolling = new FixedWindowRollingPolicy();
    rolling.setMinIndex(1);
    rolling.setMaxIndex(maxBackupIndex);
    rolling.setActiveFileName(rfa.getFile());
    rolling.setFileNamePattern(rfa.getFile() + ".%i");
    rolling.activateOptions();
    rfa.setRollingPolicy(rolling);

    rfa.activateOptions();
  }


  /**
   * Add a filter to the end of the filter list.
   *
   * @since 0.9.0
   */
  public void addFilter(final Filter newFilter) {
    rfa.addFilter(newFilter);
  }

  /**
   * Returns the head Filter. The Filters are organized in a linked list and
   * so all Filters on this Appender are available through the result.
   *
   * @return the head Filter or null, if no Filters are present
   *
   * @since 1.1
   */
  public Filter getFilter() {
    return rfa.getFilter();
  }

  /**
   * Clear the list of filters by removing all the filters in it.
   *
   * @since 0.9.0
   */
  public void clearFilters() {
    rfa.clearFilters();
  }

  /**
   * Release any resources allocated within the appender such as file handles,
   * network connections, etc.
   *
   * <p>
   * It is a programming error to append to a closed appender.
   * </p>
   *
   * @since 0.8.4
   */
  public void close() {
    rfa.close();
  }

  /**
   * Is this appender closed?
   *
   * @since 1.3
   */
  public boolean isClosed() {
    return rfa.isClosed();
  }

  /**
   * Is this appender in working order?
   *
   * @since 1.3
   */
  public boolean isActive() {
    return rfa.isActive();
  }

  /**
   * Log in <code>Appender</code> specific way. When appropriate, Loggers will
   * call the <code>doAppend</code> method of appender implementations in
   * order to log.
   */
  public void doAppend(final LoggingEvent event) {
    rfa.doAppend(event);
  }

  /**
   * Get the name of this appender. The name uniquely identifies the appender.
   */
  public String getName() {
    return rfa.getName();
  }

  /**
   * Set the {@link Layout} for this appender.
   *
   * @since 0.8.1
   */
  public void setLayout(final Layout layout) {
    rfa.setLayout(layout);
  }

  /**
   * Returns this appenders layout.
   *
   * @since 1.1
   */
  public Layout getLayout() {
    return rfa.getLayout();
  }

  /**
   * Set the name of this appender. The name is used by other components to
   * identify this appender.
   *
   * @since 0.8.1
   */
  public void setName(final String name) {
    rfa.setName(name);
  }

  public void setLoggerRepository(final LoggerRepository repository)
    throws IllegalStateException {
    rfa.setLoggerRepository(repository);
  }

  /**
     The <b>File</b> property takes a string value which should be the
     name of the file to append to.

     <p><font color="#DD0044"><b>Note that the special values
     "System.out" or "System.err" are no longer honored.</b></font>

     <p>Note: Actual opening of the file is made when {@link
     #activateOptions} is called, not when the options are set.  */
  public void setFile(final String file) {
    rfa.setFile(file);
  }

  /**
      Returns the value of the <b>Append</b> option.
   */
  public boolean getAppend() {
    return rfa.getAppend();
  }

  /** Returns the value of the <b>File</b> option. */
  public String getFile() {
    return rfa.getFile();
  }

  /**
     Get the value of the <b>BufferedIO</b> option.

     <p>BufferedIO will significatnly increase performance on heavily
     loaded systems.

  */
  public boolean getBufferedIO() {
    return rfa.getBufferedIO();
  }

  /**
     Get the size of the IO buffer.
  */
  public int getBufferSize() {
    return rfa.getBufferSize();
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
  public void setAppend(final boolean flag) {
    rfa.setAppend(flag);
  }

  /**
     The <b>BufferedIO</b> option takes a boolean value. It is set to
     <code>false</code> by default. If true, then <code>File</code>
     will be opened and the resulting {@link java.io.Writer} wrapped
     around a {@link java.io.BufferedWriter}.

     BufferedIO will significatnly increase performance on heavily
     loaded systems.

  */
  public void setBufferedIO(final boolean bufferedIO) {
    rfa.setBufferedIO(bufferedIO);
  }

  /**
     Set the size of the IO buffer.
  */
  public void setBufferSize(final int bufferSize) {
    rfa.setBufferSize(bufferSize);
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
    public // synchronization not necessary since doAppend is alreasy synched
    void rollOver() {
        rfa.rollover();
    }

}
