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

package org.apache.log4j.rolling.helper;

import org.apache.log4j.Logger;

import java.io.*;

import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * The <code>Compression</code> class implements ZIP and GZ
 * file compression/decompression methods.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @since 1.3
 */
public class Compress {
  static final Logger logger = Logger.getLogger(Compress.class);
  public static final int NONE = 0;
  public static final int GZ = 1;
  public static final int ZIP = 2;
  /**
   * String constant representing no compression. The value of this
   * constant is "NONE".
   */
  public static final String NONE_STR = "NONE";

  /**
   * String constant representing compression in the GZIP format. The
   * value of this constant is "GZ".
   */
  public static final String GZ_STR = "GZ";

  /**
   * String constant representing compression in the ZIP format. The
   * value of this constant is "ZIP".
   */
  public static final String ZIP_STR = "ZIP";

  public static void ZIPCompress(String nameOfFile2zip) {
    // Here we rely on the fact that the two argument version of ZIPCompress 
    // automaticallys add a .zip extension to the second argument 
    GZCompress(nameOfFile2zip, nameOfFile2zip);
  }
  
  public static void ZIPCompress(
    String nameOfFile2zip, String nameOfZippedFile) {
    File file2zip = new File(nameOfFile2zip);

    if (!file2zip.exists()) {
      logger.warn(
        "The file to compress named [" + nameOfFile2zip + "] does not exist.");

      return;
    }

    if (!nameOfZippedFile.endsWith(".zip")) {
      nameOfZippedFile = nameOfZippedFile + ".zip";
    }

    File zippedFile = new File(nameOfZippedFile);

    if (zippedFile.exists()) {
      logger.warn(
        "The target compressed file named [" + nameOfZippedFile
        + "] exist already.");

      return;
    }

    try {
      FileOutputStream fos = new FileOutputStream(nameOfZippedFile);
      ZipOutputStream zos = new ZipOutputStream(fos);
      FileInputStream fis = new FileInputStream(nameOfFile2zip);

      ZipEntry zipEntry = new ZipEntry(file2zip.getName());
      zos.putNextEntry(zipEntry);

      byte[] inbuf = new byte[8102];
      int n;

      while ((n = fis.read(inbuf)) != -1) {
        zos.write(inbuf, 0, n);
      }

      fis.close();
      zos.close();

      if (!file2zip.delete()) {
        logger.warn("Could not delete [" + nameOfFile2zip + "].");
      }
    } catch (Exception e) {
      logger.error(
        "Error occured while compressing [" + nameOfFile2zip + "] into ["
        + nameOfZippedFile + "].", e);
    }
  }

  public static void GZCompress(String nameOfFile2gz) {
    // Here we rely on the fact that the two argument version of GZCompress 
    // automatically adds a .gz extension to the second argument 
    GZCompress(nameOfFile2gz, nameOfFile2gz);
  }

  public static void GZCompress(String nameOfFile2gz, String nameOfgzedFile) {
    File file2gz = new File(nameOfFile2gz);

    if (!file2gz.exists()) {
      logger.warn(
        "The file to compress named [" + nameOfFile2gz + "] does not exist.");

      return;
    }

    if (!nameOfgzedFile.endsWith(".gz")) {
      nameOfgzedFile = nameOfgzedFile + ".gz";
    }

    File gzedFile = new File(nameOfgzedFile);

    if (gzedFile.exists()) {
      logger.warn(
        "The target compressed file named [" + nameOfgzedFile
        + "] exist already.");

      return;
    }

    try {
      FileOutputStream fos = new FileOutputStream(nameOfgzedFile);
      GZIPOutputStream gzos = new GZIPOutputStream(fos);
      FileInputStream fis = new FileInputStream(nameOfFile2gz);
      byte[] inbuf = new byte[8102];
      int n;

      while ((n = fis.read(inbuf)) != -1) {
        gzos.write(inbuf, 0, n);
      }

      fis.close();
      gzos.close();

      if (!file2gz.delete()) {
        logger.warn("Could not delete [" + nameOfFile2gz + "].");
      }
    } catch (Exception e) {
      logger.error(
        "Error occured while compressing [" + nameOfFile2gz + "] into ["
        + nameOfgzedFile + "].", e);
    }
  }
}
