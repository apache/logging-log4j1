package org.apache.log4j.net;

import junit.framework.TestCase;

import java.io.File;

public class SocketServerTest extends TestCase {
  public void testSubclassing() {
    new SocketServer(new File(".")) {};
  }
}
