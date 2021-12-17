package org.apache.log4j.net;

import junit.framework.TestCase;

public class SocketNodeTest extends TestCase {

  public void testTestRun() {
    new SocketNode(null, null).run();
  }
}
