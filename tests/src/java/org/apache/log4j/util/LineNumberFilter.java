
package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class LineNumberFilter implements Filter {

  Perl5Util util = new Perl5Util();

  public 
  String filter(String in) {
    if(util.match("/\\(.*:\\d{1,4}\\)/", in)) {
      return util.substitute("s/:\\d{1,4}\\)/:XXX)/", in);
    } else {
      return in;
    }
  }
}
