/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.or;

import java.util.Hashtable;

/**
   Map class objects to an {@link ObjectRenderer}.

   @author Ceki G&uuml;lc&uuml;
   @since version 1.0 */
public class RendererMap {

  Hashtable map;

  static ObjectRenderer defaultRenderer = new DefaultRenderer();

  public
  RendererMap() {
    map = new Hashtable();
  }

  public
  String findAndRender(Object o) {
    if(o == null)
      return null;
    else 
      return get(o.getClass()).doRender(o);
  }


  /**
     Syntactic sugar method that calls {@link #get(Class)} with the
     class of the object parameter. */
  public 
  ObjectRenderer get(Object o) {
    if(o == null) 
      return null;
    else
      return get(o.getClass());
  }
  

  /**
     Search the parents of <code>clazz</code> for a renderer. The
     renderer closest in the hierarchy will be returned. If no
     renderers could be found, then the default renderer is returned.          

     <p>The search first looks for a renderer configured for clazz. If
     a renderer could not be found, then the search continues by
     looking at the interfaces implemented by clazz. If a renderer
     cannot be found, then the search looks for a renderer defined for
     the parent of clazz. If that fails, then it then looks at the
     interfaces implemented by the parent of clazz and so on.
     
 */
  public
  ObjectRenderer get(Class clazz) {
    //System.out.println("\nget: "+clazz);    
    ObjectRenderer r = null;
    for(Class c = clazz; c != null; c = c.getSuperclass()) {
      //System.out.println("Searching for class: "+c);
      r = (ObjectRenderer) map.get(c);
      if(r != null) {
	return r;
      }      
      r = searchInterfaces(c);
      if(r != null)
	return r;
    }
    return defaultRenderer;
  }  
  
  ObjectRenderer searchInterfaces(Class c) {
    //System.out.println("Searching interfaces of class: "+c);
    
    ObjectRenderer r = (ObjectRenderer) map.get(c);
    if(r != null) {
      return r;
    } else {
      Class[] ia = c.getInterfaces();
      for(int i = 0; i < ia.length; i++) {
	r = searchInterfaces(ia[i]);
	if(r != null)
	  return r; 
      }
    }
    return null;
  }


  public
  ObjectRenderer getDefaultRenderer() {
    return defaultRenderer;
  }


  public
  void clear() {
    map.clear();
  }

  /**
     Register an {@link ObjectRenderer} for <code>clazz</code>.

     <b>Warning:</b>Interfaces cannot have object renderers. If clazz
     is an interface, then a {@link IllegalArgumentException} will be
     thrown.
     
  */
  public
  void put(Class clazz, ObjectRenderer or) {
    //if(clazz.isInterface()) {
    //throw new IllegalArgumentException(clazz +
    //" is an interface. Only classes allowed to regiter object renderers.");
    //}
    map.put(clazz, or);
  }
}
