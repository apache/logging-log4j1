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

import java.util.ArrayList;
import java.util.List;


/**
 * NameAbbreviator generates abbreviated logger and class names.
 *
 * @author Curt Arnold
 * @since 1.3
 */
public abstract class NameAbbreviator {
  /**
   * Default (no abbreviation) abbreviator.
   */
  private static final NameAbbreviator defaultAbbreviator =
    new NOPAbbreviator();

  /**
   * Gets an abbreviator.
   *
   *
   * @param pattern abbreviation pattern.
   * @return abbreviator, will not be null.
   */
  public static NameAbbreviator getAbbreviator(final String pattern) {
    if (pattern.length() > 0) {
      //  if pattern is just spaces and numbers then
      //     use MaxElementAbbreviator
      String trimmed = pattern.trim();

      if (trimmed.length() == 0) {
        return defaultAbbreviator;
      }

      int i = 0;

      for (
        ;
          (i < trimmed.length()) && (trimmed.charAt(i) >= '0')
          && (trimmed.charAt(i) <= '9'); i++)
        ;

      //
      //  if all blanks and digits
      //
      if (i == trimmed.length()) {
        return new MaxElementAbbreviator(Integer.parseInt(trimmed));
      }

      ArrayList fragments = new ArrayList(5);
      char ellipsis;
      int charCount;
      int pos = 0;

      while ((pos < trimmed.length()) && (pos >= 0)) {
        int ellipsisPos = pos;

        if (trimmed.charAt(pos) == '*') {
          charCount = Integer.MAX_VALUE;
          ellipsisPos++;
        } else {
          if ((trimmed.charAt(pos) >= '0') && (trimmed.charAt(pos) <= '9')) {
            charCount = trimmed.charAt(pos) - '0';
            ellipsisPos++;
          } else {
            charCount = 0;
          }
        }

        ellipsis = '\0';

        if (ellipsisPos < trimmed.length()) {
          ellipsis = trimmed.charAt(ellipsisPos);

          if (ellipsis == '.') {
            ellipsis = '\0';
          }
        }

        fragments.add(new PatternAbbreviatorFragment(charCount, ellipsis));
        pos = trimmed.indexOf(".", pos);

        if (pos == -1) {
          break;
        }

        pos++;
      }

      return new PatternAbbreviator(fragments);
    }

    //
    //  no matching abbreviation, return defaultAbbreviator
    //
    return defaultAbbreviator;
  }

  /**
   * Gets default abbreviator.
   *
   * @return default abbreviator.
   */
  public static NameAbbreviator getDefaultAbbreviator() {
    return defaultAbbreviator;
  }

  /**
   * Appends abbreviated name to StringBuffer.
   *
   *
   * @param buf buffer, may not be null.
   * @param name name, may not be null.
   *
   */
  public abstract void abbreviate(final StringBuffer buf, String name);

  /**
   * Abbreviator that simply appends full name to buffer.
   */
  private static class NOPAbbreviator extends NameAbbreviator {
    public NOPAbbreviator() {
    }

    public void abbreviate(final StringBuffer buf, String name) {
      buf.append(name);
    }
  }

  /**
   * Abbreviator that drops starting path elements.
   */
  private static class MaxElementAbbreviator extends NameAbbreviator {
    private final int count;

    /**
     * Create new instance.
     * @param count maximum number of path elements to output.
     */
    public MaxElementAbbreviator(final int count) {
      this.count = count;
    }

    /**
     * Abbreviate name.
     * @param buf buffer to append abbreviation.
     * @param name name to abbreviate.
     */
    public void abbreviate(final StringBuffer buf, String name) {
      int len = name.length();

      // We substract 1 from 'len' when assigning to 'end' to avoid out of
      // bounds exception in return r.substring(end+1, len). This can happen if
      // precision is 1 and the category name ends with a dot.
      int end = len - 1;

      for (int i = count; i > 0; i--) {
        end = name.lastIndexOf('.', end - 1);

        if (end == -1) {
          // not enough dot characters. The whole string should be returned
          buf.append(name);

          return;
        }
      }

      // The end variable should point to the left-most dot character to
      // the right of which all character should be returned.
      buf.append(name.substring(end + 1, len));
    }
  }

  /**
   * Fragment of an pattern abbreviator.
   *
   */
  private static class PatternAbbreviatorFragment {
    /**
     * Count of initial characters of element to output.
     */
    private final int charCount;

    /**
     *  Character used to represent dropped characters.
     * '\0' indicates no representation of dropped characters.
     */
    private final char ellipsis;

    /**
     * Creates a PatternAbbreviatorFragment.
     * @param charCount number of initial characters to preserve.
     * @param ellipsis character to represent elimination of characters,
     *    '\0' if no ellipsis is desired.
     */
    public PatternAbbreviatorFragment(
      final int charCount, final char ellipsis) {
      this.charCount = charCount;
      this.ellipsis = ellipsis;
    }

    /**
     * Abbreviate element of name.
     * @param buf buffer to receive element.
     * @param name name.
     * @param startPos starting index of name element.
     * @return starting index of next element.
     */
    public int abbreviate(
      final StringBuffer buf, final String name, final int startPos) {
      int nextDot = name.indexOf(".", startPos);

      if (nextDot != -1) {
        if ((nextDot - startPos) > charCount) {
          if (charCount > 0) {
            buf.append(name.substring(startPos, startPos + charCount));
          }

          if (ellipsis != '\0') {
            buf.append(ellipsis);
          }

          buf.append('.');
        } else {
          buf.append(name.substring(startPos, nextDot + 1));
        }

        return nextDot + 1;
      }

      buf.append(name.substring(startPos));

      return name.length();
    }
  }

  /**
   * Pattern abbreviator.
   *
   *
   */
  private static class PatternAbbreviator extends NameAbbreviator {
    /**
     * Element abbreviation patterns.
     */
    private final PatternAbbreviatorFragment[] fragments;

    /**
     * Create PatternAbbreviator.
     *
     * @param fragments element abbreviation patterns.
     */
    public PatternAbbreviator(List fragments) {
      if (fragments.size() == 0) {
        throw new IllegalArgumentException(
          "fragments must have at least one element");
      }

      this.fragments = new PatternAbbreviatorFragment[fragments.size()];
      fragments.toArray(this.fragments);
    }

    /**
     * Abbreviate name.
     * @param buf buffer that abbreviated name is appended.
     * @param name name.
     */
    public void abbreviate(final StringBuffer buf, final String name) {
      //
      //  all non-terminal patterns are executed once
      //
      int pos = 0;

      for (int i = 0; (i < (fragments.length - 1)) && (pos < name.length());
          i++) {
        pos = fragments[i].abbreviate(buf, name, pos);
      }

      //
      //   last pattern in executed repeatedly
      //
      PatternAbbreviatorFragment terminalFragment =
        fragments[fragments.length - 1];

      while (pos < name.length()) {
        pos = terminalFragment.abbreviate(buf, name, pos);
      }
    }
  }
}
