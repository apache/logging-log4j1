/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;


/**
  Interface used to listen for {@link Logger} related events such as
  add/remove appender or changing levels.  Clients register an instance of
  the interface and the instance is called back when the various events occur.

  {@link LoggerRepository} provides methods for adding and removing
  LoggerEventListener instances.

  When implementing the methods of this interface, it is useful to remember
  that the Logger can access the repository using its getRepository()
  method.

  @author Ceki G&uuml;lc&uuml;
  @author Mark Womack
  @since 1.3
*/
public interface LoggerEventListener {
  /**
    Called when an appender is added to the logger.

    @param logger The logger to which the appender was added.
    @param appender The appender added to the logger. */
  void appenderAddedEvent(Logger logger, Appender appender);

  /**
    Called when an appender is removed from the logger.

    @param logger The logger from which the appender was removed.
    @param appender The appender removed from the logger. */
  void appenderRemovedEvent(Logger logger, Appender appender);

  /**
    Called when level changed on the logger.

    @param logger The logger that changed levels. */
  void levelChangedEvent(Logger logger);
}
