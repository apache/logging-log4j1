/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
// NOTICE: Some tests are sensitive to line numbers!
package org.apache.log4j.test;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.LogManager;
import org.apache.log4j.NDC;
import org.apache.log4j.Level;
/**
   This class is a shallow test of the various appenders and
   layouts. It also tests their reading of the configuration file.
   @author  Ceki G&uuml;lc&uuml;
*/
public class Shallow {

  static Category cat = Category.getInstance(Shallow.class);

  public
  static
  void main(String argv[]) {
    if(argv.length == 1)
      init(argv[0]);
    else
      usage("Wrong number of arguments.");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java "+ Shallow.class.getName()+"configFile");
    System.exit(1);
  }

  static
  void init(String configFile) {
    if(configFile.endsWith(".xml"))
      DOMConfigurator.configure(configFile);
    else
      PropertyConfigurator.configure(configFile);
  }

  static
  void test() {
    int i = -1;
    NDC.push("NDC");
    Category root = Category.getRoot();
    cat.debug("Message " + ++i);
    root.debug("Message " + i);

    cat.info ("Message " + ++i);
    root.info("Message " + i);

    cat.warn ("Message " + ++i);
    root.warn("Message " + i);

    cat.error("Message " + ++i);
    root.error("Message " + i);

    cat.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);

    Exception e = new Exception("Just testing");
    cat.debug("Message " + ++i, e);
    root.debug("Message " + i, e);

    cat.info("Message " + ++i, e);
    root.info("Message " + i, e);

    cat.warn("Message " + ++i , e);
    root.warn("Message " + i , e);

    cat.error("Message " + ++i, e);
    root.error("Message " + i, e);

    cat.log(Level.FATAL, "Message " + ++i, e);
    root.log(Level.FATAL, "Message " + i, e);

    root.setLevel(Level.FATAL);

    // It is always a good idea to call this method when exiting an
    // application.
    LogManager.shutdown();
  }


  static
  void delay(int amount) {
    try {
      Thread.sleep(amount);
    }
    catch(Exception e) {}
  }
}
