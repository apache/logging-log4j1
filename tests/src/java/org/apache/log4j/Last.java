
package org.apache.log4j;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

public class Last extends TestCase {

  public Last(String name) {
    super(name);
  }


  public void test1() {
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new Last("test1"));
    return suite;
  }

}
