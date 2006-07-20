/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.log4j.varia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * LogFilePatternReceiver can parse and tail log files, converting entries into
 * LoggingEvents.  If the file doesn't exist when the receiver is initialized, the
 * receiver will look for the file once every 10 seconds.
 * <p>
 * This receiver relies on ORO Perl5 features to perform the parsing of text in the 
 * log file, however the only regular expression field explicitly supported is 
 * a glob-style wildcard used to ignore fields in the log file if needed.  All other
 * fields are parsed by using the supplied keywords.
 * <p>
 * <b>Features:</b><br>
 * - specify the URL of the log file to be processed<br>
 * - specify the timestamp format in the file (if one exists)<br>
 * - specify the pattern (logFormat) used in the log file using keywords, a wildcard character (*) and fixed text<br>
 * - 'tail' the file (allows the contents of the file to be continually read and new events processed)<br>
 * - supports the parsing of multi-line messages and exceptions
 * - 'hostname' property set to URL host (or 'file' if not available)
 * - 'application' property set to URL path (or value of fileURL if not available) 
 *<p>
 * <b>Keywords:</b><br>
 * TIMESTAMP<br>
 * LOGGER<br>
 * LEVEL<br>
 * THREAD<br>
 * CLASS<br>
 * FILE<br>
 * LINE<br>
 * METHOD<br>
 * RELATIVETIME<br>
 * MESSAGE<br>
 * NDC<br>
 * PROP(key)<br>
 * <p>
 * Use a * to ignore portions of the log format that should be ignored
 * <p>
 * Example:<br>
 * If your file's patternlayout is this:<br>
 * <b>%d %-5p [%t] %C{2} (%F:%L) - %m%n</b>
 *<p>
 * specify this as the log format:<br>
 * <b>TIMESTAMP LEVEL [THREAD] CLASS (FILE:LINE) - MESSAGE</b>
 *<p>
 * To define a PROPERTY field, use PROP(key)
 * <p>
 * Example:<br> 
 * If you used the RELATIVETIME pattern layout character in the file, 
 * you can use PROP(RELATIVETIME) in the logFormat definition to assign 
 * the RELATIVETIME field as a property on the event.
 * <p>
 * If your file's patternlayout is this:<br>
 * <b>%r [%t] %-5p %c %x - %m%n</b>
 *<p>
 * specify this as the log format:<br>
 * <b>PROP(RELATIVETIME) [THREAD] LEVEL LOGGER * - MESSAGE</b>
 * <p>
 * Note the * - it can be used to ignore a single word or sequence of words in the log file
 * (in order for the wildcard to ignore a sequence of words, the text being ignored must be
 *  followed by some delimiter, like '-' or '[') - ndc is being ignored in this example.
 * <p>
 * Assign a filterExpression in order to only process events which match a filter.
 * If a filterExpression is not assigned, all events are processed.
 *<p>
 * <b>Limitations:</b><br>
 * - no support for the single-line version of throwable supported by patternlayout<br>
 *   (this version of throwable will be included as the last line of the message)<br>
 * - the relativetime patternLayout character must be set as a property: PROP(RELATIVETIME)<br>
 * - messages should appear as the last field of the logFormat because the variability in message content<br>
 * - exceptions are converted if the exception stack trace (other than the first line of the exception)<br>
 *   is stored in the log file with a tab followed by the word 'at' as the first characters in the line<br>
 * - tailing may fail if the file rolls over. 
 *<p>
 * <b>Example receiver configuration settings</b> (add these as params, specifying a LogFilePatternReceiver 'plugin'):<br>
 * param: "timestampFormat" value="yyyy-MM-d HH:mm:ss,SSS"<br>
 * param: "logFormat" value="RELATIVETIME [THREAD] LEVEL LOGGER * - MESSAGE"<br>
 * param: "fileURL" value="file:///c:/events.log"<br>
 * param: "tailing" value="true"
 *<p>
 * This configuration will be able to process these sample events:<br>
 * 710    [       Thread-0] DEBUG                   first.logger first - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test><br>
 * 880    [       Thread-2] DEBUG                   first.logger third - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test><br>
 * 880    [       Thread-0] INFO                    first.logger first - infomsg-0<br>
 * java.lang.Exception: someexception-first<br>
 *     at Generator2.run(Generator2.java:102)<br>
 *
 *@author Scott Deboy
 */
public class LogFilePatternReceiver extends Receiver {
  private final Set keywords = new HashSet();

  private static final String PROP_START = "PROP(";
  private static final String PROP_END = ")";

  private static final String LOGGER = "LOGGER";
  private static final String MESSAGE = "MESSAGE";
  private static final String TIMESTAMP = "TIMESTAMP";
  private static final String NDC = "NDC";
  private static final String LEVEL = "LEVEL";
  private static final String THREAD = "THREAD";
  private static final String CLASS = "CLASS";
  private static final String FILE = "FILE";
  private static final String LINE = "LINE";
  private static final String METHOD = "METHOD";
  
  private static final String DEFAULT_HOST = "file";
  
  //all lines other than first line of exception begin with tab followed by 'at' followed by text
  private static final String EXCEPTION_PATTERN = "\tat.*";
  private static final String REGEXP_DEFAULT_WILDCARD = ".+?";
  private static final String REGEXP_GREEDY_WILDCARD = ".+";
  private static final String PATTERN_WILDCARD = "*";
  private static final String DEFAULT_GROUP = "(" + REGEXP_DEFAULT_WILDCARD + ")";
  private static final String GREEDY_GROUP = "(" + REGEXP_GREEDY_WILDCARD + ")";
  private static final String MULTIPLE_SPACES_REGEXP = "[ ]+";
  
  private final String newLine = System.getProperty("line.separator");

  private final String[] emptyException = new String[] { "" };

  private SimpleDateFormat dateFormat;
  private String timestampFormat = "yyyy-MM-d HH:mm:ss,SSS";
  private String logFormat;
  private String fileURL;
  private String host;
  private String path;
  private boolean tailing;
  private String filterExpression;

  private Perl5Util util = null;
  private Perl5Compiler exceptionCompiler = null;
  private Perl5Matcher exceptionMatcher = null;
  private static final String VALID_DATEFORMAT_CHAR_PATTERN = "[GyMwWDdFEaHkKhmsSzZ]";

  private Rule expressionRule;

  private Map currentMap;
  private List additionalLines;
  private List matchingKeywords;

  private String regexp;
  private Reader reader;
  private String timestampPatternText;

  public LogFilePatternReceiver() {
    keywords.add(TIMESTAMP);
    keywords.add(LOGGER);
    keywords.add(LEVEL);
    keywords.add(THREAD);
    keywords.add(CLASS);
    keywords.add(FILE);
    keywords.add(LINE);
    keywords.add(METHOD);
    keywords.add(MESSAGE);
    keywords.add(NDC);
  }

  /**
   * Accessor
   * 
   * @return file URL
   */
  public String getFileURL() {
    return fileURL;
  }

  /**
   * Mutator
   * 
   * @param fileURL
   */
  public void setFileURL(String fileURL) {
    this.fileURL = fileURL;
  }

  /**
   * Accessor
   * 
   * @return filter expression
   */
  public String getFilterExpression() {
    return filterExpression;
  }

  /**
   * Mutator
   * 
   * @param filterExpression
   */
  public void setFilterExpression(String filterExpression) {
    this.filterExpression = filterExpression;
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
   * @param logFormat
   *          the format
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
   * Walk the additionalLines list, looking for the EXCEPTION_PATTERN.
   * <p>
   * Return the index of the first matched line minus 1 
   * (the match is the 2nd line of an exception)
   * <p>
   * Assumptions: <br>
   * - the additionalLines list may contain both message and exception lines<br>
   * - message lines are added to the additionalLines list and then
   * exception lines (all message lines occur in the list prior to all 
   * exception lines)
   * 
   * @return -1 if no exception line exists, line number otherwise
   */
  private int getExceptionLine() {
    try {
      Pattern exceptionPattern = exceptionCompiler.compile(EXCEPTION_PATTERN);
      for (int i = 0; i < additionalLines.size(); i++) {
        if (exceptionMatcher.matches((String) additionalLines.get(i), exceptionPattern)) {
          return i - 1;
        }
      }
    } catch (MalformedPatternException mpe) {
      getLogger().warn("Bad pattern: " + EXCEPTION_PATTERN);
    }
    return -1;
  }

  /**
   * Combine all message lines occuring in the additionalLines list, adding
   * a newline character between each line
   * <p>
   * the event will already have a message - combine this message
   * with the message lines in the additionalLines list 
   * (all entries prior to the exceptionLine index)
   * 
   * @param firstMessageLine primary message line
   * @param exceptionLine index of first exception line
   * @return message
   */
  private String buildMessage(String firstMessageLine, int exceptionLine) {
    if (additionalLines.size() == 0 || exceptionLine == 0) {
      return firstMessageLine;
    }
    StringBuffer message = new StringBuffer();
    if (firstMessageLine != null) {
      message.append(firstMessageLine);
    }
      
    int linesToProcess = (exceptionLine == -1?additionalLines.size(): exceptionLine);

    for (int i = 0; i < linesToProcess; i++) {
      message.append(newLine);
      message.append(additionalLines.get(i));
    }
    return message.toString();
  }

  /**
   * Combine all exception lines occuring in the additionalLines list into a 
   * String array
   * <p>
   * (all entries equal to or greater than the exceptionLine index)
   * 
   * @param exceptionLine index of first exception line
   * @return exception
   */
  private String[] buildException(int exceptionLine) {
    if (exceptionLine == -1) {
      return emptyException;
    }
    String[] exception = new String[additionalLines.size() - exceptionLine];
    for (int i = 0; i < additionalLines.size() - exceptionLine; i++) {
      exception[i] = (String) additionalLines.get(i + exceptionLine);
    }
    return exception;
  }

  /**
   * Construct a logging event from currentMap and additionalLines 
   * (additionalLines contains multiple message lines and any exception lines)
   * <p>
   * CurrentMap and additionalLines are cleared in the process
   * 
   * @return event
   */
  private LoggingEvent buildEvent() {
    if (currentMap.size() == 0) {
      if (additionalLines.size() > 0) {
        for (Iterator iter = additionalLines.iterator();iter.hasNext();) {
          getLogger().info("found non-matching line: " + iter.next());
        }
      }
      additionalLines.clear();
      return null;
    }
    //the current map contains fields - build an event
    int exceptionLine = getExceptionLine();
    String[] exception = buildException(exceptionLine);

    //messages are listed before exceptions in additionallines
    if (additionalLines.size() > 0 && exceptionLine != 0) {
      currentMap.put(MESSAGE, buildMessage((String) currentMap.get(MESSAGE),
          exceptionLine));
    }
    LoggingEvent event = convertToEvent(currentMap, exception);
    currentMap.clear();
    additionalLines.clear();
    return event;
  }

  /**
   * Read, parse and optionally tail the log file, converting entries into logging events.
   * 
   * A runtimeException is thrown if the logFormat pattern is malformed 
   * according to ORO's Perl5Compiler.
   * 
   * @param unbufferedReader
   * @throws IOException
   */
  protected void process(Reader unbufferedReader) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(unbufferedReader);

    Perl5Compiler compiler = new Perl5Compiler();
    Pattern regexpPattern = null;
    try {
      regexpPattern = compiler.compile(regexp);
    } catch (MalformedPatternException mpe) {
      throw new RuntimeException("Bad pattern: " + regexp);
    }

    Perl5Matcher eventMatcher = new Perl5Matcher(); 
    String line = null;
    getLogger().debug("tailing file: " + tailing);
    do {
      while ((line = bufferedReader.readLine()) != null) {
        if (eventMatcher.matches(line, regexpPattern)) {
          //build an event from the previous match (held in current map)
          LoggingEvent event = buildEvent();
          if (event != null) {
            if (passesExpression(event)) {
              doPost(event);
            }
          }
          currentMap.putAll(processEvent(eventMatcher.getMatch()));
        } else {
          //getLogger().debug("line doesn't match pattern - must be ")
          //may be an exception or additional message lines
          additionalLines.add(line);
        }
      }

      //process last event if one exists
      LoggingEvent event = buildEvent();
      if (event != null) {
        if (passesExpression(event)) {
          doPost(event);
        }
        getLogger().debug("no further lines to process in " + fileURL);
      }
      try {
        synchronized (this) {
          wait(2000);
        }
      } catch (InterruptedException ie) {
      }
    } while (tailing);
    getLogger().debug("processing " + fileURL + " complete");
    shutdown();
  }

  /**
   * Helper method that supports the evaluation of the expression
   * 
   * @param event
   * @return true if expression isn't set, or the result of the evaluation otherwise 
   */
  private boolean passesExpression(LoggingEvent event) {
    if (event != null) {
      if (expressionRule != null) {
        return (expressionRule.evaluate(event));
      }
    }
    return true;
  }

  /**
   * Convert the ORO match into a map.
   * <p>
   * Relies on the fact that the matchingKeywords list is in the same
   * order as the groups in the regular expression
   *  
   * @param result
   * @return map
   */
  private Map processEvent(MatchResult result) {
    Map map = new HashMap();
    //group zero is the entire match - process all other groups
    for (int i = 1; i < result.groups(); i++) {
      map.put(matchingKeywords.get(i - 1), result.group(i));
    }
    return map;
  }
  
  /**
   * Helper method that will convert timestamp format to a pattern
   * 
   * 
   * @return string
   */
  private String convertTimestamp() {
    return util.substitute("s/("+VALID_DATEFORMAT_CHAR_PATTERN+")+/\\\\w+/g", timestampFormat);
  }
  
  protected void setHost(String host) {
	  this.host = host;
  }
  
  protected void setPath(String path) {
	  this.path = path;
  }

  /**
   * Build the regular expression needed to parse log entries
   *  
   */
  protected void initialize() {
	if (host == null && path == null) {
		try {
			URL url = new URL(fileURL);
			host = url.getHost();
			path = url.getPath();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	if (host == null || host.trim().equals("")) {
		host = DEFAULT_HOST; 
	}
	if (path == null || path.trim().equals("")) {
		path = fileURL;
	}
	
    util = new Perl5Util();
    exceptionCompiler = new Perl5Compiler();
    exceptionMatcher = new Perl5Matcher();

    currentMap = new HashMap();
    additionalLines = new ArrayList();
    matchingKeywords = new ArrayList();
    
    if (timestampFormat != null) {
      dateFormat = new SimpleDateFormat(timestampFormat);
      timestampPatternText = convertTimestamp();
    }

    try {
      if (filterExpression != null) {
        expressionRule = ExpressionRule.getRule(filterExpression);
      }
    } catch (Exception e) {
      getLogger().warn("Invalid filter expression: " + filterExpression, e);
    }

    Map keywordMap = new TreeMap();

    String newPattern = logFormat;

    /*
     * examine pattern, adding properties to an index-based map where the key is the 
     * numeric offset from the start of the pattern so that order can be preserved
     * 
     * Replaces PROP(X) definitions in the pattern with the short version X, so 
     * that the name can be used as the event property later 
     */
    int index = 0;
    int currentPosition = 0;
    String current = newPattern;
    while (index > -1) {
      index = current.indexOf(PROP_START);
      currentPosition = currentPosition + index;
      if (index > -1) {
        String currentProp = current.substring(current.indexOf(PROP_START));
        String prop = currentProp.substring(0,
            currentProp.indexOf(PROP_END) + 1);
        current = current.substring(current.indexOf(currentProp) + 1);
        String shortProp = prop.substring(PROP_START.length(),
            prop.length() - 1);
        keywordMap.put(new Integer(currentPosition), shortProp);
        newPattern = replace(prop, shortProp, newPattern);
      }
    }

    newPattern = replaceMetaChars(newPattern);

    //compress one or more spaces in the pattern into the [ ]+ regexp
    //(supports padding of level in log files)
    newPattern = util.substitute("s/" + MULTIPLE_SPACES_REGEXP +"/" + MULTIPLE_SPACES_REGEXP + "/g", newPattern);
    newPattern = replace(PATTERN_WILDCARD, REGEXP_DEFAULT_WILDCARD, newPattern);

    /*
     * we're using a treemap, so the index will be used as the key to ensure
     * keywords are ordered correctly
     * 
     * examine pattern, adding keywords to an index-based map patterns can
     * contain only one of these per entry...properties are the only 'keyword'
     * that can occur multiple times in an entry
     */
    Iterator iter = keywords.iterator();
    while (iter.hasNext()) {
      String keyword = (String) iter.next();
      int index2 = newPattern.indexOf(keyword);
      if (index2 > -1) {
        keywordMap.put(new Integer(index2), keyword);
      }
    }

    //keywordMap should be ordered by index..add all values to a list
    matchingKeywords.addAll(keywordMap.values());

    /*
     * iterate over the keywords found in the pattern and replace with regexp
     * group
     */
    String currentPattern = newPattern;
    for (int i = 0;i<matchingKeywords.size();i++) {
      String keyword = (String) matchingKeywords.get(i);
      //make the final keyword greedy
      if (i == (matchingKeywords.size() - 1)) {
        currentPattern = replace(keyword, GREEDY_GROUP, currentPattern);
      } else if (TIMESTAMP.equals(keyword)) {
        currentPattern = replace(keyword, "(" + timestampPatternText + ")", currentPattern);
      } else {
        currentPattern = replace(keyword, DEFAULT_GROUP, currentPattern);
      }
    }

    regexp = currentPattern;
    getLogger().debug("regexp is " + regexp);
  }

  /**
   * Helper method that will globally replace a section of text
   * 
   * @param pattern
   * @param replacement
   * @param input 
   * 
   * @return string
   */
  private String replace(String pattern, String replacement, String input) {
    return util.substitute("s/" + Perl5Compiler.quotemeta(pattern) + "/"
        + Perl5Compiler.quotemeta(replacement) + "/g", input);
  }

  /**
   * Some perl5 characters may occur in the log file format.  
   * Escape these characters to prevent parsing errors.
   * 
   * @param input
   * @return string
   */
  private String replaceMetaChars(String input) {
    input = replace("(", "\\(", input);
    input = replace(")", "\\)", input);
    input = replace("[", "\\[", input);
    input = replace("]", "\\]", input);
    input = replace("{", "\\{", input);
    input = replace("}", "\\}", input);
    input = replace("#", "\\#", input);
    input = replace("/", "\\/", input);
    return input;
  }

  /**
   * Convert a keyword-to-values map to a LoggingEvent
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

    //a logger must exist at a minimum for the event to be processed
    if (!fieldMap.containsKey(LOGGER)) {
      fieldMap.put(LOGGER, "Unknown");
    }
    if (exception == null) {
      exception = emptyException;
    }

    Logger logger = null;
    long timeStamp = 0L;
    String level = null;
    String threadName = null;
    Object message = null;
    String ndc = null;
    String className = null;
    String methodName = null;
    String eventFileName = null;
    String lineNumber = null;
    Hashtable properties = new Hashtable();

    logger = Logger.getLogger((String) fieldMap.remove(LOGGER));

    if ((dateFormat != null) && fieldMap.containsKey(TIMESTAMP)) {
      try {
        timeStamp = dateFormat.parse((String) fieldMap.remove(TIMESTAMP))
            .getTime();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    //use current time if timestamp not parseable
    if (timeStamp == 0L) {
      timeStamp = System.currentTimeMillis();
    }

    level = (String) fieldMap.remove(LEVEL);
    Level levelImpl = Level.toLevel(level.trim());

    threadName = (String) fieldMap.remove(THREAD);

    message = fieldMap.remove(MESSAGE);
    if (message == null) {
      message = "";
    }

    ndc = (String) fieldMap.remove(NDC);

    className = (String) fieldMap.remove(CLASS);

    methodName = (String) fieldMap.remove(METHOD);

    eventFileName = (String) fieldMap.remove(FILE);

    lineNumber = (String) fieldMap.remove(LINE);

    properties.put(Constants.HOSTNAME_KEY, host);
    properties.put(Constants.APPLICATION_KEY, path);
    properties.put(Constants.RECEIVER_NAME_KEY, getName());

    //all remaining entries in fieldmap are properties
    properties.putAll(fieldMap);

    LocationInfo info = null;

    if ((eventFileName != null) || (className != null) || (methodName != null)
        || (lineNumber != null)) {
      info = new LocationInfo(eventFileName, className, methodName, lineNumber);
    } else {
      info = LocationInfo.NA_LOCATION_INFO;
    }

    LoggingEvent event = new LoggingEvent();
    event.setLogger(logger);
    event.setTimeStamp(timeStamp);
    event.setLevel(levelImpl);
    event.setThreadName(threadName);
    event.setMessage(message);
    event.setThrowableInformation(new ThrowableInformation(exception));
    event.setLocationInformation(info);
    event.setNDC(ndc);
    event.setProperties(properties);
    return event;
  }

  public static void main(String[] args) {
    /*
    LogFilePatternReceiver test = new LogFilePatternReceiver();
    test.setLogFormat("TIMESTAMP LEVEL [THREAD] LOGGER (FILE:LINE) - MESSAGE");
    test.setTailing(true);
    test.setFileURL("file:///C:/log/test.log");
    test.initialize();
    try {
      test.process(new InputStreamReader(new URL(test.getFileURL())
          .openStream()));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    */
  }

  /**
   * Close the reader. 
   */
  public void shutdown() {
    try {
      if (reader != null) {
        reader.close();
        reader = null;
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Read and process the log file.
   */
  public void activateOptions() {
    new Thread(new Runnable() {
      public void run() {
        initialize();
        while (reader == null) {
          getLogger().info("attempting to load file: " + getFileURL());
	        try {
	          reader = new InputStreamReader(new URL(getFileURL()).openStream());
	        } catch (FileNotFoundException fnfe) {
	          getLogger().info("file not available - will try again in 10 seconds");
	          synchronized(this) {
	            try {
	              wait(10000);
	            } catch (InterruptedException ie){}
	          }
	        } catch (IOException ioe) {
	          getLogger().warn("unable to load file", ioe);
	          return;
	        }
        } 
        try {
          process(reader);
        } catch (IOException ioe) {
          //io exception - probably shut down
          getLogger().info("stream closed");
        }
      }
    }).start();
  }
}