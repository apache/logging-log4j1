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
   @since version 0.9.2 */
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
     Search the parents (classes not interfaces) of <code>clazz</code>
     for a renderer. The renderer closest in the hierarchy will be
     returned. If no renderers could be found, then the default
     renderer is returned.
     
 */
  public
  ObjectRenderer get(Class clazz) {
    ObjectRenderer r;
    for(Class c = clazz; c != null; c = c.getSuperclass()) {
      r = (ObjectRenderer) map.get(c);
      if(r != null)
	return r;
    }
    return defaultRenderer;
    
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
    if(clazz.isInterface()) {
      throw new IllegalArgumentException(clazz +
     " is an interface. Only classes allowed to regiter object renderers.");
    }
    map.put(clazz, or);
  }
}
