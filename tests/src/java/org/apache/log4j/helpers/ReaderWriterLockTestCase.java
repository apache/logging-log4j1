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

package org.apache.log4j.helpers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;


/**
 * This test checks the correctness of ReaderWriterLock.
 *
 * <b>Warning</b> This test should not use log4j loggers.
 *
 * There is not much point in having a test depend on the component being tested.
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class ReaderWriterLockTestCase extends TestCase {
  static final int NUM_READERS = 120; //120;
  static final int NUM_WRITERS = 4; //4;
  static long WLOOP; // number of repetitions for writer threads
  static long RLOOP; // number of repetitions for reader threads
  double value1 = 0;
  double value2 = 0;

  // this is the object we are testing:
  ReaderWriterLock lock;

  // The bufferedReader will be passed to the VerifierThread
  BufferedReader bufferedReader;

  // This is wehere readers and writers send their output
  PrintWriter printWriter;

  /**
   * Constructor for ReaderWriterLockTestCase.
   * @param arg0
   */
  public ReaderWriterLockTestCase(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    // We write to a piped buffer so that a verifer thread can check the output
    PipedWriter pipedWriter = new PipedWriter();
    PipedReader pipedReader = new PipedReader();
    bufferedReader = new BufferedReader(pipedReader);
    pipedReader.connect(pipedWriter);

    printWriter = new PrintWriter(pipedWriter);
	  lock = new ReaderWriterLock(printWriter);
  }

  protected void tearDown() throws Exception {
  }

  public void test1() throws Exception {
    WLOOP = Long.parseLong(System.getProperty("runLen"));
    RLOOP = (long) (WLOOP * (1.0)); // readers loop longer

    Thread[] threads = new Thread[NUM_READERS + NUM_WRITERS];

    VerifierThread vt =
      new VerifierThread(bufferedReader, NUM_READERS, NUM_WRITERS);
    vt.start();

    for (int i = 0; i < NUM_READERS; i++) {
      threads[i] = new ReaderThread(i, vt);
    }

    for (int i = 0; i < NUM_WRITERS; i++) {
      threads[NUM_READERS + i] = new WriterThread(i, vt);
    }

    for (int i = 0; i < (NUM_WRITERS + NUM_READERS); i++) {
      threads[i].start();
    }

    for (int i = 0; i < (NUM_WRITERS + NUM_READERS); i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
      }
    }

    Exception e = vt.getException();

    if (e != null) {
      throw e;
    }
  }

  void delay(long delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new ReaderWriterLockTestCase("test1"));

    return suite;
  }

  void printMessage(String msg) {
	  //printWriter.print("[");      
	  printWriter.println(Thread.currentThread().getName()+" "+msg);
  }


  class ReaderThread extends Thread {
    int tNum;
    VerifierThread vt;

    ReaderThread(int i, VerifierThread vt) {
      super("R-" + i);
      tNum = i;
      this.vt = vt;
    }

    public void run() {
      printMessage("In run()");

      for (int t = 0; t < RLOOP; t++) {
        if (vt.getInterrupt()) {
          return;
        }

        //printMessage("Asking for read lock.");
        lock.getReadLock();
        //printMessage("Got read lock.");
        printMessage("Value1 is " + value1);
        printMessage("Value2 is " + value2);

        delay(10);

        //printMessage("About to release read lock.");
        lock.releaseReadLock();
      }
    }
  }

  class WriterThread extends Thread {
    int tNum;
    VerifierThread vt;

    WriterThread(int i, VerifierThread vt) {
      super("W-" + i);
      tNum = i;
      this.vt = vt;
    }

    public void run() {
      printMessage("In run");

      for (int t = 0; t < WLOOP; t++) {
        if (vt.getInterrupt()) {
          return;
        }

        // on average, the wait is (3.5)*30
        delay((((tNum * 13) + t) % 7) * 30);

        //printMessage("Asking for write lock.");
        lock.getWriteLock();
        //printMessage("Got write lock.");
        printMessage("About to increment values.");
        value1 += 1;
        value2 += 10;

        delay(10);

        //printMessage("About to release write lock.");
        lock.releaseWriteLock();
      }
    }
  }
}
