
package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class ControlFilter implements Filter {

  Perl5Util util = new Perl5Util();

  String[] allowedPatterns;

  public ControlFilter(String[] allowedPatterns) {
    this.allowedPatterns = allowedPatterns;
  }

  public 
  String filter(String in) throws UnexpectedFormatException{
    int len = allowedPatterns.length;
    for(int i = 0; i < len; i++) {
      if(util.match("/"+allowedPatterns[i]+"/", in)) {
	return in;
      }	
    }

    throw new UnexpectedFormatException(in);
  }
}
