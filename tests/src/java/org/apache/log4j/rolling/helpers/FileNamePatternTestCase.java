/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.rolling.helpers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @author Ceki
 *
 */
public class FileNamePatternTestCase extends TestCase {
  /**
   * Constructor for FileNamePatternParserTestCase.
   * @param arg0
   */
  public FileNamePatternTestCase(String arg0) {
    super(arg0);
  }

  public void test1() {
    //System.out.println("Testing [t]");
    FileNamePattern pp = new FileNamePattern("t");
    assertEquals("t", pp.convert(3));

    //System.out.println("Testing [foo]");
    pp = new FileNamePattern("foo");
    assertEquals("foo", pp.convert(3));

    //System.out.println("Testing [foo%]");
    pp = new FileNamePattern("foo%");
    assertEquals("foo%", pp.convert(3));

    pp = new FileNamePattern("%ifoo");
    assertEquals("3foo", pp.convert(3));

    pp = new FileNamePattern("foo%ixixo");
    assertEquals("foo3xixo", pp.convert(3));
    
    pp = new FileNamePattern("foo%i.log");
    assertEquals("foo3.log", pp.convert(3));

    pp = new FileNamePattern("foo.%i.log");
    assertEquals("foo.3.log", pp.convert(3));
    
    pp = new FileNamePattern("%ifoo%");
    assertEquals("3foo%", pp.convert(3));

    pp = new FileNamePattern("%ifoo%%");
    assertEquals("3foo%", pp.convert(3));

    pp = new FileNamePattern("%%foo");
    assertEquals("%foo", pp.convert(3));
   
  }


  public void test2() {
    System.out.println("Testing [foo%ibar%i]");
    FileNamePattern pp = new FileNamePattern("foo%ibar%i");
    assertEquals("foo3bar3", pp.convert(3));

    ///pp = new FileNamePattern("%%foo");
    //assertEquals("%foo", pp.convert(3));
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new FileNamePatternTestCase("test1"));
    suite.addTest(new FileNamePatternTestCase("test2"));
   
    return suite;
  }
}
