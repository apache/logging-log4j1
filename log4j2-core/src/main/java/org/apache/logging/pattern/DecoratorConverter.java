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
package org.apache.logging.pattern;

import org.apache.logging.core.Converter;
import org.apache.logging.core.LogEvent;
import org.apache.logging.core.LoggingException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

/**
 *  Abstract base classes for Converters that modify the
 * rendered content from another Converter.
 *
 */
public abstract class DecoratorConverter
        implements Converter<StringBuilder>, Serializable {

    private final Converter<StringBuilder> nested;

    protected DecoratorConverter(final Converter<StringBuilder> nested) {
        if (nested == null) {
            throw new NullPointerException("nested");
        }
        this.nested = nested;
    }

    /** {*inheritDoc} */
    public Object extract(final LogEvent record) {
        return nested.extract(record);
    }


    /**
     * Modifies content rendered by nested Converter.
     * @param extract extract.
     * @param locale locale.
     * @param destination destination.
     * @param initialPos position of destination before
     *    nested Converter was rendered.
     */
    protected abstract void decorate(final Object extract,
                            final Locale locale,
                            final StringBuilder destination,
                            final int initialPos);
    /**
     * {@inheritDoc}
     */
    public void render(final Object extract,
                       final Locale locale,
                       final StringBuilder destination)
        throws IOException, LoggingException {
        int initialPos = destination.length();
        nested.render(extract, locale, destination);
        decorate(extract, locale, destination, initialPos);
    }

    /**
     * {@inheritDoc}
     */
    public void format(LogEvent record, Locale locale,
                       StringBuilder destination)
        throws IOException, LoggingException {
        render(extract(record), locale, destination);
    }

}
