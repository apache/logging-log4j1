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

package org.apache.log4j;

import junit.framework.TestCase;

import org.apache.log4j.util.SerializationTestHelper;


/**
 * Tests of Level.
 *
 * @author Curt Arnold
 * @since 1.3
 */
public class LevelTest extends TestCase {
  /**
   * Constructs new instance of test.
   * @param name test name.
   */
  public LevelTest(final String name) {
    super(name);
  }

  /**
   * Serialize Level.INFO and check against witness.
   * @throws Exception if exception during test.
   *
   */
  public void testSerializeINFO() throws Exception {
    int[] skip = new int[] {  };
    SerializationTestHelper.assertSerializationEquals(
      "witness/serialization/info.bin", Level.INFO, skip, Integer.MAX_VALUE);
  }

  /**
   * Deserialize witness and see if resolved to Level.INFO.
   * @throws Exception if exception during test.
   */
  public void testDeserializeINFO() throws Exception {
    Object obj =
      SerializationTestHelper.deserializeStream(
        "witness/serialization/info.bin");
    assertTrue(obj instanceof Level);
    assertTrue(obj == Level.INFO);
  }

  /**
   * Tests that a custom level can be serialized and deserialized
   * and is not resolved to a stock level.
   *
   * @throws Exception if exception during test.
   */
  public void testCustomLevelSerialization() throws Exception {
    CustomLevel custom = new CustomLevel();
    Object obj = SerializationTestHelper.serializeClone(custom);
    assertTrue(obj instanceof CustomLevel);

    CustomLevel clone = (CustomLevel) obj;
    assertEquals(Level.INFO.level, clone.level);
    assertEquals(Level.INFO.levelStr, clone.levelStr);
    assertEquals(Level.INFO.syslogEquivalent, clone.syslogEquivalent);
  }

  /**
   * Custom level to check that custom levels are
   * serializable, but not resolved to a plain Level.
   */
  private static class CustomLevel extends Level {
      /**
       * Create an instance of CustomLevel.
       */
    public CustomLevel() {
      super(
        Level.INFO.level, Level.INFO.levelStr, Level.INFO.syslogEquivalent);
    }
  }
}
