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

// Contributors:  Mathias Bogaert
package org.apache.log4j.helpers;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
   Check every now and then that a certain file has not changed. If it
   has, then call the {@link #doOnChange} method.

   This class has been deprecated and is no longer used by either
   PropertyConfigurator or DOMConfigurator.
   
   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.1
   @deprecated Use org.apache.log4j.watchdog.FileWatchdog instead.
*/
public abstract class FileWatchdog extends Thread {
  /**
     The default delay between every file modification check, set to 60
     seconds.  */
  public static final long DEFAULT_DELAY = 60000;
  /**
     The name of the file to observe  for changes.
   */
  protected String filename;

  private Logger logger = LogManager.getLogger(SyslogWriter.class);
  
  /**
     The delay to observe between every check. By default set {@link
     #DEFAULT_DELAY}. */
  protected long delay = DEFAULT_DELAY;
  File file;
  long lastModif = 0;
  boolean warnedAlready = false;
  boolean interrupted = false;

  protected FileWatchdog(String filename) {
    this.filename = filename;
    file = new File(filename);
    setDaemon(true);
    checkAndConfigure();
  }

  /**
     Set the delay to observe between each check of the file changes.
   */
  public void setDelay(long delay) {
    this.delay = delay;
  }

  protected abstract void doOnChange();

  protected void checkAndConfigure() {
    boolean fileExists;
    try {
      fileExists = file.exists();
    } catch (SecurityException e) {
      logger.warn(
        "Was not allowed to read check file existance, file:[" + filename
        + "].");
      interrupted = true; // there is no point in continuing
      return;
    }

    if (fileExists) {
      long l = file.lastModified(); // this can also throw a SecurityException
      if (l > lastModif) { // however, if we reached this point this
        lastModif = l; // is very unlikely.
        doOnChange();
        warnedAlready = false;
      }
    } else {
      if (!warnedAlready) {
        logger.debug("[" + filename + "] does not exist.");
        warnedAlready = true;
      }
    }
  }

  public void run() {
    while (!interrupted) {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        // no interruption expected
      }
      checkAndConfigure();
    }
  }
}
