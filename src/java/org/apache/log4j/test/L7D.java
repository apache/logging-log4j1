

//      Copyright 1996-2000, International Business Machines 
//      Corporation. All Rights Reserved.
// 
//      See the LICENCE file for the terms of distribution.

package org.apache.log4j.test; 

import org.apache.log4j.*;
import java.util.*;
import java.text.*;

/**
   This class is a simple test of the localization routines in
   Category class.

   @author Ceki G&uuml;lc&uuml;, IBM Zurich Research Laboratory */
public class L7D {
  static ResourceBundle[] bundles;
  

  public 
  static 
  void main(String args[]) {
    if(args.length == 3) 
      init(args[0], args[1], args[2]);
    else 
      Usage("Wrong number of arguments.");

    test();
  }

  static
  void Usage(String msg) {
    System.err.println(msg);
    System.err.println( "Usage: java " + L7D.class.getName() +
			"configFile ISO639LanguageCode ISO2166CountryCode");
    System.exit(1);
  }

  static
  void init(String configFile, String lanCode, String countryCode) {
    PropertyConfigurator.configure(configFile);
    bundles = new ResourceBundle[3];

    try {
      bundles[0] = ResourceBundle.getBundle("L7D", new Locale("en", "US"));
      bundles[1] = ResourceBundle.getBundle("L7D", new Locale("fr", "FR"));
      bundles[2] = ResourceBundle.getBundle("L7D", new Locale("fr", "CH")); 
					 
    }
    catch(MissingResourceException e) {
      e.printStackTrace();
    }
  }

  static
  void test() { 
    Category root = Category.getRoot();
    
    for(int i = 0; i < bundles.length; i++) {
      root.setResourceBundle(bundles[i]);
      
      root.l7dlog(Priority.DEBUG, "bogus1", null);            
      root.l7dlog(Priority.INFO, "test", null);
      root.l7dlog(Priority.WARN, "hello_world", null);
      root.l7dlog(Priority.DEBUG, "msg1",
		  new Object[] {new Integer(i+1), "log4j"}, null);
      root.l7dlog(Priority.ERROR, "bogusMsg",
		  new Object[] {new Integer(i+1), "log4j"}, null);      
      root.l7dlog(Priority.ERROR, "msg1",
		  new Object[] {new Integer(i+1), "log4j"}, null);
      root.l7dlog(Priority.INFO, "bogus2", null);
    }
    
  }
  
}
