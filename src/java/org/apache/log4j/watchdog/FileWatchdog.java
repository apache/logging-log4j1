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

package org.apache.log4j.watchdog;

import java.io.File;
import java.net.URL;

/**
 * Implements a watchdog to watch a file.  When the file changes, determined by
 * a change in the file's modification date, the contents of the file are use to
 * reconfigure the log4j environment.
 */
public class FileWatchdog extends TimedURLWatchdog {

  /** The file being watched. */
  private File watchedFile;
  
  /**
   * Sets up the reference to the file being watched, then calls the version
   * in the super class.
   */
  public void activateOptions() {

    if (watchedURL == null) {
      getLogger().error("watchdog \"{}\" not configured with URL to watch",
        this.getName());
      return;
    }

    watchedFile = new File(watchedURL.getFile());
    super.activateOptions();
  }
  
  /**
   * @return The modification time of the file.
   */
  public long getModificationTime() {
    return watchedFile.lastModified();
  }
}
