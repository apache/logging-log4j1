/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j;

import org.apache.log4j.helpers.LogLog;

import java.io.OutputStreamWriter;


/**
  * ConsoleAppender appends log events to <code>System.out</code> or
  * <code>System.err</code> using a layout specified by the user. The
  * default target is <code>System.out</code>.
  *
  * @author Ceki G&uuml;lc&uuml;
  * @since 1.1 */
public class ConsoleAppender extends WriterAppender {
  public static final String SYSTEM_OUT = "System.out";
  public static final String SYSTEM_ERR = "System.err";
  protected String target = SYSTEM_OUT;

  /**
     The default constructor does nothing.
   */
  public ConsoleAppender() {
  }

  public ConsoleAppender(Layout layout) {
    this(layout, SYSTEM_OUT);
  }

  public ConsoleAppender(Layout layout, String target) {
    this.layout = layout;

    if (SYSTEM_OUT.equals(target)) {
      setWriter(new OutputStreamWriter(System.out));
    } else if (SYSTEM_ERR.equalsIgnoreCase(target)) {
      setWriter(new OutputStreamWriter(System.err));
    } else {
      targetWarn(target);
    }
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
    LogLog.warn("[" + val + "] should be System.out or System.err.");
    LogLog.warn("Using previously set target, System.out by default.");
  }

  public void activateOptions() {
    if (target.equals(SYSTEM_OUT)) {
      setWriter(new OutputStreamWriter(System.out));
    } else {
      setWriter(new OutputStreamWriter(System.err));
    }
  }

  /**
   *  This method overrides the parent {@link
   *  WriterAppender#closeWriter} implementation to do nothing because
   *  the console stream is not ours to close.
   * */
  protected final void closeWriter() {
  }
}
