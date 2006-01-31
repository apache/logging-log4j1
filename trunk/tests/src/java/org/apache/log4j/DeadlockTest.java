/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import junit.framework.TestCase;


/**
 * Test case for bug http://nagoya.apache.org/bugzilla/show_bug.cgi?id=24159
 *
 * Actually this one is impossible to fix.
 *
 * @author Elias Ross
 * @author Ceki Gulcu
 */
public class DeadlockTest extends TestCase {
  static long RUNLENGTH = 10000;
  Logger logger = Logger.getLogger("DeadlockTest");

  public DeadlockTest() {
      super("DeadlockTest");
  }
  
  protected void setUp() throws Exception {
    super.setUp();
    System.out.println("in setup");
    BasicConfigurator.configure();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    System.out.println("tear down");
    LogManager.shutdown();
  }

  public void testDeadlock() throws InterruptedException {
    System.out.println("in testDeadlock()");

    final Deadlock d = new Deadlock();

    Thread t1 =
      new Thread() {
        public void run() {
          long start = System.currentTimeMillis();

          while ((System.currentTimeMillis() - start) < RUNLENGTH) {
            logger.debug(d);
          }
        }
      };

    Thread t2 =
      new Thread() {
        public void run() {
          long start = System.currentTimeMillis();

          while ((System.currentTimeMillis() - start) < RUNLENGTH) {
            d.setVar("n");
          }
        }
      };

    t1.start();
    t2.start();
    System.out.println("Waiting to join t1.");
    t1.join();
    System.out.println("======================Joined t1=====================");
  }
}


class Deadlock {
  static final Logger log = Logger.getLogger(Deadlock.class);
  String var;

  public synchronized void setVar(String var) {
    log.debug(this);
  }

  public synchronized String getVar() {
    return var;
  }

  public String toString() {
    return "Value x=" + getVar();
  }
}
