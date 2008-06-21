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
package org.apache.logging.core.impl.log4j1;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.logging.core.Level;
import org.apache.logging.core.LogEvent;

import java.io.Serializable;


/**
 *  This class wraps an instance of org.apache.log4j.spi.LoggingEvent
 * to implement the org.apache.logging.core.LogRecord interface.
 */
public final class Log4j1LogEvent
        implements LogEvent,
            Serializable {
    /**
     * Underlying instance.
     */
    private final LoggingEvent base;

    /**
     * Thread id.
     */
    private final long threadId;

    /**
     * Create new instance.
     * @param source underlying instance, may not be null.
     * @param threadId thread id, use -1 for unavailable.
     */
    private Log4j1LogEvent(final LoggingEvent source,
                            long threadId) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        base = source;
        this.threadId = threadId;
    }

    /**
     * Create new instance.
     * @param source underlying instance, may be null.
     * @param threadId thread id, use -1 for unavailable.
     * @return new instance of null if source is null.
     */
    public static Log4j1LogEvent getInstance(
            final LoggingEvent source,
            long threadId) {
        if (source == null) {
            return null;
        }
        return new Log4j1LogEvent(source, threadId);
    }


    /**
     * {@inheritDoc}
     */
    public Level getLevel() {
        return Log4j1Level.getInstance(base.getLevel());
    }

    /** {@inheritDoc} */
    public String getLoggerName() {
        return base.getLoggerName();
    }

    /** {@inheritDoc} */
    public StackTraceElement getSource() {
        LocationInfo li = base.getLocationInformation();
        if (li != null) {
            int lineNumber = -1;
            String lineStr = li.getLineNumber();
            if (lineStr != null && lineStr.length() > 0) {
                try {
                    lineNumber = Integer.parseInt(lineStr);
                } catch(Exception ex) {
                }
            }
            return new StackTraceElement(li.getClassName(),
                    li.getMethodName(),
                    li.getFileName(),
                    lineNumber);
        }
        return null;
    }

    /**{@inheritDoc} */
    public Object getMessage() {
        return base.toString();
    }


    /** {@inheritDoc} */
    public long getThreadID() {
        return threadId;
    }

    /** {@inheritDoc} */
    public String getThreadName() {
        return base.getThreadName();
    }

    /** {@inheritDoc} */
    public long getMillis() {
        return base.timeStamp;
    }


    /** {@inheritDoc} */
    public Throwable getThrown() {
        ThrowableInformation ti = base.getThrowableInformation();
        if (ti != null) {
            return ti.getThrowable();
        }
        return null;
    }

    /** {@inheritDoc} */
    public Object getContext() {
        return Log4j1Context.getInstance(base);
    }

}
