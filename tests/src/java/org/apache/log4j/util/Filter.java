/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.util;

public interface Filter {
  
  final String BASIC_PAT = "\\[main\\] (FATAL|ERROR|WARN|INFO|DEBUG)";
  final String ISO8601_PAT = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}";  

  // 06 avr. 2002 18:36:32,036
  // 18 fevr. 2002 20:05:36,222
  static public final String ABSOLUTE_DATE_AND_TIME_PAT = 
                           "^\\d{1,2} .{2,6}\\.? 200\\d \\d{2}:\\d{2}:\\d{2},\\d{3}";

  // 18:54:19,201
  static public final String ABSOLUTE_TIME_PAT = 
                           "^\\d{2}:\\d{2}:\\d{2},\\d{3}";

  static public final String RELATIVE_TIME_PAT = "^\\d{1,10}";


  String filter(String in) throws UnexpectedFormatException;
}
