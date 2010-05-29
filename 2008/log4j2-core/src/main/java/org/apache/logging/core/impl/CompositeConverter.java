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
import org.apache.logging.core.LoggingException;

import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Creates a composite Converter.
 *
 */
public final class CompositeConverter<T>
        implements Converter<T>, Serializable {
    /**
     * List of Converters.
     */
    private final List< Converter<T> > Converters;

    /**
     * Creates a new thread-safe Converter by
     * externally synchronizing an existing Converter.
     * @param originals original Converters, may not be null.
     */
    public CompositeConverter(final List< Converter<T> > originals) {
        if (originals != null) {
            Converters = new ArrayList < Converter<T> >(originals);
        } else {
            Converters = new ArrayList < Converter<T> >();
        }
    }

    /** {@inheritDoc} */
    public Object extract(final LogEvent record) {
        ArrayList<Object> values = new ArrayList<Object>(Converters.size());
        int i = 0;
        for( Converter<T> Converter : Converters) {
            values.set(i++, Converter.extract(record));
        }
        return values;
    }


    /** {@inheritDoc} */
    public void render(final Object extract,
                       final Locale locale,
                       final T destination)
            throws IOException, LoggingException {
        for( Converter<T> Converter : Converters) {
            Converter.render(extract, locale, destination);
        }
    }

    /** {@inheritDoc} */
    public void format(final LogEvent record,
                       final Locale locale,
                       final T destination)
            throws IOException, LoggingException {
        for( Converter<T> Converter : Converters) {
            Converter.format(record, locale, destination);
        }
    }
}
