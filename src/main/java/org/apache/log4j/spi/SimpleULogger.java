/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.spi;

import org.apache.log4j.ULogger;
import org.apache.log4j.helpers.MessageFormatter;


/**
 * A simple implementation that logs messages of level INFO or higher on
 * the console (<code>System.out</code>).
 * <p>
 * The output includes the relative time in milliseconds, thread name, level,
 * logger name, and the message followed by the line separator for the host.
 * In log4j terms it amounts to the "%r  [%t] %level %logger - %m%n" pattern.
 * <pre>
176 [main] INFO examples.Sort - Populating an array of 2 elements in reverse.
225 [main] INFO examples.SortAlgo - Entered the sort method.
304 [main] INFO SortAlgo.DUMP - Dump of interger array:
317 [main] INFO SortAlgo.DUMP - Element [0] = 0
331 [main] INFO SortAlgo.DUMP - Element [1] = 1
343 [main] INFO examples.Sort - The next log statement should be an error msg.
346 [main] ERROR SortAlgo.DUMP - Tried to dump an uninitialized array.
        at org.log4j.examples.SortAlgo.dump(SortAlgo.java:58)
        at org.log4j.examples.Sort.main(Sort.java:64)
467 [main] INFO  examples.Sort - Exiting main method.
</pre>
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class SimpleULogger implements ULogger {

    /**
     * Logger name.
     */
  private final String loggerName;


  /**
   * Mark the time when this class gets loaded into memory.
   */
  private static long startTime = System.currentTimeMillis();

    /**
     * Line separator.
     */
  public static final String LINE_SEPARATOR
            = System.getProperty("line.separator");

    /**
     * INFO string literal.
     */
  private static final String INFO_STR = "INFO";
    /**
     * WARN string literal.
     */
  private static final String WARN_STR = "WARN";
    /**
     * ERROR string literal.
     */
  private static final String ERROR_STR = "ERROR";

  /**
   * Constructor is private to force construction through getLogger.
   * @param name logger name
   */
  private SimpleULogger(final String name) {
    super();
    this.loggerName = name;
  }

  /**
   * Creates a new instance.
   *
   * @param name logger name
   * @return  logger.
   */
  public static SimpleULogger getLogger(final String name) {
      return new SimpleULogger(name);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isDebugEnabled() {
    return false;
  }

    /**
     * {@inheritDoc}
     */
  public void debug(final Object msg) {
    // NOP
  }

    /**
     * {@inheritDoc}
     */
  public void debug(final Object parameterizedMsg, final Object param1) {
    // NOP
  }

    /**
     * {@inheritDoc}
     */
  public void debug(final String parameterizedMsg,
                    final Object param1,
                    final Object param2) {
    // NOP
  }

    /**
     * {@inheritDoc}
     */
  public void debug(final Object msg, final Throwable t) {
    // NOP
  }

  /**
   * This is our internal implementation for logging regular (non-parameterized)
   * log messages.
   *
   * @param level level
   * @param message message
   * @param t throwable
   */
  private void log(final String level,
                   final String message,
                   final Throwable t) {
    StringBuffer buf = new StringBuffer();

    long millis  = System.currentTimeMillis();
    buf.append(millis - startTime);

    buf.append(" [");
    buf.append(Thread.currentThread().getName());
    buf.append("] ");

    buf.append(level);
    buf.append(" ");

    buf.append(loggerName);
    buf.append(" - ");

    buf.append(message);

    buf.append(LINE_SEPARATOR);

    System.out.print(buf.toString());
    if (t != null) {
      t.printStackTrace(System.out);
    }
    System.out.flush();
  }
  /**
   * For parameterized messages, first substitute parameters and then log.
   *
   * @param level level
   * @param parameterizedMsg message pattern
   * @param param1 param1
   * @param param2 param2
   */
  private void parameterizedLog(final String level,
                                final Object parameterizedMsg,
                                final Object param1,
                                final Object param2) {
    if (parameterizedMsg instanceof String) {
      String msgStr = (String) parameterizedMsg;
      msgStr = MessageFormatter.format(msgStr, param1, param2);
      log(level, msgStr, null);
    } else {
      // To be failsafe, we handle the case where 'messagePattern' is not
      // a String. Unless the user makes a mistake, this should not happen.
      log(level, parameterizedMsg.toString(), null);
    }
  }

    /**
     * {@inheritDoc}
     */
  public boolean isInfoEnabled() {
    return true;
  }

    /**
     * {@inheritDoc}
     */
  public void info(final Object msg) {
    log(INFO_STR, msg.toString(), null);
  }


    /**
     * {@inheritDoc}
     */
  public void info(final Object parameterizedMsg, final Object param1) {
    parameterizedLog(INFO_STR, parameterizedMsg, param1, null);
  }

    /**
     * {@inheritDoc}
     */
  public void info(final String parameterizedMsg,
                   final Object param1,
                   final Object param2) {
    parameterizedLog(INFO_STR, parameterizedMsg, param1, param2);
  }

    /**
     * {@inheritDoc}
     */
  public void info(final Object msg, final Throwable t) {
    log(INFO_STR, msg.toString(), t);
  }

    /**
     * {@inheritDoc}
     */
  public boolean isWarnEnabled() {
    return true;
  }

    /**
     * {@inheritDoc}
     */
  public void warn(final Object msg) {
    log(WARN_STR, msg.toString(), null);
  }

    /**
     * {@inheritDoc}
     */
  public void warn(final Object parameterizedMsg, final Object param1) {
    parameterizedLog(WARN_STR, parameterizedMsg, param1, null);
  }

    /**
     * {@inheritDoc}
     */
  public void warn(final String parameterizedMsg,
                   final Object param1,
                   final Object param2) {
    parameterizedLog(WARN_STR, parameterizedMsg, param1, param2);
  }

    /**
     * {@inheritDoc}
     */
  public void warn(final Object msg, final Throwable t) {
    log(WARN_STR, msg.toString(), t);
  }

    /**
     * {@inheritDoc}
     */
  public boolean isErrorEnabled() {
    return true;
  }

    /**
     * {@inheritDoc}
     */
  public void error(final Object msg) {
    log(ERROR_STR, msg.toString(), null);
  }


    /**
     * {@inheritDoc}
     */
  public void error(final Object parameterizedMsg, final Object param1) {
    parameterizedLog(ERROR_STR, parameterizedMsg, param1, null);
  }

    /**
     * {@inheritDoc}
     */
  public void error(final String parameterizedMsg,
                    final Object param1,
                    final Object param2) {
    parameterizedLog(ERROR_STR, parameterizedMsg, param1, param2);
  }

    /**
     * {@inheritDoc}
     */
  public void error(final Object msg, final Throwable t) {
    log(ERROR_STR, msg.toString(), t);
  }

}
