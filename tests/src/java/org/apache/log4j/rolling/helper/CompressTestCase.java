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
package org.apache.log4j.rolling.helper;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.rolling.helper.Compress;
import org.apache.log4j.util.Compare;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Ceki

 */
public class CompressTestCase extends TestCase {

  Compress compress = new Compress();
  
  /**
   * Constructor for CompressTestCase.
   * @param arg0
   */
  public CompressTestCase(String arg0) {
    super(arg0);
  }
  
  public void setUp() {
    BasicConfigurator.configure();;
  }

  public void tearDown() {
    LogManager.shutdown();
  }
  
  public void test1() throws Exception {
    compress.GZCompress("input/compress1.txt", "output/compress1.txt.gz");   
    assertTrue(Compare.gzCompare("output/compress1.txt.gz",
           "witness/compress1.txt.gz"));  
  }
  
  public void test2() throws Exception {
     compress.GZCompress("input/compress2.txt", "output/compress2.txt");   
     assertTrue(Compare.gzCompare("output/compress2.txt.gz",
            "witness/compress2.txt.gz"));  
   }
   
  public void test3() throws Exception {
      compress.ZIPCompress("input/compress3.txt", "output/compress3.txt");   
      //assertTrue(Compare.compare("output/compress3.txt.zip",
        //     "witness/compress3.txt.zip"));  
    }
  
  public static Test suite() {
      TestSuite suite = new TestSuite();
      suite.addTest(new CompressTestCase("test1"));
      suite.addTest(new CompressTestCase("test2"));
      suite.addTest(new CompressTestCase("test3"));
      return suite;
    }
}
