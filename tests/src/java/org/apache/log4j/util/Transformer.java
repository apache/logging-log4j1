/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Transformer {

  public 
  static 
  void transform(String in, String out, Filter[] filters) throws IOException,
                                                                 UnexpectedFormatException {

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
  void transform(String in, String out, Filter filter) throws IOException,
                                                              UnexpectedFormatException {

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
