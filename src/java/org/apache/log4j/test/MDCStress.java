
package org.apache.log4j.test;

import org.apache.log4j.*;

public class MDCStress extends Thread {

  static Category root = Category.getRoot();  

  public 
  static 
  void main(String args[]) {
    for(int i = 0; i < 2; i++) {
      MDC.put("x", new Integer(i));
      MDCStress ms = new MDCStress(true);
      ms.start();
    }    

    try {Thread.currentThread().sleep(1000);}catch(Exception e){}
    System.out.println("==========");
    System.gc();
    System.gc();
    try {Thread.currentThread().sleep(1000);}catch(Exception e){}
    System.gc();
    System.out.println("==========");
    try {Thread.currentThread().sleep(1000);}catch(Exception e){}
    System.gc();
  }

  boolean dosub;

  MDCStress(boolean dosub) {
    this.dosub = dosub;
  }

  public
  void run() {
    System.out.println("x="+MDC.get("x")+ "  y="+MDC.get("y"));
    if(dosub) {
      Object o = MDC.get("x");
      if(o instanceof Integer) {
	Integer io = (Integer) o;	
	MDC.put("y", new Integer(io.intValue()*10));
	MDCStress ms = new MDCStress(false);
	ms.start();	
      }
    }

  }

}
