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

package org.apache.log4j.joran.spi;


/**
 * By throwing an exception an action can signal the Interpreter to skip
 * processing, all the nested elements nested within the element throwing the
 * exception or skip all following sibling elements in the document.
 *
 * @author <a href="http://www.qos.ch/log4j/">Ceki Gulcu</a>
 */
public class ActionException extends Exception {
  /**
   * SKIP_CHILDREN signals the {@link Interpreter} to skip processing all the
   * nested elements contained within the element causing this ActionException.
   */
  public static final int SKIP_CHILDREN = 1;

  /**
   * SKIP_SIBLINGS signals the {@link Interpreter} to skip processing all the
   * children of this element as well as all the siblings of this elements,
   * including any children they may have.
   */
  public static final int SKIP_SIBLINGS = 2;
  final Throwable rootCause;
  final int skipCode;

  public ActionException(final int skipCode) {
    this(skipCode, null);
  }

  public ActionException(final int skipCode, final Throwable rootCause) {
    this.skipCode = skipCode;
    this.rootCause = rootCause;
  }

  public Throwable getCause() {
    return rootCause;
  }

  public int getSkipCode() {
    return skipCode;
  }
}
