/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.helpers;

import java.net.URL;
//import java.awt.Image;
//import java.awt.Toolkit;

/**
   Load resources (or images) from various sources.
 
  @author Sven Reimers
  @author Ceki G&uuml;lc&uuml;
 */

public class Loader extends java.lang.Object { 

  static String JARSTR = "Caught InvalidJarException. This may be innocuous.";
  
  /**
     This method will search for <code>resource</code> in different
     places. The rearch order is as follows:

     <ol>

     <p><li>Search for <code>fully/qualified/clazz/name/resource</code>
     with the {@link ClassLoader} that loaded <code>clazz</code>.

     <p><li>Search for <code>fully/qualified/clazz/name/resource</code>
     with the <code>null</code> (bootstrap) class loader.

     <p><li>Search for <code>resource</code> with the class loader
     that loaded <code>clazz</code>. 

     <pi><li>Try one last time with
     <code>ClassLoader.getSystemResource(resource)</code> 
     </ol>
     
     
  */
  static 
  public
  URL getResource(String resource, Class clazz) {
    
    URL url = null;
    

    // Is it under CLAZZ/resource somewhere in the classpath?    
    // where CLAZZ is the fully qualified name of clazz where dots have been
    // changed to directory separators
    LogLog.debug("Trying to find ["+resource+"] using Class.getResource().");



    try {
      url = clazz.getResource(resource);
      if(url != null) 
	return url;
    } catch (sun.misc.InvalidJarIndexException e) {
      LogLog.debug(JARSTR);
    }


    // attempt to get the resource under CLAZZ/resource from the
    // system class path. The system class loader should not throw
    // InvalidJarIndexExceptions
    String fullyQualified = resolveName(resource, clazz);
    LogLog.debug("Trying to find ["+fullyQualified+
		 "] using ClassLoader.getSystemResource().");
    url = ClassLoader.getSystemResource(fullyQualified);
    if(url != null) 
      return url;

    // Let the class loader of clazz and parents (by the delagation
    // property) seearch for resource
    ClassLoader loader = clazz.getClassLoader();
    LogLog.debug("Trying to find ["+resource+"] using "+loader
		 +" class loader.");
    
    try {
      url = loader.getResource(resource); 
      if(url != null) 
	return url;
    } catch(sun.misc.InvalidJarIndexException e) {
      LogLog.debug(JARSTR);
    }
    

    // Attempt to get the resource from the class path. It may be the
    // case that clazz was loaded by the Extentsion class loader which
    // the parent of the system class loader. Hence the code below.
    LogLog.debug("Trying to find ["+resource+"] using ClassLoader.getSystemResource().");
    url = ClassLoader.getSystemResource(resource);
    return url;
  }

  /**
     Add the fully qualified name of a class before resource (replace . with /).
   */
  static
  String resolveName(String resource, Class clazz) {
    String fqcn = clazz.getName();
    int index = fqcn.lastIndexOf('.');
    if (index != -1) {
      fqcn = fqcn.substring(0, index).replace('.', '/');
      resource = fqcn+"/"+resource;
    }
    return resource;
  }


  //public static Image getGIF_Image ( String path ) {
  //  Image img = null;
  //  try {
  //	URL url = ClassLoader.getSystemResource(path);
  //	System.out.println(url);
  //	img = (Image) (Toolkit.getDefaultToolkit()).getImage(url);
  //  }
  //  catch (Exception e) {
  //	System.out.println("Exception occured: " + e.getMessage() + 
  //			   " - " + e );
  //	      
  //  }
  //  return (img);
  //}
  //
  //public static Image getGIF_Image ( URL url ) {
  //  Image img = null;
  //  try {
  //	System.out.println(url);
  //	img = (Image) (Toolkit.getDefaultToolkit()).getImage(url);
  //  } catch (Exception e) {
  //	System.out.println("Exception occured: " + e.getMessage() + 
  //			   " - " + e );
  //	      
  //  }
  //  return (img);
  //}
  //
  //public static URL getHTML_Page ( String path ) {
  //  URL url = null;
  //  return (url = ClassLoader.getSystemResource(path));
  //  }    
}
