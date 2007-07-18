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

package org.apache.log4j.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Thresholdable;


/**
 * Defines the base class for Receiver plugins.
 * <p/>
 * <p>Just as Appenders send logging events outside of the log4j
 * environment (to files, to smtp, to sockets, etc), Receivers bring
 * logging events inside the log4j environment.
 * <p/>
 * <p>Receivers are meant to support the receiving of
 * remote logging events from another process. For example,
 * SocketAppender "appends" a logging event to a socket, configured
 * for a specific host and port number.  On the receiving side of
 * the socket can be a SocketReceiver object.  The SocketReceiver
 * object receives the logging event, and then "posts" it to the
 * log4j environment (LoggerRepository) on the receiving machine, to
 * be handled by the configured appenders, etc.  The various
 * settings in this environment (Logger levels, Appender filters &
 * thresholds) are applied to the received logging event.
 * <p/>
 * <p>Receivers can also be used to "import" log messages from other
 * logging packages into the log4j environment.
 * <p/>
 * <p>Receivers can be configured to post events to a given
 * LoggerRepository.
 * <p/>
 * <p>Subclasses of Receiver must implement the isActive(),
 * activateOptions(), and shutdown() methods. The doPost() method
 * is provided to standardize the "import" of remote events into
 * the repository.
 *
 * @author Mark Womack
 * @author Ceki G&uuml;lc&uuml;
 * @author Paul Smith (psmith@apache.org)
 * @since 1.3
 */
public abstract class Receiver extends PluginSkeleton implements Thresholdable {
    /**
     * Threshold level.
     */
    protected Level thresholdLevel;

    /**
     * Create new instance.
     */
    protected Receiver() {
        super();
    }

    /**
     * Sets the receiver theshold to the given level.
     *
     * @param level The threshold level events must equal or be greater
     *              than before further processing can be done.
     */
    public void setThreshold(final Level level) {
        Level oldValue = this.thresholdLevel;
        thresholdLevel = level;
        firePropertyChange("threshold", oldValue, this.thresholdLevel);
    }

    /**
     * Gets the current threshold setting of the receiver.
     *
     * @return Level The current threshold level of the receiver.
     */
    public Level getThreshold() {
        return thresholdLevel;
    }

    /**
     * Returns true if the given level is equals or greater than the current
     * threshold value of the receiver.
     *
     * @param level The level to test against the receiver threshold.
     * @return boolean True if level is equal or greater than the
     *         receiver threshold.
     */
    public boolean isAsSevereAsThreshold(final Level level) {
        return ((thresholdLevel == null)
                || level.isGreaterOrEqual(thresholdLevel));
    }

    /**
     * Posts the logging event to a logger in the configured logger
     * repository.
     *
     * @param event the log event to post to the local log4j environment.
     */
    public void doPost(final LoggingEvent event) {
        // if event does not meet threshold, exit now
        if (!isAsSevereAsThreshold(event.getLevel())) {
            return;
        }

        // get the "local" logger for this event from the
        // configured repository.
        Logger localLogger =
                getLoggerRepository().getLogger(event.getLoggerName());

        // if the logger level is greater or equal to the level
        // of the event, use the logger to append the event.
        if (event.getLevel()
                .isGreaterOrEqual(localLogger.getEffectiveLevel())) {
            // call the loggers appenders to process the event
            localLogger.callAppenders(event);
        }
  }
}
