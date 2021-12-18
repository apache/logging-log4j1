package org.apache.log4j.jdbc;

import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class JDBCAppenderTest extends TestCase {

  public void tearDown() {
    LogManager.resetConfiguration();
  }

  public void testActivateOptions() {
    new JDBCAppender().activateOptions();
  }

  public void testClose() {
    new JDBCAppender().close();
  }

  public void testAppend() {
    Logger logger = Logger.getRootLogger();
    logger.addAppender(new JDBCAppender());
    logger.info("Should not be logged by JDBCAppenderTest.testAppend.");
  }

  public void testRequiresLayout() {
    assertTrue(new JDBCAppender().requiresLayout());
  }
}
