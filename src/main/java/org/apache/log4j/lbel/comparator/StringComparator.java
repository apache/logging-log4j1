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

package org.apache.log4j.lbel.comparator;

import org.apache.log4j.lbel.Operator;
import org.apache.log4j.lbel.ScanError;
import org.apache.log4j.spi.LoggingEvent;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;


/**
 * Base class for string-based comparators.
 *
 * <p>Allowed comparison operators are 'CHILDOF', '=', '!=', '>', '>=', '<',
 * '<=', '~' and '!~' where '~' stands for regular expression match.
 *
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * @author Scott Deboy
 */
public abstract class StringComparator implements Comparator {
  Operator operator;
  String rightSide;
  String rightSideWithDotSuffix;
  Pattern rightSidePattern;
  Perl5Matcher matcher;

  public StringComparator(Operator operator, String rightSide)
    throws ScanError {
    this.operator = operator;
    this.rightSide = rightSide;

    if (operator.isRegex()) {
      Perl5Compiler compiler = new Perl5Compiler();
      matcher = new Perl5Matcher();
      try {
        rightSidePattern = compiler.compile(rightSide);
      } catch (MalformedPatternException mfpe) {
        throw new ScanError("Malformed pattern [" + rightSide + "]", mfpe);
      }
    }

    // if CHILDOF operator and rightSide does not end with a dot add one
    if ((operator.getCode() == Operator.CHILDOF) && !rightSide.endsWith(".")) {
      this.rightSideWithDotSuffix = rightSide + ".";
    }
  }

  /**
   * Derived classes supply the left side of the comparison based on the event.
   *
   * @param event
   * @return the left side of the expression
   */
  protected abstract String getLeftSide(LoggingEvent event);

  public boolean compare(LoggingEvent event) throws NullPointerException {
    String leftSide = getLeftSide(event);

    if (leftSide == null) {
      switch (operator.getCode()) {
      case Operator.EQUAL:
        return leftSide == rightSide;
      case Operator.NOT_EQUAL:
        return leftSide != rightSide;
      default:
        throw new NullPointerException(
          "null leftside can only be used with == or != operators");
      }
    }

    if (operator.isRegex()) {
      boolean match = matcher.contains(leftSide, rightSidePattern);
      if (operator.getCode() == Operator.REGEX_MATCH) {
        return match;
      } else {
        return !match;
      }
    }

    if (operator.getCode() == Operator.CHILDOF) {
      if (leftSide.equals(rightSide)) {
        return true;
      } else {
        return leftSide.startsWith(rightSideWithDotSuffix);
      }
    }

    if (rightSide == null) {
      switch (operator.getCode()) {
      case Operator.EQUAL:
        return leftSide == rightSide;
      case Operator.NOT_EQUAL:
        return leftSide != rightSide;
      default:
        throw new NullPointerException(
          "null rightside can only be used with == or != operators");
      }
    }
    int compResult = leftSide.compareTo(rightSide);

    switch (operator.getCode()) {
    case Operator.EQUAL: 
      return compResult == 0;
    case Operator.NOT_EQUAL:
      return compResult != 0;
    case Operator.GREATER:
      return compResult > 0;
    case Operator.GREATER_OR_EQUAL:
      return compResult >= 0;
    case Operator.LESS:
      return compResult < 0;
    case Operator.LESS_OR_EQUAL:
      return compResult <= 0;
    }

    throw new IllegalStateException(
      "Unreachable state reached, operator " + operator);
  }

  public String toString() {
    String full = this.getClass().getName();
    int i = full.lastIndexOf(".");
    return full.substring(i) + "(" + operator + ", " + rightSide + ")";
  }
}
