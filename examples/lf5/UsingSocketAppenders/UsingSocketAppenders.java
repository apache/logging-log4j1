/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 */

package examples.lf5.UsingSocketAppenders;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.URL;

/**
 * This is another simple example of how to use the LogFactor5
 * logging console.
 *
 * The LF5Appender is the primary class that enables logging to the
 * LogFactor5 logging window. If the following line is added to a properties
 * file, the LF5Appender will be appended to the root category when
 * the properties file is loaded:
 *
 *    log4j.appender.A1=org.apache.log4j.lf5.LF5Appender
 *
 * To make this example work, you must ensure that the example.properties file
 * is in your classpath.You can then run the example at the command line.
 *
 * @author Brent Sprecher
 */

// Contributed by ThoughtWorks Inc.

public class UsingSocketAppenders {
  //--------------------------------------------------------------------------
  //   Constants:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Protected Variables:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Private Variables:
  //--------------------------------------------------------------------------

  private static Category cat1 =
      Category.getInstance(UsingSocketAppenders.class);
  private static Category cat2 =
      Category.getInstance("TestClass.Subclass");
  private static Category cat3 =
      Category.getInstance("TestClass.Subclass.Subclass");
  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static void main(String argv[]) {
    // Use a PropertyConfigurator to initialize from a property file.
    String resource =
        "/examples/lf5/UsingSocketAppenders/socketclient.properties";
    URL configFileResource =
        UsingSocketAppenders.class.getResource(resource);
    PropertyConfigurator.configure(configFileResource);

    // Add a bunch of logging statements ...
    cat1.debug("Hello, my name is Homer Simpson.");
    cat1.debug("Hello, my name is Lisa Simpson.");
    cat2.debug("Hello, my name is Marge Simpson.");
    cat2.debug("Hello, my name is Bart Simpson.");
    cat3.debug("Hello, my name is Maggie Simpson.");

    cat2.info("We are the Simpsons!");
    cat2.info("Mmmmmm .... Chocolate.");
    cat3.info("Homer likes chocolate");
    cat3.info("Doh!");
    cat3.info("We are the Simpsons!");

    cat1.warn("Bart: I am through with working! Working is for chumps!" +
        "Homer: Son, I'm proud of you. I was twice your age before " +
        "I figured that out.");
    cat1.warn("Mmm...forbidden donut.");
    cat1.warn("D'oh! A deer! A female deer!");
    cat1.warn("Truly, yours is a butt that won't quit." +
        "- Bart, writing as Woodrow to Ms. Krabappel.");

    cat2.error("Dear Baby, Welcome to Dumpsville. Population: you.");
    cat2.error("Dear Baby, Welcome to Dumpsville. Population: you.",
        new IOException("Dumpsville, USA"));
    cat3.error("Mr. Hutz, are you aware you're not wearing pants?");
    cat3.error("Mr. Hutz, are you aware you're not wearing pants?",
        new IllegalStateException("Error !!"));


    cat3.fatal("Eep.");

    cat3.fatal("Mmm...forbidden donut.",
        new SecurityException("Fatal Exception ... "));

    cat3.fatal("D'oh! A deer! A female deer!");
    cat2.fatal("Mmmmmm .... Chocolate.",
        new SecurityException("Fatal Exception"));

    // Put the main thread is put to sleep for 5 seconds to allow the
    // SocketServer to process all incoming messages before the Socket is
    // closed. This is done to overcome some basic limitations with the
    // way the SocketServer and SocketAppender classes manage sockets.
    try {
      Thread.currentThread().sleep(5000);
    } catch (InterruptedException ie) {
    }

  }

  //--------------------------------------------------------------------------
  //   Protected Methods:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Private Methods:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Nested Top-Level Classes or Interfaces:
  //--------------------------------------------------------------------------

}
