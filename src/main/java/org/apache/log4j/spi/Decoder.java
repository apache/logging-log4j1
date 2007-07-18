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


import java.io.IOException;

import java.net.URL;

import java.util.Map;
import java.util.Vector;


/**
 *  Allow LoggingEvents to be reconstructed from a different format
 * (usually XML).
 *
 *  @author Scott Deboy (sdeboy@apache.org)
 *  @since 1.3
 */
public interface Decoder {
    /**
     * Decode events from document.
     * @param document document to decode.
     * @return list of LoggingEvent instances.
     */
  Vector decodeEvents(String document);

    /**
     * Decode event from string.
     * @param event string representation of event
     * @return event
     */
  LoggingEvent decode(String event);

    /**
     * Decode event from document retreived from URL.
     * @param url url of document
     * @return list of LoggingEvent instances.
     * @throws IOException if IO error resolving document.
     */
  Vector decode(URL url) throws IOException;

    /**
     * Sets additional properties.
     * @param additionalProperties map of additional properties.
     */
  void setAdditionalProperties(Map additionalProperties);
}
