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

import java.util.Locale;

/**
 * This class wraps an instance of org.apache.log4j.Priority
 * to support the org.apache.logging.core.Level interface.
 *
 *
 */
public class Log4j1Level implements org.apache.logging.core.Level {
    /**
     * Underlying instance of org.apache.log4j.Priority.
     */
    private final org.apache.log4j.Priority base;

    /**
     * Create new instance.
     * @param source underlying instance, may not be null.
     */
    private Log4j1Level(final org.apache.log4j.Priority source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        base = source;
    }


    /**
     * Create new instance.
     * @param source underlying instance.
     * @return new instance or null if source is null.
     */
    public static Log4j1Level getInstance(final org.apache.log4j.Priority source) {
        if (source == null) {
            return null;
        }
        return new Log4j1Level(source);
    }


    /** {@inheritDoc} */
    public String toString() {
        return base.toString();
    }

    /** {@inheritDoc} */
    public int intValue() {
        return base.toInt();
    }

    /** {@inheritDoc} */
    public String getLocalizedName(final Locale locale) {
        return base.toString();
    }
}
