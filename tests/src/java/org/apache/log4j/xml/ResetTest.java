package org.apache.log4j.xml;

import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.joran.JoranConfigurator;

import junit.framework.TestCase;

public class ResetTest extends TestCase {
  
  Logger logx; 
  
  protected void setUp() {
    LogManager.getLoggerRepository().resetConfiguration();
    initx();
  }
  
  public void testResetXml() {
    test("tests/input/xml/defaultInit.xml", false);
    test("tests/input/xml/resetTest.xml", true);
  }
  
  private void initx() {
    logx = Logger.getLogger("x");
    logx.setLevel(Level.ERROR);
    assertEquals(Level.ERROR, logx.getLevel());
  }
  
  public void testResetProps() {
    Properties p = new Properties();
    p.put("log4j.rootLogger","DEBUG,C");
    p.put("log4j.appender.C","org.apache.log4j.ConsoleAppender");
    p.put("log4j.appender.C","layout=org.apache.log4j.SimpleLayout");
    testProperties(p, false);
    testProperties(p, true);
  }

  private void test(String file, boolean reset)
  {
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure(file, LogManager.getLoggerRepository());
    if (reset)
      assertEquals("reset=" + reset, null, logx.getLevel());
    else
      assertEquals(Level.ERROR, logx.getLevel());
  }
  
  private void testProperties(Properties p, boolean reset)
  {
    if (reset) {
      p.put("log4j.reset", "true");
    } else {
      p.remove("log4j.reset");
    }
    System.out.println(" --- test " + p);
    PropertyConfigurator.configure(p);
    if (reset)
      assertEquals("reset=" + reset, null, logx.getLevel());
    else
      assertEquals(Level.ERROR, logx.getLevel());
  }

  
}
