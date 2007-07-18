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

import org.apache.log4j.DefaultLoggerFactory;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.joran.spi.ActionException;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.xml.sax.Attributes;

public class LoggerFactoryAction extends Action {

  public void begin(ExecutionContext ec, String name, Attributes attributes) throws ActionException {
    LoggerRepositoryEx repository = (LoggerRepositoryEx) ec.getObject(0);
    String factoryClassName = attributes.getValue(CLASS_ATTRIBUTE);

    if (factoryClassName != null) {
      LoggerFactory loggerFactory =
        (LoggerFactory) OptionConverter.instantiateByClassName(
            factoryClassName, LoggerFactory.class, new DefaultLoggerFactory());
      repository.setLoggerFactory(loggerFactory);
    }
    else
    {
      getLogger().warn("Missing " + CLASS_ATTRIBUTE + " for logger factory");
    }
  }

  public void end(ExecutionContext ec, String name) throws ActionException {
  }

}
