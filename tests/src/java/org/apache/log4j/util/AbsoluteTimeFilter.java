
package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class AbsoluteTimeFilter implements Filter {

  Perl5Util util = new Perl5Util();
  
  public 
  String filter(String in) {
    String pat = "/"+Filter.ABSOLUTE_TIME_PAT+"/";

    if(util.match(pat, in)) {    
      return util.substitute("s/"+Filter.ABSOLUTE_TIME_PAT+"//", in);
    } else {
      return in;
    }
  }
}
