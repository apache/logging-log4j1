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

package org.apache.log4j.db;

import java.util.Set;

import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBHelper {
  
  public static short PROPERTIES_EXIST = 0x01;
  public static short EXCEPTION_EXISTS = 0x02;
  
  static short computeReferenceMask(LoggingEvent event) {
    short mask = 0;
    Set propertiesKeys = event.getPropertyKeySet();
    if(propertiesKeys.size() > 0) {
      mask = PROPERTIES_EXIST;
    }
    String[] strRep = event.getThrowableStrRep();
    if(strRep != null) {
      mask |= EXCEPTION_EXISTS;
    }
    return mask;
  }
  

}
