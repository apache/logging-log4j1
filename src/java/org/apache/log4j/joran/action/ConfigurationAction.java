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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggerRepository;

import org.xml.sax.Attributes;


public class ConfigurationAction extends ActionBase {
  static final Logger logger = Logger.getLogger(ConfigurationAction.class);
  static final String INTERNAL_DEBUG_ATTR = "debug";
  boolean attachment = false;

  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    String debugAttrib = attributes.getValue(INTERNAL_DEBUG_ATTR);

    if (
      (debugAttrib == null) || debugAttrib.equals("")
        || debugAttrib.equals("null")) {
      LogLog.debug("Ignoring " + INTERNAL_DEBUG_ATTR + " attribute.");
    } else {
      LoggerRepository repository = (LoggerRepository) ec.getObject(0);
      attachTemporaryConsoleAppender(repository);
      attachment = true;
    }
  }

  protected void attachTemporaryConsoleAppender(LoggerRepository repository) {
    Logger ll = repository.getLogger(Constants.LOG4J_PACKAGE_NAME);
    
    ConsoleAppender appender = new ConsoleAppender();
    appender.setLayout(
      new PatternLayout("LOG4J-INTERNAL: %d %level [%t] %c - %m%n"));
    appender.setName(Constants.TEMP_CONSOLE_APPENDER_NAME);
    appender.activateOptions();
    ll.addAppender(appender);
  }

  protected void detachTemporaryConsoleAppender(ExecutionContext ec) {
    LoggerRepository repository = (LoggerRepository) ec.getObject(0);
    Logger ll = repository.getLogger(Constants.LOG4J_PACKAGE_NAME);
    ConsoleAppender consoleAppender =
      (ConsoleAppender) ll.getAppender(Constants.TEMP_CONSOLE_APPENDER_NAME);
    if (consoleAppender == null) {
      String errMsg =
        "Could not find appender " + Constants.TEMP_LIST_APPENDER_NAME;
      getLogger(repository).error(errMsg);
      ec.addError(new ErrorItem(errMsg));
      return;
    }
    consoleAppender.close();
    ll.removeAppender(consoleAppender);
  }

  public void finish(ExecutionContext ec) {
  }

  public void end(ExecutionContext ec, String e) {
    if (attachment) {
      detachTemporaryConsoleAppender(ec);
    }
  }
}
