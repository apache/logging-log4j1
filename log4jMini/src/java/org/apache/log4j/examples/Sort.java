
package org.apache.log4j.examples;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;  

/**
   Example code for log4j to viewed in conjunction with the {@link
   org.apache.log4j.examples.SortAlgo SortAlgo} class.
   
   <p>This program expects a configuration file name as its first
   argument, and the size of the array to sort as the second and last
   argument. See its <b><a href="doc-files/Sort.java">source
   code</a></b> for more details.

   <p>Play around with different values in the configuration file and
   watch the changing behavior.

   <p>Example configuration files <a
   href="doc-files/sort1.lcf">sort1.lcf</a>, 
   <a href="doc-files/sort2.lcf">sort2.lcf</a>, 
   <a href="doc-files/sort3.lcf">sort3.lcf</a> and 
   <a href="doc-files/sort4.lcf">sort4.lcf</a> are supplied with the
   package.
   
   <p>If you are interested in logging performance, then have look at
   the {@link org.apache.log4j.performance.Logging} class.

   @author Ceki G&uuml;lc&uuml; */

public class Sort {

  static Category CAT = Category.getInstance(Sort.class.getName());
  
  public static void main(String[] args) {
    if(args.length != 2) {
      usage("Incorrect number of parameters.");
    }
    int arraySize = -1;
    try {
      arraySize = Integer.valueOf(args[1]).intValue();
      if(arraySize <= 0) 
	usage("Negative array size.");
    }
    catch(java.lang.NumberFormatException e) {
      usage("Could not number format ["+args[1]+"].");
    }

    Sort.init(args[0]);

    int[] intArray = new int[arraySize];

    CAT.info("Populating an array of " + arraySize + " elements in" +
	     " reverse order.");
    for(int i = arraySize -1 ; i >= 0; i--) {
      intArray[i] = arraySize - i - 1;
    }

    SortAlgo sa1 = new SortAlgo(intArray);
    sa1.bubbleSort();
    sa1.dump();

    // We intentionally initilize sa2 with null.
    SortAlgo sa2 = new SortAlgo(null);
    CAT.info("The next log statement should be an error message.");
    sa2.dump();  
    CAT.info("Exiting main method.");    
  }
  
  static
  void usage(String errMsg) {
    System.err.println(errMsg);
    System.err.println("\nUsage: java org.apache.log4j.examples.Sort " +
		       "configFile ARRAY_SIZE\n"+
      "where  configFile is a configuration file\n"+
      "      ARRAY_SIZE is a positive integer.\n");
    System.exit(1);
  }

  static
  void init(String configFile) {
    PropertyConfigurator.configure(configFile);
  }
}
