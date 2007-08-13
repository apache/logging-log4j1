/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.helpers;

import java.util.Hashtable;

/**
   <code>ThreadLocalMap</code> extends {@link InheritableThreadLocal}
   to bequeath a copy of the hashtable of the MDC of the parent
   thread.

   @author Ceki G&uuml;lc&uuml;
   @since 1.2
*/
final public class ThreadLocalMap extends InheritableThreadLocal {

  public
  final
  Object childValue(Object parentValue) {
    Hashtable ht = (Hashtable) parentValue;
    if(ht != null) {
      return ht.clone();
    } else {
      return null;
    }
  }
}
