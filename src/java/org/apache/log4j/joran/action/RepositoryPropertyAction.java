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

import java.util.Properties;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.log4j.spi.LoggerRepository;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RepositoryPropertyAction extends PropertyAction {

  LoggerRepository getLoggerRepository(ExecutionContext ec) {
    Object o = ec.getObjectStack().get(0);
    if(o instanceof LoggerRepository) {
      return (LoggerRepository) o;
    } else {
      String errMsg = "There is no LoggerRepository at the top of the object stack.";
      ec.addError(new ErrorItem(errMsg));
      throw new IllegalStateException(errMsg);
    }
  }
  
  public void setProperties(ExecutionContext ec, Properties props) {
    LoggerRepository repository = getLoggerRepository(ec);
    repository.getProperties().putAll(props);
  }
  
  public void setProperty(ExecutionContext ec, String key, String value) {
    LoggerRepository repository = getLoggerRepository(ec);
    repository.setProperty(key, value);
  
  }
}
