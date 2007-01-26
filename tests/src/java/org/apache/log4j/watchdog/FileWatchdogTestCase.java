/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

package org.apache.log4j.watchdog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.util.Compare;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;

public class FileWatchdogTestCase extends TestCase {

    private Logger testLogger = Logger.getLogger(FileWatchdogTestCase.class);
    private Logger logger = Logger.getLogger("test.FileWatchdogTestCase");

    static String SOURCE_CONFIG = "input/watchdog/watchdog.FileWatchdog";
    static String FILE = "output/watchdog.FileWatchdog";
    static String WITNESS = "witness/watchdog/watchdog.FileWatchdog";

    public FileWatchdogTestCase(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
      LogManager.getLoggerRepository().resetConfiguration();
      logger.setLevel(Level.DEBUG);      
      Thread.sleep(100);
    }

    private void copyFile(File src, File dst) throws Exception {
      if (dst.exists()) {
        for (int x = 0; x < 5; x++) {
          if (x == 4) {
            assertTrue("File " + dst.getAbsolutePath() +
            " not deleted", false);
          }
          if (dst.delete()) break;
          Thread.sleep(750);
        }
      }

      FileOutputStream out = new FileOutputStream(dst);
      FileInputStream in = new FileInputStream(src);
      byte[] buffer = new byte[1024];
      int size;
      do {
        size = in.read(buffer);
        if (size > 0) out.write(buffer,0,size);
      } while (size > 0);

      try {
        in.close();
      } catch (Exception e) {
        // don't care
      }

      try {
        out.close();
      } catch (Exception e) {
        // don't care
      }
    }

    private String getSourceXMLConfigFile(String caseName, int index) {

        return SOURCE_CONFIG + "." + caseName + "_" + index + ".xml";
    }

    private String getXMLConfigFile(String caseName) {

      return FILE + "." + caseName + ".xml";
    }

    private String getSourceConfigFile(String caseName, int index) {

        return SOURCE_CONFIG + "." + caseName + "_" + index + ".properties";
    }

    private String getConfigFile(String caseName) {

      return FILE + "." + caseName + ".properties";
    }

    private String getOutputFile(String caseName) {

      return FILE + "." + caseName + ".txt";
    }

    private String getWitnessFile(String caseName) {

      return WITNESS + "." + caseName + ".txt";
    }

    // basic test of plugin in standalone mode
    public void testJoranConfigurator() throws Exception {

      File outFile = new File(getOutputFile("test1"));
      if (outFile.exists()) {
          assertTrue(outFile.delete());
      }

      // set up the needed file references
      File sourceFile1 = new File(getSourceXMLConfigFile("test1", 1));
      File sourceFile2 = new File(getSourceXMLConfigFile("test1", 2));
      assertTrue(sourceFile1.exists());
      assertTrue(sourceFile2.exists());

      File configFile = new File(getXMLConfigFile("test1"));

      // move the first config file into place
      copyFile(sourceFile1, configFile);
      assertTrue(configFile.exists());

      testLogger.debug("first config file in place: " + configFile.getAbsolutePath());

      // configure environment to first config file
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.doConfigure(configFile.getAbsolutePath(),
          LogManager.getLoggerRepository());

      testLogger.debug("log4j configured with configFile");

      // now watch the file for changes
      FileWatchdog watchdog = new FileWatchdog();
      watchdog.setName("test1");
      watchdog.setFile(configFile.getAbsolutePath());
      watchdog.setInterval(1000);
      watchdog.setConfigurator(JoranConfigurator.class.getName());
      ((LoggerRepositoryEx) LogManager.getLoggerRepository()).getPluginRegistry().addPlugin(watchdog);
      watchdog.activateOptions();

      testLogger.debug("watchdog activated");

      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");

      testLogger.debug("first set of test messages output");

      Thread.sleep(2000);

      testLogger.debug("about to copy second config file");

      // copy over a new version of the config file
      copyFile(sourceFile2, configFile);

      testLogger.debug("second config file copied");

      // wait a few seconds for the watchdog to react
      for (int i = 0; i < 40; i++) {
          testLogger.debug("sleeping for 500 ms");
          Thread.sleep(500);
          testLogger.debug("level for logger " + logger.getName() + " is " + logger.getLevel());
          if (logger.getLevel() == Level.INFO) {
              // output some test messages
              logger.debug("debug message");
              logger.info("info message");
              logger.warn("warn message");
              logger.error("error message");
              logger.fatal("fatal message");

              testLogger.debug("second set of test messages output");

              assertTrue(Compare.compare(getOutputFile("test1"),
                getWitnessFile("test1")));
              return;
          }
          testLogger.debug("looping for level check");
      }
      fail("Expected change in level did not occur within 20 seconds.");
  }

  // basic test of plugin in standalone mode with PropertyConfigurator
  public void testPropertyConfigurator() throws Exception {
    File outFile = new File(getOutputFile("test2"));
    delete(outFile);

    // set up the needed file references
    File sourceFile1 = new File(getSourceConfigFile("test2", 1));
    File sourceFile2 = new File(getSourceConfigFile("test2", 2));
    assertTrue(sourceFile1.exists());
    assertTrue(sourceFile2.exists());

    File configFile = new File(getConfigFile("test2"));

    // move the first config file into place
    copyFile(sourceFile1, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("first config file in place: " + configFile.getAbsolutePath());

    // configure environment to first config file
    PropertyConfigurator configurator = new PropertyConfigurator();
    configurator.doConfigure(configFile.getAbsolutePath(),
        LogManager.getLoggerRepository());

    testLogger.debug("log4j configured with configFile");

    // now watch the file for changes
    FileWatchdog watchdog = new FileWatchdog();
    watchdog.setName("test2");
    watchdog.setFile(configFile.getAbsolutePath());
    watchdog.setInterval(1000);
    watchdog.setConfigurator(PropertyConfigurator.class.getName());
    ((LoggerRepositoryEx) LogManager.getLoggerRepository()).getPluginRegistry().addPlugin(watchdog);
    watchdog.activateOptions();

    testLogger.debug("watchdog activated");

    // output some test messages
    logger.debug("debug message");
    logger.info("info message");
    logger.warn("warn message");
    logger.error("error message");
    logger.fatal("fatal message");

    testLogger.debug("first set of test messages output");

    Thread.sleep(2000);

    testLogger.debug("about to copy second config file");

    // copy over a new version of the config file
    copyFile(sourceFile2, configFile);

    testLogger.debug("second config file copied");

    // wait a few seconds for the watchdog to react
    for (int i = 0; i < 40; i++) {
        testLogger.debug("sleeping for 500 ms");
        Thread.sleep(500);
        testLogger.debug("level for logger " + logger.getName() + " is " + logger.getLevel());
        if (logger.getLevel() == Level.INFO) {
            // output some test messages
            logger.debug("debug message");
            logger.info("info message");
            logger.warn("warn message");
            logger.error("error message");
            logger.fatal("fatal message");

            testLogger.debug("second set of test messages output");
            Thread.sleep(500);

            assertTrue("output does not match", Compare.compare(getOutputFile("test2"),
              getWitnessFile("test2")));
            return;
        }
        testLogger.debug("looping for level check");
    }
    fail("Expected change in level did not occur within 20 seconds.");
  }

  public void testJoranConfigurationError() throws Exception {
    File outFile = new File(getOutputFile("test3"));
    delete(outFile);

    // set up the needed file references
    File sourceFile1 = new File(getSourceXMLConfigFile("test3", 1));
    File sourceFile2 = new File(getSourceXMLConfigFile("test1", 2));
    assertTrue(sourceFile1.exists());
    assertTrue(sourceFile2.exists());

    // config file should not exist yet
    File configFile = new File(getXMLConfigFile("test3"));
    delete(configFile);

    // now watch the nonexistent file for changes
    FileWatchdog watchdog = new FileWatchdog();
    watchdog.setName("test3");
    watchdog.setFile(configFile.getAbsolutePath());
    watchdog.setInterval(1000);
    watchdog.setConfigurator(JoranConfigurator.class.getName());
    ((LoggerRepositoryEx) LogManager.getLoggerRepository()).getPluginRegistry().addPlugin(watchdog);
    watchdog.activateOptions();

    testLogger.debug("watchdog activated");

    // the file does not exist, so the modification time should never change
    long modTime = watchdog.getLastModTime();
    for (int count = 0; count < 5; count++) {
      if (modTime != watchdog.getLastModTime()) {
        assertTrue("watchdog mod time changed when no file", false);
      }
      Thread.sleep(500);
    }

    testLogger.debug("no file, mod time not changed: " + modTime);

    // move the bad config file into place
    copyFile(sourceFile1, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("bad config file put into place");

    // the file is "bad", so the modification time should never change
    for (int count = 0; count < 7; count++) {
      if (modTime != watchdog.getLastModTime()) {
        assertTrue("watchdog mod time changed for bad file", false);
      }
      Thread.sleep(500);
    }

    testLogger.debug("bad file, mod time not changed: " + modTime);

    // move the good config file into place
    copyFile(sourceFile2, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("moved good config file into place");

    // the file is good, so the modification time and level should change
    for (int count = 0; count < 7; count++) {
      if (modTime != watchdog.getLastModTime()) {
        assertTrue(logger.getLevel() == Level.INFO);
        break;
      }

      if (count == 6) {
        assertTrue("mod time for good file never changed", false);
      }

      Thread.sleep(500);
    }

    testLogger.debug("good file, modTime changed: " + modTime);
  }
  
  private void delete(File f) {
    if (f.exists()) {
      assertTrue(f.delete());
    }
  }

  public void testPropertyConfigurationError() throws Exception {
    File outFile = new File(getOutputFile("test4"));
    delete(outFile);

    // set up the needed file references
    // need a "bad" property file
    //File sourceFile1 = new File(getSourceConfigFile("test4", 1));
    File sourceFile2 = new File(getSourceConfigFile("test2", 2));
    //assertTrue(sourceFile1.exists());
    assertTrue(sourceFile2.exists());

    File configFile = new File(getConfigFile("test4"));
    delete(configFile);
    // assertFalse(configFile.exists());

    // now watch the nonexistent file for changes
    FileWatchdog watchdog = new FileWatchdog();
    watchdog.setName("test4");
    watchdog.setFile(configFile.getAbsolutePath());
    watchdog.setInterval(1000);
    watchdog.setConfigurator(PropertyConfigurator.class.getName());
    ((LoggerRepositoryEx) LogManager.getLoggerRepository()).getPluginRegistry().addPlugin(watchdog);
    watchdog.activateOptions();

    testLogger.debug("watchdog activated");

    // the file does not exist, so the modification time should never change
    long modTime = watchdog.getLastModTime();
    for (int count = 0; count < 5; count++) {
      if (modTime != watchdog.getLastModTime()) {
        assertTrue("watchdog mod time changed when no file", false);
      }
      Thread.sleep(500);
    }

    testLogger.debug("no file, mod time not changed: " + modTime);

/* need a "bad" property file
    // move the bad config file into place
    copyFile(sourceFile1, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("bad config file put into place");

    // the file is "bad", so the modification time should never change
    for (int count = 0; count < 7; count++) {
      if (modTime != watchdog.getLastModTime()) {
        assertTrue("watchdog mod time changed for bad file", false);
      }
      Thread.sleep(500);
    }

    testLogger.debug("bad file, mod time not changed: " + modTime);
*/

    // move the good config file into place
    copyFile(sourceFile2, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("moved good config file into place");

    // the file is good, so the modification time and level should change
    for (int count = 0; count < 7; count++) {
      if (modTime != watchdog.getLastModTime()) {
        assertTrue(logger.getLevel() == Level.INFO);
        break;
      }

      if (count == 6) {
        assertTrue("mod time for good file never changed", false);
      }

      Thread.sleep(500);
    }

    testLogger.debug("good file, modTime changed: " + modTime);
  }

  public void testDOMConfigureAndWatch() throws Exception {
    File outFile = new File(getOutputFile("test5"));
    if (outFile.exists()) {
        assertTrue(outFile.delete());
    }

    // set up the needed file references
    File sourceFile1 = new File(getSourceXMLConfigFile("test5", 1));
    File sourceFile2 = new File(getSourceXMLConfigFile("test5", 2));
    assertTrue(sourceFile1.exists());
    assertTrue(sourceFile2.exists());

    File configFile = new File(getXMLConfigFile("test5"));

    // move the first config file into place
    copyFile(sourceFile1, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("first config file in place: " + configFile.getAbsolutePath());

    // now watch the file for changes
    DOMConfigurator.configureAndWatch(configFile.getAbsolutePath(), 1000);

    testLogger.debug("configureAndWatch activated");

    // output some test messages
    logger.debug("debug message");
    logger.info("info message");
    logger.warn("warn message");
    logger.error("error message");
    logger.fatal("fatal message");

    testLogger.debug("first set of test messages output");

    Thread.sleep(2000);

    testLogger.debug("about to copy second config file");

    // copy over a new version of the config file
    copyFile(sourceFile2, configFile);

    testLogger.debug("second config file copied");

    // wait a few seconds for the watchdog to react
    for (int i = 0; i < 40; i++) {
        testLogger.debug("sleeping for 500 ms");
        Thread.sleep(500);
        testLogger.debug("level for logger " + logger.getName() + " is " + logger.getLevel());
        if (logger.getLevel() == Level.INFO) {
            // output some test messages
            logger.debug("debug message");
            logger.info("info message");
            logger.warn("warn message");
            logger.error("error message");
            logger.fatal("fatal message");

            testLogger.debug("second set of test messages output");
            Thread.sleep(500);

            assertTrue("output does not match", Compare.compare(getOutputFile("test5"),
              getWitnessFile("test5")));
            return;
        }
        testLogger.debug("looping for level check");
    }
    fail("Expected change in level did not occur within 20 seconds.");
  }

  /* there is a bug in property configurator where it will not work a second
     time, so commenting this out for now
  public void testPropertyConfigureAndWatch() throws Exception {
    File outFile = new File(getOutputFile("test6"));
    if (outFile.exists()) {
          assertTrue(outFile.delete());
    }

    // set up the needed file references
    File sourceFile1 = new File(getSourceConfigFile("test6", 1));
    File sourceFile2 = new File(getSourceConfigFile("test6", 2));
    assertTrue(sourceFile1.exists());
    assertTrue(sourceFile2.exists());

    File configFile = new File(getConfigFile("test6"));

    // move the first config file into place
    copyFile(sourceFile1, configFile);
    assertTrue(configFile.exists());

    testLogger.debug("first config file in place: " + configFile.getAbsolutePath());

    // now watch the file for changes
    PropertyConfigurator.configureAndWatch(configFile.getAbsolutePath(), 1000);

    testLogger.debug("configureAndWatch activated");

    // output some test messages
    logger.debug("debug message");
    logger.info("info message");
    logger.warn("warn message");
    logger.error("error message");
    logger.fatal("fatal message");

    testLogger.debug("first set of test messages output");

    Thread.sleep(2000);

    testLogger.debug("about to copy second config file");

    // copy over a new version of the config file
    copyFile(sourceFile2, configFile);

    testLogger.debug("second config file copied");

    // wait a few seconds for the watchdog to react
    for (int i = 0; i < 40; i++) {
        testLogger.debug("sleeping for 500 ms");
        Thread.sleep(500);
        testLogger.debug("level for logger " + logger.getName() + " is " + logger.getLevel());
        if (logger.getLevel() == Level.INFO) {
            // output some test messages
            logger.debug("debug message");
            logger.info("info message");
            logger.warn("warn message");
            logger.error("error message");
            logger.fatal("fatal message");

            testLogger.debug("second set of test messages output");
            Thread.sleep(1000);

            assertTrue("output does not match", Compare.compare(getOutputFile("test6"),
              getWitnessFile("test6")));
            return;
        }
        testLogger.debug("looping for level check");
    }
    fail("Expected change in level did not occur within 20 seconds.");
  }
  */
}
