/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class Transformer {

  public 
  static 
  void transform(String in, String out, Filter[] filters) throws FileNotFoundException, 
                                                                 IOException,
                                                                 UnexpectedFormatException {

    Perl5Util util = new Perl5Util();
    String line;
    BufferedReader input = new BufferedReader(new FileReader(in));
    PrintStream output = new PrintStream(new FileOutputStream(out, false));
  
    // Initialization of input and output omitted
    while((line = input.readLine()) != null) {
      // apply all filters
      for(int i = 0; i < filters.length; i++) {
	line = filters[i].filter(line);
      }
      output.println(line);
    }
  }



  public 
  static 
  void transform(String in, String out, Filter filter) throws FileNotFoundException, 
                                                              IOException,
                                                              UnexpectedFormatException {

    Perl5Util util = new Perl5Util();
    String line;
    BufferedReader input = new BufferedReader(new FileReader(in));
    PrintStream output = new PrintStream(new FileOutputStream(out));
  
    // Initialization of input and output omitted
    while((line = input.readLine()) != null) {
      line = filter.filter(line);
      output.println(line);
    }
  }

}
