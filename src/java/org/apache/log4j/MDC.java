/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j;

import java.util.Hashtable;
//import org.apache.log4j.helpers.ThreadLocalMap;
import org.apache.log4j.helpers.ITLContext;

/**
   The MDC class supercedes the {@link NDC} class. It provides
   <em>mapped diagnostic contexts</em>. A <em>Mapped Diagnostic
   Context</em>, or MDC in short, is an instrument to distinguish
   interleaved log output from different sources. Log output is
   typically interleaved when a server handles multiple clients
   near-simultaneously.

   <p><b><em>The MDC is managed on a per thread basis</em></b>. A
   child thread automatically inherits a <em>copy</em> of the mapped
   diagnostic context of its parent which is managed independently of
   the parent's context.

   

   <p>The MDC class requires JDK 1.2. It will not work under JDK 1.1.

   
   @since 1.2

   @author Ceki G&uuml;lc&uuml; */
public class MDC {
  
  final static ITLContext context = new ITLContext();
  
  /**
     Put a context value (the <code>o</code> parameter) as identified
     with the <code>key</code> parameter into the current thread's
     context map.

     <p>If the current thread does not have a context map it is
     created as a side effect.
    
   */
  static
  public
  void put(String key, Object o) {
    context.put(key, o);
  }
  
  /**
     Get the context identified by the <code>key</code> parameter.

     <p>This method has no side effects.
   */
  static 
  public
  Object get(String key) {
    return context.get(key);
  }

  public
  static
  Hashtable getContext() {
    //return (Hashtable) context.get();
    return null;
  }

}
