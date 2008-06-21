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
package org.apache.logging.core;

import java.util.Locale;
import java.io.*;

/**
 * A Layout extracts information from a LogRecord and
 * renders it into the parameterized type.  Layouts
 * support a two stage process of value extraction and rendering.
 * Value extraction occurs on the same thread as the log request
 * and extracts an object with value semantics like String,
 * Date, Integer, or collection of such objects.  Rendering
 * of the may occur on the same or different thread.
 * If a layout is immutable or thread-safe it should declare
 * so with an appropriate attribute.  Layouts not declared
 * as thread-safe will externally synchronized.  Layouts may
 * be combined to composite layout.
 *
 * Types used to parameterize Layout include
 * StringBuilder (patterns),
 * org.xml.sax.ContentHandler (XML),
 * ObjectOutputStream (serialization).
 *
 *
 */
public interface Converter<T> extends Extractor {

    /**
     * Renders the value object obtained from an earlier call
     * to extract on the same instance.
     * @param extract An extracted value from a previous call
     * to extract.
     * @param locale locale.
     * @param destination destination, may not be null.
     * @throws IOException if error writing to destination.
     * @throws LoggingException any other error.
     */
    void render(Object extract, Locale locale, T destination)
            throws IOException, LoggingException;

    /**
     * Formats some aspect of the LogRecord.
     * This function is functionally equivalent to calling render with
     * the value returned from extract, but may be slightly more
     * efficient.
     *
     * @param record logging record, may not be null.
     * @param locale locale.
     * @param destination destination, may not be null.
     * @throws IOException if error writing to destination.
     * @throws LoggingException any other error.
     */
    void format(LogEvent record, Locale locale, T destination)
            throws IOException, LoggingException;
}
