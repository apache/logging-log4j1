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
 *  As written, this is how an appender or destination sees the Logger (which could be
 *  a java.util.logger, org.apache.log4j.Logger or something else).  It may not be how
 *  the client sees a logger.
 *
 */
public interface Logger {
    String getName();

   /**
     *  This would encapsulate all the thresholds and filters attached to the appender.
     *
     * @return filter, may not be null, but may be a DenyAll or AcceptAll filter.
     */
    Filter getFilter();




}
