/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.joran;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ErrorItem {
  String message;
  int colNumber;
  int lineNumber;
  Throwable exception;

  public ErrorItem(String message, Exception e) {
    this.message = message;
    exception = e;
  }

  public ErrorItem(String message) {
    this(message, null);
  }

  public int getColNumber() {
    return colNumber;
  }

  public void setColNumber(int colNumber) {
    this.colNumber = colNumber;
  }

  public Throwable getException() {
    return exception;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String toString() {
    String str =
      "Reported error: \"" + message + "\" at line " + lineNumber + " column "
      + colNumber;

    if (exception != null) {
      str += (" with exception " + exception);
    }
    return str;
  }
}
