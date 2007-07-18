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

package org.apache.log4j.rolling.helper;

import org.apache.log4j.ULogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.GZIPOutputStream;


/**
 * Compresses a file using GZ compression.
 *
 * @author Curt Arnold
 * @since 1.3
 */
public final class GZCompressAction extends ActionBase {
  /**
   * Source file.
   */
  private final File source;

  /**
   * Destination file.
   */
  private final File destination;

  /**
   * If true, attempt to delete file on completion.
   */
  private final boolean deleteSource;

  /**
   * Logger to receive diagnostic messages.
   */
  private final ULogger logger;

  /**
   * Create new instance of GZCompressAction.
   *
   * @param source file to compress, may not be null.
   * @param destination compressed file, may not be null.
   * @param deleteSource if true, attempt to delete file on completion.  Failure to delete
   * does not cause an exception to be thrown or affect return value.
   * @param logger logger, may be null.
   */
  public GZCompressAction(
    final File source, final File destination, final boolean deleteSource,
    final ULogger logger) {
    if (source == null) {
      throw new NullPointerException("source");
    }

    if (destination == null) {
      throw new NullPointerException("destination");
    }

    this.source = source;
    this.destination = destination;
    this.deleteSource = deleteSource;
    this.logger = logger;
  }

  /**
   * Compress.
   * @return true if successfully compressed.
   * @throws IOException on IO exception.
   */
  public boolean execute() throws IOException {
    return execute(source, destination, deleteSource, logger);
  }

  /**
   * Compress a file.
   *
   * @param source file to compress, may not be null.
   * @param destination compressed file, may not be null.
   * @param deleteSource if true, attempt to delete file on completion.  Failure to delete
   * does not cause an exception to be thrown or affect return value.
   * @param logger logger, may be null.
   * @return true if source file compressed.
   * @throws IOException on IO exception.
   */
  public static boolean execute(
    final File source, final File destination, final boolean deleteSource,
    final ULogger logger) throws IOException {
    if (source.exists()) {
      FileInputStream fis = new FileInputStream(source);
      FileOutputStream fos = new FileOutputStream(destination);
      GZIPOutputStream gzos = new GZIPOutputStream(fos);
      byte[] inbuf = new byte[8102];
      int n;

      while ((n = fis.read(inbuf)) != -1) {
        gzos.write(inbuf, 0, n);
      }

      gzos.close();
      fis.close();

      if (deleteSource) {
        if (!source.delete() && (logger != null)) {
          logger.info("Unable to delete {}.", source.toString());
        }
      }

      return true;
    }

    return false;
  }


    /**
     * Capture exception.
     *
     * @param ex exception.
     */
    protected void reportException(final Exception ex) {
        if (logger != null) {
            logger.info("Exception during compression of '" + source.toString() + "'.", ex);
        }
    }

}
