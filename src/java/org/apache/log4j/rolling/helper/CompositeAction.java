/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.rolling.helper;

import java.io.IOException;

import java.util.List;
import org.apache.log4j.ULogger;


/**
 * A group of Actions to be executed in sequence.
 *
 * @author Curt Arnold
 * @since 1.3
 *
 */
public class CompositeAction extends ActionBase {
  /**
   * Actions to perform.
   */
  private final Action[] actions;

  /**
   * Stop on error.
   */
  private final boolean stopOnError;
    /**
     * Logger.
     */
  private final ULogger logger;

  /**
   * Construct a new composite action.
   * @param actions list of actions, may not be null.
   * @param stopOnError if true, stop on the first false return value or exception.
   * @param logger logger, may be null.
   */
  public CompositeAction(final List actions, 
                         final boolean stopOnError, 
                         final ULogger logger) {
    this.actions = new Action[actions.size()];
    actions.toArray(this.actions);
    this.stopOnError = stopOnError;
    this.logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  public void run() {
    try {
      execute();
    } catch (IOException ex) {
        if (logger != null) {
            logger.info("Exception during file rollover.", ex);
        }
    }
  }

  /**
   * Execute sequence of actions.
   * @return true if all actions were successful.
   * @throws IOException on IO error.
   */
  public boolean execute() throws IOException {
    if (stopOnError) {
      for (int i = 0; i < actions.length; i++) {
        if (!actions[i].execute()) {
          return false;
        }
      }

      return true;
    } else {
      boolean status = true;
      IOException exception = null;

      for (int i = 0; i < actions.length; i++) {
        try {
          status &= actions[i].execute();
        } catch (IOException ex) {
          status = false;

          if (exception == null) {
            exception = ex;
          }
        }
      }

      if (exception != null) {
        throw exception;
      }

      return status;
    }
  }
}
