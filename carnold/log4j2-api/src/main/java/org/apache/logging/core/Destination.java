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
 *  Implementation of a log destination such as a file, network transport,
 *  database table, etc.
 *
 *   Implementation should be thread-safe, would be mutable.
 */
public interface Destination {
    /**
     *  Appends a logging event to the destination either synchronously (in which case
     *  it returns null) or asynchronous in which case it returns a Runnable to be executed.
     *
     * @param record logging record, may not be null.
     * @return a runnable to complete the append, or null if the action was completed.
     */
    Runnable append(LogEvent record) throws LoggingException;

    /**
     *  Closes the destination.
     * @return a runnable to complete the close, or null if the close was completed.
     */
    Runnable close() throws LoggingException;
    
}
