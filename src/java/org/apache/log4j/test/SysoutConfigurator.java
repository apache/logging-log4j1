package org.apache.log4j.test;

import java.util.Properties;
import org.apache.log4j.Category;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.FileAppender;

/**
 * This configurator simply always adds a FileAppender writing to
 * System.out to the root Category and ignores whatever is in the
 * properties file.
 */
public class SysoutConfigurator implements Configurator {
  public
  void
  doConfigure(java.net.URL url, Hierarchy hierarchy) {
    Category.getRoot().addAppender(
        new FileAppender(
            new SimpleLayout(), System.out));
  }
}
