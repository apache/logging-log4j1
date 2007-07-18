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

import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.ErrorItem;

import org.xml.sax.Attributes;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * This action looks up JNDI properties specified in the configuration
 * file and adds them to the Joran <code>ExecutionContext</code>.  The
 * element in the configuration file should have an attribute called
 * "name".  This attribute will be the key to the naming context lookup,
 * as well as the key to the <code>ExecutionContext</code> properties.  If a value
 * is found whose name matches the given name, it will be placed
 * in the ExecutionContext's properties.
 *
 * @author Yoav Shapira
 */
public class JndiSubstitutionPropertyAction extends Action {
  /**
   * The naming context.
   */
  private Context namingContext;

  /**
   * Returns the naming context for lookups.
   *
   * @return The context (may be null)
   */
  protected Context getNamingContext() {
    return namingContext;
  }

  /**
   * Creates the naming context.  This is an expensive
   * operation.
   *
   * @throws NamingException If an error occurs
   */
  protected void findNamingContext() throws NamingException {
    if (getNamingContext() != null) {
      getLogger().warn("Overwriting existing naming context.");
    }

    // POSSIBLE TO-DO: add support for properties
    // passed to the initial context, e.g. a factory,
    // to enable things like a remote context.
    InitialContext ic = new InitialContext();
    namingContext = (Context) ic.lookup("java:comp/env");
  }

  /**
   * @see Action#begin
   */
  public void begin(
    final ExecutionContext ec, final String name, final Attributes attributes) {
    // If first time, create and locate context: expensive operation.
    if (getNamingContext() == null) {
      try {
        findNamingContext();
      } catch (Exception e) {
        getLogger().error("Couldn't find JNDI naming context: ", e);
        ec.addError(new ErrorItem("Couldn't find JNDI naming context.", e));
      }
    }

    String jndiName = attributes.getValue(NAME_ATTRIBUTE);

    if ((jndiName == null) || (jndiName.trim().length() < 1)) {
      getLogger().warn("Missing {} attribute, ignoring.", NAME_ATTRIBUTE);
    } else if (getNamingContext() != null) {
      Object value = null;

      try {
        value = getNamingContext().lookup(jndiName);
      } catch (Exception e) {
        getLogger().error("Error looking up " + jndiName + ": ", e);
        ec.addError(new ErrorItem("Error looking up " + jndiName, e));
      }

      if (value == null) {
        getLogger().warn("No JNDI value found for {}.", jndiName);
      } else if (!(value instanceof String)) {
        getLogger().warn("Value for {} is not a String.", jndiName);
      } else {
        ec.addProperty(jndiName, (String) value);
      }
    } else {
      getLogger().warn("Naming context is null, cannot lookup {}", jndiName);
    }
  }

  /**
   * @see Action#end
   */
  public void end(final ExecutionContext ec, final String name) {
  }
}

// End of class: JndiSubstitutionPropertyAction.java
