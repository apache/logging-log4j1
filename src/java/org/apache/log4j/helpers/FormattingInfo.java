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

package org.apache.log4j.helpers;


/**
   FormattingInfo instances contain the information obtained when parsing
   formatting modifiers in conversion modifiers.

   @author <a href=mailto:jim_cakalic@na.biomerieux.com>Jim Cakalic</a>
   @author Ceki G&uuml;lc&uuml;

   @since 0.8.2   
 */
public class FormattingInfo {
  int min = -1;
  int max = 0x7FFFFFFF;
  boolean leftAlign = false;

  void reset() {
    min = -1;
    max = 0x7FFFFFFF;
    leftAlign = false;      
  }

  void dump() {
    LogLog.debug("min="+min+", max="+max+", leftAlign="+leftAlign);
  }
}
 
