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

package org.apache.log4j;


/**
  * ConsoleAppender appends log events to <code>System.out</code> or
  * <code>System.err</code> using a layout specified by the user. The
  * default target is <code>System.out</code>.
  *
  * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
  * @since 1.1 */
public class ConsoleAppender extends WriterAppender {
  public static final String SYSTEM_OUT = "System.out";
  public static final String SYSTEM_ERR = "System.err";
  protected String target = SYSTEM_OUT;

  /**
   * As in most cases, the default constructor does nothing.
   */
  public ConsoleAppender() {
  }

  public ConsoleAppender(Layout layout) {
    this(layout, SYSTEM_OUT);
  }

  public ConsoleAppender(Layout layout, String targetStr) {
    this.layout = layout;
    setTarget(targetStr);
    activate();
  }

  /**
   *  Sets the value of the <b>Target</b> option. Recognized values
   *  are "System.out" and "System.err". Any other value will be
   *  ignored.
   * */
  public void setTarget(String value) {
    String v = value.trim();

    if (SYSTEM_OUT.equalsIgnoreCase(v)) {
      target = SYSTEM_OUT;
    } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
      target = SYSTEM_ERR;
    } else {
      targetWarn(value);
    }
  }

  /**
   * Returns the current value of the <b>Target</b> property. The
   * default value of the option is "System.out".
   *
   * See also {@link #setTarget}.
   * */
  public String getTarget() {
    return target;
  }

  void targetWarn(String val) {
    getLogger().warn("[{}] should be System.out or System.err.", val);
    getLogger().warn("Using previously set target, System.out by default.");
  }

  public void activate() {
    if (target.equals(SYSTEM_OUT)) {
      setWriter(createWriter(System.out));
    } else {
      setWriter(createWriter(System.err));
    }
    super.activate();
  }

  /**
   *  This method overrides the parent {@link
   *  WriterAppender#closeWriter} implementation to do nothing because
   *  the console stream is not ours to close.
   * */
  protected final void closeWriter() {
  }
}
