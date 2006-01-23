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


public class FileWatchdogTestCase extends TestCase {

    private Logger logger = Logger.getLogger(FileWatchdogTestCase.class);
    
    static String SOURCE_CONFIG = "input/watchdog/watchdog.FileWatchdog";
    static String FILE = "output/watchdog.FileWatchdog";
    static String WITNESS = "witness/watchdog/watchdog.FileWatchdog";

    public FileWatchdogTestCase(String name) {
        super(name);
    }

    private void copyFile(File src, File dst) throws Exception {
      if (dst.exists()) {
          assertTrue(dst.delete());
      }
      FileOutputStream out = new FileOutputStream(dst, false);
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
    public void test1() throws Exception {
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
      
      // configure environment to first config file
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.doConfigure(configFile.getAbsolutePath(),
          LogManager.getLoggerRepository());
      
      // now watch the file for changes
      FileWatchdog watchdog = new FileWatchdog();
      watchdog.setFile(configFile.getAbsolutePath());
      watchdog.setInterval(1000);
      watchdog.setConfigurator(JoranConfigurator.class.getName());
      ((LoggerRepositoryEx) LogManager.getLoggerRepository()).getPluginRegistry().addPlugin(watchdog);
      watchdog.activateOptions();

      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");

      Thread.sleep(2000);
      // copy over a new version of the config file
      copyFile(sourceFile2, configFile);
      
      // wait a few seconds for the watchdog to react
      for (int i = 0; i < 40; i++) {
          Thread.sleep(500);
          if (logger.getLevel() == Level.INFO) {
              // output some test messages
              logger.debug("debug message");
              logger.info("info message");
              logger.warn("warn message");
              logger.error("error message");
              logger.fatal("fatal message");

              assertTrue(Compare.compare(getOutputFile("test1"),
                getWitnessFile("test1")));
              return;
          }
      }
      fail("Expected change in level did not occur within 20 seconds.");
    }
    
    // basic test of plugin in standalone mode with PropertyConfigurator
    public void test2() throws Exception {
      File outFile = new File(getOutputFile("test2"));
      if (outFile.exists()) {
            assertTrue(outFile.delete());
      }

      // set up the needed file references
      File sourceFile1 = new File(getSourceConfigFile("test2", 1));
      File sourceFile2 = new File(getSourceConfigFile("test2", 2));
      assertTrue(sourceFile1.exists());
      assertTrue(sourceFile2.exists());
      
      File configFile = new File(getConfigFile("test2"));
      
      // move the first config file into place
      copyFile(sourceFile1, configFile);
      assertTrue(configFile.exists());
      
      // configure environment to first config file
      PropertyConfigurator configurator = new PropertyConfigurator();
      configurator.doConfigure(configFile.getAbsolutePath(),
          LogManager.getLoggerRepository());
      
      // now watch the file for changes
      FileWatchdog watchdog = new FileWatchdog();
      watchdog.setFile(configFile.getAbsolutePath());
      watchdog.setInterval(1000);
      watchdog.setConfigurator(PropertyConfigurator.class.getName());
      ((LoggerRepositoryEx) LogManager.getLoggerRepository()).getPluginRegistry().addPlugin(watchdog);
      watchdog.activateOptions();

      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");

      Thread.sleep(2000);
      // copy over a new version of the config file
      copyFile(sourceFile2, configFile);
      
      // wait a few seconds for the watchdog to react
      for (int i = 0; i < 40; i++) {
          Thread.sleep(500);
          if (logger.getLevel() == Level.INFO) {
            // output some test messages
            logger.debug("debug message");
            logger.info("info message");
            logger.warn("warn message");
            logger.error("error message");
            logger.fatal("fatal message");
      
            assertTrue(Compare.compare(getOutputFile("test2"),
                getWitnessFile("test2")));
            return;
          }
      }
      fail("Expected change in level did not occur within 20 seconds.");
    }
}
