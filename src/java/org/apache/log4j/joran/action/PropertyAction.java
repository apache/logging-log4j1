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
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import org.xml.sax.Attributes;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * Set a new property for the execution context by name, value pair, or adds
 * all the properties found in the specified file.
 * 
 * Similar to the ANT <property> task.
 * @author Ceki G&uuml;lc&uuml;
 */
public class PropertyAction extends Action {
  static final Logger logger = Logger.getLogger(PropertyAction.class);
  static String INVALID_ATTRIBUTES =
    "In <property> element, either the filename attribute or both the name and value attributes must be set.";

  /**
   * Set a new property for the execution context by name, value pair, or adds
   * all the properties found in the given file.
   *
   */
  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    String name = attributes.getValue(NAME_ATTRIBUTE);
    String value = attributes.getValue(NAME_ATTRIBUTE);
    String fileName = attributes.getValue(FILE_ATTRIBUTE);

    if (
      !Option.isEmpty(fileName)
        && (Option.isEmpty(name) && Option.isEmpty(value))) {
      Properties props = new Properties();

      try {
        FileInputStream istream = new FileInputStream(fileName);
        props.load(istream);
        istream.close();
      } catch (IOException e) {
        String errMsg = "Could not read properties file [" + fileName + "].";
        LogLog.error(errMsg, e);
        ec.addError(new ErrorItem(INVALID_ATTRIBUTES, e));
        LogLog.error("Ignoring configuration file [" + fileName + "].");
    
      }
    } else if (
      !(Option.isEmpty(name) || Option.isEmpty(value))
        && Option.isEmpty(fileName)) {
      value = OptionConverter.convertSpecialChars(value);
      ec.addProperty(name, value);
    } else {
      LogLog.error(INVALID_ATTRIBUTES);
      ec.addError(new ErrorItem(INVALID_ATTRIBUTES));
    }
  }

  public void end(ExecutionContext ec, String name) {
  }

  public void finish(ExecutionContext ec) {
  }
}
