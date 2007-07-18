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


import org.apache.log4j.Logger;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.LoggerRepository;

import org.xml.sax.Attributes;

public class RootLoggerAction extends Action {
  static final String NAME_ATTR = "name";
  static final String CLASS_ATTR = "class";
  static final String ADDITIVITY_ATTR = "additivity";
  static final String EMPTY_STR = "";
  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };
 
  Logger root;
  boolean inError = false;
  
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    inError = false;
    //logger.debug("In begin method");

    LoggerRepository repository = (LoggerRepository) ec.getObject(0);
    root = repository.getRootLogger();

    getLogger().debug("Pushing root logger on stack");
    ec.pushObject(root);
  }

  public void end(ExecutionContext ec, String name) {
    //logger.debug("end() called.");

    if (inError) {
      return;
    }

    Object o = ec.peekObject();

    if (o != root) {
      getLogger().warn(
        "The object on the top the of the stack is not the root logger");
      getLogger().warn("It is: "+o);
    } else {
      getLogger().debug("Removing root logger from top of stack.");
      ec.popObject();
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
