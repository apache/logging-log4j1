/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
 * @author Curt Arnold
 *
 * @since 0.8.2
*/
public final class PatternParser extends ComponentBase {
  private static final char ESCAPE_CHAR = '%';
  private static final int LITERAL_STATE = 0;
  private static final int CONVERTER_STATE = 1;
  private static final int MINUS_STATE = 2;
  private static final int DOT_STATE = 3;
  private static final int MIN_STATE = 4;
  private static final int MAX_STATE = 5;
  private static Map globalRulesRegistry;

  static {
    // We set the global rules in the static initializer of PatternParser class
    globalRulesRegistry = new HashMap(17);
    globalRulesRegistry.put("c", LoggerPatternConverter.class);
    globalRulesRegistry.put("logger", LoggerPatternConverter.class);

    globalRulesRegistry.put("C", ClassNamePatternConverter.class);
    globalRulesRegistry.put("class", ClassNamePatternConverter.class);

    globalRulesRegistry.put("d", DatePatternConverter.class);
    globalRulesRegistry.put("date", DatePatternConverter.class);

    globalRulesRegistry.put("F", FileLocationPatternConverter.class);
    globalRulesRegistry.put("file", FileLocationPatternConverter.class);

    globalRulesRegistry.put("l", FullLocationPatternConverter.class);

    globalRulesRegistry.put("L", LineLocationPatternConverter.class);
    globalRulesRegistry.put("line", LineLocationPatternConverter.class);

    globalRulesRegistry.put("m", MessagePatternConverter.class);
    globalRulesRegistry.put("message", MessagePatternConverter.class);

    globalRulesRegistry.put("n", LineSeparatorPatternConverter.class);

    globalRulesRegistry.put("M", MethodLocationPatternConverter.class);
    globalRulesRegistry.put("method", MethodLocationPatternConverter.class);

    globalRulesRegistry.put("p", LevelPatternConverter.class);
    globalRulesRegistry.put("level", LevelPatternConverter.class);

    globalRulesRegistry.put("r", RelativeTimePatternConverter.class);
    globalRulesRegistry.put("relative", RelativeTimePatternConverter.class);

    globalRulesRegistry.put("t", ThreadPatternConverter.class);
    globalRulesRegistry.put("thread", ThreadPatternConverter.class);

    globalRulesRegistry.put("x", NDCPatternConverter.class);
    globalRulesRegistry.put("ndc", NDCPatternConverter.class);

    globalRulesRegistry.put("X", PropertiesPatternConverter.class);
    globalRulesRegistry.put("properties", PropertiesPatternConverter.class);

    globalRulesRegistry.put("sn", SequenceNumberPatternConverter.class);
    globalRulesRegistry.put(
      "sequenceNumber", SequenceNumberPatternConverter.class);

    globalRulesRegistry.put(
      "throwable", ThrowableInformationPatternConverter.class);
  }

  private int state;
  private StringBuffer currentLiteral = new StringBuffer(32);
  private int patternLength;
  private int i;
  private PatternConverter head;
  private PatternConverter tail;
  private FormattingInfo formattingInfo = new FormattingInfo();
  private String pattern;

  /**
   * Additional rules for this particular instance.
   * key: the conversion word (as String)
   * value: the pattern converter class (as String)
   */
  private Map converterRegistry;

  public PatternParser(
    final String pattern, final LoggerRepository repository) {
    if (pattern == null) {
      throw new NullPointerException("pattern");
    }

    this.pattern = pattern;
    this.repository = repository;
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
  private String extractConverter(char lastChar) {
    // When this method is called, lastChar points to the first character of the
    // conersion word. For example:
    // For "%hello"     lastChar = 'h'
    // For "%-5hello"   lastChar = 'h'
    //System.out.println("lastchar is "+lastChar);
    if (!Character.isUnicodeIdentifierStart(lastChar)) {
      return null;
    }

    StringBuffer convBuf = new StringBuffer(16);
    convBuf.append(lastChar);

    while (
      (i < patternLength)
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
  private List extractOptions() {
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
          getLogger().error(
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

  /**
   * Creates a new pattern converter.
   * @param converterId converter identifier.
   * @param formattingInfo formatting info.
   * @param options options.
   * @return pattern converter, may be null.
   */
  private PatternConverter createConverter(
    final String converterId, final FormattingInfo formattingInfo,
    final List options) {
    PatternConverter converter = null;

    if (converterId == null) {
      getLogger().warn("converterId is null");
    } else {
      if (converterRegistry != null) {
        String r = (String) converterRegistry.get(converterId);

        if (r != null) {
          converter =
            (PatternConverter) OptionConverter.instantiateByClassName(
              r, PatternConverter.class, null);
        }
      }

      Class converterClass = (Class) globalRulesRegistry.get(converterId);

      if (converterClass != null) {
        try {
          converter = (PatternConverter) converterClass.newInstance();
        } catch (Exception ex) {
          getLogger().error("Error creating converter for " + converterId, ex);
        }
      }
    }

    if (converter != null) {
      converter.setFormattingInfo(formattingInfo);
      converter.setOptions(options);
    }

    return converter;
  }

  /**
   * When finalizeConverter is called 'c' is the current conversion caracter
   * and i points to the character following 'c'.
   */
  private void finalizeConverter(char c) {
    String converterId = extractConverter(c);

    List options = extractOptions();

    PatternConverter pc =
      createConverter(converterId, formattingInfo, options);

    if (pc == null) {
      StringBuffer msg;

      if ((converterId == null) || (converterId.length() == 0)) {
        msg =
          new StringBuffer("Empty conversion specifier starting at position ");
      } else {
        msg = new StringBuffer("Unrecognized conversion specifier [");
        msg.append(converterId);
        msg.append("] starting at position ");
      }

      msg.append(Integer.toString(i));
      msg.append(" in conversion pattern.");
      getLogger().error(msg.toString());
      pc = new LiteralPatternConverter(currentLiteral.toString());
    }

    // setting the logger repository is an important configuration step.
    pc.setLoggerRepository(this.repository);

    currentLiteral.setLength(0);
    addConverter(pc);
  }

  private void addConverter(final PatternConverter pc) {
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
   * @return map of custom pattern converters, may be null.
   */
  public Map getConverterRegistry() {
    if (converterRegistry == null) {
      return null;
    }

    return new HashMap(converterRegistry);
  }

  /**
   * Set the converter registry for this PatternParser instance.
   * @param converterRegistry map of format specifiers to class names, may be null.
   */
  public void setConverterRegistry(final Map converterRegistry) {
    if (converterRegistry == null) {
      this.converterRegistry = null;
    } else {
      this.converterRegistry = new HashMap(converterRegistry);
    }
  }
}
