/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.
 */
package examples.lf5.InitUsingDefaultConfigurator;

import org.apache.log4j.lf5.DefaultLF5Configurator;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.util.LogMonitorAdapter;
import org.apache.log4j.Category;
import org.apache.log4j.NDC;

import java.io.IOException;

/**
 * This class is a simple example of how to configure the LogFactor5
 * logging window using the DefaultLF5Configurator.
 *
 * The DefaultLF5Configurator uses a default configuration file stored
 * in the lf5.jar in order to provide a default configuration for
 * the LF5Appender.
 *
 * To make this example work, ensure that the lf5.jar and lf5-license.jar
 * files are in your classpath, and then run the example at the command line.
 *
 * @author Brent Sprecher
 */

// Contributed by ThoughtWorks Inc.

public class InitUsingDefaultConfigurator {
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
      Category.getInstance(InitUsingDefaultConfigurator.class);

  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    // Configure the LF5Appender using the DefaultLF5Configurator.  This
    // will add the LF5Appender to the root of the Category tree.
    DefaultLF5Configurator.configure();

    // Add an NDC to demonstrate how NDC information is output.
    NDC.push("#23856");
    // Log some information.
    for (int i = 0; i < 10; i++) {
      cat.debug("Hello, my name is Homer Simpson.");
      cat.info("Mmmmmm .... Chocolate.");
      cat.warn("Mmm...forbidden donut.");
    }
    // Clean up NDC
    NDC.pop();
    NDC.remove();

    NDC.push("Another NDC");
    // Log some information.
    cat.fatal("Hello, my name is Bart Simpson.");
    cat.error("Hi diddly ho good neighbour.");
    // Clean up NDC
    NDC.pop();
    NDC.remove();

    // Call methods on both classes.
    InitUsingDefaultConfigurator.foo();
    InnerInitUsingDefaultConfigurator.foo();

    cat.info("Exiting InitUsingDefaultConfigurator.");

  }

  public static void foo() {
    cat.debug("Entered foo in InitUsingDefaultConfigurator class");

    NDC.push("#123456");
    cat.debug("Hello, my name is Marge Simpson.");
    cat.info("D'oh!! A deer! A female deer.");
    // Clean up NDC
    NDC.pop();
    NDC.remove();
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

  public static class InnerInitUsingDefaultConfigurator {
    static Category cat =
        Category.getInstance(InnerInitUsingDefaultConfigurator.class.getName());

    static void foo() throws IOException {
      // Configure the LF5Appender again. You can call
      // DefaultLF5Configurator.configure() as often as you want
      // without unexpected behavior.
      DefaultLF5Configurator.configure();

      cat.info("Entered foo in InnerInitUsingDefaultConfigurator class.");
    }
  }
}





