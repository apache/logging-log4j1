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
package org.apache.logging.core.impl.log4j1;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.core.PropertyContext;
import org.apache.logging.core.ValueContext;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 *  This class wraps an instance of org.apache.log4j.spi.LoggingEvent
 * to implement the org.apache.logging.core.LogRecord interface.
 */
public final class Log4j1Context
        implements ValueContext, PropertyContext,
            Serializable {
    private static final Method getPropertiesMethod;
    static {
        Method getProps;
            try {
               getProps = LoggingEvent.class.getMethod(
                          "getProperties");
            } catch(Exception ex) {
               getProps = null;
            }
        getPropertiesMethod = getProps;
    }

    /**
     * Underlying instance.
     */
    private final LoggingEvent base;

    /**
     * Create new instance.
     * @param source underlying instance, may not be null.
     */
    private Log4j1Context(final LoggingEvent source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        base = source;
    }

    /**
     * Create new instance.
     * @param source underlying instance, may be null.
     * @return new instance.
     */
    public static Log4j1Context getInstance(
            final LoggingEvent source) {
        if (source == null) {
            return null;
        }
        return new Log4j1Context(source);
    }


    /**
     * {@inheritDoc}
     */
    public Object getValue() {
        return base.getNDC();
    }

    /**
     * {@inheritDoc}
     *
     */
    public Map<String,Object> getProperties() {
        return new HashMap<String, Object>();
    }
}
