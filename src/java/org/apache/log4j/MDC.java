/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j;

import org.apache.log4j.helpers.ThreadLocalMap;

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
   * Put a context value (the <code>o</code> parameter) as identified
   * with the <code>key</code> parameter into the current thread's
   * context map.
   *
   * <p>If the current thread does not have a context map it is
   * created as a side effect.
   * */
  public static void put(String key, Object o) {
    Hashtable ht = (Hashtable) tlm.get();

    if (ht == null) {
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
  public static Object get(String key) {
    Hashtable ht = (Hashtable) tlm.get();

    if ((ht != null) && (key != null)) {
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
}
