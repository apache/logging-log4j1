/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 */
package examples.lf5.InitUsingPropertiesFile;

import org.apache.log4j.lf5.LogLevel;
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

public class InitUsingPropertiesFile {
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
      Category.getInstance(InitUsingPropertiesFile.class);

  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static void main(String argv[]) {
    // Use a PropertyConfigurator to initialize from a property file.
    String resource =
        "/examples/lf5/InitUsingPropertiesFile/example.properties";
    URL configFileResource =
        InitUsingPropertiesFile.class.getResource(resource);
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
