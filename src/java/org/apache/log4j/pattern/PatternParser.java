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

package org.apache.log4j.pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


// Contributors:   Nelson Minar <(nelson@monkey.org>
//                 Igor E. Poteryaev <jah@mail.ru>
//                 Reinhard Deschler <reinhard.deschler@web.de>

/**
 * Most of the work of the {@link org.apache.log4j.PatternLayout} class 
 * is delegated to the PatternParser class. 
 * <p>It is this class that parses conversion patterns and creates 
 * a chained list of {@link OptionConverter OptionConverters}.
 * 
 * @author James P. Cakalic
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 * @author Paul Smith
 *
 * @since 0.8.2
*/
public class PatternParser {
  private static final char ESCAPE_CHAR = '%';
  private static final int LITERAL_STATE = 0;
  private static final int CONVERTER_STATE = 1;
  private static final int MINUS_STATE = 2;
  private static final int DOT_STATE = 3;
  private static final int MIN_STATE = 4;
  private static final int MAX_STATE = 5;

  static Map globalRulesRegistry;

  static {
    // We set the global rules in the static initializer of PatternParser class
    globalRulesRegistry = new HashMap(17);
    globalRulesRegistry.put("c", LoggerPatternConverter.class.getName());
    globalRulesRegistry.put("logger", LoggerPatternConverter.class.getName());
    
    globalRulesRegistry.put("C", ClassNamePatternConverter.class.getName());
    globalRulesRegistry.put("class", ClassNamePatternConverter.class.getName());
 
    globalRulesRegistry.put("d", DatePatternConverter.class.getName());
    globalRulesRegistry.put("date", DatePatternConverter.class.getName());
    
    globalRulesRegistry.put("F", FileLocationPatternConverter.class.getName());
    globalRulesRegistry.put("file", FileLocationPatternConverter.class.getName());
    
    globalRulesRegistry.put("l", FullLocationPatternConverter.class.getName()); 

    globalRulesRegistry.put("L", LineLocationPatternConverter.class.getName());
    globalRulesRegistry.put("line", LineLocationPatternConverter.class.getName());

    globalRulesRegistry.put("m", MessagePatternConverter.class.getName());
    globalRulesRegistry.put("message", MessagePatternConverter.class.getName());

    globalRulesRegistry.put("n", LineSeparatorPatternConverter.class.getName());

    globalRulesRegistry.put(
      "M", MethodLocationPatternConverter.class.getName());
    globalRulesRegistry.put(
      "method", MethodLocationPatternConverter.class.getName());

    globalRulesRegistry.put("p", LevelPatternConverter.class.getName());
	  globalRulesRegistry.put("level", LevelPatternConverter.class.getName());
	       
    globalRulesRegistry.put("r", RelativeTimePatternConverter.class.getName());
    globalRulesRegistry.put("relative", RelativeTimePatternConverter.class.getName());
    
    globalRulesRegistry.put("t", ThreadPatternConverter.class.getName());
    globalRulesRegistry.put("thread", ThreadPatternConverter.class.getName());
    
    globalRulesRegistry.put("x", NDCPatternConverter.class.getName());
    globalRulesRegistry.put("ndc", NDCPatternConverter.class.getName());
    
    globalRulesRegistry.put("X", PropertiesPatternConverter.class.getName());
    globalRulesRegistry.put("properties", PropertiesPatternConverter.class.getName());

    globalRulesRegistry.put("sn", SequenceNumberPatternConverter.class.getName());
    globalRulesRegistry.put("sequenceNumber", SequenceNumberPatternConverter.class.getName());
    
    globalRulesRegistry.put("throwable", ThrowableInformationPatternConverter.class.getName());
    
  }

  int state;
  protected StringBuffer currentLiteral = new StringBuffer(32);
  protected int patternLength;
  protected int i;
  PatternConverter head;
  PatternConverter tail;
  protected FormattingInfo formattingInfo = new FormattingInfo();
  protected String pattern;
  
  /**
   * Additional rules for this particular instance.
   * key: the conversion word (as String)
   * value: the pattern converter class (as String) 
   */
  Map converterRegistry;

  private Logger logger  = Logger.getLogger(PatternParser.class);
  
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
  	
    // When this method is called, lastChar points to the first character of the
    // conersion word. For example:
    // For "%hello"     lastChar = 'h'
    // For "%-5hello"   lastChar = 'h'
      
  	//System.out.println("lastchar is "+lastChar);

    if(!Character.isUnicodeIdentifierStart(lastChar)) {
      return null;
    }  	
    
    StringBuffer convBuf = new StringBuffer(16);
	  convBuf.append(lastChar);
	
    while ((i < patternLength) 
                  && Character.isUnicodeIdentifierPart(pattern.charAt(i))) {
      convBuf.append(pattern.charAt(i));
      //System.out.println("conv buffer is now ["+convBuf+"].");
      i++;
    }

    return convBuf.toString();
  }

  /**
   * Returns the option, null if not in the expected format.
   */
  protected List extractOptions() {
    ArrayList options = null;
    while ((i < patternLength) && (pattern.charAt(i) == '{')) {
      int end = pattern.indexOf('}', i);

      if (end > i) {
        if (options == null) {
            options = new ArrayList();
        }
        String r = pattern.substring(i + 1, end);
        options.add(r);
        i = end + 1;
      }
    }

    return options;
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
          logger.error(
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

    String converterId = extractConverter(c);

    //System.out.println("converter ID[" + converterId + "]");
    //System.out.println("c is [" + c + "]");
    String className = (String) findConverterClass(converterId);

    //System.out.println("converter class [" + className + "]");
    
    List options = extractOptions();

    //System.out.println("Option is [" + option + "]");
    if (className != null) {
      pc =
        (PatternConverter) OptionConverter.instantiateByClassName(
          className, PatternConverter.class, null);

      // formattingInfo variable is an instance variable, occasionally reset 
      // and used over and over again
      pc.setFormattingInfo(formattingInfo);
      pc.setOptions(options);
      currentLiteral.setLength(0);
    } else {
      logger.error(
          "Unexpected char [" + c + "] at position " + i
          + " in conversion patterrn.");
        pc = new LiteralPatternConverter(currentLiteral.toString());
        currentLiteral.setLength(0);
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

  /**
   * Returns the converter registry for this PatternParser instance.
   */
  public Map getConverterRegistry() {
    return converterRegistry;
  }

  /**
   * Set the converter registry for this PatternParser instance.
   */
  public void setConverterRegistry(Map converterRegistry) {
    this.converterRegistry = converterRegistry;
  }
}
