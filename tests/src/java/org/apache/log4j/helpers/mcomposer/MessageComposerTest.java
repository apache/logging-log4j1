/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.helpers.mcomposer;

import org.apache.log4j.helpers.mcompose.MessageComposer;

import junit.framework.TestCase;


/**
 * @author Ceki Gulcu
 *
 */
public class MessageComposerTest extends TestCase {
  
  public void test1Param() {
    String result;
    Integer i3 = new Integer(3);
    
    result = MessageComposer.compose("Value is {}.", i3);
    assertEquals("Value is 3.", result);

    result = MessageComposer.compose("Value is {", i3);
    assertEquals("Value is {", result);
    
    result = MessageComposer.compose("Value is {}.", null);
    assertEquals("Value is null.", result);

    result = MessageComposer.compose("{} is larger than 2.", i3);
    assertEquals("3 is larger than 2.", result);

    result = MessageComposer.compose("No subst", i3);
    assertEquals("No subst", result);
    
    result = MessageComposer.compose("Incorrect {subst", i3);
    assertEquals("Incorrect {subst", result);
    
    result = MessageComposer.compose("Escaped \\{} subst", i3);
    assertEquals("Escaped \\{} subst", result);

    result = MessageComposer.compose("\\{Escaped", i3);
    assertEquals("\\{Escaped", result);

    result = MessageComposer.compose("\\{}Escaped", i3);
    assertEquals("\\{}Escaped", result);
  }
  
  public void test2Param() {
    String result;
    Integer i1 = new Integer(1);
    Integer i2 = new Integer(2);
    
    result = MessageComposer.compose("Value {} is larger than {}.", i1, i2);
    assertEquals("Value 1 is larger than 2.", result);
    
    result = MessageComposer.compose("Value {} is larger than {}", i1, i2);
    assertEquals("Value 1 is larger than 2", result);
    
    result = MessageComposer.compose("{}{}", i1, i2);
    assertEquals("12", result);
    result = MessageComposer.compose("Val1={}, Val2={", i1, i2);
    assertEquals("Val1=1, Val2={", result);
  }
}
