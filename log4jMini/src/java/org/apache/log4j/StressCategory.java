/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;


import org.apache.log4j.Priority;
import org.apache.log4j.Category;
import java.util.Random;

/*
  Stress test the Category class.

*/

class StressCategory {

  static Priority[] priority = new Priority[] {Priority.DEBUG, 
					       Priority.INFO, 
					       Priority.WARN,
					       Priority.ERROR,
					       Priority.FATAL};

  static Priority defaultPriority = Category.getRoot().getPriority();
  
  static int LENGTH;
  static String[] names;
  static Category[] cat;
  static CT[] ct;

  static Random random = new Random(10);

  public static void main(String[] args) {
    
    LENGTH = args.length;

    if(LENGTH == 0) {
      System.err.println( "Usage: java " + StressCategory.class.getName() +
			  " name1 ... nameN\n.");      
      System.exit(1);
    }
    if(LENGTH >= 7) {
      System.err.println(
        "This stress test suffers from combinatorial explosion.\n"+
        "Invoking with seven arguments takes about 90 minutes even on fast machines");
    }

    names = new String[LENGTH];
    for(int i=0; i < LENGTH; i++) {
      names[i] = args[i];
    }    
    cat = new Category[LENGTH];
    ct = new CT[LENGTH]; 


    permute(0); 

    // If did not exit, then passed all tests.
  }

  // Loop through all permutations of names[].
  // On each possible permutation call createLoop
  static
  void permute(int n) {
    if(n == LENGTH)
      createLoop(0);
    else
      for(int i = n; i < LENGTH; i++) {
	swap(names, n, i);
	permute(n+1);
	swap(names, n, i);	
      }
  }

  static
  void swap(String[] names, int i, int j) {
    String t = names[i];
    names[i] = names[j];
    names[j] = t;
  }
  
  public
  static
  void permutationDump() {
    System.out.print("Current permutation is - ");
    for(int i = 0; i < LENGTH; i++) {
      System.out.print(names[i] + " ");
    }
    System.out.println();
  }


  // Loop through all possible 3^n combinations of not instantiating, 
  // instantiating and setting/not setting a priority.

  static
  void createLoop(int n) {
    if(n == LENGTH) {  
      //System.out.println("..............Creating cat[]...........");
      for(int i = 0; i < LENGTH; i++) {
	if(ct[i] == null)
	  cat[i] = null;
	else {
	  cat[i] = Category.getInstance(ct[i].catstr);
	  cat[i].setPriority(ct[i].priority);
	}
      }
      test();
      // Clear hash table for next round
      Category.defaultHierarchy.clear();
    }
    else {      
      ct[n]  = null;
      createLoop(n+1);  

      ct[n]  = new CT(names[n], null);
      createLoop(n+1);  
      
      int r = random.nextInt(); if(r < 0) r = -r;
      ct[n]  = new CT(names[n], priority[r%5]);
      createLoop(n+1);
    }
  }


  static
  void test() {    
    //System.out.println("++++++++++++TEST called+++++++++++++");
    //permutationDump();
    //catDump();

    for(int i = 0; i < LENGTH; i++) {
      if(!checkCorrectness(i)) {
	System.out.println("Failed stress test.");
	permutationDump();
	
	//Hierarchy._default.fullDump();
	ctDump();
	catDump();
	System.exit(1);
      }
    }
  }
  
  static
  void ctDump() {
    for(int j = 0; j < LENGTH; j++) {
       if(ct[j] != null) 
	    System.out.println("ct [" + j + "] = ("+ct[j].catstr+"," + 
			       ct[j].priority + ")");
       else 
	 System.out.println("ct [" + j + "] = undefined");
    }
  }
  
  static
  void catDump() {
    for(int j = 0; j < LENGTH; j++) {
      if(cat[j] != null)
	System.out.println("cat[" + j + "] = (" + cat[j].name + "," +
			   cat[j].getPriority() + ")");
      else
	System.out.println("cat[" + j + "] = undefined"); 
    }
  }

  //  static
  //void provisionNodesDump() {
  //for (Enumeration e = CategoryFactory.ht.keys(); e.hasMoreElements() ;) {
  //  CategoryKey key = (CategoryKey) e.nextElement();
  //  Object c = CategoryFactory.ht.get(key);
  //  if(c instanceof  ProvisionNode) 
  //((ProvisionNode) c).dump(key.name);
  //}
  //}
  
  static
  boolean checkCorrectness(int i) {
    CT localCT = ct[i];

    // Can't perform test if category is not instantiated
    if(localCT == null) 
      return true;
    
    // find expected priority
    Priority expected = getExpectedPrioriy(localCT);

			    
    Priority purported = cat[i].getChainedPriority();

    if(expected != purported) {
      System.out.println("Expected priority for " + localCT.catstr + " is " +
		       expected);
      System.out.println("Purported priority for "+ cat[i].name + " is "+purported);
      return false;
    }
    return true;
      
  }

  static
  Priority getExpectedPrioriy(CT ctParam) {
    Priority priority = ctParam.priority;
    if(priority != null) 
      return priority;

    
    String catstr = ctParam.catstr;    
    
    for(int i = catstr.lastIndexOf('.', catstr.length()-1); i >= 0; 
	                              i = catstr.lastIndexOf('.', i-1))  {
      String substr = catstr.substring(0, i);

      // find the priority of ct corresponding to substr
      for(int j = 0; j < LENGTH; j++) {	
	if(ct[j] != null && substr.equals(ct[j].catstr)) {
	  Priority p = ct[j].priority;
	  if(p != null) 
	    return p;	  
	}
      }
    }
    return defaultPriority;
  }

  

  static class CT {
    public String   catstr;
    public Priority priority;

    CT(String catstr,  Priority priority) {
      this.catstr = catstr;
      this.priority = priority;
    }
  }
}
