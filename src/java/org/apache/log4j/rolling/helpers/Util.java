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

package org.apache.log4j.rolling.helpers;

import org.apache.log4j.Logger;

import java.io.File;


/**
 * @author Ceki
 *
 */
public class Util {
  static Logger logger = Logger.getLogger(Util.class);

  public static void rename(String from, String to) {
    File fromFile = new File(from);

    if (fromFile.exists()) {
      File toFile = new File(to);
      logger.debug("Renaming file [" + fromFile + "] to [" + toFile + "]");

      boolean result = fromFile.renameTo(toFile);

      if (!result) {
        logger.debug(
          "Failed to rename file [" + fromFile + "] to [" + toFile + "].");
      }
    }
  }
}
