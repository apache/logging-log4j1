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

package org.apache.log4j.spi;

import org.apache.log4j.Level;

/**
 * An interface that defines the required methods for supporting the
 * setting and getting of a level threshold.  Components should implement
 * this interface if logging events they process should meet a certain
 * threshold before being processed further.  Examples of this are
 * Appenders and Receivers which will not process logging events unless
 * the event level is at or greater than a set threshold level.
 *
 * @author Paul Smith (psmith@apache.org)
 * @author Mark Womack
 * @since 1.3
 */
public interface Thresholdable {
    /**
     * Sets the component theshold to the given level.
     *
     * @param level The threshold level events must equal or be greater
     *              than before further processing can be done.
     */
    void setThreshold(Level level);

    /**
     * Gets the current threshold setting of the component.
     *
     * @return Level The current threshold level of the component.
     */
    Level getThreshold();

    /**
     * Returns true if the given level is equals or greater than the current
     * threshold value of the component.
     *
     * @param level The level to test against the component threshold.
     * @return boolean True if level is equal or greater than the
     *         component threshold.
     */
    boolean isAsSevereAsThreshold(Level level);
}
