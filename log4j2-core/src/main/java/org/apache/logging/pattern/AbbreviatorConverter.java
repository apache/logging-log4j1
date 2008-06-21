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

import java.util.Locale;

/**
 * Class handles abbreviation of fields.
 *
 *
 */
public final class AbbreviatorConverter extends DecoratorConverter {

    public AbbreviatorConverter(final Converter<StringBuilder> nested,
                       final int width,
                       final boolean leftJustify) {
        super(nested);

    }

    /**  {@inheritDoc} */
    protected void decorate(final Object extract,
                            final Locale locale,
                            final StringBuilder destination,
                            final int initialPos) {

    }


}
