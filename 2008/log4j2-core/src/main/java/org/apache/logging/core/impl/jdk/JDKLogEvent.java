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
import org.apache.logging.core.impl.log4j1.Log4j1Context;

import java.io.Serializable;

/**
 *  This class wraps an instance of java.util.logging.LogRecord
 * to implement the org.apache.logging.core.LogRecord interface.
 */
public final class JDKLogEvent
        implements org.apache.logging.core.LogEvent, Serializable {
    /**
     * Underlying instance.
     */
    private final java.util.logging.LogRecord base;

    /**
     * Thread name.
     */
    private final String threadName;

    /**
     * Thread name.
     */
    private final Object context;

    /**
     * Create new instance.
     * @param source underlying instance, may not be null.
     */
    private JDKLogEvent(final java.util.logging.LogRecord source,
                         final String threadName,
                         final Object context) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        base = source;
        this.threadName = threadName;
        this.context = context;
    }

    /**
     * Create new instance.
     * @param source underlying instance, may be null.
     */
    public static JDKLogEvent getInstance(
            final java.util.logging.LogRecord source,
            final String threadName,
            final Object context) {
        if (source == null) {
            return null;
        }
        return new JDKLogEvent(source, threadName, context);
    }


    /**
     * {@inheritDoc}
     */
    public Level getLevel() {
        return JDKLevel.getInstance(base.getLevel());
    }

    /**
     * {@inheritDoc}
     */
    public String getLoggerName() {
        return base.getLoggerName();
    }

    /** {@inheritDoc} */
    public StackTraceElement getSource() {
        return new StackTraceElement(base.getSourceClassName(),
                base.getSourceMethodName(),
                null, -1);
                
    }

    /**{@inheritDoc} */
    public Object getMessage() {
        return JDKMessage.getInstance(base.getMessage(),
                base.getParameters(),
                base.getResourceBundle(),
                base.getResourceBundleName());

    }


    /** {@inheritDoc} */
    public long getThreadID() {
        return base.getThreadID();
    }

    /** {@inheritDoc} */
    public String getThreadName() {
        return threadName;
    }



    /** {@inheritDoc} */
    public long getMillis() {
        return base.getMillis();
    }


    /** {@inheritDoc} */
    public Throwable getThrown() {
        return base.getThrown();
    }

    /** {@inheritDoc} */
    public Object getContext() {
        return context;
    }


}
