/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Compare {

  static 
  public
  boolean compare(String file1, String file2) throws FileNotFoundException, 
                                                            IOException {
    InputStream in1 = new FileInputStream(file1);
    InputStream in2 = new FileInputStream(file2);
    
    int b1;
    while((b1 = in1.read()) != -1) {
      int b2 = in2.read();
      if(b2 != b1) {
	System.out.println("Files ["+file1+"] and ["+file2+"] differ.");
	return false;
      }
    }
    
    // the second file is longer
    if(in2.read() != -1) {
      System.out.println("File ["+file2+"] longer than file ["+file1+"].");
      return false;
    }
    

    return true;
  }

}
