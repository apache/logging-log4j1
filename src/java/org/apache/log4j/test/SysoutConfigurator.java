package org.apache.log4j.test;

import org.apache.log4j.spi.*;
import org.apache.log4j.*;

/**
 * This configurator simply always adds a FileAppender writing to
 * System.out to the root Category and ignores whatever is in the
 * properties file.
 */
public class SysoutConfigurator implements Configurator {
  public
  void
  doConfigure(java.net.URL url,  LoggerRepository hierarchy) {
    Logger.getRootLogger().addAppender(
        new ConsoleAppender(
            new SimpleLayout(), ConsoleAppender.SYSTEM_OUT));
  }
}
