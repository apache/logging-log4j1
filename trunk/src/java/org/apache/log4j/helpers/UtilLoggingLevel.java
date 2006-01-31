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

package org.apache.log4j.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

/**
 *  An extension of the Level class that provides support for java.util.logging 
 * Levels.
 *
 *  @author Scott Deboy <sdeboy@apache.org>
 *
 */

public class UtilLoggingLevel extends Level {

  public static final int SEVERE_INT = 17000;
  public static final int WARNING_INT = 16000;
  public static final int INFO_INT = 15000;
  public static final int CONFIG_INT = 14000;
  public static final int FINE_INT = 13000;
  public static final int FINER_INT = 12000;
  public static final int FINEST_INT = 11000;
  public static final int UNKNOWN_INT = 10000;
  
  public static final UtilLoggingLevel SEVERE = new UtilLoggingLevel(SEVERE_INT, "SEVERE", 0);
  public static final UtilLoggingLevel WARNING = new UtilLoggingLevel(WARNING_INT, "WARNING", 4);
  public static final UtilLoggingLevel INFO = new UtilLoggingLevel(INFO_INT, "INFO", 5);
  public static final UtilLoggingLevel CONFIG = new UtilLoggingLevel(CONFIG_INT, "CONFIG", 6);
  public static final UtilLoggingLevel FINE = new UtilLoggingLevel(FINE_INT, "FINE", 7);
  public static final UtilLoggingLevel FINER = new UtilLoggingLevel(FINER_INT, "FINER", 8);      
  public static final UtilLoggingLevel FINEST = new UtilLoggingLevel(FINEST_INT, "FINEST", 9);      

  protected UtilLoggingLevel(int level, String levelStr, int syslogEquivalent) {
    super(level, levelStr, syslogEquivalent);
  }

  /**
    Convert an integer passed as argument to a level. If the
    conversion fails, then this method returns the specified default.
  */
  public static UtilLoggingLevel toLevel(int val, UtilLoggingLevel defaultLevel) {
    switch (val) {
    case SEVERE_INT:
      return SEVERE;

    case WARNING_INT:
      return WARNING;

    case INFO_INT:
      return INFO;

    case CONFIG_INT:
      return CONFIG;

    case FINE_INT:
      return FINE;

    case FINER_INT:
      return FINER;

    case FINEST_INT:
      return FINEST;

    default:
      return defaultLevel;
    }
  }

  public static Level toLevel(int val) {
    return toLevel(val, FINEST);
  }

  public static List getAllPossibleLevels() {
  	ArrayList list=new ArrayList();
  	list.add(FINE);
  	list.add(FINER);
  	list.add(FINEST);
  	list.add(INFO);
  	list.add(CONFIG);
  	list.add(WARNING);
  	list.add(SEVERE);
  	return list;
  }

  public static Level toLevel(String s) {
  	return toLevel(s, Level.DEBUG);
  }
  
  public static Level toLevel(String sArg, Level defaultLevel) {
    if (sArg == null) {
      return defaultLevel;
    }

    String s = sArg.toUpperCase();

    if (s.equals("SEVERE")) {
      return SEVERE;
    }

    //if(s.equals("FINE")) return Level.FINE; 
    if (s.equals("WARNING")) {
      return WARNING;
    }

    if (s.equals("INFO")) {
      return INFO;
    }

    if (s.equals("CONFI")) {
      return CONFIG;
    }

    if (s.equals("FINE")) {
      return FINE;
    }

    if (s.equals("FINER")) {
      return FINER;
    }

    if (s.equals("FINEST")) {
      return FINEST;
    }
    return defaultLevel;
  }

}
