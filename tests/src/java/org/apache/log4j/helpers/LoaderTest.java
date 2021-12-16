package org.apache.log4j.helpers;

import junit.framework.TestCase;

public class LoaderTest extends TestCase {

    public void testIsJava1IsAlwaysFalse() {
        assertFalse(Loader.isJava1());
    }
}
