/*
 * Copyright 1999-2005 The Apache Software Foundation.
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

/**
   CategoryKey is a wrapper for String that apparently accellerated
   hash table lookup in early JVM's.
   @author Ceki G&uuml;lc&uuml; 
*/
class CategoryKey {

  String   name;  
  int hashCache;

  CategoryKey(String name) {
    this.name = name;
    hashCache = name.hashCode();
  }

  final
  public  
  int hashCode() {
    return hashCache;
  }

  final
  public
  boolean equals(Object rArg) {
    if(this == rArg)
      return true;
    
    if(rArg != null && CategoryKey.class == rArg.getClass()) 
      return  name.equals(((CategoryKey)rArg ).name);
    else 
      return false;
  }
}
