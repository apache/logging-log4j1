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

import org.apache.logging.core.Immutable;
import org.apache.logging.core.Converter;
import org.apache.logging.core.ThreadSafe;

import java.util.List;

/**
 * Creates thread-safe Converters.
 *
 * @Immutable
 *
 */
public final class ThreadSafeConverterFactory<T> {

    /**
     * Creates an instance.
     */
    public ThreadSafeConverterFactory() {
    }

    /**
     * Determines if Converter is thread-safe based on class annotations.
     * @param Converter Converter, may not be null.
     * @return true if thread-safe based on annotations.
     */
    public boolean isThreadSafe(final Converter<T> Converter) {
        return Converter.getClass().isAnnotationPresent(Immutable.class) ||
               Converter.getClass().isAnnotationPresent(ThreadSafe.class);
    }

    /**
     * Get a thread-safe Converter given a base Converter.
     * Base Converter should not be directly used after this call.
     *
     * @param Converter base Converter, may be null.
     * @return thread-safe Converter, may be same instance.
     */
    public Converter<T> get(final Converter<T> Converter) {
        if (isThreadSafe(Converter)) {
            return Converter;
        }
        return new SynchronizedConverter<T>(Converter);
    }

    /** Get a thread-safe composite Converter given a list of Converters.
    * Base Converters should not be directly used after this call.
    *
    * @param Converters base Converters, may be null.
    * @return thread-safe Converter, may be same instance.
    */
   public Converter<T> get(final List< Converter<T> > Converters) {
       for (Converter<T> Converter : Converters) {
           if (!isThreadSafe(Converter)) {
               return new SynchronizedConverter<T>(new CompositeConverter<T>(Converters));
           }
       }
       return new CompositeConverter<T>(Converters);
   }

}
