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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.helpers.UtilLoggingLevel;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A Rule class implementing inequality evaluation for Levels (log4j and
 * util.logging) using the toInt method.
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class LevelInequalityRule {
    private static List levelList;
    private static List utilLoggingLevelList;

    static {
        populateLevels();
    }

    private LevelInequalityRule() {
    }
    
    private static void populateLevels() {
        levelList = new LinkedList();

        levelList.add(Level.FATAL.toString());
        levelList.add(Level.ERROR.toString());
        levelList.add(Level.WARN.toString());
        levelList.add(Level.INFO.toString());
        levelList.add(Level.DEBUG.toString());
        levelList.add(Level.TRACE.toString());
        
        utilLoggingLevelList = new LinkedList();

        utilLoggingLevelList.add(UtilLoggingLevel.SEVERE.toString());
        utilLoggingLevelList.add(UtilLoggingLevel.WARNING.toString());
        utilLoggingLevelList.add(UtilLoggingLevel.INFO.toString());
        utilLoggingLevelList.add(UtilLoggingLevel.CONFIG.toString());
        utilLoggingLevelList.add(UtilLoggingLevel.FINE.toString());
        utilLoggingLevelList.add(UtilLoggingLevel.FINER.toString());
        utilLoggingLevelList.add(UtilLoggingLevel.FINEST.toString());

    }

    public static Rule getRule(String inequalitySymbol, String value) {

        Level thisLevel = null;
        
        //if valid util.logging levels are used against events with log4j levels, the 
        //DEBUG level is used and an illegalargumentexception won't be generated
        
        //an illegalargumentexception is only generated if the user types a level name
        //that doesn't exist as either a log4j or util.logging level name
        if (levelList.contains(value.toUpperCase())) {
            thisLevel = Level.toLevel(value.toUpperCase());
        } else if (utilLoggingLevelList.contains(value.toUpperCase())){
            thisLevel = UtilLoggingLevel.toLevel(value.toUpperCase());
        } else {
            throw new IllegalArgumentException("Invalid level inequality rule - " + value + " is not a supported level");
        }

        if ("<".equals(inequalitySymbol)) {
            return new LessThanRule(thisLevel);
        }
        if (">".equals(inequalitySymbol)) {
            return new GreaterThanRule(thisLevel);
        }
        if ("<=".equals(inequalitySymbol)) {
            return new LessThanEqualsRule(thisLevel);
        }
        if (">=".equals(inequalitySymbol)) {
            return new GreaterThanEqualsRule(thisLevel);
        }

        return null;
    }

    static class LessThanRule extends AbstractRule {
        private final int newLevelInt;

        public LessThanRule(Level level) {
            newLevelInt = level.toInt();
        }

        public boolean evaluate(LoggingEvent event) {
            return (event.getLevel().toInt() < newLevelInt);
        }
    }

    static class GreaterThanRule extends AbstractRule {
        private final int newLevelInt;

        public GreaterThanRule(Level level) {
            newLevelInt = level.toInt();
        }

        public boolean evaluate(LoggingEvent event) {
            return (event.getLevel().toInt() > newLevelInt);
        }
    }

    static class GreaterThanEqualsRule extends AbstractRule {
        private final int newLevelInt;

        public GreaterThanEqualsRule(Level level) {
            newLevelInt = level.toInt();
        }

        public boolean evaluate(LoggingEvent event) {
            return event.getLevel().toInt() >= newLevelInt;
        }
    }

    static class LessThanEqualsRule extends AbstractRule {
        private final int newLevelInt;

        public LessThanEqualsRule(Level level) {
            newLevelInt = level.toInt();
        }

        public boolean evaluate(LoggingEvent event) {
            return (event.getLevel().toInt() <= newLevelInt);
        }
    }
}
