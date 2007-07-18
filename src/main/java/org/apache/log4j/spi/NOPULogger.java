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
 * A no operation (NOP) implementation of {@link ULogger}.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class NOPULogger implements ULogger {

    /**
     * The unique instance of NOPLogger.
     */
    public static final NOPULogger NOP_LOGGER = new NOPULogger();

    /**
     * There is no point in people creating multiple instances of NullLogger.
     * Hence, the private access modifier.
     */
    private NOPULogger() {
        super();
    }

    /**
     * Get instance.
     * @param name logger name.
     * @return logger.
     */
    public static NOPULogger getLogger(final String name) {
        return NOP_LOGGER;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDebugEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object msg) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object parameterizedMsg, final Object param1) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final String parameterizedMsg,
                      final Object param1,
                      final Object param2) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void debug(final Object msg, final Throwable t) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInfoEnabled() {
        // NOP
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object msg) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object parameterizedMsg, final Object param1) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void info(final String parameterizedMsg,
                     final Object param1, final Object param2) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void info(final Object msg, final Throwable t) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWarnEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object msg) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object parameterizedMsg,
                     final Object param1) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final String parameterizedMsg,
                     final Object param1,
                     final Object param2) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void warn(final Object msg, final Throwable t) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public boolean isErrorEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object msg) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object parameterizedMsg, final Object param1) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void error(final String parameterizedMsg,
                      final Object param1,
                      final Object param2) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    public void error(final Object msg, final Throwable t) {
        // NOP
    }

}
