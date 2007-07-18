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

import java.io.PrintStream;

/**
 * Used to store special log4j errors which cannot be logged using internal
 * logging. Such errors include those occurring during the initial phases
 * of log4j configuration or errors emanating from core components such as
 * Logger or Hierarchy.
 *
 * @author Ceki Gulcu
 */
public class ErrorItem {
    /**
     * Message.
     */
  String message;
    /**
     * Column.
     */
  int colNumber = -1;
    /**
     * Line number.
     */
  int lineNumber = -1;
    /**
     * Exception.
     */
  Throwable exception;

    /**
     * Create new instance.
     * @param message message
     * @param e exception
     */
  public ErrorItem(final String message, final Exception e) {
    super();
    this.message = message;
    exception = e;
  }

    /**
     * Creaet new instance.
     * @param message message.
     */
  public ErrorItem(final String message) {
    this(message, null);
  }

    /**
     * Get column number.
     * @return column number.
     */
  public int getColNumber() {
    return colNumber;
  }

    /**
     * Set column number.
     * @param colNumber new column number.
     */
  public void setColNumber(int colNumber) {
    this.colNumber = colNumber;
  }

    /**
     * Get exception.
     * @return exception.
     */
  public Throwable getException() {
    return exception;
  }

    /**
     * Set exception.
     * @param exception exception
     */
  public void setException(final Throwable exception) {
    this.exception = exception;
  }

    /**
     * Get line number.
     * @return line number.
     */
  public int getLineNumber() {
    return lineNumber;
  }

    /**
     * Set line number.
     * @param lineNumber line number.
     */
  public void setLineNumber(final int lineNumber) {
    this.lineNumber = lineNumber;
  }

    /**
     * Get message.
     * @return message.
     */
  public String getMessage() {
    return message;
  }

    /**
     * Set message.
     * @param message message.
     */
  public void setMessage(final String message) {
    this.message = message;
  }

    /**
     * String representation of ErrorItem.
     * @return string.
     */
  public String toString() {
    String str =
      "Reported error: \"" + message + "\"";

    if (lineNumber != -1) {
      str += " at line " + lineNumber + " column " + colNumber;
    }
    if (exception != null) {
      str += (" with exception " + exception);
    }
    return str;
  }

  /**
   * Dump the details of this ErrorItem to System.out.
   */
  public void dump() {
    dump(System.out);
  }
  
  /**
   * Dump the details of this ErrorItem on the specified {@link PrintStream}.
   * @param ps print stream.
   */
  public void dump(final PrintStream ps) {
    String str =
      "Reported error: \"" + message + "\"";

    if (lineNumber != -1) {
      str += " at line " + lineNumber + " column " + colNumber;
    }
    ps.println(str);

    if (exception != null) {
      exception.printStackTrace(ps);
    }
  }
}
