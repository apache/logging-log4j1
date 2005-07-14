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

package org.apache.log4j.xml;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.util.*;

import java.io.File;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class DOMTest extends TestCase {
  static String TEMP_A1 = "output/temp.A1";
  static String TEMP_A2 = "output/temp.A2";
  static String FILTERED_A1 = "output/filtered.A1";
  static String FILTERED_A2 = "output/filtered.A2";
  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";
  static String TEST1_1A_PAT =
    "(DEBUG|INFO |WARN |ERROR|FATAL) \\w*\\.\\w* - Message \\d";
  static String TEST1_1B_PAT =
    "(DEBUG|INFO |WARN |ERROR|FATAL) root - Message \\d";
  static String TEST1_2_PAT =
    "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} "
    + "\\[main]\\ (DEBUG|INFO|WARN|ERROR|FATAL) .* - Message \\d";
  Logger root;
  Logger logger;

  public DOMTest(String name) {
    super(name);
  }

  public void setUp() {
    root = Logger.getRootLogger();
    logger = Logger.getLogger(DOMTest.class);
  }

  public void tearDown() {
    root.getLoggerRepository().resetConfiguration();
  }

  public void test1() throws Exception {
    //org.apache.log4j.BasicConfigurator.configure();
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure("input/xml/DOMTest1.xml", LogManager.getLoggerRepository());
    dumpErrors(jc.getErrorList());
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] {
          TEST1_1A_PAT, TEST1_1B_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3
        });

    ControlFilter cf2 =
      new ControlFilter(
        new String[] { TEST1_2_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3 });

    Transformer.transform(
      TEMP_A1, FILTERED_A1,
      new Filter[] {
        cf1, new LineNumberFilter(), new SunReflectFilter(),
        new JunitTestRunnerFilter()
      });

    Transformer.transform(
      TEMP_A2, FILTERED_A2,
      new Filter[] {
        cf2, new LineNumberFilter(), new ISO8601Filter(),
        new SunReflectFilter(), new JunitTestRunnerFilter()
      });

    assertTrue(Compare.compare(FILTERED_A1, "witness/xml/dom.A1.1"));
    assertTrue(Compare.compare(FILTERED_A2, "witness/xml/dom.A2.1"));
  }

  /**
   * Identical test except that backslashes are used instead of
   * forward slashes on all file specifications.  Test is
   * only applicable to Windows.
   *
   * @throws Exception Any exception will cause test to fail
   */
  public void test2() throws Exception {
    if (File.separatorChar == '\\') {
      JoranConfigurator jc = new JoranConfigurator();
      jc.doConfigure(
        "input\\xml\\DOMTest2.xml", LogManager.getLoggerRepository());
      dumpErrors(jc.getErrorList());
      common();

      ControlFilter cf1 =
        new ControlFilter(
          new String[] {
            TEST1_1A_PAT, TEST1_1B_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3
          });

      ControlFilter cf2 =
        new ControlFilter(
          new String[] { TEST1_2_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3 });

      Transformer.transform(
        TEMP_A1 + ".2", FILTERED_A1 + ".2",
        new Filter[] {
          cf1, new LineNumberFilter(), new SunReflectFilter(),
          new JunitTestRunnerFilter()
        });

      Transformer.transform(
        TEMP_A2 + ".2", FILTERED_A2 + ".2",
        new Filter[] {
          cf2, new LineNumberFilter(), new ISO8601Filter(),
          new SunReflectFilter(), new JunitTestRunnerFilter()
        });

      assertTrue(Compare.compare(FILTERED_A1 + ".2", "witness/xml/dom.A1.2"));
      assertTrue(Compare.compare(FILTERED_A2 + ".2", "witness/xml/dom.A2.2"));
    }
  }

  /**
   * This test checks the implementation of DOMConfigurator.doConfigure(Element)
   * which is provided for compatibility with log4j 1.2 and used by excalibur-logging.
   *
   * @deprecated This test checks a deprecated method and so needs to be deprecated itself.
   *
   * @throws Exception on failure to find parser, etc.
   */
  public void test3() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);

    DocumentBuilder builder = factory.newDocumentBuilder();
    org.w3c.dom.Document doc =
      builder.parse(new File("input/xml/DOMTest3.xml"));

    DOMConfigurator domConfig = new DOMConfigurator();
    domConfig.doConfigure(
      doc.getDocumentElement(), LogManager.getLoggerRepository());

    dumpErrors(domConfig.getErrorList());
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] {
          TEST1_1A_PAT, TEST1_1B_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3
        });

    ControlFilter cf2 =
      new ControlFilter(
        new String[] { TEST1_2_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3 });

    Transformer.transform(
      TEMP_A1 + ".3", FILTERED_A1 + ".3",
      new Filter[] {
        cf1, new LineNumberFilter(), new SunReflectFilter(),
        new JunitTestRunnerFilter()
      });

    Transformer.transform(
      TEMP_A2 + ".3", FILTERED_A2 + ".3",
      new Filter[] {
        cf2, new LineNumberFilter(), new ISO8601Filter(),
        new SunReflectFilter(), new JunitTestRunnerFilter()
      });

    assertTrue(Compare.compare(FILTERED_A1 + ".3", "witness/xml/dom.A1.3"));
    assertTrue(Compare.compare(FILTERED_A2 + ".3", "witness/xml/dom.A2.3"));
  }
  
  /**
   *   Tests processing of external entities in XML file.
   */  
  public void test4() throws Exception {
    //org.apache.log4j.BasicConfigurator.configure();
    JoranConfigurator jc = new JoranConfigurator();
    jc.doConfigure("input/xml/DOMTest4.xml", LogManager.getLoggerRepository());
    dumpErrors(jc.getErrorList());
    common();

    ControlFilter cf1 =
      new ControlFilter(
        new String[] {
          TEST1_1A_PAT, TEST1_1B_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3
        });

    ControlFilter cf2 =
      new ControlFilter(
        new String[] { TEST1_2_PAT, EXCEPTION1, EXCEPTION2, EXCEPTION3 });

    Transformer.transform(
      TEMP_A1 + ".4", FILTERED_A1 + ".4",
      new Filter[] {
        cf1, new LineNumberFilter(), new SunReflectFilter(),
        new JunitTestRunnerFilter()
      });

    Transformer.transform(
      TEMP_A2 + ".4", FILTERED_A2 + ".4",
      new Filter[] {
        cf2, new LineNumberFilter(), new ISO8601Filter(),
        new SunReflectFilter(), new JunitTestRunnerFilter()
      });

    assertTrue(Compare.compare(FILTERED_A1 + ".4", "witness/xml/dom.A1.4"));
    assertTrue(Compare.compare(FILTERED_A2 + ".4", "witness/xml/dom.A2.4"));
  }


  void common() {
    int i = -1;

    logger.debug("Message " + ++i);
    root.debug("Message " + i);

    logger.info("Message " + ++i);
    root.info("Message " + i);

    logger.warn("Message " + ++i);
    root.warn("Message " + i);

    logger.error("Message " + ++i);
    root.error("Message " + i);

    logger.log(Level.FATAL, "Message " + ++i);
    root.log(Level.FATAL, "Message " + i);

    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.debug("Message " + i, e);

    logger.error("Message " + ++i, e);
    root.error("Message " + i, e);
  }

  void dumpErrors(List errorList) {
    for (int i = 0; i < errorList.size(); i++) {
      ErrorItem ei = (ErrorItem) errorList.get(i);
      System.out.println(ei);

      Throwable t = ei.getException();

      if (t != null) {
        t.printStackTrace(System.out);
      }
    }
  }
}
