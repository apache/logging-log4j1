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
 *   The class implements a chain of two filters.
 *
 *  Immutable
 */
public final class ChainedFilter implements Filter {
    private final Filter head;
    private final Filter tail;
    private final int threshold;

    public ChainedFilter(Filter head, Filter tail) {
        if(head == null || tail == null) {
            throw new NullPointerException();
        }
        this.head = head;
        this.tail = tail;
        threshold = Math.min(head.getThreshold(), tail.getThreshold());
    }
    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level) {
        if(level == null || level.getGenericValue() >= threshold) {
            Result headResult = head.filter(level);
            if(headResult != Filter.Result.NEUTRAL) {
                return headResult;
            }
            return tail.filter(level);
        }
        return Result.DENY;
    }
    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level, Object userContext) {
        if(level == null || level.getGenericValue() >= threshold) {
            Result headResult = head.filter(level, userContext);
            if(headResult != Filter.Result.NEUTRAL) {
                return headResult;
            }
            return tail.filter(level, userContext);
        }
        return Result.DENY;
    }

    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level, Object userContext, Object message) {
        if(level == null || level.getGenericValue() >= threshold)
        {
            Result headResult = head.filter(level, userContext, message);
            if(headResult != Filter.Result.NEUTRAL) {
                return headResult;
            }
            return tail.filter(level, userContext, message);
        }
        return Result.DENY;
    }

   /**
    *  {@inheritDoc}
     */
    public Result filter(LogEvent event) {
       final Level level = event.getLevel();
       if(level == null || level.getGenericValue() >= threshold) {
           Result headResult = head.filter(event);
           if(headResult != Filter.Result.NEUTRAL) {
               return headResult;
           }
           return tail.filter(event);
        }
        return Result.DENY;
   }

   /**
    *  {@inheritDoc}
     */
   public int getThreshold() {
       return threshold;
   }
}
