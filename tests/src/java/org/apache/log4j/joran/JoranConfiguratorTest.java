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

package org.apache.log4j.joran;

import java.util.List;

import org.apache.log4j.LogManager;

import junit.framework.TestCase;


/**
 * @author ceki
 *
 */
public class JoranConfiguratorTest extends TestCase {
  public void test1() {
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure("./input/joran/simple2.xml", LogManager.getLoggerRepository());
    
    List errorList = jc.getExecutionContext().getErrorList();
    for(int i = 0; i < errorList.size(); i++) {
      System.out.println(errorList.get(i));
    }
  }
}
