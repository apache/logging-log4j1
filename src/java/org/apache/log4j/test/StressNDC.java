//      Copyright 1996-1999, International Business Machines 
//      Corporation and others. All Rights Reserved.

package org.apache.log4j.test;


import org.apache.log4j.Category;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Priority;
import org.apache.log4j.NDC;


import java.util.Random;
import java.util.Stack;

/**
   Stress test {@link NDC}.
   
 */
public class StressNDC extends Thread {

  static Category root = Category.getRoot();  

  static Random random = new Random(101);

  static final int LOOP_LENGTH = 24;
  static final int PUSH_MISS = LOOP_LENGTH/2;      
  static final int POP_MISS = PUSH_MISS*2;    

  static final int BRANCHING_FACTOR = 4 + 1; // add 1 to the number you want
  
  static int maxThreads;  
  static int msgCounter = 0;
  static int threadCounter = 0;  

  static double LOG_2 = Math.log(2);

  static Object lock = new Object();

  
  public 
  static 
  void main(String args[]) {
    root.setPriority(Priority.DEBUG);
    BasicConfigurator.configure();

    if(args.length != 1) {
      usage();
    }

    try {
      maxThreads =  Integer.parseInt(args[0]);
    }
    catch(java.lang.NumberFormatException e) {
      System.err.println(e);
      usage();
    }
    
    root.debug( "push(IP=127.0.0.1)");
    
    NDC.push("IP=127.0.0.1");

    while(true) {
      synchronized(lock) {
	// Adding 1 to ensure that at least 1 child is created. 	
	createChildren(randomInt(BRANCHING_FACTOR) + 1);

	// wait until all threads are finished
	try {
	  root.debug( "About to wait for notification.");
	  lock.wait();
	  root.debug( "Got a notification.");
	}
	catch(InterruptedException e) {
	  root.warn("Unpextected InterruptedException received.", e);
	}
      }
    }
  }

  static
  void usage() {
    System.err.println( "Usage: java org.apache.log4j.test.StressNDC " +
			"MAX_THREADS");
    System.exit(1);
  }


  Stack parentDC;
  
  public
  StressNDC(Stack parentDC) {
    this.setName(randomID());
    this.parentDC = parentDC;
  }

  public
  void run() {
    NDC.inherit(parentDC);
    
    int loopLength = StressNDC.randomInt(LOOP_LENGTH);
    root.debug("In run loop.debug( loopLength = "+loopLength);

    int createIndex = loopLength/2;
      
    for(int i = 0; i <= loopLength; i++) {

      if(i == createIndex)
	createChildren(randomInt(BRANCHING_FACTOR));
      
      if(randomInt(PUSH_MISS) == 0) {
	String id = randomID();
	root.debug( "push("+id+")"); 
	NDC.push(id);
      }      
      root.debug( "Message number " + StressNDC.msgCounter++);	
      if(randomInt(POP_MISS) == 0) {
	root.debug( "pop()");
	NDC.pop();
      }
    }    

    synchronized(lock) {
      StressNDC.threadCounter--;
      root.debug( "Exiting run loop. " + threadCounter);
      if(StressNDC.threadCounter <= 0) {
	root.debug( "Notifying [main] thread.");
	lock.notify(); // wake up the main thread
      }
    }

    // We sometimes forget to remove references
    if((loopLength % 2) == 0) {
      root.debug("Removing NDC for this thread.");
      NDC.remove();
    }
  }

  public
  static
  void createChildren(int n) {
    if (n <= 0)
      return;

    synchronized(lock) {
      n = maxThreadsConstained(n);    
      root.debug("Creating " + n+ " child StressNDC threads.");
      for(int i = 0; i < n; i++) {
	root.debug("New StressNDC, threadCounter = " + (++threadCounter));
	new StressNDC(NDC.cloneStack()).start();
      }
    }
  }

  static
  public
  int maxThreadsConstained(int a) {
    int maxAllowed = StressNDC.maxThreads - StressNDC.threadCounter;      
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
  static
  String randomID() {
    return Integer.toHexString(random.nextInt()& 0xFFFFFF);
  }
}
