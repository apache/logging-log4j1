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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.rolling.helper.FileNamePattern;

import java.util.Calendar;


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

  public void setUp() throws Exception {
    BasicConfigurator.configure();

    //Logger root = Logger.getRootLogger();
    //Appender appender =
    //  new FileAppender(new PatternLayout(), "filenamepattern.log");
    //root.addAppender(appender);
  }

  public void tearDown() {
    LogManager.shutdown();
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

  public void test3() {
    Calendar cal = Calendar.getInstance();
    cal.set(2003, 4, 20, 17, 55);

    FileNamePattern pp = new FileNamePattern("foo%d{yyyy.MM.dd}");
    assertEquals("foo2003.05.20", pp.convert(cal.getTime()));

    pp = new FileNamePattern("foo%d{yyyy.MM.dd HH:mm}");
    assertEquals("foo2003.05.20 17:55", pp.convert(cal.getTime()));

    pp = new FileNamePattern("%d{yyyy.MM.dd HH:mm} foo");
     assertEquals("2003.05.20 17:55 foo", pp.convert(cal.getTime()));


    // Degenerate cases:
    pp = new FileNamePattern("foo%dyyyy.MM.dd}");
    assertEquals("foo%dyyyy.MM.dd}", pp.convert(cal.getTime()));
    
    pp = new FileNamePattern("foo%d{yyyy.MM.dd");
    assertEquals("foo%d{yyyy.MM.dd", pp.convert(cal.getTime()));
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new FileNamePatternTestCase("test1"));
    suite.addTest(new FileNamePatternTestCase("test2"));
    suite.addTest(new FileNamePatternTestCase("test3"));

    return suite;
  }
}
