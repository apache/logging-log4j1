
package org.apache.log4j.test;

import org.apache.log4j.*;


import java.util.Random;

public class MDCStress extends Thread {

  static Category root = Category.getRoot();  

  static Random random = new Random(17);

  static final int BRANCHING_FACTOR = 2;
  static final int LOOP_LENGTH = 12;

  static int maxThreads;  
  static int threadCounter = 0;  


  public 
  static 
  void main(String args[]) {
    
    Layout layout = new PatternLayout("%r [%t] depth:%X{depth} - %m%n");
    Appender appender = new ConsoleAppender(layout);
    root.addAppender(appender);

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

    while(true) {
      synchronized(MDCStress.class) {
	// Adding 1 to ensure that at least 1 child is created. 	
	createChildren(randomInt(BRANCHING_FACTOR) + 1, 0);

	// wait until all threads are finished
	try {
	  root.debug("About to wait for notification.");
	  MDCStress.class.wait();
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
    System.err.println( "Usage: "+MDCStress.class + " maxThreads");
    System.exit(1);
  }


  public
  static
  void createChildren(int n, int depth) {
    if (n <= 0)
      return;

    synchronized(MDCStress.class) {
      n = maxThreadsConstained(n);    
      root.debug("Creating " + n+ " child MDCStress threads.");
      for(int i = 0; i < n; i++) {
	root.debug("New MDCStress, threadCounter = " + (++threadCounter));
	new MDCStress(depth+1).start();
      }
    }
  }


  int depth;
  
  MDCStress(int depth) {
    this.depth = depth;
  }

  public
  void run() {
    MDC.put("depth", new Integer(depth));
    System.out.println("depth="+MDC.get("depth"));
    
    int loopLength = randomInt(LOOP_LENGTH);
    root.debug("In run loop.debug( loopLength = "+loopLength);

    int createIndex = loopLength/2;
    
    for(int i = 0; i <= loopLength; i++) {
      if(i==0) {
	createChildren(randomInt(BRANCHING_FACTOR), depth+1);
      }
    } 
    

    synchronized(MDCStress.class) {
      threadCounter--;
      root.debug( "Exiting run loop. " + threadCounter);
      if(threadCounter <= 0) {
	root.debug( "Notifying [main] thread.");
	MDCStress.class.notify(); // wake up the main thread
      }
    }     

  }


  static
  public
  int maxThreadsConstained(int a) {
    int maxAllowed = MDCStress.maxThreads - MDCStress.threadCounter;      
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
