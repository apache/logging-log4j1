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

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;

import org.xml.sax.Attributes;

public class PluginAction extends Action {
  static final Logger logger = Logger.getLogger(PluginAction.class);
  Plugin plugin;

  /**
   * Instantiates an plugin of the given class and sets its name.
   *
   * The plugin thus generated is placed in the ExecutionContext plugin bag.
   */
  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    String className = attributes.getValue(CLASS_ATTRIBUTE);

    try {
      logger.debug(
        "About to instantiate plugin of type [" + className + "]");

      Object instance =
        OptionConverter.instantiateByClassName(
          className, org.apache.log4j.plugins.Plugin.class, null);
      plugin = (Plugin) instance;

      String pluginName = attributes.getValue(NAME_ATTRIBUTE);

      if (Option.isEmpty(pluginName)) {
        logger.warn(
          "No plugin name given for plugin of type " + className + "].");
      } else {
        plugin.setName(pluginName);
        logger.debug("plugin named as [" + pluginName + "]");
      }

      LoggerRepository repository = (LoggerRepository) ec.getObject(0);
      
      repository.getPluginRegistry().addPlugin(plugin);
	    plugin.setLoggerRepository(repository);
      
      logger.debug("Pushing plugin on to the object stack.");
      ec.pushObject(plugin);
    } catch (Exception oops) {
      inError = true;
      logger.error(
        "Could not create a plugin. Reported error follows.", oops);
      ec.addError(
        new ErrorItem(
          "Could not create plugin of type " + className + "]."));
    }
  }

  /**
   * Once the children elements are also parsed, now is the time to activate
   * the plugin options.
   */
  public void end(ExecutionContext ec, String name) {
    if (inError) {
      return;
    }

    if (plugin instanceof OptionHandler) {
      ((OptionHandler) plugin).activateOptions();
    }

    Object o = ec.peekObject();

    if (o != plugin) {
      logger.warn(
        "The object at the of the stack is not the plugin named ["
        + plugin.getName() + "] pushed earlier.");
    } else {
      logger.warn(
        "Popping plugin named [" + plugin.getName()
        + "] from the object stack");
      ec.popObject();
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
