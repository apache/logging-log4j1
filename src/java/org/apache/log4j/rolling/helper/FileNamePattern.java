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

package org.apache.log4j.rolling.helper;

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
      // We do not want to deal with trailing spaces in the pattern.
      this.pattern = this.pattern.trim();
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

          String option;

          if (optionEnd != -1) {
            option = pattern.substring(i + 3, optionEnd);
            lastIndex = optionEnd + 1;
          } else {
            logger.debug("Assuming daily rotation schedule");
            option = "yyyy-MM-dd";
            lastIndex = i+2;
          }
          tc = addTokenConverter(tc, new DateTokenConverter(option));
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

  public IntegerTokenConverter getIntegerTokenConverter() {
    TokenConverter p = headTokenConverter;

    while (p != null) {
      if (p.getType() == TokenConverter.INTEGER) {
        return (IntegerTokenConverter) p;
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
}
