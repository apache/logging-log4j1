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

package org.apache.log4j;

import java.util.ArrayList;
import java.util.List;

/**
 *  An extension of the Level class that provides support for java.util.logging 
 * Levels.
 *
 *  @author Scott Deboy <sdeboy@apache.org>
 *
 */

public class UtilLoggingLevel extends Level {

  public static final int SEVERE_INT = 1000;
  public static final int WARNING_INT = 900;
  public static final int INFO_INT = 800;
  public static final int CONFIG_INT = 700;
  public static final int FINE_INT = 500;
  public static final int FINER_INT = 400;
  public static final int FINEST_INT = 300;
  public static final int UNKNOWN_INT = 200;
  
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
      return FINEST;
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
