/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 */
package examples.lf5.InitUsingMultipleAppenders;

import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.net.URL;

/**
 * This example shows how to use LogFactor5 with other Log4J appenders
 * (In this case the RollingFileAppender).
 *
 * The following lines can be added to the log4j.properties file or a
 * standard Java properties file.
 *
 *   # Two appenders are registered with the root of the Category tree.
 *
 *   log4j.rootCategory=, A1, R
 *
 *   # A1 is set to be a LF5Appender which outputs to a swing
 *   # logging console.
 *
 *   log4j.appender.A1=org.apache.log4j.lf5.LF5Appender
 *
 *   # R is the RollingFileAppender that outputs to a rolling log
 *   # file called rolling_log_file.log.
 *
 * log4j.appender.R=org.apache.log4j.RollingFileAppender
 * log4j.appender.R.File=rolling_log_file.log
 *
 * log4j.appender.R.layout=org.apache.log4j.PatternLayout
 * log4j.appender.R.layout.ConversionPattern=Date - %d{DATE}%nPriority
 * - %p%nThread - %t%nCategory - %c%nLocation - %l%nMessage - %m%n%n
 * log4j.appender.R.MaxFileSize=100KB
 * log4j.appender.R.MaxBackupIndex=1
 *
 * To make this example work, either run the InitUsingMultipleAppenders.bat
 * file located in the examples folder or run it at the command line. If you
 * are running the example at the command line, you must ensure that the
 * example.properties file is in your classpath.
 *
 * @author Brent Sprecher
 * @author Brad Marlborough
 */

// Contributed by ThoughtWorks Inc.

public class InitUsingMultipleAppenders {

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
      Category.getInstance(InitUsingMultipleAppenders.class);

  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static void main(String argv[]) {
    // Use a PropertyConfigurator to initialize from a property file.
    String resource =
        "/examples/lf5/InitUsingMultipleAppenders/example.properties";
    URL configFileResource =
        InitUsingMultipleAppenders.class.getResource(resource);
    PropertyConfigurator.configure(configFileResource);

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
