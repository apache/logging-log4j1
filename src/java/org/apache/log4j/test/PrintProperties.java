/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.
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