
package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class AbsoluteTDFFilter implements Filter {

  Perl5Util util = new Perl5Util();

  String datePat;

  // 18 fevr. 2002 20:05:36,222
  static public final String ADAT_PAT = "^\\d{1,2} .{2,6} 200\\d \\d{2}:\\d{2}:\\d{2},\\d{3}";

  public AbsoluteTDFFilter(String datePat) {
    this.datePat = datePat;
  }

  public 
  String filter(String in) {
    String pat = "/"+datePat+"/";

    if(util.match(pat, in)) {    
      return util.substitute("s/"+datePat+"//", in);
    } else {
      return in;
    }
  }
}
