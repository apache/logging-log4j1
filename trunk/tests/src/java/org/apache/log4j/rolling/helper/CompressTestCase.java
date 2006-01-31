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
package org.apache.log4j.rolling.helper;

import org.apache.log4j.util.Compare;

import junit.framework.TestCase;
import java.io.File;

/**
 * @author Ceki

 */
public class CompressTestCase extends TestCase {

  /**
   * Constructor for CompressTestCase.
   * @param arg0
   */
  public CompressTestCase(String arg0) {
    super(arg0);
  }
  
  public void test1() throws Exception {
    GZCompressAction.execute(new File("input/compress1.txt"), new File("output/compress1.txt.gz"), false, null);
    assertTrue(Compare.gzCompare("output/compress1.txt.gz",
           "witness/compress1.txt.gz"));  
  }
  
  public void test2() throws Exception {
     GZCompressAction.execute(new File("input/compress2.txt"), new File("output/compress2.txt.gz"), false, null);
     assertTrue(Compare.gzCompare("output/compress2.txt.gz",
            "witness/compress2.txt.gz"));  
   }

  /*  witness file does not exist
  public void test3() throws Exception {
      ZipCompressAction.execute(new File("input/compress3.txt"), new File("output/compress3.txt.zip"), false, null);
      assertTrue(Compare.compare("output/compress3.txt.zip",
             "witness/compress3.txt.zip"));
    }
    */
}
