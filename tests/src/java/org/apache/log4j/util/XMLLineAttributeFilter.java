/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.util;

import java.io.*;

import org.apache.oro.text.perl.Perl5Util;

public class XMLLineAttributeFilter implements Filter {

  Perl5Util util = new Perl5Util();

  public 
  String filter(String in) {
    if(util.match("/line=\"\\d{1,3}\"/", in)) {
      return util.substitute("s/line=\"\\d{1,3}\"/line=\"X\"/", in);
    } else if(util.match("/line=\"?\"/", in)) {
      return util.substitute("s/line=\"?\"/line=\"X\"/", in);
    } else {
      return in;
    }
  }
}
