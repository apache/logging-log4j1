
package org.apache.log4j.spi;

import org.apache.log4j.*;
import org.apache.log4j.or.RendererMap;
import java.util.Enumeration;

public interface LoggerRepository {

  public 
  void addHierarchyEventListener(HierarchyEventListener listener);

  boolean isDisabled(int level);

  public
  void disable(String val);

  public 
  void enable(Level level);

  public
  void emitNoAppenderWarning(Category cat);
  
  public
  Level getEnable();

  public
  Logger getLogger(String name);

  public
  Logger getLogger(String name, LoggerFactory factory);

  public
  Logger getRootLogger();

  public
  abstract
  Logger exists(String name);

  public
  abstract
  void shutdown();
  
  public
  Enumeration getCurrentLoggers();

  /**
     @deprecated Please use {@link getCurrentLoggers} instead.
   */
  public
  Enumeration getCurrentCategories();


  public
  abstract
  void fireAddAppenderEvent(Category logger, Appender appender);

  public
  abstract
  void resetConfiguration();

}
