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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class ReflectionFilter extends Filter {

	/**
	 * NOTE: This filter modifies logging events by adding   
	 * properties to the event.
	 * 
	 * The object passed in as the message must provide a message via toString 
	 * or provide a 'message' property, which will be set as the rendered message.
	 * 
	 * This ReflectionFilter uses the JavaBeans BeanInfo and PropertyDescriptor mechanisms to discover 
	 * readMethods available on the 'message' object provided by the event.
	 *  
	 * For each method available on the object via the BeanInfo PropertyDescriptors, the method is executed
	 * and a property is added to the event, using the results of the method call as the value 
	 * and the method name as the key.
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
	
		//ignore strings and the object class properties
		if (!(event.getMessage() instanceof String)) {
			PropertyDescriptor[] props;
			try {
				props = Introspector.getBeanInfo(event.getMessage().getClass(), Object.class).getPropertyDescriptors();
				for (int i=0;i<props.length;i++) {
					if ("message".equalsIgnoreCase(props[i].getName())) {
						event.setRenderedMessage(props[i].getReadMethod().invoke(event.getMessage(), (Object[]) null).toString());
					} else {
						eventProps.put(props[i].getName(), props[i].getReadMethod().invoke(event.getMessage(), (Object[]) null).toString());
					}
				}
				event.setProperties(eventProps);
			} catch (IntrospectionException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
		return Filter.NEUTRAL;
	}
}
