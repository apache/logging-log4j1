package org.log4j.test;

import java.util.Properties;
import org.log4j.Category;
import org.log4j.spi.Configurator;
import org.log4j.spi.LoggingEvent;
import org.log4j.SimpleLayout;
import org.log4j.FileAppender;

/**
 * This configurator simply always adds a FileAppender writing to
 * System.out to the root Category and ignores whatever is in the
 * properties file.
 */
public class SysoutConfigurator implements Configurator {
  public
  void
  doConfigure(java.net.URL url) {
    Category.getRoot().addAppender(
        new FileAppender(
            new SimpleLayout(), System.out));
  }
}
