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

import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;

import org.xml.sax.Attributes;

import java.lang.reflect.Method;


public class LoggerAction extends Action {
  boolean inError = false;
  
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
