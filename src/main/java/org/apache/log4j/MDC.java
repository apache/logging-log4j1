/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.log4j;

import org.apache.log4j.helpers.ThreadLocalMap;

import java.util.Enumeration;
import java.util.Hashtable;


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
  private static final ThreadLocalMap tlm = new ThreadLocalMap();

  private MDC() {
  }

  /**
   * Put a context value (the <code>val</code> parameter) as identified
   * with the <code>key</code> parameter into the current thread's
   * context map.
   *
   * <p>If the current thread does not have a context map it is
   * created as a side effect.
   * */
  public static void put(String key, String val) {
    Hashtable ht = (Hashtable) tlm.get();

    if (ht == null) {
      ht = new Hashtable(HT_SIZE);
      tlm.set(ht);
    }

    ht.put(key, val);
  }

  /**
   * Please use the {@link #put(String key, String val)} form instead. The 'val'
   * parameter needs to be a string.
   * 
   * @param key the key for the contextual information
   * @param val is transformed into a string before being placed in the MDC
   * @deprecated please use the {@link #put(String key, String val)} form.
   */
  public static void put(String key, Object val) {
    put(key, val.toString());
  }
  
  /**
   * Get the context identified by the <code>key</code> parameter.
   *
   *  <p>This method has no side effects.
   * */
  public static Object get(String key) {
    Hashtable ht = (Hashtable) tlm.get();

    if ((ht != null) && (key != null)) {
      return (String) ht.get(key);
    } else {
      return null;
    }
  }

  /**
   * Remove the the context identified by the <code>key</code>
   * parameter. */
  public static void remove(String key) {
    Hashtable ht = (Hashtable) tlm.get();

    if (ht != null) {
      ht.remove(key);
    }
  }

  /**
   * Clear all entries in the MDC.
   * @since 1.3
   */
  public static void clear() {
    Hashtable ht = (Hashtable) tlm.get();

    if (ht != null) {
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
  
  /** 
   * Returns the keys in the MDC as an {@link Enumeration}. The returned value can be 
   * null.
   * 
   * @since version 1.3
   */
  public static Enumeration getKeys() {
	  Hashtable ht = (Hashtable) tlm.get();

	  if (ht != null) {
  		return ht.keys();
	  } else {
	  	return null;
	  }
  }
}
