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

package org.apache.log4j.spi;

import java.io.PrintWriter;
import java.io.Writer;

import java.util.Vector;


/**
  * ThrowableInformation is log4j's internal representation of
  * throwables. It essentially consists of a string array, called
  * 'rep', where the first element, that is rep[0], represents the
  * string representation of the throwable (i.e. the value you get
  * when you do throwable.toString()) and subsequent elements
  * correspond the stack trace with the top most entry of the stack
  * corresponding to the second entry of the 'rep' array that is
  * rep[1].
  *
  * @author Ceki G&uuml;lc&uuml;
  *
  * */
public class ThrowableInformation implements java.io.Serializable {
  static final long serialVersionUID = -4748765566864322735L;
  private transient Throwable throwable;
  private String[] rep;

  public ThrowableInformation(Throwable throwable) {
    this.throwable = throwable;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public String[] getThrowableStrRep() {
    if (rep != null) {
      return (String[]) rep.clone();
    } else {
      VectorWriter vw = new VectorWriter();
      throwable.printStackTrace(vw);
      rep = vw.toStringArray();

      return rep;
    }
  }
}


/**
  * VectorWriter is a seemingly trivial implemtantion of PrintWriter.
  * The throwable instance that we are trying to represnt is asked to
  * print itself to a VectorWriter.
  *
  * By our design choice, r string representation of the throwable
  * does not contain any line separators. It follows that println()
  * methods of VectorWriter ignore the 'ln' part.
  * */
class VectorWriter extends PrintWriter {
  private Vector v;

  VectorWriter() {
    super(new NullWriter());
    v = new Vector();
  }

  public void print(Object o) {
    v.addElement(o.toString());
  }

  public void print(char[] chars) {
    v.addElement(new String(chars));
  }

  public void print(String s) {
    v.addElement(s);
  }

  public void println(Object o) {
    v.addElement(o.toString());
  }

  // JDK 1.1.x apprenly uses this form of println while in
  // printStackTrace()
  public void println(char[] chars) {
    v.addElement(new String(chars));
  }

  public void println(String s) {
    v.addElement(s);
  }

  public void write(char[] chars) {
    v.addElement(new String(chars));
  }

  public void write(char[] chars, int off, int len) {
    v.addElement(new String(chars, off, len));
  }

  public void write(String s, int off, int len) {
    v.addElement(s.substring(off, off + len));
  }

  public void write(String s) {
    v.addElement(s);
  }

  public String[] toStringArray() {
    int len = v.size();
    String[] sa = new String[len];

    for (int i = 0; i < len; i++) {
      sa[i] = (String) v.elementAt(i);
    }

    return sa;
  }
}


class NullWriter extends Writer {
  public void close() {
    // blank
  }

  public void flush() {
    // blank
  }

  public void write(char[] cbuf, int off, int len) {
    // blank
  }
}
