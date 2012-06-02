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
package org.apache.log4j.rewrite;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This policy rewrites events by evaluating any
 * JavaBean properties on the message object and adding them
 * to the event properties.  If the message object has a
 * message property, the value of that property will be
 * used as the message for the rewritten event and will
 * not be added to the event properties.  Values from the
 * JavaBean properties will replace any existing property
 * with the same name.
 *
 * The combination of the RewriteAppender and this policy
 * performs the same actions as the ReflectionFilter from log4j 1.3. 
 */
public class ReflectionRewritePolicy implements RewritePolicy {
    /**
     * {@inheritDoc}
     */
    public LoggingEvent rewrite(final LoggingEvent source) {
        Object msg = source.getMessage();
        if (!(msg instanceof String)) {
            Object newMsg = msg;
            Map rewriteProps = new HashMap(source.getProperties());

            try {
                PropertyDescriptor[] props = Introspector.getBeanInfo(
                        msg.getClass(), Object.class).getPropertyDescriptors();
                if (props.length > 0) {
                    for (int i=0;i<props.length;i++) {
                        try {
                            Object propertyValue =
                                props[i].getReadMethod().invoke(msg,
                                        (Object[]) null);
                            if ("message".equalsIgnoreCase(props[i].getName())) {
                                newMsg = propertyValue;
                            } else {
                                rewriteProps.put(props[i].getName(), propertyValue);
                            }
                        } catch (Exception e) {
                            LogLog.warn("Unable to evaluate property " +
                                    props[i].getName(), e);
                        }
                    }
                    return new LoggingEvent(
                            source.getFQNOfLoggerClass(),
                            source.getLogger() != null ? source.getLogger(): Logger.getLogger(source.getLoggerName()),
                            source.getTimeStamp(),
                            source.getLevel(),
                            newMsg,
                            source.getThreadName(),
                            source.getThrowableInformation(),
                            source.getNDC(),
                            source.getLocationInformation(),
                            rewriteProps);
                }
            } catch (Exception e) {
                LogLog.warn("Unable to get property descriptors", e);
            }

        }
        return source;
    }
}
