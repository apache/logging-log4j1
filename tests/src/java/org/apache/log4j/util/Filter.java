
package org.apache.log4j.util;

public interface Filter {
  
  final String BASIC_PAT = "\\[main\\] (FATAL|ERROR|WARN|INFO|DEBUG)";
  final String ISO8601_PAT = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} ";

  String filter(String in) throws UnexpectedFormatException;
}
