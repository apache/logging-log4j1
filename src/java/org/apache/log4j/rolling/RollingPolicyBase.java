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

package org.apache.log4j.rolling;

import org.apache.log4j.rolling.helper.Compress;
import org.apache.log4j.rolling.helper.FileNamePattern;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.OptionHandler;


/**
 * Implements methods common to most, it not all, rolling
 * policies. Currently such methods are limited to a compression mode
 * getter/setter.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 */
public abstract class RollingPolicyBase extends ComponentBase
        implements RollingPolicy, OptionHandler {
  protected int compressionMode = Compress.NONE;
  protected FileNamePattern fileNamePattern;
  protected String fileNamePatternStr;
  protected String activeFileName;

  /*
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  public abstract void activateOptions();

  /**
   * Given the FileNamePattern string, this method determines the compression
   * mode depending on last letters of the fileNamePatternStr. Patterns
   * ending with .gz imply GZIP compression, endings with '.zip' imply
   * ZIP compression. Otherwise and by default, there is no compression.
   *
   */
  protected void determineCompressionMode() {
     if (fileNamePatternStr.endsWith(".gz")) {
      getLogger().debug("Will use gz compression");
      compressionMode = Compress.GZ;
    } else if (fileNamePatternStr.endsWith(".zip")) {
      getLogger().debug("Will use zip compression");
      compressionMode = Compress.ZIP;
    } else {
      getLogger().debug("No compression will be used");
      compressionMode = Compress.NONE;
    }
  }

  public void setFileNamePattern(String fnp) {
    fileNamePatternStr = fnp;
  }

  public String getFileNamePattern() {
    return fileNamePatternStr;
  }

  /**
   * ActiveFileName can be left unset, i.e. as null.
   * @see #getActiveFileName
   */
  public void setActiveFileName(String afn) {
    activeFileName = afn;
  }
}
