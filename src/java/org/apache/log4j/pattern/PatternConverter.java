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

package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.Writer;


/**

   <p>PatternConverter is an abtract class that provides the
   formatting functionality that derived classes need.

   <p>Conversion specifiers in a conversion patterns are parsed to
   individual PatternConverters. Each of which is responsible for
   converting a logging event in a converter specific manner.

   @author <a href="mailto:cakalijp@Maritz.com">James P. Cakalic</a>
   @author Ceki G&uuml;lc&uuml;
   @author Chris Nokes

   @since 0.8.2
 */
public abstract class PatternConverter {
  static String[] SPACES =
  {
    " ", "  ", "    ", "        ", //1,2,4,8 spaces
    "                ", // 16 spaces
    "                                " // 32 spaces
  };
  public PatternConverter next;
  int min = -1;
  int max = 0x7FFFFFFF;
  boolean leftAlign = false;
  String option;

  protected PatternConverter() {
  }

  protected PatternConverter(FormattingInfo fi) {
    min = fi.min;
    max = fi.max;
    leftAlign = fi.leftAlign;
  }

  /**
     Derived pattern converters must override this method in order to
     convert conversion specifiers in the correct way.
  */
  protected abstract StringBuffer convert(LoggingEvent event);

  /**
     A template method for formatting in a converter specific way.
   */
  public void format(Writer output, LoggingEvent e) throws IOException {
    StringBuffer s = convert(e);

    if (s == null) {
      if (0 < min) {
        spacePad(output, min);
      }

      return;
    }

    int len = s.length();

    if (len > max) {
      output.write(s.substring(len - max));
    } else if (len < min) {
      if (leftAlign) {
        output.write(s.toString());
        spacePad(output, min - len);
      } else {
        spacePad(output, min - len);
        output.write(s.toString());
      }
    } else {
      output.write(s.toString());
    }
  }

  /**
     Fast space padding method.
  */
  public void spacePad(Writer output, int length) throws IOException {
    while (length >= 32) {
      output.write(SPACES[5]);
      length -= 32;
    }

    for (int i = 4; i >= 0; i--) {
      if ((length & (1 << i)) != 0) {
        output.write(SPACES[i]);
      }
    }
  }

  public String getOption() {
    return option;
  }

  public void setOption(String string) {
    option = string;
  }
}
