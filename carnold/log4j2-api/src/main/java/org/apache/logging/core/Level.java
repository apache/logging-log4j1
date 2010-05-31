package org.apache.logging.core;

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

/**
 *  This interface is supported by classes that represent levels.
 *  Logging API's typically define level classes or constant
 *  values and some frameworks allow user defined levels.
 *  This interface is supported by classes that map logging api
 *  levels to a generic level value space for integer range filtering.
 *  Filters also have access to the level object so they can distinguish
 *  between a log4j INFO and jul INFO if desired.
 *
 */
public interface Level extends Localizable {
    /**
     * Get integer value for this level in the generic level value space.
     * @return integer value.
     */
    int getGenericValue();

}
