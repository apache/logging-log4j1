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
 *   This class implements  a filter that will deny all requests..
 *
 *  Immutable
 */
public final class AcceptAllFilter implements Filter {

    /**
     *  Single instance.
     */
    public static final AcceptAllFilter INSTANCE = new AcceptAllFilter();
    /**
     *  Construct new instance;
     */
    private AcceptAllFilter() {
    }
    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level) {
        return Result.ACCEPT;
    }
    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level, Object userContext) {
        return Result.ACCEPT;
    }

    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level, Object userContext, Object message) {
        return Result.ACCEPT;
    }

   /**
    *  {@inheritDoc}
     */
    public Result filter(LogEvent event) {
       return Result.ACCEPT;
   }

   /**
    *  {@inheritDoc}
     */
   public int getThreshold() {
       return Integer.MIN_VALUE;
   }
}
