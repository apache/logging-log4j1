/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.apache.log4j.test; 

import org.apache.log4j.*;
import org.apache.log4j.spi.*;
//import org.apache.log4j.xml.examples.XPriority;

/** 
   This class is a shallow test of the various appenders and
   layouts. It also tests their reading of the configuration file.
   @author  Ceki G&uuml;lc&uuml;
*/
public class FQCNTest {

  static Category cat = Category.getInstance("dddd");
  
  public 
  static 
  void main(String argv[]) throws Exception  {
    if(argv.length == 1) 
      init(argv[0]);
    else 
      usage("Wrong number of arguments.");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java "+ FQCNTest.class.getName()+"outputFile");
    System.exit(1);
  } 

  static 
  void init(String file) throws Exception {
    Layout layout = new PatternLayout("%p %c (%C{2}#%M) - %m%n");
    FileAppender appender = new FileAppender(layout, file, false);
    appender.setLayout(layout);
    Category root = Category.getRoot();
    root.addAppender(appender);
  }


  static
  void test() {
    X1Category x1 = X1Category.getLogger("x1");
    x1.debug("hello");    
    x1.debug1("hello");  
    x1.debug2("hello");  
  }  
}


// ==========================================================================
// ==========================================================================
// ==========================================================================

class X1Category extends Category {
  static String FQCN = X1Category.class.getName() + ".";

  private static X1CategoryFactory factory = new X1CategoryFactory();

  public X1Category(String name) {
    super(name);
  }

  public 
  void debug1(Object message) {    
    super.log(FQCN, Priority.DEBUG, message + " world.", null);    
  }

  public
  void debug2(Object message) {
    super.log(FQCN, Priority.DEBUG, message, null); 
  }

  
  protected
  String getFQCN() {
    return X1Category.FQCN;
  }

  public 
  static
  X1Category getLogger(String name) {
    return ((X1Category) Category.getInstance(name, factory)); 
  }
}

class X1CategoryFactory implements CategoryFactory {

  public
  X1CategoryFactory() {
  }
  
  public
  Category makeNewCategoryInstance(String name) {
    return new X1Category(name);
  }
}
