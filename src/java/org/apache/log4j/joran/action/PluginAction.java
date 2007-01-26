/*
 * Copyright 1999,2006 The Apache Software Foundation.
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


import org.apache.log4j.helpers.Option;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.spi.OptionHandler;

import org.xml.sax.Attributes;

public class PluginAction extends Action {
 
  Plugin plugin;
  boolean inError = false;
  
  /**
   * Instantiates an plugin of the given class and sets its name.
   *
   * The plugin thus generated is placed in the ExecutionContext plugin bag.
   */
  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    String className = attributes.getValue(CLASS_ATTRIBUTE);

    try {
      getLogger().debug(
        "About to instantiate plugin of type [" + className + "]");

      plugin = (Plugin)
        OptionConverter.instantiateByClassName(
          className, org.apache.log4j.plugins.Plugin.class, null);

      String pluginName = attributes.getValue(NAME_ATTRIBUTE);

      if (Option.isEmpty(pluginName)) {
        getLogger().warn(
          "No plugin name given for plugin of type " + className + "].");
      } else {
        plugin.setName(pluginName);
        getLogger().debug("plugin named as [" + pluginName + "]");
      }

      LoggerRepository repository = (LoggerRepository) ec.getObject(0);

      //
      //   cast may fail when using user supplied repository
      //
      ((LoggerRepositoryEx) repository).getPluginRegistry().addPlugin(plugin);
	  plugin.setLoggerRepository(repository);
      
      getLogger().debug("Pushing plugin on to the object stack.");
      ec.pushObject(plugin);
    } catch (Exception oops) {
      inError = true;
      getLogger().error(
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

    plugin.activateOptions();
    Object o = ec.peekObject();

    if (o != plugin) {
      getLogger().warn(
        "The object at the of the stack is not the plugin named ["
        + plugin.getName() + "] pushed earlier.");
    } else {
      getLogger().debug(
        "Popping plugin named [{}] from the object stack", plugin.getName());
      ec.popObject();
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
