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

package org.apache.log4j.joran.spi;

//import org.apache.log4j.helpers.OptionConverter;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorItem;
import org.xml.sax.Locator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;


/**
 *
 * The ExecutionContext contains the contextual state of a Joran parsing 
 * session. {@link org.apache.log4j.joran.action.Action Actions} depend on this 
 * context to exchange and store information.
 *  
 * @author Ceki G&uuml;lc&uuml;
 */
public class ExecutionContext {
  Stack objectStack;
  Map objectMap;
  Vector errorList;
  Properties substitutionProperties;
  Interpreter joranInterpreter;

  public ExecutionContext(Interpreter joranInterpreter) {
    this.joranInterpreter = joranInterpreter;
    objectStack = new Stack();
    objectMap = new HashMap(5);
    errorList = new Vector();
    substitutionProperties = new Properties();
  }

//  /**
//   * Clear the internal structures for reuse of the execution context
//   * 
//   */
//  public void clear() {
//    objectStack.clear();
//    objectMap.clear();
//    errorList.clear();
//    substitutionProperties.clear();
//  }
  
  public void addError(ErrorItem errorItem) {
    Locator locator = joranInterpreter.getLocator();

    if (locator != null) {
      errorItem.setLineNumber(locator.getLineNumber());
      errorItem.setColNumber(locator.getColumnNumber());
    }

    errorList.add(errorItem);
  }

  public List getErrorList() {
    return errorList;
  }

  public Locator getLocator() {
    return joranInterpreter.getLocator();
  }

  public Interpreter getJoranInterpreter() {
    return joranInterpreter;
  }

  public Stack getObjectStack() {
    return objectStack;
  }

  public Object peekObject() {
    return objectStack.peek();
  }

  public void pushObject(Object o) {
    objectStack.push(o);
  }

  public Object popObject() {
    return objectStack.pop();
  }

  public Object getObject(int i) {
    return objectStack.get(i);
  }

  public Map getObjectMap() {
    return objectMap;
  }

  /**
   * Add a property to the properties of this execution context.
   * If the property exists already, it is overwritten.
   */
  public void addProperty(String key, String value) {
    if(key == null || value == null) {
      return;
    }
//    if (substitutionProperties.contains(key)) {
//      LogLog.warn(
//        "key [" + key
//        + "] already contained in the EC properties. Overwriting.");
//    }

    // values with leading or trailing spaces are bad. We remove them now.
    value = value.trim();
    substitutionProperties.put(key, value);
  }

 public void addProperties(Properties props) {
    if(props == null) {
      return;
    }
    Iterator i = props.keySet().iterator();
    while(i.hasNext()) {
      String key = (String) i.next();
      addProperty(key, props.getProperty(key));
    }
  }
 
  public String getSubstitutionProperty(String key) {
    return substitutionProperties.getProperty(key);
  }

  public String subst(String value) {
    if(value == null) {
      return null;
    }
    return OptionConverter.substVars(value, substitutionProperties);
  }
}
