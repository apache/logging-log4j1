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
import org.apache.joran.Pattern;
import org.apache.joran.helper.Option;

import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.OptionHandler;

import org.xml.sax.Attributes;

import java.util.Stack;


/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class NestComponentIA extends ImplicitAction {
  static final Logger logger = Logger.getLogger(NestComponentIA.class);

  // actionDataStack contains ActionData instances
  // We use a stack of ActionData objects in order to support nested
  // elements which are handled by the same NestComponentIA instance.
  // We push a ActionData instance in the isApplicable method (if the
  // action is applicable) and pop it in the end() method.
  // The XML well-formedness rule will guarantee that a push will eventually
  // be followed by the corresponding pop.
  Stack actionDataStack = new Stack();

  public boolean isApplicable(
    Pattern pattern, Attributes attributes, ExecutionContext ec) {
    //LogLog.debug("in NestComponentIA.isApplicable <" + pattern + ">");

    String nestedElementTagName = pattern.peekLast();

    Object o = ec.peekObject();
    PropertySetter parentBean = new PropertySetter(o);

    int containmentType = parentBean.canContainComponent(nestedElementTagName);

    switch (containmentType) {
    case PropertySetter.NOT_FOUND:
      return false;

    // we only push action data if NestComponentIA is applicable
    case PropertySetter.AS_COLLECTION:
    case PropertySetter.AS_PROPERTY:

      ActionData ad = new ActionData(parentBean, containmentType);
      actionDataStack.push(ad);

      return true;

    default:
      ec.addError(
        new ErrorItem(
          "PropertySetter.canContainComponent returned " + containmentType));

      return false;
    }
  }

  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    //LogLog.debug("in NestComponentIA begin method");

    // get the action data object pushed in isApplicable() method call
    ActionData actionData = (ActionData) actionDataStack.peek();

    String className = attributes.getValue(CLASS_ATTRIBUTE);

    // perform variable name substitution
    className = ec.subst(className);

    if (Option.isEmpty(className)) {
      actionData.inError = true;

      String errMsg = "No class name attribute in <" + localName + ">";
      logger.error(errMsg);
      ec.addError(new ErrorItem(errMsg));

      return;
    }

    try {
      LogLog.debug(
        "About to instantiate component <" + localName + "> of type ["
        + className + "]");

      actionData.nestedComponent = Loader.loadClass(className).newInstance();

      LogLog.debug(
        "Pushing component <" + localName + "> on top of the object stack.");
      ec.pushObject(actionData.nestedComponent);
    } catch (Exception oops) {
      actionData.inError = true;

      String msg = "Could not create component <" + localName + ">.";
      LogLog.error(msg, oops);
      ec.addError(new ErrorItem(msg));
    }
  }

  public void end(ExecutionContext ec, String tagName) {
    logger.debug("entering end method");

    // pop the action data object pushed in isApplicable() method call
    // we assume that each this begin
    ActionData actionData = (ActionData) actionDataStack.pop();

    if (actionData.inError) {
      return;
    }

    if (actionData.nestedComponent instanceof OptionHandler) {
      ((OptionHandler) actionData.nestedComponent).activateOptions();
    }

    Object o = ec.peekObject();

    if (o != actionData.nestedComponent) {
      logger.warn(
        "The object on the top the of the stack is not the component pushed earlier.");
    } else {
      logger.warn("Removing component from the object stack");
      ec.popObject();

      // Now let us attach the component
      switch (actionData.containmentType) {
      case PropertySetter.AS_PROPERTY:
        LogLog.debug(
          "Setting [" + tagName + "] to parent of type ["
          + actionData.parentBean.getObjClass() + "]");
        actionData.parentBean.setComponent(
          tagName, actionData.nestedComponent);

        break;

      case PropertySetter.AS_COLLECTION:
        LogLog.debug(
          "Adding [" + tagName + "] to parent of type ["
          + actionData.parentBean.getObjClass() + "]");
        actionData.parentBean.addComponent(
          tagName, actionData.nestedComponent);

        break;
      }
    }
  }

  public void finish(ExecutionContext ec) {
  }
}


class ActionData {
  PropertySetter parentBean;
  Object nestedComponent;
  int containmentType;
  boolean inError;

  ActionData(PropertySetter parentBean, int containmentType) {
    this.parentBean = parentBean;
    this.containmentType = containmentType;
  }
}
