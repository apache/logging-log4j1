/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.test;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;


/**
   This class is used in testing the DailyRollingFileAppender.
   @author  Ceki G&uuml;lc&uuml;
*/
public class DRFATest {

  static Category cat = Category.getInstance(DRFATest.class);

  static int limit;

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
    System.err.println( "Usage: java "+ DRFATest.class.getName()+" length");
    System.exit(1);
  }

  static
  void init(String limitStr) {
    try {
      limit =  Integer.parseInt(limitStr);
    } catch(java.lang.NumberFormatException e) {
      usage("Could not convert "+limitStr+" to int");
    }
  }

  static
  void test() {

    Layout layout = new PatternLayout("%d{yyyy-MM-dd-HH-mm ss:SSS} %m%n");
    try {
      Appender appender = new DailyRollingFileAppender(layout, "test",
						       "'.'yyyy-MM-dd-HH-mm" );
      appender.setName("drfa");
      BasicConfigurator.configure(appender);
    } catch(Exception e) {
      System.err.println("Could not create DailyRollingFileAppender");
      e.printStackTrace();
    }

    System.out.println("Limit: "+limit);

    for(int i = 0; i < limit; i++) {
      System.out.println(i);
      cat.debug("Message"+ i);
      delay(10000);

      if((i % 7) == 0) {
	System.out.println("Sleeping 1min.");
	delay(60000);
	System.err.println("");
      }
    }
  }


  static
  void delay(int amount) {
    try {
      Thread.sleep(amount);
    }
    catch(Exception e) {}
  }
}

