/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j;

import java.util.Hashtable;
import org.apache.log4j.helpers.ThreadLocalMap;

/**
   The MDC class supercedes the {@link NDC} class. It provides
   <em>mapped diagnostic contexts</em>. A <em>Mapped Diagnostic
   Context</em>, or MDC in short, is an instrument to distinguish
   interleaved log output from different sources. Log output is
   typically interleaved when a server handles multiple clients
   near-simultaneously.

   <p><b><em>The MDC is managed on a per thread basis</em></b>. A
   child thread automatically inherits an <em>copy</em> of the mapped
   diagnostic context of its parent which is managed independently of
   the parent's context.

   <p>The MDC requires JDK 1.2. It will not work under JDK 1.1.

   
   @since 1.2

   @author Ceki G&uuml;lc&uuml; */
public class MDC {

  final static ThreadLocalMap context = new ThreadLocalMap();
  
  static final int HT_SIZE = 7;

  static
  public
  void put(String key, Object o) {
    Hashtable ht = (Hashtable) context.get();
    if(ht == null) {
      //System.out.println("Creating new ht. [" + Thread.currentThread().getName()+
      //		 "]");
      ht = new Hashtable(HT_SIZE);
      context.set(ht);
    }    
    ht.put(key, o);
  }
  
  static 
  public
  Object get(String key) {
    Hashtable ht = (Hashtable) context.get();
    if(ht != null) {
      return ht.get(key);
    } else {
      return null;
    }
  }

  public
  static
  Hashtable getContext() {
    return (Hashtable) context.get();
  }

}
