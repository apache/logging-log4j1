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
package org.apache.logging.core.impl.jdk;

import org.apache.logging.core.Level;
import org.apache.logging.core.impl.LocalizedMessageFormat;

import java.io.Serializable;
import java.util.Locale;

/**
 * This class wraps an instance of java.util.Level
 * to support the org.apache.logging.core.Level interface.
 *
 *
 */
public class JDKLevel implements Level, Serializable {
    /**
     * Underlying instance of java.util.logging.Level.
     */
    private final java.util.logging.Level base;

    /**
     * Create new instance.
     * @param source underlying instance, may not be null.
     */
    private JDKLevel(final java.util.logging.Level source) {
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
    public static JDKLevel getInstance(final java.util.logging.Level source) {
        if (source == null) {
            return null;
        }
        return new JDKLevel(source);
    }

    /** {@inheritDoc} */
    public String toString() {
        return base.toString();
    }
    
    /** {@inheritDoc} */
    public int intValue() {
        return base.intValue();
    }

    /** {@inheritDoc} */
    public String getLocalizedName(final Locale locale) {
        return LocalizedMessageFormat.format(
                null,
                base.getResourceBundleName(),
                base.getName(),
                locale,
                null);
    }
}
