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
 *   This class implements  a filter that will reject levels outside of
 *   a range.
 *
 *  Immutable
 */
public final class LevelRangeFilter implements Filter {

   /**
     * minimum level (inclusive) to pass.
     */
    private final int levelMin;

    /**
      * maximum level (inclusive) to pass.
      */
    private final int levelMax;

    /**
     *    Result to return if level is within range.
     */
    private final Result inRange;


    /**
     *  Construct new instance;
     */
    public LevelRangeFilter(final int levelMin,
                         final int levelMax,
                         final boolean acceptOnMatch) {
        if(levelMin > levelMax) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum level");
        }
        this.levelMin = levelMin;
        this.levelMax = levelMax;
        if(acceptOnMatch) {
            inRange = Result.ACCEPT;
        } else {
            inRange = Result.NEUTRAL;
        }
    }
    /**
     *  {@inheritDoc}
     */
    public Result filter(final Level level) {
        if(level != null) {
            final int genericValue = level.getGenericValue();
            if(genericValue >= levelMin && genericValue <= levelMax) {
                return inRange;
            }
        }
        return Result.DENY;
    }
    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level, Object userContext) {
        return filter(level);
    }

    /**
     *  {@inheritDoc}
     */
    public Result filter(Level level, Object userContext, Object message) {
        return filter(level);
    }

   /**
    *  {@inheritDoc}
     */
    public Result filter(LogEvent event) {
       return filter(event.getLevel());
   }

   /**
    *  {@inheritDoc}
     */
   public int getThreshold() {
       return levelMin;
   }
}
