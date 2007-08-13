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

import junit.framework.TestCase;

/**
 * Tests for LocationInfo.
 */
public class LocationInfoTest extends TestCase {

    /**
     * Tests four parameter constructor.
     */
    public void testFourParamConstructor() {
        final String className = LocationInfoTest.class.getName();
        final String methodName = "testFourParamConstructor";
        final String fileName = "LocationInfoTest.java";
        final String lineNumber = "41";
        LocationInfo li = new LocationInfo(fileName,
                className, methodName, lineNumber);
        assertEquals(className, li.getClassName());
        assertEquals(methodName, li.getMethodName());
        assertEquals(fileName, li.getFileName());
        assertEquals(lineNumber, li.getLineNumber());
        assertEquals(className + "."  + methodName
                + "(" + fileName + ":" + lineNumber + ")",
                li.fullInfo);
    }

}
