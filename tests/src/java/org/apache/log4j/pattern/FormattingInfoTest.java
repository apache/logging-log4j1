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

package org.apache.log4j.pattern;

import junit.framework.TestCase;


/**
 * Tests for FormattingInfo.
 *
 * @author Curt Arnold
 * @since 1.3
 *
 */
public class FormattingInfoTest extends TestCase {
  /**
   * Create a new instance.
   *
   * @param name test name
   */
  public FormattingInfoTest(final String name) {
    super(name);
  }

  /**
   * Check that getDefault does not return null.
   *
   */
  public void testGetDefault() {
    FormattingInfo field = FormattingInfo.getDefault();
    assertNotNull(field);
    assertEquals(0, field.getMinLength());
    assertEquals(Integer.MAX_VALUE, field.getMaxLength());
    assertEquals(false, field.isLeftAligned());
  }

  /**
   * Check constructor
   *
   */
  public void testConstructor() {
      FormattingInfo field = new FormattingInfo(true, 3, 6);
      assertNotNull(field);
      assertEquals(3, field.getMinLength());
      assertEquals(6, field.getMaxLength());
      assertEquals(true, field.isLeftAligned());
  }

  /**
   * Field exceeds maximum width
   */
  public void testTruncate() {
      StringBuffer buf = new StringBuffer("foobar");
      FormattingInfo field = new FormattingInfo(true, 0, 3);
      field.format(2, buf);
      assertEquals("fobar", buf.toString());
  }

    /**
     * Add padding to left since field is not minimum width.
     */
    public void testPadLeft() {
        StringBuffer buf = new StringBuffer("foobar");
        FormattingInfo field = new FormattingInfo(false, 5, 10);
        field.format(2, buf);
        assertEquals("fo obar", buf.toString());
    }

    /**
     * Add padding to right since field is not minimum width.
     */
    public void testPadRight() {
        StringBuffer buf = new StringBuffer("foobar");
        FormattingInfo field = new FormattingInfo(true, 5, 10);
        field.format(2, buf);
        assertEquals("foobar ", buf.toString());
    }

}
