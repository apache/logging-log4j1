/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 */
package examples.lf5.InitUsingLog4JProperties;

import org.apache.log4j.Category;

import java.io.IOException;

/**
 * This class is a simple example of how to use the LogFactor5 logging
 * window.
 *
 * The LF5Appender is the primary class that enables logging to the
 * LogFactor5 logging window. The simplest method of using this Appender
 * is to add the following line to your log4j.properties file:
 *
 *    log4j.appender.A1=org.apache.log4j.lf5.LF5Appender
 *
 * The log4j.properties file MUST be in you system classpath. If this file
 * is in your system classpath, a static initializer in the Category class
 * will load the file during class initialization. The LF5Appender will be
 * added to the root category of the Category tree.
 *
 * To make this example work, ensure that the lf5.jar and lf5-license.jar
 * files are in your classpath. Next, create a log4j.properties file and
 * add this line to it, or add this line to your existing log4j.properties
 * file. Run the example at the command line and explore the results!
 *
 * @author Brent Sprecher
 */

// Contributed by ThoughtWorks Inc.

public class InitUsingLog4JProperties {
  //--------------------------------------------------------------------------
  //   Constants:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Protected Variables:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Private Variables:
  //--------------------------------------------------------------------------

  private static Category cat =
      Category.getInstance(InitUsingLog4JProperties.class);

  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static void main(String argv[]) {
    // Add a bunch of logging statements ...
    cat.debug("Hello, my name is Homer Simpson.");
    cat.debug("Hello, my name is Lisa Simpson.");
    cat.debug("Hello, my name is Marge Simpson.");
    cat.debug("Hello, my name is Bart Simpson.");
    cat.debug("Hello, my name is Maggie Simpson.");

    cat.info("We are the Simpsons!");
    cat.info("Mmmmmm .... Chocolate.");
    cat.info("Homer likes chocolate");
    cat.info("Doh!");
    cat.info("We are the Simpsons!");

    cat.warn("Bart: I am through with working! Working is for chumps!" +
        "Homer: Son, I'm proud of you. I was twice your age before " +
        "I figured that out.");
    cat.warn("Mmm...forbidden donut.");
    cat.warn("D'oh! A deer! A female deer!");
    cat.warn("Truly, yours is a butt that won't quit." +
        "- Bart, writing as Woodrow to Ms. Krabappel.");

    cat.error("Dear Baby, Welcome to Dumpsville. Population: you.");
    cat.error("Dear Baby, Welcome to Dumpsville. Population: you.",
        new IOException("Dumpsville, USA"));
    cat.error("Mr. Hutz, are you aware you're not wearing pants?");
    cat.error("Mr. Hutz, are you aware you're not wearing pants?",
        new IllegalStateException("Error !!"));


    cat.fatal("Eep.");
    cat.fatal("Mmm...forbidden donut.",
        new SecurityException("Fatal Exception"));
    cat.fatal("D'oh! A deer! A female deer!");
    cat.fatal("Mmmmmm .... Chocolate.",
        new SecurityException("Fatal Exception"));
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
