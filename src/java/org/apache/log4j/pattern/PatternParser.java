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

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import java.util.Hashtable;


// Contributors:   Nelson Minar <(nelson@monkey.org>
//                 Igor E. Poteryaev <jah@mail.ru>
//                 Reinhard Deschler <reinhard.deschler@web.de>

/**
   Most of the work of the {@link org.apache.log4j.PatternLayout} class
   is delegated to the PatternParser class.

   <p>It is this class that parses conversion patterns and creates
   a chained list of {@link OptionConverter OptionConverters}.

   @author <a href=mailto:"cakalijp@Maritz.com">James P. Cakalic</a>
   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen

   @since 0.8.2
*/
public class PatternParser {
  private static final char ESCAPE_CHAR = '%';
  private static final int LITERAL_STATE = 0;
  private static final int CONVERTER_STATE = 1;
  private static final int MINUS_STATE = 2;
  private static final int DOT_STATE = 3;
  private static final int MIN_STATE = 4;
  private static final int MAX_STATE = 5;
  static final int FULL_LOCATION_CONVERTER = 1000;
  static final int METHOD_LOCATION_CONVERTER = 1001;
  static final int CLASS_LOCATION_CONVERTER = 1002;
  static final int LINE_LOCATION_CONVERTER = 1003;
  static final int FILE_LOCATION_CONVERTER = 1004;
  static final int RELATIVE_TIME_CONVERTER = 2000;
  static final int THREAD_CONVERTER = 2001;
  static final int LEVEL_CONVERTER = 2002;
  static final int NDC_CONVERTER = 2003;
  static final int MESSAGE_CONVERTER = 2004;
  static Hashtable globalRulesRegistry;

  static {
    globalRulesRegistry = new Hashtable(17);
    globalRulesRegistry.put("c", LoggerPatternConverter.class.getName());
    globalRulesRegistry.put("C", ClassNamePatternConverter.class.getName());
    globalRulesRegistry.put("F", FileLocationPatternConverter.class.getName());
    globalRulesRegistry.put("l", FullLocationPatternConverter.class.getName()); 
    globalRulesRegistry.put("L", LineLocationPatternConverter.class.getName());
    globalRulesRegistry.put("m", MessagePatternConverter.class.getName());
    globalRulesRegistry.put(
      "M", MethodLocationPatternConverter.class.getName());
    globalRulesRegistry.put("p", LevelPatternConverter.class.getName());
	  globalRulesRegistry.put("level", FullLocationPatternConverter.class.getName());
	       
    globalRulesRegistry.put("r", RelativeTimePatternConverter.class.getName());
    globalRulesRegistry.put("t", ThreadPatternConverter.class.getName());
    globalRulesRegistry.put("x", NDCPatternConverter.class.getName());
    globalRulesRegistry.put("X", MDCPatternConverter.class.getName());
  }

  int state;
  protected StringBuffer currentLiteral = new StringBuffer(32);
  protected int patternLength;
  protected int i;
  PatternConverter head;
  PatternConverter tail;
  protected FormattingInfo formattingInfo = new FormattingInfo();
  protected String pattern;
  Hashtable converterRegistry;

  static Logger logger  = Logger.getLogger("LOG4J."+PatternParser.class.getName());
  
  public PatternParser(String pattern) {
    this.pattern = pattern;
    patternLength = pattern.length();
    state = LITERAL_STATE;
  }

  private void addToList(PatternConverter pc) {
    if (head == null) {
      head = tail = pc;
    } else {
      tail.next = pc;
      tail = pc;
    }
  }

  /** Extract the converter identifier found at position i.
   *
   * After this function returns, the variable i will point to the
   * first char after the end of the converter identifier.
   *
   * If i points to a char which is not a character acceptable at the
   * start of a unicode identifier, the value null is returned.
   *
   */
  protected String extractConverter(char lastChar) {
  	
  	//System.out.println("lastchar is "+lastChar);
  	
    StringBuffer convBuf = new StringBuffer(16);
	  convBuf.append(lastChar);
	
    if (i >= patternLength) {
      // the pattern converter was the last character...
      return convBuf.toString();
    }

    char c = pattern.charAt(i);

    if (Character.isUnicodeIdentifierStart(c)) {
      convBuf.append(c);
    } else {
      return convBuf.toString();
    }

    while (
      (++i < patternLength)
        && Character.isUnicodeIdentifierPart(pattern.charAt(i))) {
      convBuf.append(pattern.charAt(i));
    }

    return convBuf.toString();
  }

  /**
   * Returns the option, null if not in the expected format.
   */
  protected String extractOption() {
    if ((i < patternLength) && (pattern.charAt(i) == '{')) {
      int end = pattern.indexOf('}', i);

      if (end > i) {
        String r = pattern.substring(i + 1, end);
        i = end + 1;

        return r;
      }
    }

    return null;
  }

  public PatternConverter parse() {
    char c;
    i = 0;

    while (i < patternLength) {
      c = pattern.charAt(i++);

      switch (state) {
      case LITERAL_STATE:

        // In literal state, the last char is always a literal.
        if (i == patternLength) {
          currentLiteral.append(c);

          continue;
        }

        if (c == ESCAPE_CHAR) {
          // peek at the next char.
          switch (pattern.charAt(i)) {
          case ESCAPE_CHAR:
            currentLiteral.append(c);
            i++; // move pointer

            break;

          case 'n':
            currentLiteral.append(Layout.LINE_SEP);
            i++; // move pointer

            break;

          default:

            if (currentLiteral.length() != 0) {
              addToList(
                new LiteralPatternConverter(currentLiteral.toString()));

              //LogLog.debug("Parsed LITERAL converter: \""
              //           +currentLiteral+"\".");
            }

            currentLiteral.setLength(0);
            currentLiteral.append(c); // append %
            state = CONVERTER_STATE;
            formattingInfo.reset();
          }
        } else {
          currentLiteral.append(c);
        }

        break;

      case CONVERTER_STATE:
        currentLiteral.append(c);

        switch (c) {
        case '-':
          formattingInfo.leftAlign = true;

          break;

        case '.':
          state = DOT_STATE;

          break;

        default:

          if ((c >= '0') && (c <= '9')) {
            formattingInfo.min = c - '0';
            state = MIN_STATE;
          } else {
            finalizeConverter(c);
          }
        } // switch

        break;

      case MIN_STATE:
        currentLiteral.append(c);

        if ((c >= '0') && (c <= '9')) {
          formattingInfo.min = (formattingInfo.min * 10) + (c - '0');
        } else if (c == '.') {
          state = DOT_STATE;
        } else {
          finalizeConverter(c);
        }

        break;

      case DOT_STATE:
        currentLiteral.append(c);

        if ((c >= '0') && (c <= '9')) {
          formattingInfo.max = c - '0';
          state = MAX_STATE;
        } else {
          LogLog.error(
            "Error occured in position " + i
            + ".\n Was expecting digit, instead got char \"" + c + "\".");
          state = LITERAL_STATE;
        }

        break;

      case MAX_STATE:
        currentLiteral.append(c);

        if ((c >= '0') && (c <= '9')) {
          formattingInfo.max = (formattingInfo.max * 10) + (c - '0');
        } else {
          finalizeConverter(c);
          state = LITERAL_STATE;
        }

        break;
      } // switch
    }

    // while
    if (currentLiteral.length() != 0) {
      addToList(new LiteralPatternConverter(currentLiteral.toString()));

      //LogLog.debug("Parsed LITERAL converter: \""+currentLiteral+"\".");
    }

    return head;
  }

  String findConverterClass(String converterId) {
  	if(converterId == null) {
  		logger.warn("converterId is null");
  	}
  	
    if (converterRegistry != null) {
      String r = (String) converterRegistry.get(converterId);

      if (r != null) {
        return r;
      }
    }
 
	  String r = (String) globalRulesRegistry.get(converterId);
	  if (r != null) {
		  return r;
		}

    return null;
  }

  /**
   * When finalizeConverter is called 'c' is the current conversion caracter
   * and i points to the character following 'c'.
   */
  protected void finalizeConverter(char c) {
    PatternConverter pc = null;

    //System.out.println("============================");
    String converterId = extractConverter(c);

    //System.out.println("==============[" + converterId + "]");
    //System.out.println("c is [" + c + "]");
    String className = (String) findConverterClass(converterId);

    String option = extractOption();

    //System.out.println("Option is [" + option + "]");
    if (className != null) {
      pc =
        (PatternConverter) OptionConverter.instantiateByClassName(
          className, PatternConverter.class, null);

      // formattingInfo variable is an instance variable, occasionally reset 
      // and used over and over again
      pc.setFormattingInfo(formattingInfo);
      pc.setOption(option);
      currentLiteral.setLength(0);
    } else {
      switch (c) {
      case 'd':

        String dateFormatStr = AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT;

        //DateFormat df;
        if (option != null) {
          dateFormatStr = option;
        }

        if (
          dateFormatStr.equalsIgnoreCase(
              AbsoluteTimeDateFormat.ISO8601_DATE_FORMAT)) {
          option = "yyyy-mm-dd HH:mm:ss,SSS";

          //System.out.println("optin is " + option);
        } else if (
          dateFormatStr.equalsIgnoreCase(
              AbsoluteTimeDateFormat.ABS_TIME_DATE_FORMAT)) {
          option = "HH:mm:ss,SSS";
        } else if (
          dateFormatStr.equalsIgnoreCase(
              AbsoluteTimeDateFormat.DATE_AND_TIME_DATE_FORMAT)) {
          option = "dd MMM yyyy HH:mm:ss,SSS";
        }

        pc = new DatePatternConverter(formattingInfo);
        pc.setOption(option);

        //LogLog.debug("DATE converter {"+dateFormatStr+"}.");
        //formattingInfo.dump();
        currentLiteral.setLength(0);

        break;

      default:
        LogLog.error(
          "Unexpected char [" + c + "] at position " + i
          + " in conversion patterrn.");
        pc = new LiteralPatternConverter(currentLiteral.toString());
        currentLiteral.setLength(0);
      }
    }

    addConverter(pc);
  }

  protected void addConverter(PatternConverter pc) {
    currentLiteral.setLength(0);

    // Add the pattern converter to the list.
    addToList(pc);

    // Next pattern is assumed to be a literal.
    state = LITERAL_STATE;

    // Reset formatting info
    formattingInfo.reset();
  }

  public Hashtable getConverterRegistry() {
    return converterRegistry;
  }

  public void setConverterRegistry(Hashtable hashtable) {
    converterRegistry = hashtable;
  }
}
