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

import java.util.ResourceBundle;
import java.util.Locale;
import java.text.MessageFormat;
import java.io.Serializable;
import org.apache.logging.core.Localizable;
import org.apache.logging.core.impl.*;

/**
 * This object encapsulates all aspects of a JDKLogRecord
 * that participate in the evaluation of the
 * message text.
 *
 */
public final class JDKMessage implements Localizable, Serializable {
    /**
     * Pattern or key.
     */
    private final String message;
    /**
     * Parameters.
     */
    private final Object[] parameters;
    /**
     * Resource bundle.
     */
    private final transient ResourceBundle bundle;
    /**
     * Resource bundle name.
     */
    private final String bundleName;

    /**
     * Create new instance.
     * @param message pattern or key.
     * @param parameters parameters.
     * @param bundle resource bundle, may be null.
     * @param bundleName resource bundle name, may be null.
     */
    private JDKMessage(final String message,
                       final Object[] parameters,
                       final ResourceBundle bundle,
                       final String bundleName
                       ) {
        this.message = message;
        if (parameters == null) {
            this.parameters = null;
        } else {
            this.parameters = parameters.clone();
        }
        this.bundle = bundle;
        this.bundleName = bundleName;
    }


    /**
     * Create new instance.
     * @param message pattern or key.
     * @param parameters parameters.
     * @param bundle resource bundle, may be null.
     * @param bundleName resource bundle name, may be null.
     * @return new instance.
     */
    public static JDKMessage getInstance(final String message,
                       final Object[] parameters,
                       final ResourceBundle bundle,
                       final String bundleName
                       ) {
        return new JDKMessage(message, parameters, bundle, bundleName);
    }
    /**
     * Evaluated message for specified locale.
     * @param locale locale.
     * @return evaluated message.
     */
    public String getLocalizedName(final Locale locale) {
        return LocalizedMessageFormat.format(bundle,
                bundleName, message, locale, parameters);
    }

    /** {@inheritDoc} */
    public String toString() {
        return message;
    }

}
