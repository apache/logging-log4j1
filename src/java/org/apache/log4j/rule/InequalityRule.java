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

package org.apache.log4j.rule;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;

import java.util.Stack;


/**
 * A Rule class implementing inequality evaluation - expects to be able to convert two values to longs.
 * If a specific inequality evaluation class has been provided for the event field, the appropriate rule is returned.
 * (For example, if the expression is Level &lt DEBUG, a LevelInequalityRule is returned).
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class InequalityRule extends AbstractRule {
  private static final String LEVEL = "LEVEL";
  private static final LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  private final String field;
  private final String value;
  private final String inequalitySymbol;

  private InequalityRule(
    String inequalitySymbol, String field, String value) {
    this.inequalitySymbol = inequalitySymbol;
    this.field = field;
    this.value = value;
  }
  
  public static Rule getRule(String inequalitySymbol, Stack stack) {
      if (stack.size() < 2) {
          throw new IllegalArgumentException("Invalid " + inequalitySymbol + " rule - expected two parameters but received " + stack.size());
      }  

      String p2 = stack.pop().toString();
      String p1 = stack.pop().toString();
      return getRule(inequalitySymbol, p1, p2);
  }
  
  public static Rule getRule(String inequalitySymbol, String field, String value) {
    if (field.equalsIgnoreCase(LEVEL)) {
      //push the value back on the stack and allow the level-specific rule pop values
      return LevelInequalityRule.getRule(inequalitySymbol, value);
    } else {
      return new InequalityRule(inequalitySymbol, field, value);
    }
  }

  public boolean evaluate(LoggingEvent event) {
    long first = 0;

    try {
      first =
        new Long(resolver.getValue(field, event).toString()).longValue();
    } catch (NumberFormatException nfe) {
      return false;
    }

    long second = 0;

    try {
      second = new Long(value).longValue();
    } catch (NumberFormatException nfe) {
      return false;
    }

    boolean result = false;

    if ("<".equals(inequalitySymbol)) {
      result = first < second;
    } else if (">".equals(inequalitySymbol)) {
      result = first > second;
    } else if ("<=".equals(inequalitySymbol)) {
      result = first <= second;
    } else if (">=".equals(inequalitySymbol)) {
      result = first >= second;
    }

    return result;
  }
}
