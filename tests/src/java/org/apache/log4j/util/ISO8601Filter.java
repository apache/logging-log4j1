
package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class ISO8601Filter implements Filter {

  Perl5Util util = new Perl5Util();

  
  
  public 
  String filter(String in) {
    String pat = "/"+ISO8601_PAT +"/";

    if(util.match(pat, in)) {    
      return util.substitute("s/"+ISO8601_PAT+"//", in);
    } else {
      return in;
    }
  }
}
