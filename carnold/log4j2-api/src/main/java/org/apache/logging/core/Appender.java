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
 *    @doubt I've thrashing whether this should be an interface
 *    or an immutable composite of simpler parts.  The immutable
 *    make a switch cleaner since a replacement of an appender
 *   gives you an opportunity to reevaluate any calculated threadholds.
 *
 *   This would be the core internal appender, specific API's could offer
 *   what appear to be mutable appenders.
 */
public final class Appender {
    private final Filter filter;
    private final Destination  destination;

    public Appender(final Filter filter, final Destination destination) {
        if (filter == null || destination == null)
        {
            throw new NullPointerException();
        }
        this.filter = filter;
        this.destination = destination;
    }
    /**
     *
     *  Gets the filter associated with this appender.
     *
     *  @doubt my expectation is that it would be better to not provide
     *  a setter and make Appender immutable.  To change a filter,
     *  you  would construct a new appender and then swap.
     *
     * @return filter, may not be null by can be an instance of a PassAllFilter.
     */
    public Filter getFilter()
    {
        return filter;
    }

    /**
     *
     *  Gets the destination associated with this appender.  The destination
     * would be what would distinguish an "FileAppender" from a "NetworkAppender".
     *
     *  @doubt not set on the name, had thought of Transport, Channel, EventSink, etc.
     *
     *
     * @return destination, may not be null but may be an instance of a NullDestination.
     */
    public Destination getDestination()
    {
        return destination;
    }


}
