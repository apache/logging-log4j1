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

package org.apache.log4j;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.spi.LoggingEvent;

/**
 *  Allow LoggingEvents to be reconstructed from a different format (usually xml).
 *
 *  @author Scott Deboy <sdeboy@apache.org>
 *
 */
public interface Decoder {
	Vector decodeEvents(String document);
	LoggingEvent decode(String event);
    Vector decode(URL url) throws IOException;
    void setAdditionalProperties(Map additionalProperties);
}
