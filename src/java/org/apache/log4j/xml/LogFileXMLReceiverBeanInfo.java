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

package org.apache.log4j.xml;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * BeanInfo class for the meta-data of the LogFileXMLReceiver.
 *
 */
public class LogFileXMLReceiverBeanInfo extends SimpleBeanInfo {
  /* (non-Javadoc)
   * @see java.beans.BeanInfo#getPropertyDescriptors()
   */
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      return new PropertyDescriptor[] {
        new PropertyDescriptor("fileURL", LogFileXMLReceiver.class),
        new PropertyDescriptor("decoder", LogFileXMLReceiver.class),
        new PropertyDescriptor("name", LogFileXMLReceiver.class),
        new PropertyDescriptor("tailing", LogFileXMLReceiver.class),
        new PropertyDescriptor(
          "filterExpression", LogFileXMLReceiver.class),
      };
    } catch (Exception e) {
    }

    return null;
  }
}
