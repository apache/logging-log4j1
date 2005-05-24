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

package org.apache.log4j.or;

/**
   The default Renderer renders objects by calling their
   <code>toString</code> method.

   @author Ceki G&uuml;lc&uuml;
   @since 1.0 */
class DefaultRenderer implements ObjectRenderer {
  
  DefaultRenderer() {
  }

  /**
     Render the object passed as parameter by calling its
     <code>toString</code> method.  */
  public
  String doRender(Object o) {
    return o.toString();
  }
}  
