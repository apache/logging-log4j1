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

package org.apache.log4j.lbel;

import org.apache.log4j.spi.LoggingEvent;

/**
 * An EventEvaluator has the responsability to evaluate whether a given {@link 
 * LoggingEvent} matches a given criteria. 
 * 
 * <p>Implementations are free to evaluate the event as they see fit. In 
 * particular, the evaluation results <em>may</em> depend on previous events.
   *    
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * @since 1.3
 */

public interface EventEvaluator {
  

  /**
   * Evaluates whether the event passed as parameter matches this evaluator's 
   * matching criteria.
   * 
   * <p>The <code>Evaluator</code> instance is free to evaluate the event as
   * it pleases. In particular, the evaluation results <em>may</em> depend on 
   * previous events. 
   * 
   * @param event The event to evaluate
   * @return true if there is a match, false otherwise. 
   * @throws NullPointerException can be thrown in presence of null values
   */
  boolean evaluate(LoggingEvent event) throws NullPointerException;
}
