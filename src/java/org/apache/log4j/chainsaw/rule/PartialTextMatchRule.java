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

package org.apache.log4j.chainsaw.rule;

import org.apache.log4j.chainsaw.LoggingEventFieldResolver;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Stack;

/**
 * A Rule class implementing case-insensitive partial-text matches against two strings. 
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
class PartialTextMatchRule extends AbstractRule {
  LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  String firstParam;
  String secondParam;

  private PartialTextMatchRule(String firstParam, String secondParam) {
    this.firstParam = firstParam;
    this.secondParam = secondParam;
  }

  static Rule getRule(Stack stack) {
    String p1 = stack.pop().toString();
    String p2 = stack.pop().toString();
    System.out.println("get part text match op " + p1 + ".." + p2);

    return new PartialTextMatchRule(p1, p2);
  }

  public boolean evaluate(LoggingEvent event) {
    String p2 = resolver.getValue(secondParam, event).toString();
    System.out.println(
      "partial text match op " + firstParam + ".." + secondParam);

    boolean result =
      (((p2 != null) && (firstParam != null))
      && (p2.toLowerCase().indexOf(firstParam.toLowerCase()) > -1));
    System.out.println("result is " + result);

    return result;
  }
}
