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

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;


/**
 * 
 * Base class for other pattern converters which can return only parts of their name.
 *  
 * @author Ceki G&uuml;lc&uuml;
 */
abstract class NamedPatternConverter extends PatternConverter {
	
	
  static Logger logger = Logger.getLogger(NamedPatternConverter.class);

  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  StringBuffer buf;
  int precision;

  public NamedPatternConverter(FormattingInfo formattingInfo) {
    super(formattingInfo);
    this.buf = new StringBuffer(32);
  }

  abstract String getFullyQualifiedName(LoggingEvent event);

  public void setOption(String option) {
    super.setOption(option);

    /**
       The option is expected to be in decimal and positive. In case of
       error, zero is returned.  */
    int r = 0;

    if (option != null) {
      try {
        precision = Integer.parseInt(option);
        //System.out.println("Precision is "+precision);
        if (precision <= 0) {
          logger.error(
            "Precision option (" + option + ") isn't a positive integer.");
          precision = 0;
        }
      } catch (NumberFormatException e) {
        logger.error(
          "Category option \"" + option + "\" not a decimal integer.", e);
      }
    }
  }

  public StringBuffer convert(LoggingEvent event) {
    buf.setLength(0);

    String n = getFullyQualifiedName(event);
    //System.out.println("qname: "+n);

    if (precision <= 0) {
      buf.append(n);
    } else {
      int len = n.length();

      // We substract 1 from 'len' when assigning to 'end' to avoid out of
      // bounds exception in return r.substring(end+1, len). This can happen if
      // precision is 1 and the category name ends with a dot.
      int end = len - 1;

      for (int i = precision; i > 0; i--) {
        end = n.lastIndexOf('.', end - 1);

        if (end == -1) {
        	// not enough dot characters. The whole string should be returned
          return buf.append(n);
        }
      }

      // The end variable should point to the left-most dot character to
      // the right of which all character should be returned.
      buf.append(n.substring(end + 1, len));
    }
    return buf;
  }
}
