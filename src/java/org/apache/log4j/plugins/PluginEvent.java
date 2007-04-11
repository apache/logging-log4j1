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

import java.util.EventObject;


/**
 * All Plugin events are encapsulated in this class, which
 * simply contains the source Plugin, but may in future include more
 * information.
 *
 * @author Paul Smith
 */
public class PluginEvent extends EventObject {
    /**
     * @param source The source plugin of the event
     */
    PluginEvent(final Plugin source) {
        super(source);
    }

    /**
     * Returns the source Plugin of this event, which is simple
     * the getSource() method casted to Plugin for convenience.
     *
     * @return Plugin source of this event
     */
    public Plugin getPlugin() {
        return (Plugin) getSource();
  }
}
