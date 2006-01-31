/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

package org.apache.log4j.watchdog;

import java.io.File;
import java.net.URL;

/**
 * Implements a watchdog to watch a file.  When the file changes, determined by
 * a change in the file's modification date, the contents of the file are use to
 * reconfigure the log4j environment.
 */
public class FileWatchdog extends TimedLocationWatchdog {

  private String filePath;

  /** The file being watched. */
  private File watchedFile;
  private URL watchedURL;

  /**
   * Sets the path of the file to use and watch for configuration changes.
   *
   * @param filePath
   */
  public void setFile(String filePath) {
    this.filePath = filePath;
  }

  /**
   * Returns the path of the file being watched for confirguration changes.
   *
   * @return the path of the file being watched
   */
  public String getFile() {
    return filePath;
  }
  /**
   * Sets up the reference to the file being watched, then calls the version
   * in the super class.
   */
  public void activateOptions() {

    if (filePath == null) {
      getLogger().error("watchdog \"{}\" not configured with path to watch",
        this.getName());
      return;
    }

    watchedFile = new File(filePath);
    try {
        //
        //   attempt to invoke JDK 1.4's File.toURI and URI.toURL methods
        //       which do a better job of escaping file names than File.toURL.
        Object uri = File.class.getMethod("toURI", null).invoke(watchedFile, null);
        watchedURL = (URL) uri.getClass().getMethod("toURL", null).invoke(uri, null);
    } catch(Exception ex) {
        try {
            watchedURL = watchedFile.toURL();
        } catch(java.net.MalformedURLException ex2) {
            this.getLogger().error("Watchdog {} unable to express filename {} as a URL",
              this.getName(), watchedFile.getName());
        }
    }
    super.activateOptions();
  }

  /**
   * Returns the modification of the file being watched.
   * 
   * @return The modification time of the file.
   */
  public long getModificationTime() {
    return watchedFile.lastModified();
  }

  /**
   * Reconfigures the log4j environment using the file as the source of the
   * configuration data.
   */
  public void reconfigure() {
    if (watchedFile.exists() && watchedURL != null) {
        reconfigureByURL(watchedURL);
    } else {
        this.getLogger().warn("{} watchdog cannot find file {}",
          this.getName(), watchedFile.getName());
    }
  }
}
