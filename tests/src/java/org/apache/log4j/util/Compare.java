/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Compare {

  static final int B1_NULL = -1;
  static final int B2_NULL = -2;
  
  static 
  public
  boolean compare(String file1, String file2) throws FileNotFoundException, 
                                                            IOException {
    BufferedReader in1 = new BufferedReader(new FileReader(file1));
    BufferedReader in2 = new BufferedReader(new FileReader(file2));
    
    String s1;
    int lineCounter = 0;
    while((s1 = in1.readLine()) != null) {
      lineCounter++;
      String s2 = in2.readLine();
      if(!s1.equals(s2)) {
        System.out.println("Files ["+file1+"] and ["+file2+"] differ on line " 
			   +lineCounter);
      	System.out.println("One reads:  ["+s1+"].");
      	System.out.println("Other reads:["+s2+"].");
      	outputFile(file1);
      	outputFile(file2);
      	return false;
      }
    }
    
    // the second file is longer
    if(in2.read() != -1) {
      System.out.println("File ["+file2+"] longer than file ["+file1+"].");
    	outputFile(file1);
    	outputFile(file2);
      return false;
    }
    
    return true;
  }
  
  private static void outputFile(String file) throws FileNotFoundException, 
                                                            IOException {
    BufferedReader in1 = new BufferedReader(new FileReader(file));
    
    String s1;
    int lineCounter = 0;
    System.out.println("--------------------------------");
    System.out.println("Contents of " + file + ":");
    while((s1 = in1.readLine()) != null) {
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
  
}
