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

package org.apache.log4j.rolling.helpers;

import org.apache.log4j.Logger;

import java.util.Date;


/**
 *
 * This class helps parse file name patterns. Given a number or a date it returns
 * a file name according to the file name pattern.
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class FileNamePattern {
  static Logger logger = Logger.getLogger(FileNamePattern.class);
  String pattern;
  int patternLength;
  TokenConverter headTokenConverter;

  public FileNamePattern(String pattern) {
    if (pattern == null) {
      throw new IllegalArgumentException(
        "The argument to constrcutor cannot be null. ");
    }

    this.pattern = pattern;

    if (pattern != null) {
      patternLength = pattern.length();
    }

    parse();
  }

  public String toString() {
    return pattern;
  }

  void parse() {
    int lastIndex = 0;

    TokenConverter tc = null;

MAIN_LOOP: 
    while (true) {
      int i = pattern.indexOf('%', lastIndex);

      if (i == -1) {
        String remainingStr = pattern.substring(lastIndex);

        //System.out.println("adding the identity token, I");
        addTokenConverter(tc, new IdentityTokenConverter(remainingStr));

        break;
      } else {
        // test for degenerate case where the '%' character is at the end.
        if (i == (patternLength - 1)) {
          String remainingStr = pattern.substring(lastIndex);
          addTokenConverter(tc, new IdentityTokenConverter(remainingStr));

          break;
        }

        //System.out.println("adding the identity token, II");
        tc =
          addTokenConverter(
            tc, new IdentityTokenConverter(pattern.substring(lastIndex, i)));

        // At this stage, we can suppose that i < patternLen -1
        char nextChar = pattern.charAt(i + 1);

        switch (nextChar) {
        case 'i':
          tc = addTokenConverter(tc, new IntegerTokenConverter());
          lastIndex = i + 2;

          break; // break from switch statement 

        case 'd':

          int optionEnd = getOptionEnd(i + 2);

          if (optionEnd != -1) {
            String option = pattern.substring(i + 3, optionEnd);

            tc = addTokenConverter(tc, new DateTokenConverter(option));
            lastIndex = optionEnd + 1;
          } else {
            logger.warn(
              "Could not parse option related to the %d conversion pattern");

            tc =
              addTokenConverter(
                tc, new IdentityTokenConverter(pattern.substring(i)));

            break MAIN_LOOP;
          }

          break; // break from switch statement 

        case '%':
          tc = addTokenConverter(tc, new IdentityTokenConverter("%"));
          lastIndex = i + 2;

          break;

        default:
          throw new IllegalArgumentException(
            "The pattern[" + pattern
            + "] does not contain a valid specifer at position " + (i + 1));
        }
      }
    }
  }

  /**
   *  Find the position of the last character of option enclosed within the '{}'
   * characters inside the pattern
   * */
  protected int getOptionEnd(int i) {
    //logger.debug("Char at "+i+" "+pattern.charAt(i));
    if ((i < patternLength) && (pattern.charAt(i) == '{')) {
      int end = pattern.indexOf('}', i);

      if (end > i) {
        return end;
      } else {
        return -1;
      }
    }

    return -1;
  }

  TokenConverter addTokenConverter(
    TokenConverter tc, TokenConverter newTokenConverter) {
    if (tc == null) {
      tc = headTokenConverter = newTokenConverter;
    } else {
      tc.next = newTokenConverter;
      tc = newTokenConverter;
    }

    return tc;
  }

  public DateTokenConverter getDateTokenConverter() {
    TokenConverter p = headTokenConverter;

    while (p != null) {
      if (p.getType() == TokenConverter.DATE) {
        return (DateTokenConverter) p;
      }

      p = p.getNext();
    }

    return null;
  }

  public String convert(int i) {
    TokenConverter p = headTokenConverter;
    StringBuffer buf = new StringBuffer();

    while (p != null) {
      switch (p.getType()) {
      case TokenConverter.IDENTITY:
        buf.append(((IdentityTokenConverter) p).convert());

        break;

      case TokenConverter.INTEGER:
        buf.append(((IntegerTokenConverter) p).convert(i));

        break;

      default:
        logger.error(
          "Encountered an unknown TokenConverter type for pattern [" + pattern
          + "].");
      }

      p = p.getNext();
    }

    return buf.toString();
  }

  public String convert(Date date) {
    TokenConverter p = headTokenConverter;
    StringBuffer buf = new StringBuffer();

    while (p != null) {
      switch (p.getType()) {
      case TokenConverter.IDENTITY:
        buf.append(((IdentityTokenConverter) p).convert());

        break;

      case TokenConverter.DATE:
        buf.append(((DateTokenConverter) p).convert(date));

        break;

      default:
        logger.error(
          "Encountered an unknown TokenConverter type for pattern [" + pattern
          + "].");
      }

      p = p.getNext();
    }

    return buf.toString();
  }


  public String getPattern() {
    return pattern;
  }

//  public void setPattern(String string) {
//    pattern = string;
//  }

}
