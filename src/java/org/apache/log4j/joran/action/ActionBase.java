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

import org.apache.joran.action.Action;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;


/**
 * ActionBase extension {@link Action} by adding logging capabilities
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public abstract class ActionBase extends Action {
  Logger logger;

  Logger getLogger(LoggerRepository repository) {
    if (logger == null) {
      logger = repository.getLogger(this.getClass().getName());
    }
    return logger;
  }
}
