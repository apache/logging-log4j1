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

package org.apache.joran.action;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.helper.Option;

import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.spi.OptionHandler;

import org.xml.sax.Attributes;

/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class NestComponentIA extends ImplicitAction {
  static final Logger logger = Logger.getLogger(NestComponentIA.class);

  Object nestedComponent;
  int containmentType;
  PropertySetter parentBean;
  
  public boolean isApplicable(ExecutionContext ec, String nestedElementTagName) {
    inError = false;
    Object o = ec.peekObject();
    parentBean = new PropertySetter(o);

    containmentType = parentBean.canContainComponent(nestedElementTagName);

    switch (containmentType) {
    case PropertySetter.NOT_FOUND:
      return false;

    case PropertySetter.AS_COLLECTION:
      return true;

    case PropertySetter.AS_PROPERTY:
      return true;
      
      default: 
      inError= true;
      ec.addError(new ErrorItem("PropertySetter.canContainComponent returned "+containmentType, ec.getLocator()));
      return false;
    }
  }

  public void begin(ExecutionContext ec, String localName, Attributes attributes) {
    // inError was reset in isApplicable. It should not be touched here

      String className = attributes.getValue(CLASS_ATTRIBUTE);
      
     
      if(Option.isEmpty(className)) {
        inError = true;
        String errMsg = "No class name attribute in <"+localName+">";
        logger.error(errMsg);
        ec.addError(new ErrorItem(errMsg, ec.getLocator()));
        return;
      }
      
      try {
        logger.debug("About to instantiate component <"+localName+ "> of type [" + className + "]");

        nestedComponent = Loader.loadClass(className).newInstance();
         
            
        logger.debug("Pushing component <"+localName+"> on top of the object stack.");
        ec.pushObject(nestedComponent);
      } catch (Exception oops) {
        inError = true;      
        String msg =  "Could not create component <"+localName+">.";
        logger.error(msg, oops);
        ec.addError(new ErrorItem(msg, ec.getLocator()));
      }
  }

  public void end(ExecutionContext ec, String tagName) {
    
    logger.debug("entering end method");
    if (inError) {
        return;
      }

      if (nestedComponent instanceof OptionHandler) {
        ((OptionHandler) nestedComponent).activateOptions();
      }

      Object o = ec.peekObject();

      if (o != nestedComponent) {
        logger.warn(
          "The object on the top the of the stack is not the component pushed earlier.");
      } else {
        logger.warn("Removing component from the object stack");
        ec.popObject();

        // Now let us attach the component
        switch (containmentType) {
        case PropertySetter.AS_PROPERTY:
        logger.debug("Setting ["+tagName+"] to parent.");
          parentBean.setComponent(tagName, nestedComponent);
          break;

        case PropertySetter.AS_COLLECTION:
        logger.debug("Adding ["+tagName+"] to parent.");
          parentBean.addComponent(tagName, nestedComponent);
          break;
        } 
      }
  }

  public void finish(ExecutionContext ec) {
  }
}
