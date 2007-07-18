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
package org.apache.log4j.net;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * BeanInfo class for the JMSReceiver.
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class JMSReceiverBeanInfo extends SimpleBeanInfo {

    /* (non-Javadoc)
     * @see java.beans.BeanInfo#getPropertyDescriptors()
     */
    public PropertyDescriptor[] getPropertyDescriptors() {

        try {

            return new PropertyDescriptor[] {
                new PropertyDescriptor("name", JMSReceiver.class),
                new PropertyDescriptor("topicFactoryName", JMSReceiver.class),
                new PropertyDescriptor("topicName", JMSReceiver.class),
                new PropertyDescriptor("threshold", JMSReceiver.class),
                new PropertyDescriptor("jndiPath", JMSReceiver.class),
                new PropertyDescriptor("userId",
                        JMSReceiver.class),
            };
        } catch (Exception e) {
        }

        return null;
    }
}
