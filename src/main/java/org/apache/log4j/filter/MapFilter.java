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

package org.apache.log4j.filter;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class MapFilter extends Filter {

	/**
	 * NOTE: This filter modifies logging events by adding properties to the event.
	 * 
	 * The object passed in as the event message must implement java.util.Map.
   * 
   * This filter converts the event message (a Map) into properties on the event.
   * 
   * If the map holds an entry with a key of "message", the value of the entry is used
   * as the rendered message.
   * 
	 * @since 1.3
	 */
	public int decide(LoggingEvent event) {
		Map properties = event.getProperties();
		Hashtable eventProps = null;
		if (properties == null) {
			eventProps = new Hashtable();
		} else {
			eventProps = new Hashtable(properties);
		}
	
		if (event.getMessage() instanceof Map) {
			for (Iterator iter = ((Map)event.getMessage()).entrySet().iterator();iter.hasNext();) {
        Map.Entry entry = (Map.Entry)iter.next();
				if ("message".equalsIgnoreCase(entry.getKey().toString())) {
					event.setRenderedMessage(entry.getValue().toString());
				} else {
					eventProps.put(entry.getKey(), entry.getValue());
				}
			}
			event.setProperties(eventProps);
		}
		return Filter.NEUTRAL;
	}
}
