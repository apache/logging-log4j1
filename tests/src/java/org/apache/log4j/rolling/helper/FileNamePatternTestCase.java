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

import junit.framework.TestCase;

import java.util.Calendar;


/**
 * Tests for FileNamePattern.
 *
 * @author Ceki
 * @author Curt Arnold
 *
 */
public final class FileNamePatternTestCase extends TestCase {
    /**
     * Construct new test.
     * @param name test name
     */
    public FileNamePatternTestCase(final String name) {
        super(name);
    }

    private void assertDatePattern(final String pattern, final int year,
        final int month, final int day, final int hour, final int min,
        final String expected) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, min);
        assertEquals(expected,
            new FileNamePattern(pattern).convert(cal.getTime()));
    }

    private void assertIntegerPattern(final String pattern, final int value,
        final String expected) {
        assertEquals(expected, new FileNamePattern(pattern).convert(value));
    }

    public void testFormatInteger1() {
        assertIntegerPattern("t", 3, "t");
    }

    public void testFormatInteger2() {
        assertIntegerPattern("foo", 3, "foo");
    }

    public void testFormatInteger3() {
        assertIntegerPattern("foo%", 3, "foo%");
    }

    public void testFormatInteger4() {
        assertIntegerPattern("%ifoo", 3, "3foo");
    }

    public void testFormatInteger5() {
        assertIntegerPattern("foo%ixixo", 3, "foo3xixo");
    }

    public void testFormatInteger6() {
        assertIntegerPattern("foo%i.log", 3, "foo3.log");
    }

    public void testFormatInteger7() {
        assertIntegerPattern("foo.%i.log", 3, "foo.3.log");
    }

    public void testFormatInteger8() {
        assertIntegerPattern("%ifoo%", 3, "3foo%");
    }

    public void testFormatInteger9() {
        assertIntegerPattern("%ifoo%%", 3, "3foo%");
    }

    public void testFormatInteger10() {
        assertIntegerPattern("%%foo", 3, "%foo");
    }

    public void testFormatInteger11() {
        assertIntegerPattern("foo%ibar%i", 3, "foo3bar3");
    }

    public void testFormatDate1() {
        assertDatePattern("foo%d{yyyy.MM.dd}", 2003, 4, 20, 17, 55,
            "foo2003.05.20");
    }

    public void testFormatDate2() {
        assertDatePattern("foo%d{yyyy.MM.dd HH:mm}", 2003, 4, 20, 17, 55,
            "foo2003.05.20 17:55");
    }

    public void testFormatDate3() {
        assertDatePattern("%d{yyyy.MM.dd HH:mm} foo", 2003, 4, 20, 17, 55,
            "2003.05.20 17:55 foo");
    }

    /**
     * A %d is treated as %d{yyyy-MM-dd} if followed by a malformed format specifier.
     *
     */
    public void testFormatDate4() {
        assertDatePattern("foo%dyyyy.MM.dd}", 2003, 4, 20, 17, 55,
            "foo2003-05-20yyyy.MM.dd}");
    }

    /**
     * A %d is treated as %d{yyyy-MM-dd} if followed by a malformed format specifier.
     *
     */
    public void testFormatDate5() {
        assertDatePattern("foo%d{yyyy.MM.dd", 2003, 4, 20, 17, 55,
            "foo2003-05-20{yyyy.MM.dd");
    }

    public void testNullFormat() {
        try {
            new FileNamePattern(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}
