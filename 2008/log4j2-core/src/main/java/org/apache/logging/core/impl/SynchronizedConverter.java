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
package org.apache.logging.core.impl;

import org.apache.logging.core.Converter;
import org.apache.logging.core.LogEvent;

import java.io.Serializable;
import java.util.Locale;

/**
 * Class externally synchronizes a Converter.
 * After creation of the SynchronizedConverter
 * the original Converter should not be directly
 * used.
 *
 * @ThreadSafe
 */
public final class SynchronizedConverter<T>
        implements Converter<T>, Serializable {
    /**
     * Base extractor.
     */
    private final Converter<T> base;

    /**
     * Creates a new thread-safe Converter by
     * externally synchronizing an existing Converter.
     * @param original Converter, may not be null.
     */
    public SynchronizedConverter(final Converter<T> original) {
        if (original == null) {
            throw new NullPointerException("original");
        }
        base = original;
    }

    /** {@inheritDoc} */
    public Object extract(final LogEvent record) {
        synchronized(base) {
            return base.extract(record);
        }
    }


    /** {@inheritDoc} */
    public void render(final Object extract,
                       final Locale locale,
                       final T destination) {
        synchronized(base) {
            render(extract, locale, destination);
        }
    }

    /** {@inheritDoc} */
    public void format(final LogEvent record,
                       final Locale locale,
                       final T destination) {
        synchronized(base) {
            format(record, locale, destination);
        }
    }
}
