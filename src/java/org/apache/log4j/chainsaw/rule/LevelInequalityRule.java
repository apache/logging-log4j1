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

import org.apache.log4j.Level;
import org.apache.log4j.UtilLoggingLevel;
import org.apache.log4j.chainsaw.LoggingEventFieldResolver;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * A Rule class implementing inequality evaluation for Levels (log4j and util.logging) using the toInt method.
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class LevelInequalityRule extends AbstractRule {
  private static final LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  private final Level level;
  private final List utilList = new LinkedList();
  private final List levelList = new LinkedList();
  private final String inequalitySymbol;

  private LevelInequalityRule(
    String inequalitySymbol, String field, String value) {
    levelList.add(Level.FATAL.toString());
    levelList.add(Level.ERROR.toString());
    levelList.add(Level.WARN.toString());
    levelList.add(Level.INFO.toString());
    levelList.add(Level.DEBUG.toString());

    Iterator iter = UtilLoggingLevel.getAllPossibleLevels().iterator();

    while (iter.hasNext()) {
      utilList.add(((UtilLoggingLevel) iter.next()).toString());
    }

    if (levelList.contains(value.toUpperCase())) {
      this.level = Level.toLevel(value.toUpperCase());
    } else {
      this.level = UtilLoggingLevel.toLevel(value.toUpperCase());
    }

    this.inequalitySymbol = inequalitySymbol;
  }

  public static Rule getRule(String inequalitySymbol, String field, String value) {
      return new LevelInequalityRule(inequalitySymbol, field, value);
  }
  
  public static Rule getRule(String inequalitySymbol, Stack stack) {
    String p2 = stack.pop().toString();
    String p1 = stack.pop().toString();

    return new LevelInequalityRule(inequalitySymbol, p1, p2);
  }

  public boolean evaluate(LoggingEvent event) {
    //use the type of the first level to access the static toLevel method on the second param
    Level level2 = null;
    if (level instanceof UtilLoggingLevel) {
        level2 = UtilLoggingLevel.toLevel(resolver.getValue("LEVEL", event).toString());
    } else { 
        level2 = Level.toLevel(resolver.getValue("LEVEL", event).toString());
    }

    boolean result = false;
    int first = level2.toInt();
    int second = level.toInt();

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
