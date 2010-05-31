package org.apache.logging.core;

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


/**
 * This interface is an abstraction of a record of a logging event.
 * The current field names are patterned after java.util.logging.LogRecord.
 * Enhanced behavior beyond what java.util.LogRecord can support
 * should go in a distinct interface.  Behavior that is java.util.logging
 * specific should go in a distinct interface.
 *
 */
public interface LogEvent {

    /**
     * Get level.
     * @return level.
     */
    Level getLevel();

    /**
     * Get log name.
     * @return log name, may be null.
     */
    String getLogName();


    /**
     * Get source of logging request.
     * @return source of logging request, may be null.
     */
    StackTraceElement getSource();

    /**
     * Get the message string or an object with value
     * semantics that can produce the message string.
     *
     * @return message.
     */
    Object getMessage();

    /**
     * Get thread id.
     * @return thread id, a negative number indicates
     * that thread id is unavailable.
     */
    long getThreadID();

    /**
     * Get thread name.
     * @return thread name, may be null.
     */
    String getThreadName();


    /**
     * Get event time in milliseconds since 1970.
     * @return milliseconds since 1970.
     */
    long getMillis();


    /**
     * Get throwable associated with logging request.
     * @return throwable, may be null.
     */
    Throwable getThrown();


    /**
     * Get information about the user-supplied context of the logging request.
     *
     * @return context, may be null.
     */
    Object getUserContext();

    /**
     * Get information about the thread-associated context of the logging request.
     *
     * @return context, may be null.
     */
    Object getThreadContext();

    /**
     * Get information about the calling context of the logging request.
     *
     * @return context, may be null.
     */
    Object getCallingContext();

    /**
     * Get information about the application context of the logging request.
     *
     * @return context, may be null.
     */
    Object getApplicationContext();
}
