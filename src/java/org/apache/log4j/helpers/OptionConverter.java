/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.APL file.
 */

package org.log4j.helpers;

import java.util.Properties;
import java.net.URL;
import org.log4j.spi.Configurator;
import org.log4j.xml.DOMConfigurator;
import org.log4j.PropertyConfigurator;

// Contributors:   Avy Sharell (sharell@online.fr)

/**
   A convinience class to convert property values to specific types.

   @author Ceki G&uuml;lc&uuml;
   @author Avy Sharell
*/	
public class OptionConverter {

  static String DELIM_START = "${";
  static char   DELIM_STOP  = '}';
  static int DELIM_START_LEN = 2;
  static int DELIM_STOP_LEN  = 1;
  
  static StringBuffer sbuf = new StringBuffer();

  /** OptionConverter is a static class. */
  private OptionConverter() {}
  
  public
  static
  String[] concatanateArrays(String[] l, String[] r) {
    int len = l.length + r.length;
    String[] a = new String[len];

    System.arraycopy(l, 0, a, 0, l.length);
    System.arraycopy(r, 0, a, l.length, r.length);

    return a;
  }
  
  public
  static
  String convertSpecialChars(String s) {
    char c;
    int len = s.length();
    StringBuffer sbuf = new StringBuffer(len);
    
    int i = 0;
    while(i < len) {
      c = s.charAt(i++);
      if (c == '\\') {
	c =  s.charAt(i++);
	if(c == 'n')      c = '\n';
	else if(c == 'r') c = '\r';
	else if(c == 't') c = '\t';
	else if(c == 'f') c = '\f';
	else if(c == '\b') c = '\b';					
	else if(c == '\"') c = '\"';				
	else if(c == '\'') c = '\'';			
	else if(c == '\\') c = '\\';			
      }
      sbuf.append(c);      
    }
    return sbuf.toString();
  }
  
  public
  static
  Object instantiateByKey(Properties props, String key, Class superClass,
				Object defaultValue) {

    // Get the value of the property in string form
    String className = props.getProperty(key);
    if(className == null) {
      System.err.println("Could not find value for key " + key);
      return defaultValue;
    }
    // Trim className to avoid trailing spaces that cause problems.
    return OptionConverter.instantiateByClassName(className.trim(), superClass,
						  defaultValue);
  }

  /**
     If <code>value</code> is "true", then <code>true</code> is
     returned. If <code>value</code> is "false", then
     <code>true</code> is returned. Otherwise, <code>default</code> is
     returned.

     <p>Case of value is unimportant.  */
  public
  static
  boolean toBoolean(String value, boolean dEfault) {
    if(value == null)
      return dEfault;
    String trimmedVal = value.trim();
    if("true".equalsIgnoreCase(trimmedVal)) 
      return true;
    if("false".equalsIgnoreCase(trimmedVal))
      return false;
    return dEfault;
  }

  public
  static
  int toInt(String value, int dEfault) {
    if(value != null) {
      String s = value.trim();
      try {
	return Integer.valueOf(s).intValue();
      }
      catch (NumberFormatException e) {
	System.err.println("[" + s + "] is not in proper int form.");
	e.printStackTrace();
      }
    }
    return dEfault;
  }

  public
  static
  long toFileSize(String value, long dEfault) {
    if(value == null)
      return dEfault;
    
    String s = value.trim().toUpperCase();
    long multiplier = 1;
    int index;
    
    if((index = s.indexOf("KB")) != -1) {      
      multiplier = 1024;
      s = s.substring(0, index);
    }
    else if((index = s.indexOf("MB")) != -1) {
      multiplier = 1024*1024;
      s = s.substring(0, index);
    }
    else if((index = s.indexOf("GB")) != -1) {
      multiplier = 1024*1024*1024;
      s = s.substring(0, index);
    }    
    if(s != null) {
      try {
	return Long.valueOf(s).longValue() * multiplier;
      }
      catch (NumberFormatException e) {
	System.err.println("[" + s + "] is not in proper int form.");
	System.err.println("[" + value + "] not in expected format.");		
	e.printStackTrace();
      }
    }
    return dEfault;
  }

  /**
     Find the value corresponding to <code>key</code> in
     <code>props</code>. Then perform variable substitution on the
     found value.

 */
  public
  static
  String findAndSubst(String key, Properties props) {
    String value = props.getProperty(key);
    if(value == null) 
      return null;      
    
    try {
      return substVars(value, props);
    } catch(IllegalArgumentException e) {
      LogLog.error("Bad option value ["+value+"].", e);
      return value;
    }    
  }
   
  /**
     Instantiate an object given a class name. Check that the
     <code>className</code> is a subclass of <code>superClass</code>.

   */
  public
  static
  Object instantiateByClassName(String className, Class superClass,
				Object defaultValue) {
    if(className != null) {
      try {
	Class classObj = Class.forName(className);
	if(!superClass.isAssignableFrom(classObj)) 
	  System.err.println("A \""+className+
			     "\" object is not assignable to a \""+
			     superClass.getName() + "\" object.");
	return classObj.newInstance();
      }
      catch (Exception e) {
	System.err.println("Could not instantiate class [" + className + "].");
	e.printStackTrace();
      }
    }
    return defaultValue;    
  }


  /**
     Perform variable substitution in string <code>val</code> from the
     values of keys found in the system propeties.

     <p>The variable substitution delimeters are <b>${</b> and <b>}</b>.
     
     <p>For example, if the system properties contains "key=value", then
     the call
     <pre>
     String s = OptionConverter.substituteVars("Value of key is ${key}.");
     </pre>
  
     will set the variable <code>s</code> to "Value of key is value.".

     If no value could be found for the specified key, then the
     <code>props</code> parameter is searched, if the value could not
     be found there, then substitution defaults to the empty string.

     <p>For example, if system propeties contains no value for the key
     "inexistentKey", then the call

     <pre>
     String s = OptionConverter.subsVars("Value of inexistentKey is [${inexistentKey}]");
     </pre>
     will set <code>s</code> to "Value of inexistentKey is []"     

     <p>An {@link java.lang.IllegalArgumentException} is thrown if
     <code>val</code> contains a start delimeter "${" which is not
     balanced by a stop delimeter "}". </p>

     <p><b>Author</b> Avy Sharell</a></p>

     @param val The string on which variable substitution is performed.
     @throws IllegalArgumentException if <code>val</code> is malformed.

  */
  public static
  String substVars(String val, Properties props) throws
                        IllegalArgumentException {
    sbuf.setLength(0);

    int i = 0;
    int j, k;
    
    while(true) {
      j=val.indexOf(DELIM_START, i);
      if(j == -1) {
	if(i==0)
	  return val;
	else {
	  sbuf.append(val.substring(i, val.length()));
	  return sbuf.toString();
	}
      }
      else {
	sbuf.append(val.substring(i, j));
	k = val.indexOf(DELIM_STOP, j);
	if(k == -1) {
	  throw new IllegalArgumentException('"'+val+
		      "\" has no closing brace. Opening brace at position " + j 
					     + '.');
	}
	else {
	  j += DELIM_START_LEN;
	  String key = val.substring(j, k);
	  // first try in System properties
	  String replacement = System.getProperty(key);
	  // then try props parameter
	  if(replacement == null && props != null) {
	    replacement =  props.getProperty(key);
	  }

	  if(replacement != null) 
	    sbuf.append(replacement);
	  i = k + DELIM_STOP_LEN;	    
	}
      }
    }
  }

  static
  public
  void selectAndConfigure(URL url) {
    String clazz = url.getRef();

    Configurator configurator = null;

    if(clazz != null) {
      LogLog.debug("Preferred configurator class: " + clazz);
      configurator = (Configurator) instantiateByClassName(clazz,
						org.log4j.spi.Configurator.class,
						null);
      if(configurator == null) {
	LogLog.error("Could not instantiate configurator ["+clazz+"].");
	return;
      }
    } else {
      String filename = url.getFile();
      if(filename != null && filename.endsWith(".xml")) {
	configurator = new DOMConfigurator();
      } else {
	configurator = new PropertyConfigurator();
      }
    }

    configurator.doConfigure(url);
  }
}
