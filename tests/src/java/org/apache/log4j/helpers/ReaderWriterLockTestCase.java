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

    // It's better to wait for the writer to finish first
    for (int i = 0; i < (NUM_WRITERS + NUM_READERS); i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
      }
    }
   
    // let the verifier thread close
    vt.closed = true;
    
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
        if (vt.isClosed()) {
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
        if (vt.isClosed()) {
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
