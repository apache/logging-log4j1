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


import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;

import org.xml.sax.Attributes;


public class LayoutAction extends Action {
  Layout layout;
  boolean inError = false;

  /**
   * Instantiates an layout of the given class and sets its name.
   *
   */
  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    String className = attributes.getValue(CLASS_ATTRIBUTE);
    try {
      getLogger().debug(
        "About to instantiate layout of type [" + className + "]");

    
      layout = (Layout)
        OptionConverter.instantiateByClassName(
          className, org.apache.log4j.Layout.class, null);

      LoggerRepository repo = (LoggerRepository) ec.getObjectStack().get(0);
      layout.setLoggerRepository(repo);

      getLogger().debug("Pushing layout on top of the object stack.");
      ec.pushObject(layout);
    } catch (Exception oops) {
      inError = true;
      getLogger().error(
        "Could not create an Layout. Reported error follows.", oops);
      ec.addError(
        new ErrorItem("Could not create layout of type " + className + "]."));
    }
  }

  /**
   * Once the children elements are also parsed, now is the time to activate
   * the appender options.
   */
  public void end(ExecutionContext ec, String e) {
    if (inError) {
      return;
    }

    layout.activateOptions();
    Object o = ec.peekObject();

    if (o != layout) {
      getLogger().warn(
        "The object on the top the of the stack is not the layout pushed earlier.");
    } else {
      getLogger().debug("Popping layout from the object stack");
      ec.popObject();

      try {
        getLogger().debug(
          "About to set the layout of the containing appender.");
        Appender appender = (Appender) ec.peekObject();
        appender.setLayout(layout);
      } catch (Exception ex) {
        getLogger().error(
          "Could not set the layout for containing appender.", ex);
      }
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
