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

package org.apache.log4j.performance;


import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


  
/**
 * Logs in a loop a number of times and measure the elapsed time.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LoggingLoop {
  static int runLength;
  static int command;
  static final Logger logger = Logger.getLogger(LoggingLoop.class);
  static final double MILLION = 1000 * 1000.0;
  static final int ALL = 0;
  static final int NOLOG_BAD = 1;
  static final int NOLOG_BETTER = 2;
  static final int NOLOG_NOPARAM = 3;
  static final int LOG_BAD = 4;
  static final int LOG_BETTER = 5;
  static final int LOG_NOPARAM = 6;

  public static void main(String[] args) throws Exception {
    Logger root = Logger.getRootLogger();

    if (args.length == 2) {
      init(args[0], args[1]);
    } else {
      usage("Wrong number of arguments.");
    }
    
    switch(command) {
      case ALL:
      case NOLOG_BAD: 
        root.setLevel(Level.OFF);
        loopBad(); 
        if(command != ALL) break; 
      case NOLOG_BETTER: 
        root.setLevel(Level.OFF);
        loopBetter(); 
        if(command != ALL) break; 
      case NOLOG_NOPARAM: 
        root.setLevel(Level.OFF);
        loopNoParam(); 
        if(command != ALL) break; 
      case LOG_BAD: 
        setNullAppender();
        loopBad(); 
        if(command != ALL) break; 
      case LOG_BETTER: 
        setNullAppender();
        loopBetter(); 
        if(command != ALL) break; 
      case LOG_NOPARAM: 
        setNullAppender();
        loopNoParam(); 
        if(command != ALL) break; 
    }
  }

  static void usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " + LoggingLoop.class.getName() + " runLength configFile");
    System.err.println("\trunLength (integer) is the length of test loop.");

    System.exit(1);
  }

  static void init(String runLengthStr, String commandStr)
    throws Exception {
    runLength = Integer.parseInt(runLengthStr);
    if ("nolog-bad".equalsIgnoreCase(commandStr)) {
      command = NOLOG_BAD;
    } else if ("nolog-better".equalsIgnoreCase(commandStr)) {
      command = NOLOG_BETTER;
    } else if ("nolog-noparam".equalsIgnoreCase(commandStr)) {
      command = NOLOG_NOPARAM;
    } else if ("log-bad".equalsIgnoreCase(commandStr)) {
      command = LOG_BAD;
    } else if ("log-better".equalsIgnoreCase(commandStr)) {
      command = LOG_BETTER;
    } else if ("log-noparam".equalsIgnoreCase(commandStr)) {
      command = LOG_NOPARAM;
    } else if ("all".equalsIgnoreCase(commandStr)) {
      command = ALL;
    }
  }

  static void setNullAppender() throws Exception {
    Appender na = new NullAppender();
    //ConsoleAppender na = new ConsoleAppender(new PatternLayout());
    //Appender na = new FileAppender(new PatternLayout(), "toto.log");
    Logger root = Logger.getRootLogger();
    root.removeAllAppenders();
    root.addAppender(na);
    root.setLevel(Level.DEBUG);
  }
  
  static void loopBad() {
    String msg = "Some message of medium length. i = ";

    for (int i = 0; i < 1000; i++) {
      //logger.debug(msg + i);
    }

    long before = System.currentTimeMillis();
    for (int i = 0; i < runLength; i++) {
      logger.debug(msg + i);
    }
    long elapsedTime = System.currentTimeMillis() - before;

    double average = (elapsedTime * MILLION) / runLength;
    System.out.println(
      "Bad loop completed in [" + elapsedTime + "] milliseconds, or ["
      + average + "] nanoseconds per log.");
  }

  static void loopBetter() {
    String msg = "Some message of medium length. i = {}";

    for (int i = 0; i < 1000; i++) {
     // logger.debug(msg, "x");
    }
    long before = System.currentTimeMillis();
    for (int i = 0; i < runLength; i++) {
      logger.debug(msg, "x");
      logger.debug("sad", new Exception());
    }
    long elapsedTime = System.currentTimeMillis() - before;
    double average = (elapsedTime * MILLION) / runLength;
    System.out.println(
      "Better loop completed in [" + elapsedTime + "] milliseconds, or ["
      + average + "] nanoseconds per log.");
  }

  static void loopNoParam() {
    String msg = "Some message of medium length.";

    long before = System.currentTimeMillis();
    for (int i = 0; i < runLength; i++) {
      logger.debug(msg);
    }
    long elapsedTime = System.currentTimeMillis() - before;
    double average = (elapsedTime * MILLION) / runLength;
    System.out.println(
      "No parameter loop completed in [" + elapsedTime
      + "] milliseconds, or [" + average + "] nanoseconds per log.");
  }
}