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

import java.io.IOException;
import java.util.Iterator;
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
    static final long serialVersionUID = 851854546425717836L;

    private transient Level level;

    private static List utilList;

    private static List levelList;

    private transient String inequalitySymbol;

    static {
        levelList = new LinkedList();
        utilList = new LinkedList();

        levelList.add(Level.FATAL.toString());
        levelList.add(Level.ERROR.toString());
        levelList.add(Level.WARN.toString());
        levelList.add(Level.INFO.toString());
        levelList.add(Level.DEBUG.toString());
        levelList.add(Level.TRACE.toString());

        Iterator iter = UtilLoggingLevel.getAllPossibleLevels().iterator();

        while (iter.hasNext()) {
            utilList.add(((UtilLoggingLevel) iter.next()).toString());
        }
    }

    private LevelInequalityRule() {
    }

    public static Rule getRule(String inequalitySymbol, String value) {

        // return new LevelInequalityRule(inequalitySymbol, value);
        Level level = null;
        if (levelList.contains(value.toUpperCase())) {
            level = Level.toLevel(value.toUpperCase());
        } else {
            level = UtilLoggingLevel.toLevel(value.toUpperCase());
        }

        if ("<".equals(inequalitySymbol)) {
            return new LessThanRule(level);
        }
        if (">".equals(inequalitySymbol)) {
            return new GreaterThanRule(level);
        }
        if ("<=".equals(inequalitySymbol)) {
            return new LessThanEqualsRule(level);
        }
        if (">=".equals(inequalitySymbol)) {
            return new GreaterThanEqualsRule(level);
        }

        return null;
    }

    /**
     * Deserialize the state of the object
     * 
     * @param in
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        utilList = new LinkedList();
        levelList = new LinkedList();
        inequalitySymbol = (String) in.readObject();
        boolean isUtilLogging = in.readBoolean();
        int levelInt = in.readInt();
        if (isUtilLogging) {
            level = UtilLoggingLevel.toLevel(levelInt);
        } else {
            level = Level.toLevel(levelInt);
        }
    }

    /**
     * Serialize the state of the object
     * 
     * @param out
     * 
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(inequalitySymbol);
        out.writeBoolean(level instanceof UtilLoggingLevel);
        out.writeInt(level.toInt());
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
