package org.apache.log4j.xml;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;

import junit.framework.TestCase;

public class ResetTest extends TestCase {
  
  public void testNoReset() {
    test("input/xml/resetTest.xml", true);
    test("input/xml/defaultInit.xml", false);
  }

  private void test(String file, boolean reset)
  {
    LogManager.getLoggerRepository().resetConfiguration();
    Logger l = Logger.getLogger("x");
    l.setLevel(Level.ERROR);
    assertEquals(Level.ERROR, l.getLevel());

    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure(file, LogManager.getLoggerRepository());
    if (reset)
      assertEquals(null, l.getLevel());
    else
      assertEquals(Level.ERROR, l.getLevel());
  }
  

}
