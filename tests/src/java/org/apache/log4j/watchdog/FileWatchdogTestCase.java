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

package org.apache.log4j.watchdog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.joran.JoranConfigurator;


public class FileWatchdogTestCase extends TestCase {

    private Logger logger = Logger.getLogger(FileWatchdogTestCase.class);
    
    static String SOURCE_CONFIG = "input/watchdog/watchdog.FileWatchdog";
    static String FILE = "output/watchdog.FileWatchdog";
    static String WITNESS = "witness/watchdog.FileWatchdog";

    public FileWatchdogTestCase(String name) {
        super(name);
    }

    public void setUp() {
        // delete the output file if they happen to exist
        File file = new File(getOutputFile("test1"));
        file.delete();
    }

    private void copyFile(File src, File dst) throws Exception {
      FileInputStream in = in = new FileInputStream(src);
      FileOutputStream out = new FileOutputStream(dst);
      byte[] buffer = new byte[1024];
      int size = 0;
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
    
    private String getSourceXMLConfigFile(String caseName) {

        return SOURCE_CONFIG + "." + caseName + ".xml";
    }

    private String getSourceXMLConfigFile(String caseName, int index) {

        return SOURCE_CONFIG + "." + caseName + "_" + index + ".xml";
    }

    private String getXMLConfigFile(String caseName) {
      
      return FILE + "." + caseName + ".xml";
    }
    
    private String getSourceConfigFile(String caseName) {

        return SOURCE_CONFIG + "." + caseName + ".properties";
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
      
      // set up the needed file references
      File sourceFile1 = new File(getSourceXMLConfigFile("test1", 1));
      File sourceFile2 = new File(getSourceXMLConfigFile("test1", 2));
      assertTrue(sourceFile1.exists());
      assertTrue(sourceFile2.exists());
      
      File configFile = new File(getXMLConfigFile("test1"));
      
      // move the first config file into place
      copyFile(sourceFile1, configFile);
      assertTrue(configFile.exists());
      
      URL configURL = new URL("file:"+configFile.getAbsolutePath());
      
      // configure environment to first config file
      Configurator configurator = new JoranConfigurator();
      configurator.doConfigure(configURL, LogManager.getLoggerRepository());
      
      // now watch the file for changes
      FileWatchdog watchdog = new FileWatchdog();
      watchdog.setURL(configURL);
      watchdog.setInterval(2000);
      watchdog.setConfigurator(JoranConfigurator.class.getName());
      LogManager.getLoggerRepository().getPluginRegistry().addPlugin(watchdog);
      watchdog.activateOptions();

      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");

      // copy over a new version of the config file
      copyFile(sourceFile2, configFile);
      
      // wait a few seconds for the watchdog to react
      Thread.sleep(4000);
      
      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");
      
      /*
      assertTrue(Compare.compare(getOutputFile("test1"), 
        getWitnessFile("test1")));
      */
    }
    
    // basic test of plugin in standalone mode with PropertyConfigurator
    public void test2() throws Exception {
      
      // set up the needed file references
      File sourceFile1 = new File(getSourceConfigFile("test2", 1));
      File sourceFile2 = new File(getSourceConfigFile("test2", 2));
      assertTrue(sourceFile1.exists());
      assertTrue(sourceFile2.exists());
      
      File configFile = new File(getConfigFile("test2"));
      
      // move the first config file into place
      copyFile(sourceFile1, configFile);
      assertTrue(configFile.exists());
      
      URL configURL = new URL("file:"+configFile.getAbsolutePath());
      
      // configure environment to first config file
      Configurator configurator = new PropertyConfigurator();
      configurator.doConfigure(configURL, LogManager.getLoggerRepository());
      
      // now watch the file for changes
      FileWatchdog watchdog = new FileWatchdog();
      watchdog.setURL(configURL);
      watchdog.setInterval(2000);
      watchdog.setConfigurator(PropertyConfigurator.class.getName());
      LogManager.getLoggerRepository().getPluginRegistry().addPlugin(watchdog);
      watchdog.activateOptions();

      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");

      // copy over a new version of the config file
      copyFile(sourceFile2, configFile);
      
      // wait a few seconds for the watchdog to react
      Thread.sleep(4000);
      
      // output some test messages
      logger.debug("debug message");
      logger.info("info message");
      logger.warn("warn message");
      logger.error("error message");
      logger.fatal("fatal message");
      
      /*
      assertTrue(Compare.compare(getOutputFile("test2"), 
        getWitnessFile("test2")));
      */
    }

    public static Test suite() {

        TestSuite suite = new TestSuite();
        suite.addTest(new FileWatchdogTestCase("test1"));
        suite.addTest(new FileWatchdogTestCase("test2"));

        return suite;
    }
}
