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

import org.apache.log4j.ULogger;


/**
 * Most log4j components derive from this class.
 *
 * @author Ceki Gulcu
 * @since 1.3
 */
public class ComponentBase implements Component {

    /**
     * Error count limit.
     */
    private static final int ERROR_COUNT_LIMIT = 3;

    /**
     * Logger repository.
     */
    protected LoggerRepository repository;
    /**
     * Logger.
     */
    private ULogger logger;
    /**
     * Error count.
     */
    private int errorCount = 0;

    /**
     * Construct a new instance.
     */
    protected ComponentBase() {
        super();
    }


    /**
     * Called by derived classes when they deem that the component has recovered
     * from an erroneous state.
     */
    protected void resetErrorCount() {
        errorCount = 0;
    }

    /**
     * Set the owning repository. The owning repository cannot be set more than
     * once.
     *
     * @param repository repository
     */
    public void setLoggerRepository(final LoggerRepository repository) {
        if (this.repository == null) {
            this.repository = repository;
        } else if (this.repository != repository) {
            throw new IllegalStateException("Repository has been already set");
        }
    }

    /**
     * Return the {@link LoggerRepository} this component is attached to.
     *
     * @return Owning LoggerRepository
     */
    protected LoggerRepository getLoggerRepository() {
        return repository;
    }

    /**
     * Return an instance specific logger to be used by the component itself.
     * This logger is not intended to be accessed by the end-user, hence the
     * protected keyword.
     * <p/>
     * <p>In case the repository for this component is not set,
     * this implementations returns a {@link SimpleULogger} instance.
     *
     * @return A ULogger instance.
     */
    protected ULogger getLogger() {
        if (logger == null) {
            if (repository != null) {
                logger = repository.getLogger(this.getClass().getName());
            } else {
                logger = SimpleULogger.getLogger(this.getClass().getName());
            }
        }
        return logger;
    }

    /**
     * Frequently called methods in log4j components can invoke this method in
     * order to avoid flooding the output when logging lasting error conditions.
     *
     * @return a regular logger, or a NOPLogger if called too frequently.
     */
    protected ULogger getNonFloodingLogger() {
        if (errorCount++ >= ERROR_COUNT_LIMIT) {
            return NOPULogger.NOP_LOGGER;
        } else {
            return getLogger();
    }
  }
}
