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

package org.apache.log4j.varia;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * A receiver which supports the definition of the log format using keywords, the
 * contained timestamp using SimpleDateFormat's format support, and the file name
 *
 * FEATURES:
 * specify the file to be processed
 * specify the timestamp format (if one exists)
 * specify the layout used in the log file
 * define your file's layout using these keywords along with any text being added as delimiters
 * supports the conversion of exceptions found in the file
 *
 * TIMESTAMP
 * LOGGER
 * LEVEL
 * THREAD
 * CLASS
 * FILE
 * LINE
 * METHOD
 * RELATIVETIME
 * MESSAGE
 * *
 *
 * For example,
 *
 * If your file's patternlayout is this:
 * %d %-5p [%t] %C{2} (%F:%L) - %m%n
 *
 * specify this as the log format:
 * TIMESTAMP LEVEL [THREAD] CLASS (FILE:LINE) - MESSAGE
 *
 * If your file's patternlayout is this:
 * %r [%t] %-5p %c %x - %m%n
 *
 * specify this as the log format:
 * RELATIVETIME [THREAD] LEVEL LOGGER * - MESSAGE
 *
 * Note the * - it can be used to ignore a single word or sequence of words in the log file
 * (in order for the wildcard to ignore a sequence of words, the text being ignored must be
 *  followed by some delimiter, like '-' or '[')
 *
 * Note how keywords may be surrounded by delimiters, and in the second example,
 * ndc is ignored (even multiple words in the ndc in this case, since the keyword
 * is followed by a delimiter (-)
 *
 * LIMITATIONS:
 * - no support for ndc, properties or the single-line version of throwable supported by patternlayout
 * - relativetime is set as a property
 * - loggers with spaces in their names are not supported (but may work if followed by a delimiter,
 *   similar to wildcard example above)
 * - messages must appear at the end of the line
 * - note the explanation above describing the rules for ignoring text using the wildcard
 *   keyword
 * - exceptions will be converted if the exception stack trace lines (other than the first line)
 *   are stored in the log file with a tab as the first character
 *
 * EXAMPLE RECEIVER CONFIGURATION (add these as params, specifying a LogFilePatternReceiver 'plugin'
 *
 * param: "timestampFormat" value="yyyy-MM-d HH:mm:ss,SSS"
 * param: "logFormat" value="RELATIVETIME [THREAD] LEVEL LOGGER * - MESSAGE"
 * param: "fileName" value="c:/logs/A4.log"
 * param: "tailing" value="true"
 *
 * The 'tailing' parameter allows the contents of the file to be continually read and new events processed.
 * 
 * NOTE: in our example file content below, the timestampFormat entry defined above
 * is not required, but included as an example of how to specify the format.  See SimpleDateFormat
 * for more information.
 *
 * This configuration will be able to process these sample events:
 * 710    [       Thread-0] DEBUG                   first.logger first - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test>
 * 880    [       Thread-2] DEBUG                   first.logger third - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test>
 * 880    [       Thread-0] INFO                    first.logger first - infomsg-0
 * java.lang.Exception: someexception-first
 *     at Generator2.run(Generator2.java:102)
 *
 */
public class LogFilePatternReceiver extends Receiver {
  public static final String TIMESTAMP = "TIMESTAMP";
  public static final String LOGGER = "LOGGER";
  public static final String LEVEL = "LEVEL";
  public static final String THREAD = "THREAD";
  public static final String CLASS = "CLASS";
  public static final String FILE = "FILE";
  public static final String LINE = "LINE";
  public static final String METHOD = "METHOD";
  public static final String RELATIVETIME = "RELATIVETIME";
  public static final String MESSAGE = "MESSAGE";
  public static final String WILDCARD = "*";
  private static final String TAB = "\t";
  private final List keywords = new ArrayList();
  private final List logFormatFields = new ArrayList();
  private final Map defaultMap = new HashMap();
  private SimpleDateFormat dateFormat;
  private String timestampFormat = "yyyy-MM-d HH:mm:ss,SSS";
  private String logFormat;
  private String fileName;
  private String shortFileName;
  private boolean tailing;

  /**
   * Creates a new LogFilePatternReceiver object.
   */
  public LogFilePatternReceiver() {
    //define defaults which may not be provided by log file but are required by log4j
    defaultMap.put(LOGGER, "Unknown");

    //supported keyword replacements are expected to be single words, except for the MESSAGE keyword,
    //which is expected to appear at the end of each entry in the log file
    //since throwable, ndc and properties can all have spaces, they're not yet supported
    //while loggers may containspaces, only loggers without spaces are currently supported
    //fullinfo pattern is not supported directly - build from individual keywords instead
    keywords.add(TIMESTAMP);
    keywords.add(LOGGER);
    keywords.add(LEVEL);
    keywords.add(THREAD);
    keywords.add(CLASS);
    keywords.add(FILE);
    keywords.add(LINE);
    keywords.add(METHOD);

    //wildcard removes any single word 
    //will also remove any number of words if the next entry in the log file format
    //is not found in the wildcard text itself 
    //(for example, 'LOGGER * [THREAD]' would successfully resolve keywords for 
    //'MYLOGGER any text here [Thread-0]'
    //but, 'LOGGER * THREAD' would not successfully resolve keywords for
    //'MYLOGGER any text here Thread-0'
    //but would successfully resolve
    //'MYLOGGER singleword Thread-0'
    keywords.add(WILDCARD);

    //supported as a property on the event
    keywords.add(RELATIVETIME);

    //keywords.add("NDC");
    //keywords.add("MDC");
    //keywords.add("THROWABLE");
    //keywords.add("PROPERTIES");
    keywords.add(MESSAGE);
  }

  /**
   * Test
   *
   * @param args file name to parse
   */
  public static void main(String[] args) {
    LogFilePatternReceiver parser = new LogFilePatternReceiver();
    parser.setTimestampFormat("yyyy-MM-d HH:mm:ss,SSS");

    //parser.parse(args[0]);
    //%d %-5p [%t] %C{2} (%F:%L) - %m%n -- a2
    //parser.setLogFormat("TIMESTAMP LEVEL * [THREAD] CLASS (FILE:LINE) - MESSAGE");
    //parser.initialize();
    //System.out.println("Created event: " + parser.convertToEvent("2004-12-13 22:49:22,820 DEBUG SOME VALUE [Thread-0] Generator2 (Generator2.java:100) - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test>"));
    //parser.initialize("THREAD LEVEL LOGGER - MESSAGE");
    parser.setLogFormat("RELATIVETIME [THREAD] LEVEL LOGGER * - MESSAGE");
    parser.setFileName(args[0]);
    parser.initialize();

    try {
      parser.process(new FileReader(new File(parser.getFileName())));

      //parser.process(new StringReader("2004-12-13 22:49:22,820 DEBUG SOME VALUE [Thread-0] Generator2 (Generator2.java:100) - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test>\nException blah\n\tat someplace.java:555\n\tat nextline blah:55"));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    //comment out doPost calls to run via main
  }

  /**
   * Accessor
   *
   * @return file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Mutator
   *
   * @param fileName
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
    shortFileName = new File(fileName).getName();
  }

  /**
   * Accessor
   *
   * @return tailing
   */
  public boolean isTailing() {
    return tailing;
  }

  /**
   * Mutator
   *
   * @param tailing
   */
  public void setTailing(boolean tailing) {
    this.tailing = tailing;
  }

  /**
   * Accessor
   *
   * @return log format
   */
  public String getLogFormat() {
    return logFormat;
  }

  /**
   * Mutator
   *
   * @param logFormat the format
   */
  public void setLogFormat(String logFormat) {
    this.logFormat = logFormat;
  }

  /**
   * Mutator
   *
   * @param timestampFormat
   */
  public void setTimestampFormat(String timestampFormat) {
    this.timestampFormat = timestampFormat;
  }

  /**
   * Accessor
   *
   * @return timestamp format
   */
  public String getTimestampFormat() {
    return timestampFormat;
  }

  /**
   * Convert a reader into a buffered reader, reading lines and converting lines into log events
   *
   * @param unbufferedReader
   *
   * @throws IOException
   */
  public void process(Reader unbufferedReader) throws IOException {
    BufferedReader reader = new BufferedReader(unbufferedReader);
    LinkedList list = new LinkedList();
    String line = null;

    do {
      while ((line = reader.readLine()) != null) {
        //ignore blank lines in file
        if (line.length() == 0) {
          continue;
        }

        //System.out.println("Added: " + line);
        list.addLast(line);

        if (list.size() > 2) {
          //System.out.println("size > 2 - processing");
          if (line.startsWith(TAB)) {
            String firstLine = (String) list.remove(0);

            //System.out.println("exception line1: " + firstLine);
            while ((line != null) && line.startsWith(TAB)) {
              line = reader.readLine();

              if ((line != null) && (line.length() != 0)) {
                list.addLast(line);
              }
            }

            //hold a reference to the last entry added - while loop adds one too many
            String nextLine = (String) list.getLast();

            if ((nextLine != null) && !(nextLine.startsWith(TAB))) {
              list.removeLast();
            }

            String[] exception = new String[list.size()];

            for (int i = 0, j = list.size(); i < j; i++) {
              exception[i] = (String) list.remove(0);

              //System.out.println("exception " + i + ".." + exception[i]);
            }

            //now that exceptions have been taken from list, re-add last entry if not an 
            //exception line
            if ((nextLine != null) && !(nextLine.startsWith(TAB))) {
              list.addLast(nextLine);
            }

            //GENERATE EXCEPTION EVENT
            LoggingEvent event = convertToEvent(firstLine, exception);

            //System.out.println(
            //  "created event with exception " + event.getLoggerName() + ".."
            //  + event.getMessage());
            if (event != null) {
              doPost(event);
            }
          } else {
            //GENERATE NON-EXCEPTION EVENT
            LoggingEvent event = convertToEvent((String) list.remove(0));

            if (event != null) {
              doPost(event);
            }

            //System.out.println(
            //  "Created event " + event.getLoggerName() + ".."
            //  + event.getMessage());
          }
        }
      }

      //System.out.println(
      //  "outside loop - processing remaining entries: " + list.size());
      //clean up remaining - should not be an exception
      for (int k = 0, l = list.size(); k < l; k++) {
        String s = (String) list.remove(0);

        if ((s != null) && (s.length() > 0)) {
          //GENERATE NON-EXCEPTION EVENT
          LoggingEvent event = convertToEvent(s);

          if (event != null) {
            doPost(event);
          }

          //System.out.println(
          //  "cleanup - Created non-exception event " + event.getLoggerName()
          //  + ".." + event.getMessage());
        }
      }

      try {
        synchronized (this) {
          wait(2000);
        }
      } catch (InterruptedException ie) {
      }
    } while (tailing);
    try {
      if (reader != null) {
          reader.close();
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Walk the entries in the log format fields, adding keyword matches to a
   * map
   *
   * @param logEntry
   *
   * @return map of keywords to values in the log entry
   */
  private Map extractEventFields(String logEntry) {
    //System.out.println("extracting from " + logEntry);
    Map fieldMap = new HashMap(defaultMap);

    for (int i = 0; i < logFormatFields.size(); i++) {
      String thisField = (String) logFormatFields.get(i);

      //ignore wildcard entries - skip to next field
      if (thisField.equals(WILDCARD)) {
        String nextField = (String) logFormatFields.get(i + 1);

        if (logEntry.indexOf(nextField) == -1) {
          LogLog.info("Couldn't process line, ignoring: " + logEntry);

          return null;
        }

        logEntry = logEntry.substring(logEntry.indexOf(nextField));
      } else if (thisField.equals(TIMESTAMP)) {
        String nextField = (String) logFormatFields.get(i + 1);

        if (logEntry.indexOf(nextField) == -1) {
          LogLog.info("Couldn't process line, ignoring: " + logEntry);

          return null;
        }

        //uses nextfield and length of format to guess at timestamp field
        //luckily, SimpleDateFormat is very flexible and forgiving of trailing text
        int firstLength =
          logEntry.substring(0, timestampFormat.length()).length() - 1;
        int nextLength =
          (firstLength
          + logEntry.substring(firstLength - 1).indexOf(nextField)) - 1;

        int length = Math.max(firstLength, nextLength);

        //        System.out.println("parsing timestamp: " + logEntry.substring(0, length));
        fieldMap.put(thisField, logEntry.substring(0, length));

        //System.out.println(
        //  "added " + thisField + ":" + fieldMap.get(thisField));
        logEntry = logEntry.substring(length);
      } else if (keywords.contains(thisField)) {
        logEntry = logEntry.trim();

        if (i < (logFormatFields.size() - 1)) {
          //expects no two keywords to be butted up directly against eachother 
          String nextField = (String) logFormatFields.get(i + 1);

          if (logEntry.indexOf(nextField) == -1) {
            LogLog.info("Couldn't process line, ignoring: " + logEntry);

            return null;
          }

          fieldMap.put(
            thisField,
            logEntry.substring(0, logEntry.indexOf(nextField)).trim());

          //System.out.println(
          //  "added " + thisField + ":" + fieldMap.get(thisField));
          logEntry = logEntry.substring(logEntry.indexOf(nextField));
        } else {
          fieldMap.put(thisField, logEntry.trim());

          //System.out.println(
          //  "added " + thisField + ":" + fieldMap.get(thisField));
        }
      } else {
        //ignore non-fields
        //System.out.println("text: " + thisField);
        logEntry = logEntry.substring(thisField.length());
      }
    }

    return fieldMap;
  }

  /**
   * Helper convert method that doesn't support exceptions
   *
   * @param logEntry
   *
   * @return logging event
   */
  private LoggingEvent convertToEvent(String logEntry) {
    return convertToEvent(logEntry, null);
  }

  /**
   * Convert log entry with exceptions
   *
   * @param logEntry
   * @param exception string array of exception lines
   *
   * @return logging event
   */
  private LoggingEvent convertToEvent(String logEntry, String[] exception) {
    return convertToEvent(extractEventFields(logEntry), exception);
  }

  /**
   * Convert entries in the map of keywords to values in a log entry into a
   * loggingEvent
   *
   * @param fieldMap
   * @param exception
   *
   * @return logging event
   */
  private LoggingEvent convertToEvent(Map fieldMap, String[] exception) {
    if (fieldMap == null) {
      return null;
    }

    Logger logger = null;
    long timeStamp = 0L;
    String level = null;
    String threadName = null;
    Object message = null;

    //    String ndc = null;
    //    Hashtable mdc = null;
    String className = null;
    String methodName = null;
    String eventFileName = null;
    String lineNumber = null;
    Hashtable properties = new Hashtable();

    if ((dateFormat != null) && fieldMap.containsKey(TIMESTAMP)) {
      try {
        timeStamp =
          dateFormat.parse((String) fieldMap.get(TIMESTAMP)).getTime();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (timeStamp == 0L) {
      timeStamp = System.currentTimeMillis();
    }

    logger = Logger.getLogger((String) fieldMap.get(LOGGER));

    level = (String) fieldMap.get(LEVEL);
    threadName = (String) fieldMap.get(THREAD);

    message = (String) fieldMap.get(MESSAGE);

    className = (String) fieldMap.get(CLASS);
    methodName = (String) fieldMap.get(METHOD);
    eventFileName = (String) fieldMap.get(FILE);
    lineNumber = (String) fieldMap.get(LINE);

    if (fieldMap.get(RELATIVETIME) != null) {
      properties.put(RELATIVETIME, fieldMap.get(RELATIVETIME));
    }

    Level levelImpl = Level.toLevel(level);

    LocationInfo info = null;

    if (
      (eventFileName != null) || (className != null) || (methodName != null)
        || (lineNumber != null)) {
      info =
        new LocationInfo(eventFileName, className, methodName, lineNumber);
    } else {
      info = LocationInfo.NA_LOCATION_INFO;
    }

    properties.put(Constants.HOSTNAME_KEY, "file");
    properties.put(Constants.APPLICATION_KEY, shortFileName);

    LoggingEvent event = new LoggingEvent();
    event.setLogger(logger);
    event.setTimeStamp(timeStamp);
    event.setLevel(levelImpl);
    event.setThreadName(threadName);
    event.setMessage(message);
    event.setThrowableInformation(new ThrowableInformation(exception));
    event.setLocationInformation(info);
    event.setProperties(properties);
   
    return event;
  }

  /**
   * Initialize and post log entries to framework
   */
  public void activateOptions() {
    new Thread(
      new Runnable() {
        public void run() {
          initialize();

          try {
            process(new FileReader(new File(getFileName())));
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      }).start();
  }

  /**
   * Walk the passed-in log format, building a list of entries which are (from
   * left to right), either a keyword, or any string of characters not a
   * keyword.
   */
  private void initialize() {
    if (timestampFormat != null) {
      dateFormat = new SimpleDateFormat(timestampFormat);
    }

    int i = 0;
    int j = 0;

    while (i < logFormat.length()) {
      String keyword = endsInKeyword(logFormat.substring(i, j));

      //System.out.println("current arg is " + arg.substring(i,j));
      //fragment ends in keyword.  
      //add everything before the keyword as an entry in the list
      //then add the keyword
      if (keyword != null) {
        String firstPart = logFormat.substring(i, j - keyword.length());

        if (firstPart.length() > 0) {
          //System.out.println("ADDED FIRSTPART: " + firstPart);
          logFormatFields.add(firstPart);
        }

        logFormatFields.add(keyword);

        //System.out.println("ADDED: " + keyword);
        i = j;
        j = j + 1;
      } else {
        j++;
      }
    }
  }

  /**
   * Check to see if the log format fragment passed in ends with a keyword.
   *
   * @param logFormatFragment
   *
   * @return keyword or null
   */
  private String endsInKeyword(String logFormatFragment) {
    Iterator iter = keywords.iterator();

    while (iter.hasNext()) {
      String keyword = (String) iter.next();

      if (logFormatFragment.endsWith(keyword)) {
        return keyword;
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#shutdown()
   */
  public void shutdown() {
  }
}
