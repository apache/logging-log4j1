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


import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.ErrorItem;

import org.xml.sax.Attributes;


public class ParamAction extends Action {
  static String NO_NAME = "No name attribute in <param> element";
  static String NO_VALUE = "No name attribute in <param> element";

  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    String name = attributes.getValue(NAME_ATTRIBUTE);
    String value = attributes.getValue(VALUE_ATTRIBUTE);

    if (name == null) {
      getLogger().error(NO_NAME);
      ec.addError(new ErrorItem(NO_NAME));
      return;
    }

    if (value == null) {
      getLogger().error(NO_VALUE);
      ec.addError(new ErrorItem(NO_VALUE));
      return;
    }

    // remove both leading and trailing spaces
    value = value.trim();

    Object o = ec.peekObject();
    PropertySetter propSetter = new PropertySetter(o);
    value = ec.subst(value);

    // allow for variable substitution for name as well
    name = ec.subst(name);

    getLogger().debug(
      "In ParamAction setting parameter [{}] to value [{}].", name, value);
    propSetter.setProperty(name, value);
  }

  public void end(ExecutionContext ec, String localName) {
  }

  public void finish(ExecutionContext ec) {
  }
}
