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

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
  This helper class can be used to extract/traverse logger information
  for a given LoggerRepository.  It is a work in progress and focus to
  date has been functionality, not performance or efficiency.

  The set of loggers can be retrieved in one of two ways:

  1) getLoggerNames() - A complete list of the loggers
  2) getLoggerPackageNames() - A list of package names, starting at a
     given package name pattern.

  If the second retrieval method is used, the caller can iteratively call
  the LoggerTraverse to retrieve sub-packages and children.

  This class is dependent on logger names that match Java fully qualified
  class names.

  It also provides methods for querying the current level of a given
  logger and

  NOTE: This class does not cause any side effects in the LoggerRepository.
  It does not inadvertantly create new Loggers in the process of parsing the
  package names or accessing information.

  NOTE: This class does not automatically keep track of changes in the given
  LoggerRepository.  The caller must call the update() method to get the
  current set of loggers.

  @author Mark Womack <mwomack@apache.org>
*/
public class LoggerTraverse {
  /** A map of all the loggers in the LoggerRepository. */
  private Map loggerMap = new TreeMap();
  
  /** A reference to the root logger of the LoggerRepository. */
  private Logger rootLogger;

  /**
    Empty constructor. */
  public LoggerTraverse() {
  }

  /**
    @param repository The LoggerRepository to traverse. */
  public LoggerTraverse(LoggerRepository repository) {
    update(repository);
  }

  /**
    Updates the LoggerTraverse to the current information in the given
    LoggerRepository.

    @param repository LoggerRepository to use for Logger information. */
  public void update(LoggerRepository repository) {
    // clear any old information
    loggerMap.clear();

    // set the root logger
    rootLogger = repository.getRootLogger();

    // enumerate through the current set of loggers
    // the key is the logger name, the value is the logger
    Enumeration loggerEnum = repository.getCurrentLoggers();

    while (loggerEnum.hasMoreElements()) {
      Logger logger = (Logger) loggerEnum.nextElement();
      loggerMap.put(logger.getName(), logger);
    }
  }

  /**
    Returns the list of all loggers, sorted by name.

    @return List List of the current loggers. */
  public List getLoggerNames() {
    List loggerList = new ArrayList(loggerMap.size());
    Iterator loggerIter = loggerMap.keySet().iterator();

    while (loggerIter.hasNext()) {
      loggerList.add((String) loggerIter.next());
    }

    return loggerList;
  }

  /**
    Using a starting name pattern, returns the next level of package names
    that start with that pattern.  Returns a list, as there can be more than
    one return value.  Passing in an empty string for the starting pattern
    will return a list of the top level package names.

    For example, if the following logger names were defined: org.apache.log4j
    and org.apache.log4j-extensions, then passing in an empty string would
    return one item in the list with a value of "org".  If the pattern
    "org.apache" were passed in, then the list would contain two items,
    "log4j" and "log4j-extensions".

    @param startPattern The name pattern to match for Logger name.
    @return List List of matching Logger names that start with the pattern. */
  public List getLoggerPackageNames(String startPattern) {
    String name = "";
    List packageList = new ArrayList(1);

    // iterate through the loggerMap, checking the name of each logger
    // against the starting pattern.  If name starts with pattern, then
    // add the next part of the package name to the return list.
    Iterator loggerIter = loggerMap.keySet().iterator();

    while (loggerIter.hasNext()) {
      String loggerName = (String) loggerIter.next();

      // does the logger name start with the startPattern
      if (loggerName.startsWith(startPattern)) {
        loggerName = loggerName.substring(startPattern.length());

        // is there part of the name left after the start pattern is removed?
        if (loggerName.length() > 0) {
          // if the left over string starts with '.'. remove it
          if (loggerName.startsWith(".")) {
            loggerName = loggerName.substring(1);
          } else if (startPattern.length() > 0) {
            break;
          }

          // find the next index of '.' and grab the part of the name before it
          int index = loggerName.indexOf('.');

          if (index != -1) {
            //System.out.println("found . at " + index);
            loggerName = loggerName.substring(0, index);
          }

          // if this is not a name we have previously encountered,
          // put it in the return list.
          if (!loggerName.equals(name)) {
            packageList.add(loggerName);
            name = loggerName;
          }
        }
      }
    }

    return packageList;
  }

  /**
    Returns true if the given package name appears to have sub-package.

    @param startPattern The name pattern to match for Logger name.
    @return boolean True if there are existing loggers that match. */
  public boolean loggerHasSubPackages(String startPattern) {
    int len = startPattern.length();

    // iterate through logger names and first one that starts with
    // pattern and the length is greater, return true.
    Iterator loggerIter = loggerMap.keySet().iterator();

    while (loggerIter.hasNext()) {
      String loggerName = (String) loggerIter.next();

      if (loggerName.startsWith(startPattern) && (loggerName.length() > len)) {
        return true;
      }
    }

    return false;
  }

  /**
    Returns the level for the root logger.

    @return Level The current Level for the root logger. */
  public Level getLevelForRootLogger() {
    return rootLogger.getEffectiveLevel();
  }

  /**
    Returns the effective level for the given package name. If no level is
    set for the given package, then search back through the package names
    until one is found that is set or return the level of the root logger.

    @param packageName The name of the logger to return the level for.
    @return Level The level of the logger. */
  public Level getLevelForPackage(String packageName) {
    String name = packageName;
    Logger logger = (Logger) loggerMap.get(packageName);

    while ((logger == null) && (name != null)) {
      int index = name.lastIndexOf('.');

      if (index != -1) {
        name = name.substring(0, index - 1);
        logger = (Logger) loggerMap.get(packageName);
      } else {
        name = null;
      }
    }

    if (logger != null) {
      return logger.getEffectiveLevel();
    } else {
      return rootLogger.getEffectiveLevel();
    }
  }

  /**
    Returns true of the package has had its level set directly or
    false if the level is inherited.

    @param packageName The name of the logger to return the level for.
    @return boolean True if the level has been explicitly configured. */
  public boolean getLevelIsSetForPackage(String packageName) {
    String name = packageName;
    Logger logger = (Logger) loggerMap.get(packageName);

    while ((logger == null) && (name != null)) {
      int index = name.lastIndexOf('.');

      if (index != -1) {
        name = name.substring(0, index - 1);
        logger = (Logger) loggerMap.get(packageName);
      } else {
        name = null;
      }
    }

    if (logger != null) {
      if (logger == rootLogger) {
        return true;
      } else {
        return (logger.getLevel() != null);
      }
    } else {
      return false;
    }
  }

  /**
    here is an example of using the hierarchical version, iterating
    through all the package names, all the loggers.
    
    @param args Parameters for main execution. */
  public static void main(String[] args) {
    // set the root to level warn
    Logger.getRootLogger().setLevel(Level.ERROR);

    // create some loggers
    Logger.getLogger("org.womacknet.wgntool.Researcher");
    Logger.getLogger("org.womacknet.wgntool.ResearcherList");
    Logger.getLogger("org.womacknet.wgntool").setLevel(Level.WARN);
    Logger.getLogger("org.womacknet.util.NameUtil");
    Logger.getLogger("com.widgets_r_us.util.StringUtil").setLevel(Level.DEBUG);

    LoggerTraverse info = new LoggerTraverse(LogManager.getLoggerRepository());
    System.out.println("NOTE: '*' indicates the level has not been "
      + "explicitly configured for that logger");
    System.out.println("root - " + info.getLevelForRootLogger());
    iteratePackages("", 1, info);
  }

  /**
    Starting with a package name, iterate through all subpackages and loggers.
    
    @param startPackageName The logger name to start iterating from.
    @param level The indentation value for display of logger names.
    @param info The TraverseInfo instance to iterate. */
  static void iteratePackages(
    String startPackageName, int level, LoggerTraverse info) {
    List packageInfo = info.getLoggerPackageNames(startPackageName);
    Iterator iter = packageInfo.iterator();

    while (iter.hasNext()) {
      String packageName = (String) iter.next();

      for (int x = 0; x < level; x++) {
        System.out.print(" ");
      }

      System.out.print(packageName);

      String subpackageName;

      if (startPackageName.length() > 0) {
        subpackageName = startPackageName + "." + packageName;
      } else {
        subpackageName = packageName;
      }

      System.out.print(" - " + info.getLevelForPackage(subpackageName));
      System.out.println(
        (info.getLevelIsSetForPackage(subpackageName) ? "" : "*"));

      if (info.loggerHasSubPackages(subpackageName)) {
        iteratePackages(subpackageName, level + 1, info);
      }
    }
  }
}
