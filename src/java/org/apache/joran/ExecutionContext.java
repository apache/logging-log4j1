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

package org.apache.joran;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.xml.sax.Locator;


/**
 *
 * Joran is designed to parse DOM trees
 *
 */
public class ExecutionContext {
	final static Logger logger = Logger.getLogger(ExecutionContext.class);

  Stack objectStack;
	HashMap objectMap;
	
	Vector errorList;
	Properties substProperties;
	
	Interpreter joranInterpreter;
    
	public ExecutionContext(Interpreter joranInterpreter) {
		this.joranInterpreter = joranInterpreter;
		objectStack = new Stack();
		objectMap = new HashMap(5);
		errorList = new Vector();
	}
	
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
    return 	objectStack.pop();
  }
  
  public Object getObject(int i) {
		return objectStack.get(i);
  }
 
  public HashMap getObjectMap() {
    return objectMap;
  }
  
	public String subst(String value) {
		try {
			return OptionConverter.substVars(value, substProperties);
		} catch (IllegalArgumentException e) {
			logger.warn("Could not perform variable substitution for variable ["
			+value+"]", e);
			return value;
		}
	}

}
