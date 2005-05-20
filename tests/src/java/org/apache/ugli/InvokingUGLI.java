/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

/*
 * Created on Dec 31, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.ugli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;


/**
 * Test whether invoking the UGLI API causes problems or not.
 * 
 * @author Ceki Gulcu
 *
 */
public class InvokingUGLI extends TestCase {
  /**
   * Constructor for InvokingAPI.
   * @param arg0
   */
  public InvokingUGLI(String arg0) {
    super(arg0);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void test1() {
    Logger logger = LoggerFactory.getLogger("test1");
    logger.debug("Hello world.");
  }
  
  public void test2() {
    Logger logger = LoggerFactory.getLogger("test2");
    logger.debug("Hello world 1.");
    logger.info("Hello world 2.");
  }
}
