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

package org.apache.log4j.db;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import org.xml.sax.Attributes;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;


public class BindDataSourceToJNDIAction extends Action {
  static final Logger logger =
    Logger.getLogger(BindDataSourceToJNDIAction.class);
  static final String DATA_SOURCE_CLASS = "dataSourceClass";
  static final String URL = "url";
  static final String USER = "user";
  static final String PASSWORD = "password";
  private boolean inError = false;

  /**
   * Instantiates an a data source and bind it to JNDI
   * Most of the required parameters are placed in the ec.substitutionProperties
   */
  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    String dsClassName = ec.getSubstitutionProperty(DATA_SOURCE_CLASS);

    LogLog.info("==============************************");
    if (Option.isEmpty(dsClassName)) {
      LogLog.warn("dsClassName is a required parameter");
      ec.addError(new ErrorItem("dsClassName is a required parameter"));

      return;
    }

    String urlStr = ec.getSubstitutionProperty(URL);
    String userStr = ec.getSubstitutionProperty(USER);
    String passwordStr = ec.getSubstitutionProperty(PASSWORD);

    try {
      DataSource ds =
        (DataSource) OptionConverter.instantiateByClassName(
          dsClassName, DataSource.class, null);

      PropertySetter setter = new PropertySetter(ds);

      if (!Option.isEmpty(urlStr)) {
        setter.setProperty("url", urlStr);
      }

      if (!Option.isEmpty(userStr)) {
        setter.setProperty("user", userStr);
      }

      if (!Option.isEmpty(passwordStr)) {
        setter.setProperty("password", passwordStr);
      }

      Context ctx = new InitialContext();
      ctx.rebind("dataSource", ds);
    } catch (Exception oops) {
      inError = true;
      logger.error(
        "Could not bind  datasource. Reported error follows.", oops);
      ec.addError(
        new ErrorItem(
          "Could not not bind  datasource of type [" + dsClassName + "]."));
    }
  }

  public void end(ExecutionContext ec, String name) {
  }

  public void finish(ExecutionContext ec) {
  }
}
