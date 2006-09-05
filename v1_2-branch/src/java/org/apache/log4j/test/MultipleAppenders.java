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


package org.apache.log4j.test;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import java.util.Enumeration;
import java.util.Vector;

public class MultipleAppenders {

  public 
  static 
  void main(String argv[]) {

    // A1 and A2 should be added to root by reading the config file
    PropertyConfigurator.configure(argv[0]);

    Logger root = Logger.getRootLogger();

    Enumeration e1 = root.getAllAppenders();
    Vector v = new Vector(1);
    
    while(e1.hasMoreElements()) {
      Appender a = (Appender) e1.nextElement();
      v.addElement(a);
      String appenderName = a.getName();	
      if(a != root.getAppender(appenderName)) {
	System.out.println(appenderName + " lookup failed. Exiting.");
	System.exit(1);
      }
      // attempt to add the existing appender
      root.addAppender(a);
    }

    // attempt to add a null appender
    root.addAppender(null);

    Enumeration e2 = root.getAllAppenders();

    for(int i = 0; i < v.size(); i++) {
      if(v.elementAt(i) != e2.nextElement()) {
      }
    }

    if(e2.hasMoreElements()){
      System.out.println("Failure, e2 has remaining elements. Exiting.");
      System.exit(1);      
    }
    System.out.println("OK");
  }

  
}
