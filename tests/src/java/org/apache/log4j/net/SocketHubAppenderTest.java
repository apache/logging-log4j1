package org.apache.log4j.net;

import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SocketHubAppenderTest extends TestCase {

  public void tearDown() {
    LogManager.resetConfiguration();
  }

  public void testActivateOptions() {
    new SocketHubAppender().activateOptions();
  }

  public void testClose() {
    new SocketHubAppender().close();
  }

  public void testAppend() {
    Logger logger = Logger.getRootLogger();
    logger.addAppender(new SocketHubAppender());
    logger.info("Should not be logged by SocketHubAppenderTest.testAppend.");
  }

  public void testRequiresLayout() {
    assertFalse(new SocketHubAppender().requiresLayout());
  }
}
