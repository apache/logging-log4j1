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

package org.apache.log4j.rule;

import org.apache.log4j.Level;
import org.apache.log4j.UtilLoggingLevel;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
    String inequalitySymbol, String value) {
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

  public static Rule getRule(String inequalitySymbol, String value) {
      return new LevelInequalityRule(inequalitySymbol, value);
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
