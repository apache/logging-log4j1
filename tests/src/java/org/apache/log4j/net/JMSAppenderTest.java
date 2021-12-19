package org.apache.log4j.net;

import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class JMSAppenderTest extends TestCase {

  public void tearDown() {
    LogManager.resetConfiguration();
  }

  public void testActivateOptions() {
    new JMSAppender().activateOptions();
  }

  public void testClose() {
    new JMSAppender().close();
  }

  public void testAppend() {
    Logger logger = Logger.getRootLogger();
    logger.addAppender(new JMSAppender());
    logger.info("Should not be logged by JMSAppenderTest.testAppend.");
  }

  public void testRequiresLayout() {
    assertFalse(new JMSAppender().requiresLayout());
  }
}
