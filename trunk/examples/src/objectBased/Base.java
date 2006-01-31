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

package objectBased;

import org.apache.log4j.Logger;


/**
 * 
 * An almost trivially simple Base class.
 * 
 * It uses the {@link #getLogger} method to retreive the logger to use. This 
 * method can be overrriden by derived clases to acheive "object" based logging.
 * 
 * @author Scott Melcher
 * @author G&uuml;lc&uuml;
 */
public class Base {
  static Logger logger = Logger.getLogger(Base.class);

  public void myMethod() {
    getLogger().debug("logging message");
  }

  public Logger getLogger() {
    System.out.println("Base.getLogger called");
    return Base.logger;
  }
  
}
