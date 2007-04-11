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


/**
 * A common interface shared by log4j components.
 *
 * @author Ceki Gulcu
 * @since 1.3
 */
public interface Component {


    /**
     * Set owning logger repository for this component. This operation can
     * only be performed once.
     * Once set, a subsequent attempt will throw an IllegalStateException.
     *
     * @param repository The repository where this appender is attached.
     */
    void setLoggerRepository(LoggerRepository repository);

}
