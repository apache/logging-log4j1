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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class LocalizedMessageFormat {
    private LocalizedMessageFormat() {

    }
    private static String getPattern(final String key,
                              final ResourceBundle bundle) {
        if (bundle != null) {
            try {
                return bundle.getString(key);
            } catch(Exception ex) {
                return key;
            }
        }
        return key;
    }

    private static String getPattern(final ResourceBundle bundle,
                              final String bundleName,
                              final Locale locale,
                              final String key) {
        if (bundle != null) {
            return getPattern(key, bundle);
        }
        try {
            return getPattern(key,
                    ResourceBundle.getBundle(bundleName,
                        locale,
                        Thread.currentThread().getContextClassLoader()));
        } catch (Exception ex) {
            try {
                return getPattern(key,
                    ResourceBundle.getBundle(bundleName,
                        locale,
                        ClassLoader.getSystemClassLoader()));
            } catch(Exception ex2) {
                return key;
            }
        }
    }

    public static String format(final ResourceBundle bundle,
                          final String bundleName,
                          final String key,
                          final Locale locale,
                          final Object[] parameters) {
        if (bundle == null &&
                (bundleName == null || bundleName.length() == 0) &&
                (parameters == null || parameters.length == 0) ) {
            return key;
        }
        String pattern = getPattern(bundle, bundleName, locale, key);
        try {
            return (new MessageFormat(pattern, locale)).format(parameters,
                    new StringBuffer(), null).toString();
        } catch(Exception ex) {
            return pattern;
        }
    }

}
