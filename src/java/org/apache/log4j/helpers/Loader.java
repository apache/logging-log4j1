/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.helpers;

import java.net.URL;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * Load things from a jar file. 
 * 
 * Created: Thu Dec 16 16:11:05 1999 <br>
 *
 * @author Sven Reimers
 */

public class Loader extends java.lang.Object { 

  public static Image getGIF_Image ( String path ) {
    Image img = null;
    try {
      URL url = ClassLoader.getSystemResource(path);
      System.out.println(url);
      img = (Image) (Toolkit.getDefaultToolkit()).getImage(url);
    }
    catch (Exception e) {
      System.out.println("Exception occured: " + e.getMessage() + 
			 " - " + e );
	    
    }
    return (img);
  }

  public static Image getGIF_Image ( URL url ) {
    Image img = null;
    try {
      System.out.println(url);
      img = (Image) (Toolkit.getDefaultToolkit()).getImage(url);
    } catch (Exception e) {
      System.out.println("Exception occured: " + e.getMessage() + 
			 " - " + e );
	    
    }
    return (img);
  }

  public static URL getHTML_Page ( String path ) {
    URL url = null;
    return (url = ClassLoader.getSystemResource(path));
  }    
}
