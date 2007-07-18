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
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.Option;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;

import org.xml.sax.Attributes;

import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class LoggerAction extends Action {
  boolean inError = false;
  
  final static String RESOURCE_BUNDLE = "resourceBundle";
  
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    LoggerRepository repository = (LoggerRepository) ec.getObject(0);

    // Create a new org.apache.log4j.Category object from the <category> element.
    String loggerName = attributes.getValue(NAME_ATTRIBUTE);

    if (Option.isEmpty(loggerName)) {
      inError = true;

      String line =
        ", around line " + getLineNumber(ec) + " column "
        + getColumnNumber(ec);

      String errorMsg = "No 'name' attribute in element " + name + line;

      getLogger().warn(errorMsg);
      ec.addError(new ErrorItem(errorMsg));

      return;
    }

    getLogger().debug("Logger name is [" + loggerName + "].");

    Logger l;

    String className = attributes.getValue(CLASS_ATTRIBUTE);

    if (Option.isEmpty(className)) {
      getLogger().debug("Retreiving an instance of org.apache.log4j.getLogger().");
      l = repository.getLogger(loggerName);
    } else {
      getLogger().debug("Desired logger sub-class: [" + className + ']');

      try {
        Class clazz = Loader.loadClass(className);
        Method getInstanceMethod =
          clazz.getMethod("getLogger", ActionConst.ONE_STRING_PARAM);
        l = (Logger) getInstanceMethod.invoke(
            null, new Object[] { loggerName });
      } catch (Exception oops) {
        getLogger().error(
          "Could not retrieve category [" + loggerName
          + "]. Reported error follows.", oops);

        return;
      }
    }

    boolean additivity =
      OptionConverter.toBoolean(
        attributes.getValue(ActionConst.ADDITIVITY_ATTRIBUTE), true);
    getLogger().debug(
      "Setting [" + l.getName() + "] additivity to [" + additivity + "].");
    l.setAdditivity(additivity);

    getLogger().debug("Pushing logger named [" + loggerName + "].");
    ec.pushObject(l);
    
    String resourceBundle = attributes.getValue(RESOURCE_BUNDLE);
    try {
      if (resourceBundle != null) {
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundle);
        l.setResourceBundle(bundle);
        getLogger().debug(
            "Setting [" + l.getName() + "] resourceBundle to [" + resourceBundle + "].");
      }
    } catch (MissingResourceException e) {
      getLogger().error("Error loading resource bundle [" + resourceBundle + "]", e);
    }
  }

  public void end(ExecutionContext ec, String e) {
    getLogger().debug("end() called.");

    if (!inError) {
      getLogger().debug("Removing logger from stack.");
      ec.popObject();
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
