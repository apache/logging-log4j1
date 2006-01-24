/*
 * Copyright 1999-2006 The Apache Software Foundation.
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
      if(line != null) {
	output.println(line);
      }
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
