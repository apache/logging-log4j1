/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
