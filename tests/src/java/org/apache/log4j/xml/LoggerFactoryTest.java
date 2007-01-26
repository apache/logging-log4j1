package org.apache.log4j.xml;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.LoggerFactory;

import junit.framework.TestCase;

public class LoggerFactoryTest extends TestCase {
  
  static boolean pass = false;
  
  public static class Factory implements LoggerFactory {

    public Logger makeNewLoggerInstance(String name) {
      pass = true;
      return new MyLogger(name);
    }
    
  }
  
  public static class MyLogger extends Logger {

    protected MyLogger(String name) {
      super(name);
    }
    
  }
  
  public void testSelectLogFactory()
  {
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure("input/xml/loggerFactory1.xml", LogManager.getLoggerRepository());
    Logger l = Logger.getLogger("x");
    assertEquals(MyLogger.class, l.getClass());
    assertEquals(true, pass);
    pass = false;
    
    jc.doConfigure("input/xml/loggerFactory2.xml", LogManager.getLoggerRepository());
    Logger.getLogger("xy");
    assertEquals(true, pass);
  }
  
}
