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

import org.apache.log4j.pattern.FormattingInfo;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.pattern.PatternParser;
import org.apache.log4j.spi.LoggingEvent;


/**
  Example showing how to extend PatternParser to recognize additional
  conversion characters.  The examples shows that minimum and maximum
  width and alignment settings apply for "extension" conversion
  characters just as they do for PatternLayout recognized characters.

  <p>In this case MyPatternParser recognizes %# and outputs the value
  of an internal counter which is also incremented at each call.

  See <a href=doc-files/MyPatternParser.java><b>source</b></a> code
   for more details.

  @see org.apache.log4j.examples.MyPatternLayout
  @see org.apache.log4j.helpers.PatternParser
  @see org.apache.log4j.PatternLayout

  @author Anders Kristensen
*/
public class MyPatternParser extends PatternParser {
  int counter = 0;

  public MyPatternParser(String pattern) {
    super(pattern);
  }

  public void finalizeConverter(char c) {
    if (c == '#') {
      addConverter(new UserDirPatternConverter(formattingInfo));
      currentLiteral.setLength(0);
    } else {
      super.finalizeConverter(c);
    }
  }

  private class UserDirPatternConverter extends PatternConverter {
    StringBuffer buf;

    UserDirPatternConverter(FormattingInfo formattingInfo) {
      super(formattingInfo);
      buf = new StringBuffer(3);
    }

    public StringBuffer convert(LoggingEvent event) {
      buf.setLength(0);

      return buf.append(String.valueOf(++counter));
    }
  }
}
