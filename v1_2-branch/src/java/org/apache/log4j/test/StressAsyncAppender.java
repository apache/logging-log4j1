/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
//  Copyright 2000 Ceki Gulcu.  All Rights Reserved.
//  See the LICENCE file for the terms of distribution.

package org.apache.log4j.test;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


import java.util.Random;

/**
   Stress test {@link AsyncAppender}.

 */
public class StressAsyncAppender extends Thread {

  static Logger root = Logger.getRootLogger();

  static Random random = new Random(101);

  static final int LOOP_LENGTH = 24;
  static final int BRANCHING_FACTOR = 4;

  static int maxThreads;
  static long msgCounter = 0;
  static int threadCounter = 0;

  static double LOG_2 = Math.log(2);

  static Object lock = new Object();


  public
  static
  void main(String args[]) {
    if(args.length != 1) {
      usage();
    }

    DOMConfigurator.configure("xml/stressAsyncAppender.xml");

    try {
      maxThreads =  Integer.parseInt(args[0]);
    }
    catch(java.lang.NumberFormatException e) {
      System.err.println(e);
      usage();
    }

    while(true) {
      synchronized(lock) {
	// Adding 1 to ensure that at least 1 child is created.
	createChildren(randomInt(BRANCHING_FACTOR) + 1);

	// wait until all threads are finished
	try {
	  root.debug("About to wait for notification.");
	  lock.wait();
	  root.debug("Got a notification.");
	}
	catch(InterruptedException e) {
	  root.warn("Unpextected InterruptedException received.", e);
	}
      }
    }
  }


  static
  void usage() {
    System.err.println("Usage: java "+ StressAsyncAppender.class.getName() +
			" MAX_THREADS");
    System.exit(1);
  }


  public
  StressAsyncAppender() {
  }

  public
  void run() {
    int loopLength = StressAsyncAppender.randomInt(LOOP_LENGTH);
    root.debug("In run loop, loopLength = "+loopLength);

    // half of the way, create new childres
    int createIndex = loopLength/2;

    for(int i = 0; i <= loopLength; i++) {

      if(i == createIndex)
	createChildren(randomInt(BRANCHING_FACTOR));

      synchronized(lock) {
	root.debug("Message number " + msgCounter++);
      }
      //delay(1+randomInt(4)*100);
    }

    synchronized(lock) {
      StressAsyncAppender.threadCounter--;
      root.debug("Exiting run loop. " + threadCounter);
      if(StressAsyncAppender.threadCounter <= 0) {
	root.debug("Notifying [main] thread.");
	lock.notify(); // wake up the main thread
      }
    }

  }

  public
  static
  void createChildren(int n) {
    if (n <= 0)
      return;

    synchronized(lock) {
      n = maxThreadsConstrained(n);
      root.debug("Creating " + n+ " child StressAsyncAppender threads.");
      for(int i = 0; i < n; i++) {
	root.debug("New StressAsyncAppender, threadCounter = " + (++threadCounter));
	new StressAsyncAppender().start();
      }
    }
  }

  static
  public
  int maxThreadsConstrained(int a) {
    int maxAllowed = StressAsyncAppender.maxThreads -
                                                 StressAsyncAppender.threadCounter;
    return a <= maxAllowed ? a : maxAllowed;
  }

  /**
     Return a random value in the range
   */
  public
  static
  int randomInt(int n) {
    int r = random.nextInt() % n;
    return r >= 0 ? r : -r;
  }


  public
  void delay(long millis) {
    try {
      Thread.currentThread().sleep(millis);
    } catch(Exception e) {}
  }

}
