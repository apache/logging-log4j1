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


package org.apache.log4j;


/**
 * Test if ConsoleAppender honors the Appender contract.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 *
 */
public class ConsoleAppenderTest extends AbstractAppenderTest {
  protected Appender getAppender() {
    return new ConsoleAppender();
  }

  Appender getConfiguredAppender() {
    ConsoleAppender ca = new ConsoleAppender();

    // set a bogus layout
    ca.setLayout(new DummyLayout());
    return ca;
  }

  public void testPartiallyConfiguredAppender() {
    ConsoleAppender wa1 = new ConsoleAppender();
    assertFalse(wa1.isActive());
  }
}
