/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.varia;

import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * NOTE: This filter modifies logging events by adding properties to the event.
 * 
 * The 'properties' param is converted to event properties, which are 
 * set on every event processed by the filter.
 * 
 * This class relies on the convention that property name/value pairs are 
 * equals-symbol delimited, and each name/value pair is comma-delimited
 * 
 * Example properties param:
 * somename=somevalue,anothername=anothervalue,thirdname=third value
 * 
 * @since 1.3
 */
public class PropertyFilter extends Filter {
	private Hashtable properties;
	public void setProperties(String props) {
		properties = parseProperties(props);
	}
	
	public int decide(LoggingEvent event) {
		Map eventProps = event.getProperties();
		if (eventProps == null) {
			eventProps = new Hashtable();
		} else {
			eventProps = new Hashtable(properties);
		}
		properties.putAll(eventProps);
	
		event.setProperties(properties);
		return Filter.NEUTRAL;
	}
	
	private Hashtable parseProperties(String props) {
		Hashtable hashTable = new Hashtable();
		StringTokenizer pairs = new StringTokenizer(props, ",");
		while (pairs.hasMoreTokens()) {
			StringTokenizer entry = new StringTokenizer(pairs.nextToken(), "=");
			hashTable.put(entry.nextElement().toString().trim(), entry.nextElement().toString().trim());
		}
		return hashTable;
	}
}
