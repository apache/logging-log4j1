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

import java.io.PrintWriter;


/**
 * Unit tests for ThrowableInformation.
 */
public class ThrowableInformationTest extends TestCase {
    /**
     * Create ThrowableInformationTest.
     *
     * @param name test name.
     */
    public ThrowableInformationTest(final String name) {
        super(name);
    }

    /**
     * Custom throwable that only calls methods
     * overridden by VectorWriter in log4j 1.2.14 and earlier.
     */
    private static final class OverriddenThrowable extends Throwable {
        /**
         * Create new instance.
         */
        public OverriddenThrowable() {
        }

        /**
         * Print stack trace.
         *
         * @param s print writer.
         */
        public void printStackTrace(final PrintWriter s) {
            s.print((Object) "print(Object)");
            s.print("print(char[])".toCharArray());
            s.print("print(String)");
            s.println((Object) "println(Object)");
            s.println("println(char[])".toCharArray());
            s.println("println(String)");
            s.write("write(char[])".toCharArray());
            s.write("write(char[], int, int)".toCharArray(), 2, 8);
            s.write("write(String, int, int)", 2, 8);
        }
    }

    /**
     * Test capturing stack trace from a throwable that only uses the
     * PrintWriter methods overridden in log4j 1.2.14 and earlier.
     */
    public void testOverriddenBehavior() {
        ThrowableInformation ti = new ThrowableInformation(new OverriddenThrowable());
        String[] rep = ti.getThrowableStrRep();
        assertEquals(9, rep.length);
        assertEquals("print(Object)", rep[0]);
        assertEquals("print(char[])", rep[1]);
        assertEquals("print(String)", rep[2]);
        assertEquals("println(Object)", rep[3]);
        assertEquals("println(char[])", rep[4]);
        assertEquals("println(String)", rep[5]);
        assertEquals("write(char[])", rep[6]);
        assertEquals("ite(char", rep[7]);
        assertEquals("ite(Stri", rep[8]);
    }

    /**
     * Custom throwable that calls methods
     * not overridden by VectorWriter in log4j 1.2.14 and earlier.
     */
    private static final class NotOverriddenThrowable extends Throwable {
        /**
         * Create new instance.
         */
        public NotOverriddenThrowable() {
        }

        /**
         * Print stack trace.
         *
         * @param s print writer.
         */
        public void printStackTrace(final PrintWriter s) {
            s.print(true);
            s.print('a');
            s.print(1);
            s.print(2L);
            s.print(Float.MAX_VALUE);
            s.print(Double.MIN_VALUE);
            s.println(true);
            s.println('a');
            s.println(1);
            s.println(2L);
            s.println(Float.MAX_VALUE);
            s.println(Double.MIN_VALUE);
            s.write('C');
        }
    }

    /**
     * Test capturing stack trace from a throwable that uses the
     * PrintWriter methods not overridden in log4j 1.2.14 and earlier.
     */
    public void testNotOverriddenBehavior() {
        ThrowableInformation ti = new ThrowableInformation(new NotOverriddenThrowable());
        String[] rep = ti.getThrowableStrRep();
        //
        //   The results under log4j 1.2.14 could change depending on implementation
        //     of java.io.PrintWriter
        //
        assertEquals(10, rep.length);
        assertEquals(String.valueOf(true), rep[0]);
//      Calls to print(char) are discarded
//         assertEquals("a", rep[1]);
        assertEquals(String.valueOf(1), rep[1]);
        assertEquals(String.valueOf(2L), rep[2]);
        assertEquals(String.valueOf(Float.MAX_VALUE), rep[3]);
        assertEquals(String.valueOf(Double.MIN_VALUE), rep[4]);
        assertEquals(String.valueOf(true), rep[5]);
//      Calls to println(char) are discarded
//        assertEquals("a", rep[7]);
        assertEquals(String.valueOf(1), rep[6]);
        assertEquals(String.valueOf(2L), rep[7]);
        assertEquals(String.valueOf(Float.MAX_VALUE), rep[8]);
        assertEquals(String.valueOf(Double.MIN_VALUE), rep[9]);
//        output to write(int) are discarded
//        assertEquals("C", rep[12]);
    }

    /**
     * Custom throwable that calls methods of VectorWriter
     * with null.
     */
    private static final class NullThrowable extends Throwable {
        /**
         * Create new instance.
         */
        public NullThrowable() {
        }

        /**
         * Print stack trace.
         *
         * @param s print writer.
         */
        public void printStackTrace(final PrintWriter s) {
            s.print((Object) null);
            s.print((String) null);
            s.println((Object) null);
            s.println((String) null);
        }
    }

    /**
     * Test capturing stack trace from a throwable that passes
     * null to PrintWriter methods.
     */

    public void testNull() {
        ThrowableInformation ti = new ThrowableInformation(new NullThrowable());
        try {
            String[] rep = ti.getThrowableStrRep();
        } catch (NullPointerException ex) {
            return;
        }
        fail("log4j 1.2.14 would throw exception");
    }

    /**
     * Custom throwable that does nothing in printStackTrace.
     */
    private static final class EmptyThrowable extends Throwable {
        /**
         * Create new instance.
         */
        public EmptyThrowable() {
        }

        /**
         * Print stack trace.
         *
         * @param s print writer.
         */
        public void printStackTrace(final PrintWriter s) {
        }
    }

    /**
     * Test capturing stack trace from a throwable that
     * does nothing on a call to printStackTrace.
     */

    public void testEmpty() {
        ThrowableInformation ti = new ThrowableInformation(new EmptyThrowable());
        String[] rep = ti.getThrowableStrRep();
        assertEquals(0, rep.length);
    }
}
