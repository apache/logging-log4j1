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

package org.apache.log4j.pattern;

import junit.framework.TestCase;


/**
 * @author Ceki Gulcu
 *  */
public class CacheUtilTest extends TestCase {
  
  
  public CacheUtilTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public void testRemoveLiteral() {
    String result;
    result = CacheUtil.removeLiterals("a");
    assertEquals("a", result);
    
    result = CacheUtil.removeLiterals("a'a'");
    assertEquals("a", result);
    
    result = CacheUtil.removeLiterals("-+?.a124'a'");
    assertEquals("a", result);

    result = CacheUtil.removeLiterals("ZZZEEE");
    assertEquals("ZZZEEE", result);
  }

  public void testIsPatternSafeForCachingRemoveLiteral() {
    boolean result;
    result = CacheUtil.isPatternSafeForCaching("a");
    assertEquals(true, result);
    
    result = CacheUtil.isPatternSafeForCaching("aS");
    assertEquals(true, result);

    result = CacheUtil.isPatternSafeForCaching("aSS");
    assertEquals(true, result);

    result = CacheUtil.isPatternSafeForCaching("aSSS");
    assertEquals(true, result);

    result = CacheUtil.isPatternSafeForCaching("aSSSS");
    assertEquals(true, result);

    result = CacheUtil.isPatternSafeForCaching("aSaS");
    assertEquals(false, result);
    
    result = CacheUtil.isPatternSafeForCaching("aSSSSSaSSS");
    assertEquals(false, result);
    
    result = CacheUtil.isPatternSafeForCaching("aSaSa");
    assertEquals(false, result);

    result = CacheUtil.isPatternSafeForCaching("aSSaSSa");
    assertEquals(false, result);
    
    result = CacheUtil.isPatternSafeForCaching("aSSSaSSSa");
    assertEquals(false, result);
    


    result = CacheUtil.isPatternSafeForCaching("aEEEE SSS");
    assertEquals(true, result);
    
    result = CacheUtil.isPatternSafeForCaching("aEEEEMMMMM SSS");
    assertEquals(false, result);
  }
  
  public void testComputeSuccessiveS() {
    int result;
    result = CacheUtil.computeSuccessiveS("a");
    assertEquals(0, result);
    
    result = CacheUtil.computeSuccessiveS("aS");
    assertEquals(1, result);

    result = CacheUtil.computeSuccessiveS("aSS");
    assertEquals(2, result);

    result = CacheUtil.computeSuccessiveS("aSSS");
    assertEquals(3, result);
    
    result = CacheUtil.computeSuccessiveS("aSSSS");
    assertEquals(4, result);
    
    result = CacheUtil.computeSuccessiveS("aSxx");
    assertEquals(1, result);

    result = CacheUtil.computeSuccessiveS("aSSxx");
    assertEquals(2, result);

    result = CacheUtil.computeSuccessiveS("aSSSxx");
    assertEquals(3, result);
    
    result = CacheUtil.computeSuccessiveS("aSSSSxx");
    assertEquals(4, result);
  }

}
