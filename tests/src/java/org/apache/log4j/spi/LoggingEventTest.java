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

package org.apache.log4j.spi;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.location.LocationInfo;
import org.apache.log4j.util.SerializationTestHelper;


/**
 * Tests LoggingEvent.
 *
 * @author Curt Arnold
 * @since 1.3
 */
public class LoggingEventTest extends TestCase {
  /**
   * Create LoggingEventTest.
   *
   * @param name test name.
   */
  public LoggingEventTest(final String name) {
    super(name);
  }

  /**
   * Serialize a simple logging event and check it against
   * a witness.
   * @throws Exception if exception during test.
   */
  public void testSerializationSimple() throws Exception {
    Logger root = Logger.getRootLogger();
    LoggingEvent event =
      new LoggingEvent(
        root.getClass().getName(), root, Level.INFO, "Hello, world.", null);
    event.prepareForDeferredProcessing();

    int[] skip = new int[] { 358, 359, 360, 361, 362 };
    SerializationTestHelper.assertSerializationEquals(
      "witness/serialization/simple.bin", event, skip, Integer.MAX_VALUE);
  }

  /**
   * Serialize a logging event with an exception and check it against
   * a witness.
   * @throws Exception if exception during test.
   *
   */
  public void testSerializationWithException() throws Exception {
    Logger root = Logger.getRootLogger();
    Exception ex = new Exception("Don't panic");
    LoggingEvent event =
      new LoggingEvent(
        root.getClass().getName(), root, Level.INFO, "Hello, world.", ex);
    event.prepareForDeferredProcessing();

    int[] skip = new int[] { 358, 359, 360, 361, 362, 600, 734, 735, 1511 };
    SerializationTestHelper.assertSerializationEquals(
      "witness/serialization/exception.bin", event, skip, 1089);
  }

  /**
   * Serialize a logging event with an exception and check it against
   * a witness.
   * @throws Exception if exception during test.
   *
   */
  public void testSerializationWithLocation() throws Exception {
    Logger root = Logger.getRootLogger();
    LoggingEvent event =
      new LoggingEvent(
        root.getClass().getName(), root, Level.INFO, "Hello, world.", null);
    LocationInfo info = event.getLocationInformation();
    event.prepareForDeferredProcessing();

    int[] skip = new int[] { 354, 355, 356, 357, 358, 359, 360, 361, 362 };
    SerializationTestHelper.assertSerializationEquals(
      "witness/serialization/location.bin", event, skip, Integer.MAX_VALUE);
  }

  /**
   * Serialize a logging event with ndc.
   * @throws Exception if exception during test.
   *
   */
  public void testSerializationNDC() throws Exception {
    Logger root = Logger.getRootLogger();
    NDC.push("ndc test");

    LoggingEvent event =
      new LoggingEvent(
        root.getClass().getName(), root, Level.INFO, "Hello, world.", null);
    event.prepareForDeferredProcessing();

    int[] skip = new int[] { 354, 355, 356, 357, 358, 359, 360, 361, 362 };
    SerializationTestHelper.assertSerializationEquals(
      "witness/serialization/ndc.bin", event, skip, Integer.MAX_VALUE);
  }

  /**
   * Serialize a logging event with mdc.
   * @throws Exception if exception during test.
   *
   */
  public void testSerializationMDC() throws Exception {
    Logger root = Logger.getRootLogger();
    MDC.put("mdckey", "mdcvalue");

    LoggingEvent event =
      new LoggingEvent(
        root.getClass().getName(), root, Level.INFO, "Hello, world.", null);
    event.prepareForDeferredProcessing();

    int[] skip = new int[] { 354, 355, 356, 357, 358, 359, 360, 361, 362 };
    SerializationTestHelper.assertSerializationEquals(
      "witness/serialization/mdc.bin", event, skip, Integer.MAX_VALUE);
  }

  /**
   * Deserialize a simple logging event.
   * @throws Exception if exception during test.
   *
   */
  public void testDeserializationSimple() throws Exception {
    Object obj =
      SerializationTestHelper.deserializeStream(
        "witness/serialization/simple.bin");
    assertTrue(obj instanceof LoggingEvent);

    LoggingEvent event = (LoggingEvent) obj;
    assertEquals("Hello, world.", event.getMessage());
    assertEquals(Level.INFO, event.getLevel());
  }

  /**
   * Deserialize a logging event with an exception.
   * @throws Exception if exception during test.
   *
   */
  public void testDeserializationWithException() throws Exception {
    Object obj =
      SerializationTestHelper.deserializeStream(
        "witness/serialization/exception.bin");
    assertTrue(obj instanceof LoggingEvent);

    LoggingEvent event = (LoggingEvent) obj;
    assertEquals("Hello, world.", event.getMessage());
    assertEquals(Level.INFO, event.getLevel());
  }

  /**
   * Deserialize a logging event with an exception.
   * @throws Exception if exception during test.
   *
   */
  public void testDeserializationWithLocation() throws Exception {
    Object obj =
      SerializationTestHelper.deserializeStream(
        "witness/serialization/location.bin");
    assertTrue(obj instanceof LoggingEvent);

    LoggingEvent event = (LoggingEvent) obj;
    assertEquals("Hello, world.", event.getMessage());
    assertEquals(Level.INFO, event.getLevel());
  }
}
