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

import org.apache.log4j.rolling.TimeBasedRollingPolicy;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

import java.io.IOException;


/**
  * org.apache.log4j.DailyRollingFileAppender emulates earlier implementations
  * by delegating to general purpose org.apache.log4j.rollling.RollingFileAppender
  * introduced in log4j 1.3.  This class is provided for compatibility with
  * existing configuration files but is not intended to be compatible with
  * existing extensions of the prior RollingFileAppender and is marked final
  * to prevent such use.
  *
  *  @author Curt Arnold
  * @deprecated Replaced by {@link org.apache.log4j.rolling.RollingFileAppender}
*/
public final class DailyRollingFileAppender implements Appender, OptionHandler {

  /**
   * It is assumed and enforced that errorHandler is never null.
   * 
   * @deprecated as of 1.3
   */
  private final org.apache.log4j.spi.ErrorHandler errorHandler = new org.apache.log4j.helpers.OnlyOnceErrorHandler();

  /**
     The date pattern used to initiate rollover.
  */
  private String datePattern = "'.'yyyy-MM-dd";

  /**
   *  Nested new rolling file appender.
   */
  private final org.apache.log4j.rolling.RollingFileAppender rfa =
    new org.apache.log4j.rolling.RollingFileAppender();

  /**
     The default constructor simply calls its {@link
     FileAppender#FileAppender parents constructor}.  */
  public DailyRollingFileAppender() {
  }

  /**
    Instantiate a DailyRollingFileAppender and open the file designated by
    <code>filename</code>. The opened filename will become the ouput
    destination for this appender.

  */
  public DailyRollingFileAppender(
    final Layout layout, final String filename, final String datePattern)
    throws IOException {
    rfa.setLayout(layout);
    rfa.setFile(filename);
    this.datePattern = datePattern;
    activateOptions();
  }

  /**
     The <b>DatePattern</b> takes a string in the same format as
     expected by {@link java.text.SimpleDateFormat}. This options determines the
     rollover schedule.
   */
  public void setDatePattern(String pattern) {
    datePattern = pattern;
  }

  /** Returns the value of the <b>DatePattern</b> option. */
  public String getDatePattern() {
    return datePattern;
  }

  /**
   * Prepares DailyRollingFileAppender for use.
   */
  public void activateOptions() {
    TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
    StringBuffer pattern = new StringBuffer(rfa.getFile());
    boolean inLiteral = false;
    boolean inPattern = false;

    for (int i = 0; i < datePattern.length(); i++) {
      if (datePattern.charAt(i) == '\'') {
        inLiteral = !inLiteral;

        if (inLiteral && inPattern) {
          pattern.append("}");
          inPattern = false;
        }
      } else {
        if (!inLiteral && !inPattern) {
          pattern.append("%d{");
          inPattern = true;
        }

        pattern.append(datePattern.charAt(i));
      }
    }

    if (inPattern) {
      pattern.append("}");
    }

    policy.setFileNamePattern(pattern.toString());
    policy.activateOptions();
    rfa.setTriggeringPolicy(policy);
    rfa.setRollingPolicy(policy);

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
   * Return the hardcoded <code>OnlyOnceErrorHandler</code> for this Appender.
   * <code>ErrorHandler</code>'s are no longer utilized as of version 1.3.
   *
   * @since 0.9.0
   * @deprecated As of 1.3
   */
  public final org.apache.log4j.spi.ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }

  /**
   * Ignored as of 1.3
   *
   * @since 0.9.0
   * @deprecated As of 1.3
   */
  public final void setErrorHandler(org.apache.log4j.spi.ErrorHandler eh) {
    ; //ignore
  }

}
