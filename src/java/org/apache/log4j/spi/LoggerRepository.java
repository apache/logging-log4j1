
package org.apache.log4j.spi;

import org.apache.log4j.*;
import java.util.Enumeration;

public interface LoggerRepository {

  public
  Logger getLogger(String name);

  public
  Logger getLogger(String name, LoggerFactory factory);

  
  public
  Logger getRootLogger();

  public
  Logger exists(String name);

  public
  void shutdown();
  
  public
  Enumeration getCurrentLoggers();

  public
  void fireAddAppenderEvent(Category cat, Appender appender);

  public
  void resetConfiguration();

}
