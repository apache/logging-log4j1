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

package org.apache.log4j.joran.action;

import java.util.Properties;

import org.apache.joran.ExecutionContext;


/**
 * This action set new substitution property for the execution context by name, 
 * value pair, or adds all the properties found in the specified file.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class SubstitutionPropertyAction extends PropertyAction {

  public void setProperties(ExecutionContext ec, Properties props) {
    ec.addProperties(props);
  }
  
  public void setProperty(ExecutionContext ec, String key, String value) {
    ec.addProperty(key, value);
  }
}
