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

package org.apache.log4j.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;


public class Compare {
  static final int B1_NULL = -1;
  static final int B2_NULL = -2;

  public static boolean compare(String file1, String file2)
    throws FileNotFoundException, IOException {
    BufferedReader in1 = new BufferedReader(new FileReader(file1));
    BufferedReader in2 = new BufferedReader(new FileReader(file2));

    String s1;
    int lineCounter = 0;

    while ((s1 = in1.readLine()) != null) {
      lineCounter++;

      String s2 = in2.readLine();

      if (!s1.equals(s2)) {
        System.out.println(
          "Files [" + file1 + "] and [" + file2 + "] differ on line "
          + lineCounter);
        System.out.println("One reads:  [" + s1 + "].");
        System.out.println("Other reads:[" + s2 + "].");
        outputFile(file1);
        outputFile(file2);

        return false;
      }
    }

    // the second file is longer
    if (in2.read() != -1) {
      System.out.println(
        "File [" + file2 + "] longer than file [" + file1 + "].");
      outputFile(file1);
      outputFile(file2);

      return false;
    }

    return true;
  }

  /** 
   * 
   * Prints file on the console.
   *
   */
  private static void outputFile(String file)
    throws FileNotFoundException, IOException {
    BufferedReader in1 = new BufferedReader(new FileReader(file));

    String s1;
    int lineCounter = 0;
    System.out.println("--------------------------------");
    System.out.println("Contents of " + file + ":");

    while ((s1 = in1.readLine()) != null) {
      lineCounter++;
      System.out.print(lineCounter);

      if (lineCounter < 10) {
        System.out.print("   : ");
      } else if (lineCounter < 100) {
        System.out.print("  : ");
      } else if (lineCounter < 1000) {
        System.out.print(" : ");
      } else {
        System.out.print(": ");
      }

      System.out.println(s1);
    }
  }
  
    public static boolean gzCompare(String file1, String file2)
      throws FileNotFoundException, IOException {
      BufferedReader in1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file1))));      
      BufferedReader in2 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file2))));

      String s1;
      int lineCounter = 0;

      while ((s1 = in1.readLine()) != null) {
        lineCounter++;

        String s2 = in2.readLine();

        if (!s1.equals(s2)) {
          System.out.println(
            "Files [" + file1 + "] and [" + file2 + "] differ on line "
            + lineCounter);
          System.out.println("One reads:  [" + s1 + "].");
          System.out.println("Other reads:[" + s2 + "].");
          outputFile(file1);
          outputFile(file2);

          return false;
        }
      }

      // the second file is longer
      if (in2.read() != -1) {
        System.out.println(
          "File [" + file2 + "] longer than file [" + file1 + "].");
        outputFile(file1);
        outputFile(file2);

        return false;
      }

      return true;
    }
}
