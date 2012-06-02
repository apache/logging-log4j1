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

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;

import java.util.Enumeration;
import java.util.Properties;

/**
 * This appender forwards a logging request to another
 * appender after possibly rewriting the logging event.
 *
 * This appender (with the appropriate policy)
 * replaces the MapFilter, PropertyFilter and ReflectionFilter
 * from log4j 1.3.
 */
public class RewriteAppender extends AppenderSkeleton
     implements AppenderAttachable, UnrecognizedElementHandler {
    /**
     * Rewrite policy.
     */
    private RewritePolicy policy;
    /**
     * Nested appenders.
     */
    private final AppenderAttachableImpl appenders;

    public RewriteAppender() {
        appenders = new AppenderAttachableImpl();
    }

    /**
     * {@inheritDoc}
     */
    protected void append(final LoggingEvent event) {
        LoggingEvent rewritten = event;
        if (policy != null) {
            rewritten = policy.rewrite(event);
        }
        if (rewritten != null) {
            synchronized (appenders) {
              appenders.appendLoopOnAppenders(rewritten);
            }
        }
    }

    /**
     * Add appender.
     *
     * @param newAppender appender to add, may not be null.
     */
    public void addAppender(final Appender newAppender) {
      synchronized (appenders) {
        appenders.addAppender(newAppender);
      }
    }

    /**
     * Get iterator over attached appenders.
     * @return iterator or null if no attached appenders.
     */
    public Enumeration getAllAppenders() {
      synchronized (appenders) {
        return appenders.getAllAppenders();
      }
    }

    /**
     * Get appender by name.
     *
     * @param name name, may not be null.
     * @return matching appender or null.
     */
    public Appender getAppender(final String name) {
      synchronized (appenders) {
        return appenders.getAppender(name);
      }
    }


    /**
     * Close this <code>AsyncAppender</code> by interrupting the dispatcher
     * thread which will process all pending events before exiting.
     */
    public void close() {
      closed = true;
      //
      //    close all attached appenders.
      //
      synchronized (appenders) {
        Enumeration iter = appenders.getAllAppenders();

        if (iter != null) {
          while (iter.hasMoreElements()) {
            Object next = iter.nextElement();

            if (next instanceof Appender) {
              ((Appender) next).close();
            }
          }
        }
      }
    }

    /**
     * Determines if specified appender is attached.
     * @param appender appender.
     * @return true if attached.
     */
    public boolean isAttached(final Appender appender) {
      synchronized (appenders) {
        return appenders.isAttached(appender);
      }
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresLayout() {
      return false;
    }

    /**
     * Removes and closes all attached appenders.
     */
    public void removeAllAppenders() {
      synchronized (appenders) {
        appenders.removeAllAppenders();
      }
    }

    /**
     * Removes an appender.
     * @param appender appender to remove.
     */
    public void removeAppender(final Appender appender) {
      synchronized (appenders) {
        appenders.removeAppender(appender);
      }
    }

    /**
     * Remove appender by name.
     * @param name name.
     */
    public void removeAppender(final String name) {
      synchronized (appenders) {
        appenders.removeAppender(name);
      }
    }


    public void setRewritePolicy(final RewritePolicy rewritePolicy) {
        policy = rewritePolicy;
    }
    /**
     * {@inheritDoc}
     */
    public boolean parseUnrecognizedElement(final Element element,
                                            final Properties props) throws Exception {
        final String nodeName = element.getNodeName();
        if ("rewritePolicy".equals(nodeName)) {
            Object rewritePolicy =
                    org.apache.log4j.xml.DOMConfigurator.parseElement(
                            element, props, RewritePolicy.class);
            if (rewritePolicy != null) {
                if (rewritePolicy instanceof OptionHandler) {
                    ((OptionHandler) rewritePolicy).activateOptions();
                }
                this.setRewritePolicy((RewritePolicy) rewritePolicy);
            }
            return true;
        }
        return false;
    }

}
