/*
 * Copyright 1999-2005 The Apache Software Foundation.
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

package org.apache.log4j.test;

import java.io.*;
import org.apache.log4j.config.PropertyPrinter;

/**
   Prints the configuration of the log4j default hierarchy
   (which needs to be auto-initialized) as a propoperties file
   on System.out.
   
   @author  Anders Kristensen
 */
public class PrintProperties {
  public
  static
  void main(String[] args) {
    new PropertyPrinter(new PrintWriter(System.out), true);
  }
}