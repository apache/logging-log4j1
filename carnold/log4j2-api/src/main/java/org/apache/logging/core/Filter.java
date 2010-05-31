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
 *  This interface is used for any object that can determine
 *   to accept or reject a logging request.
 *
 *  Classes implementing this interface should be immutable
 *   and thread-safe.
 *
 *  Filter "chains" are a specific implementation of this class.
 *
 */
public interface Filter {

    /**
     *   Potential results of filter evaluation.
     *
     *  INDETERMINANT is used when a call does not have enough
     *  information to make a determination, such as when calling
     *  the eventless decide call with a filter that does depend on the event
     *  for a determination.
     */
    enum Result {
        DENY,
        ACCEPT,
        NEUTRAL,
        INDETERMINANT
    }

    /**
     *  Evaluate filter for level.
     *
     * @param level  level, may not be null.
     * @return result, should return INDETERMINANT is unable to determine
     *  without additional information.
     */
    Result filter(Level level);
    /**
     *  Evaluate filter for level and user-supplied context.
     *
     * @param level  level, may not be null.
     * @param userContext user-supplied context, may be null.
     * @return result, should return NDETERMINANT is unable to determine
     *  without additional information.
     */
    Result filter(Level level, Object userContext);
    /**
     *  Evaluate filter for level , user-supplied context and message.
     *
     * @param level  level, may not be null.
     * @param userContext user-supplied context, may be null.
     * @return result, should return INDETERMINANT is unable to determine
     *  without additional information.
     */
    Result filter(Level level, Object userContext, Object message);

   /**
     * Evaluate filter for event.
     * @param event   event, may not be null.
     * @return  result of evaluation, may not be INDETERMINANT.
     */
    Result filter(LogEvent event);

   /**
     *  Generic level value below which filter will always return DENY.
    *  Set to Integer.MIN_VALUE if does not consider level.
    *  This method allows for level calculus to determine composite threshold.
     * @return  lowest level that could result in something other than DENY.
     */
    int getLowerLimit();
    /**
      *  Generic level value above which filter will always return PASS.
     *  Set to Integer.MAX_VALUE if does not consider level.
     *  This method allows for level calculus to determine composite threshold.
      * @return  highest level that could result in something other than PASS.
      */
    int getUpperLimit();
}
