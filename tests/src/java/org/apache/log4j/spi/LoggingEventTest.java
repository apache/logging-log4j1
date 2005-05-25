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

import java.io.*;

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
     *
     */
   public void testSerializationSimple() throws Exception {
      Logger root = Logger.getRootLogger();
      LoggingEvent event = new LoggingEvent(root.getClass().getName(),
              root, Level.INFO, "Hello, world.", null);
      event.prepareForDeferredProcessing();
      int[] skip = new int[] { 358, 359, 360, 361, 362 };
      assertSerializationEquals("witness/serialization/simple.bin", event, skip, Integer.MAX_VALUE);
   }

    /**
     * Serialize a logging event with an exception and check it against
     * a witness.
     *
     */
   public void testSerializationWithException() throws Exception {
      Logger root = Logger.getRootLogger();
      Exception ex = new Exception("Don't panic");
      LoggingEvent event = new LoggingEvent(root.getClass().getName(),
              root, Level.INFO, "Hello, world.", ex);
      event.prepareForDeferredProcessing();
      int[] skip = new int[] { 358, 359, 360, 361, 362, 600, 735, 1511};
      assertSerializationEquals("witness/serialization/exception.bin", event, skip, 1089);
   }

    /**
     * Serialize a logging event with an exception and check it against
     * a witness.
     *
     */
   public void testSerializationWithLocation() throws Exception {
      Logger root = Logger.getRootLogger();
      LoggingEvent event = new LoggingEvent(root.getClass().getName(),
              root, Level.INFO, "Hello, world.", null);
      LocationInfo info = event.getLocationInformation();
      event.prepareForDeferredProcessing();
      int[] skip = new int[] { 354, 355, 356, 357, 358, 359, 360, 361, 362 };
      assertSerializationEquals("witness/serialization/location.bin", event, skip, Integer.MAX_VALUE);
   }

    /**
     * Serialize a logging event with ndc.
     *
     */
   public void testSerializationNDC() throws Exception {
      Logger root = Logger.getRootLogger();
      NDC.push("ndc test");
      LoggingEvent event = new LoggingEvent(root.getClass().getName(),
              root, Level.INFO, "Hello, world.", null);
      event.prepareForDeferredProcessing();
      int[] skip = new int[] { 354, 355, 356, 357, 358, 359, 360, 361, 362 };
      assertSerializationEquals("witness/serialization/ndc.bin", event, skip, Integer.MAX_VALUE);
   }

    /**
     * Serialize a logging event with mdc.
     *
     */
   public void testSerializationMDC() throws Exception {
      Logger root = Logger.getRootLogger();
      MDC.put("mdckey", "mdcvalue");
      LoggingEvent event = new LoggingEvent(root.getClass().getName(),
              root, Level.INFO, "Hello, world.", null);
      event.prepareForDeferredProcessing();
      int[] skip = new int[] { 354, 355, 356, 357, 358, 359, 360, 361, 362 };
      assertSerializationEquals("witness/serialization/mdc.bin", event, skip, Integer.MAX_VALUE);
   }


    /**
     * Deserialize a simple logging event.
     *
     */
   public void testDeserializationSimple() throws Exception {
      Object obj = deserializeStream("witness/serialization/simple.bin");
      assertTrue(obj instanceof LoggingEvent);
      LoggingEvent event = (LoggingEvent) obj;
      assertEquals("Hello, world.", event.getMessage());
      assertEquals(Level.INFO, event.getLevel());
   }

    /**
     * Deserialize a logging event with an exception.
     *
     */
   public void testDeserializationWithException() throws Exception {
        Object obj = deserializeStream("witness/serialization/exception.bin");
        assertTrue(obj instanceof LoggingEvent);
        LoggingEvent event = (LoggingEvent) obj;
        assertEquals("Hello, world.", event.getMessage());
        assertEquals(Level.INFO, event.getLevel());
   }

    /**
     * Deserialize a logging event with an exception.
     *
     */
   public void testDeserializationWithLocation() throws Exception {
        Object obj = deserializeStream("witness/serialization/location.bin");
        assertTrue(obj instanceof LoggingEvent);
        LoggingEvent event = (LoggingEvent) obj;
        assertEquals("Hello, world.", event.getMessage());
        assertEquals(Level.INFO, event.getLevel());
   }



   private static Object deserializeStream(final String witness)
      throws Exception {
      FileInputStream fileIs = new FileInputStream(witness);
      ObjectInputStream objIs = new ObjectInputStream(fileIs);
      return objIs.readObject();
   }



   private static void assertSerializationEquals(final String witness,
                                                 final LoggingEvent event,
                                                 final int[] skip,
                                                 final int endCompare) throws Exception {

       ByteArrayOutputStream memOut = new ByteArrayOutputStream();
       ObjectOutputStream objOut = new ObjectOutputStream(memOut);
       objOut.writeObject(event);
       objOut.close();

       assertStreamEquals(witness,
               memOut.toByteArray(),
               skip, endCompare);

   }

   private static void assertStreamEquals(final String witness,
                                          final byte[] actual,
                                          final int[] skip,
                                          final int endCompare)
    throws IOException {
       File witnessFile = new File(witness);
       if (witnessFile.exists()) {
           int skipIndex = 0;
           byte[] expected = new byte[actual.length];
           FileInputStream is = new FileInputStream(witnessFile);
           int bytesRead = is.read(expected);
           is.close();
           assertEquals(bytesRead, actual.length);
           int endScan = actual.length;
           if (endScan > endCompare) {
              endScan = endCompare;
           }
           for (int i = 0; i < endScan; i++) {
               if (skipIndex < skip.length && skip[skipIndex] == i) {
                   skipIndex++;
               } else {
                   if (expected[i] != actual[i]) {
                       assertEquals("Difference at offset " + i, expected[i], actual[i]);
                   }
               }
           }

       } else {
           //
           //  if the file doesn't exist then
           //      assume that we are setting up and need to write it
           FileOutputStream os = new FileOutputStream(witnessFile);
           os.write(actual);
           os.close();
           fail("Writing witness file " + witness);
       }
   }
}
