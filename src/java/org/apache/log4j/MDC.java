/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j;

import java.util.Hashtable;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.ThreadLocalMap;

/**
   The MDC class is similar to the {@link NDC} class except that it is
   based on a map instead of a stack. It provides <em>mapped
   diagnostic contexts</em>. A <em>Mapped Diagnostic Context</em>, or
   MDC in short, is an instrument for distinguishing interleaved log
   output from different sources. Log output is typically interleaved
   when a server handles multiple clients near-simultaneously.

   <p><b><em>The MDC is managed on a per thread basis</em></b>. A
   child thread automatically inherits a <em>copy</em> of the mapped
   diagnostic context of its parent.
  
   <p>The MDC class requires JDK 1.2 or above. Under JDK 1.1 the MDC
   will always return empty values but otherwise will not affect or
   harm your application.
   
   @since 1.2

   @author Ceki G&uuml;lc&uuml; */
public class MDC {
  
  static final int HT_SIZE = 7;
  
  static private final ThreadLocalMap tlm = new ThreadLocalMap();
  
  private MDC() {
  }

  /**
   * Put a context value (the <code>o</code> parameter) as identified
   * with the <code>key</code> parameter into the current thread's
   * context map.
   *
   * <p>If the current thread does not have a context map it is
   * created as a side effect.
   * */
  public static void put(String key, Object o) {
    Hashtable ht = (Hashtable) tlm.get();
    if(ht == null) {
      ht = new Hashtable(HT_SIZE);
      tlm.set(ht);
    }    
    ht.put(key, o);
  }

  /**
   * Get the context identified by the <code>key</code> parameter.
   *
   *  <p>This method has no side effects.  
   * */  
  static public Object get(String key) {
    Hashtable ht = (Hashtable) tlm.get();
    if(ht != null && key != null) {
      return ht.get(key);
    } else {
      return null;
    }
  }

  /**
   * Remove the the context identified by the <code>key</code>
   * parameter. */
  public static void remove(String key) {
    Hashtable ht = (Hashtable) tlm.get();
    if(ht != null) {
      ht.remove(key);
    }
  }

  /**
   * Clear all entries in the MDC. 
   * @since 1.3
   */
  public static void clear() {
    Hashtable ht = (Hashtable) tlm.get();
    if(ht != null) {
      ht.clear();
    }
  }


  /**
   * Get the current thread's MDC as a hashtable. This method is
   * intended to be used internally.  
   * */
  public static Hashtable getContext() {
    return (Hashtable) tlm.get();
  }
}
