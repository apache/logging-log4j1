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

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import org.apache.logging.core.*;
import java.util.*;

/**
 * Abstract class for any Converter that extracts a
 * string from the log record and writes to an Appendable.
 *
 * @PatternSpecifier("x");
 */
public final class PropertiesContextConverter<T extends Appendable>
        implements Converter<T>, Serializable {

    private final String key;

    /**
     * Output all properties from context.
     */
    public PropertiesContextConverter() {
        key = null;
    }

    /**
     * Output one property.
     */
    public PropertiesContextConverter(final String key) {
        this.key = key;
    }

    public Object extract(final LogEvent record) {
        Object context = record.getContext();
        if (context instanceof PropertyContext) {
            PropertyContext properties = (PropertyContext) context;
            if (key == null) {
                return new HashMap<String, Object>(properties.getProperties());
            } else {
                return properties.getProperties().get(key);
            }
        }
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void render(Object extract, Locale locale, T destination)
        throws IOException {
        if (extract instanceof String) {
            destination.append(extract.toString());
        } else if (extract != null) {
            Map<String, Object> properties = (Map<String, Object>) extract;
            destination.append('{');
            for(Map.Entry<String, Object> entry : properties.entrySet()) {
                destination.append(entry.getKey());
                destination.append(',');
                destination.append(String.valueOf(entry.getValue()));
            }
            destination.append('}');
        }
    }

    /**
     * {@inheritDoc}
     */
    public void format(LogEvent record, Locale locale, T destination)
        throws IOException {
        render(extract(record), locale, destination);
    }

}
