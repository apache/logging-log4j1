/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.plugins;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.util.Compare;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootCategory;

public class PluginTestCase extends TestCase {
  
  static String FILE    = "output/plugins.PluginTestCase";
  static String WITNESS = "witness/plugins.PluginTestCase";
  
  public PluginTestCase(String name) {
    super(name);
  }

  public void setUp() {
    // delete the output file if they happen to exist
    File file = new File(getOutputFile("test1"));
    file.delete();
    file = new File(getOutputFile("test2"));
    file.delete();
  }
  
  private String getOutputFile(String caseName) {
    return FILE + "." + caseName + ".txt";
  }

  private String getWitnessFile(String caseName) {
    return WITNESS + "." + caseName + ".txt";
  }
  
  private void setupAppender(String caseName) throws IOException {
    Logger root = Logger.getRootLogger();
    root.removeAllAppenders();

    // set up appender
    FileAppender appender = new FileAppender(new SimpleLayout(),
      getOutputFile(caseName), false);
    //FileAppender appender = new FileAppender(new PatternLayout("%c{1}: %m%n"),
    //  getOutputFile(caseName), false);

    root.addAppender(appender);
    root.setLevel(Level.DEBUG);
  }
  
  // basic test of plugin in standalone mode
  public void test1() throws Exception {
    String testName = "test1";
    Logger logger = Logger.getLogger(testName);
    
    setupAppender(testName);
    
    PluginTester plugin1 = new PluginTester1("plugin1", 1);
    PluginTester plugin2 = new PluginTester1("plugin1", 2);
    PluginTester plugin3 = new PluginTester2("plugin1", 3);
    PluginTester plugin4 = new PluginTester2("plugin2", 4);
    PluginTester retPlugin;

    // test basic starting/stopping
    logger.info("test 1.1 - basic starting/stopping");
    logger.info("starting " + plugin1.getIdentifier());
    PluginRegistry.startPlugin(plugin1);
    logger.info("stopping " + plugin1.getIdentifier() + " using plugin object");
    PluginRegistry.stopPlugin(plugin1); 
    
    // test restarting and starting when already started
    logger.info("test 1.2 - restarting and starting when already started");
    logger.info("restarting " + plugin1.getIdentifier());
    PluginRegistry.startPlugin(plugin1);
    logger.info("restarting " + plugin1.getIdentifier() + " again");
    PluginRegistry.startPlugin(plugin1);
   
    // test stopping and stopping when already stopped
    logger.info("test 1.3- stopping and stopping when already stopped");
    logger.info("stopping " + plugin1.getIdentifier());
    PluginRegistry.stopPlugin(plugin1); 
    logger.info("stopping " + plugin1.getIdentifier() + " again");
    PluginRegistry.stopPlugin(plugin1);
    
    logger.info("test 1.4 - restarting then stopping by plugin name");
    logger.info("starting " + plugin1.getIdentifier());
    PluginRegistry.startPlugin(plugin1);
    logger.info("stopping " + plugin1.getIdentifier() + " using plugin name");
    PluginRegistry.stopPlugin(plugin1.getName()); 

    // test starting of an "equal" plugin
    logger.info("test 1.5 - starting of an \"equal\" plugin");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin2.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + retPlugin.getIdentifier());
    PluginRegistry.stopPlugin(retPlugin);
    
    // test starting an "equal" plugin after original stopped
    logger.info("test 1.6 - starting an \"equal\" plugin after original stopped");
    logger.info("starting " + plugin2.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + retPlugin.getIdentifier());
    PluginRegistry.stopPlugin(retPlugin); 
 
     // test starting of an "unequal" plugin with same name
    logger.info("test 1.7 - starting of an \"unequal\" plugin with same name");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin3.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin3);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + retPlugin.getIdentifier());
    PluginRegistry.stopPlugin(retPlugin);

    // test starting of multiple plugins and stopAll
    logger.info("test 1.8 - starting of multiple plugins and stopAll");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin4.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin4);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping all plugins");
    PluginRegistry.stopAllPlugins();
    logger.info("stopping all plugins again");
    PluginRegistry.stopAllPlugins();

    // test starting of multiple plugins and stopAll
    logger.info("test 1.9 - starting of multiple plugins, stopping, and stopAll");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin4.getIdentifier());
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin4);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + plugin1.getIdentifier() + " using plugin object");
    PluginRegistry.stopPlugin(plugin1); 
    logger.info("stopping all plugins");
    PluginRegistry.stopAllPlugins();
    logger.info("stopping all plugins again");
    PluginRegistry.stopAllPlugins();

    assertTrue(Compare.compare(getOutputFile(testName), getWitnessFile(testName)));
  }

  // basic test of plugin with repositories
  public void test2() throws Exception {
    String testName = "test2";
    Logger logger = Logger.getLogger(testName);
    
    setupAppender(testName);

    PluginTester plugin1 = new PluginTester1("plugin1", 1);
    PluginTester plugin2 = new PluginTester1("plugin2", 2);
    PluginTester retPlugin;
    LoggerRepository repo1 = new Hierarchy(new RootCategory(Level.DEBUG));
    LoggerRepository repo2 = new Hierarchy(new RootCategory(Level.DEBUG));
    HashMap repoMap = new HashMap();
    repoMap.put(repo1, "repository1");
    repoMap.put(repo2, "repository2");

    logger.info("test 2.1 - starting plugins in multiple repositories");
    logger.info("starting " + plugin1.getIdentifier() + 
      " in " + repoMap.get(repo1));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1, repo1);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("starting " + plugin2.getIdentifier() + 
      " in " + repoMap.get(repo2));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2, repo2);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.2 - stopping plugins in multiple repositories");
    logger.info("stopping " + plugin1.getIdentifier() + 
      " in " + repoMap.get(plugin1.getLoggerRepository()));
    retPlugin = (PluginTester)PluginRegistry.stopPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("stopping " + plugin2.getIdentifier() + 
      " in " + repoMap.get(plugin2.getLoggerRepository()));
    retPlugin = (PluginTester)PluginRegistry.stopPlugin(plugin2);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.3 - restarting plugins in different repositories");
    logger.info("starting " + plugin1.getIdentifier() + 
      " in " + repoMap.get(repo2));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1, repo2);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("starting " + plugin2.getIdentifier() + 
      " in " + repoMap.get(repo1));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2, repo1);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.4 - stopping plugins using stopAll");
    logger.info("stopping all plugins in " + repoMap.get(repo1));
    PluginRegistry.stopAllPlugins(repo1);
    logger.info("stopping all plugins in " + repoMap.get(repo2));
    PluginRegistry.stopAllPlugins(repo2);

    logger.info("test 2.5 - starting a plugin already active in another repository");
    logger.info("starting " + plugin1.getIdentifier() + 
      " in " + repoMap.get(repo1));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1, repo1);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("starting " + plugin2.getIdentifier() + 
      " in " + repoMap.get(repo2));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2, repo2);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("restarting " + plugin1.getIdentifier() + 
      " in " + repoMap.get(repo2));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1, repo2);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("restarting " + plugin2.getIdentifier() + 
      " in " + repoMap.get(repo1));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2, repo1);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.6 - handle repository reset");
    logger.info("resetting " + repoMap.get(repo1));
    repo1.resetConfiguration();
    logger.info("resetting " + repoMap.get(repo2));
    repo2.resetConfiguration();
    
    logger.info("test 2.7 - handle repository shutdown");
    logger.info("starting " + plugin1.getIdentifier() + 
      " in " + repoMap.get(repo1));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin1, repo1);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("starting " + plugin2.getIdentifier() + 
      " in " + repoMap.get(repo2));
    retPlugin = (PluginTester)PluginRegistry.startPlugin(plugin2, repo2);
    logger.info("returned plugin is " + retPlugin.getIdentifier() +
      " in " + repoMap.get(retPlugin.getLoggerRepository()));
    logger.info("shutting down " + repoMap.get(repo1));
    repo1.shutdown(); 
    logger.info("shutting down " + repoMap.get(repo2));
    repo2.shutdown(); 
   
    //assertTrue(Compare.compare(getOutputFile(testName), getWitnessFile(testName)));
  }

    /*
    System.out.println("creating the sink repository");
    LoggerRepository repo1 = new Hierarchy(new RootCategory(Level.DEBUG));
    System.out.println("configuring the sink repository");
    configurator.doConfigure("input/sink.xml",repo1);
    
    System.out.println("creating the source repository");
    LoggerRepository repo2 = new Hierarchy(new RootCategory(Level.DEBUG));
    System.out.println("configuring the source repository");
    configurator.doConfigure("input/source.xml",repo2);
    
    System.out.println("sending messages via source loggers");
    Logger logger = repo2.getLogger("repo2.logger");
    logger.debug("Message 1");
    logger.debug("Message 2");
    logger = repo2.getLogger("repo2.logger2");
    logger.debug("Message 3");
    logger.debug("Message 4");
    
    System.out.println("sending messages via sink loggers");
    logger = repo1.getLogger("repo1.logger");
    logger.debug("Message 5");
    logger.debug("Message 6");
    logger = repo1.getLogger("repo1.logger2");
    logger.debug("Message 7");
    logger.debug("Message 8");
    
    Thread.currentThread().sleep(5000);
    
    repo2.shutdown();
    repo1.shutdown();
    */

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new PluginTestCase("test1"));
    suite.addTest(new PluginTestCase("test2"));
    return suite;
  }
  
  /**
    Class to test the Plugin and PluginRegistry functionality. */
  private static class PluginTester extends PluginSkeleton {
    protected Logger logger;
    
    private boolean active = false;
    public int id;
    
    // test to see if the given obj "equals" this object
    // considered equal if same class, same name and same
    // repository
    public boolean equals(Object obj) {
      if (!(obj.getClass() == this.getClass())) {
        logger.debug("plugin not equal, different class: " + 
          this.getClass().getName() + " != " + obj.getClass().getName());
        return false;
      }
        
      Plugin plugin = (PluginTester)obj;
      
      if (!this.getName().equals(plugin.getName())) {
        logger.debug("plugin not equal, different name: " + 
          this.getName() + " != " + plugin.getName());
        return false;
      }
        
      if (!this.getLoggerRepository().equals(plugin.getLoggerRepository())) {
        logger.debug("plugin not equal, different repository: " + 
          this.getLoggerRepository() + " != " + plugin.getLoggerRepository());
        return false;
      }
          
      logger.debug("plugin equal");
      return true;
    }
        
    public synchronized boolean isActive() {
      logger.debug("plugin " + this.getIdentifier() + " is " + (active ? "active" : "inactive"));
      return active;
    }
    
    private synchronized boolean setActive(boolean _active) {
      if (active != _active) {
        active = _active;
        return true;
      }
      else {
        return false;
      }
    }
    
    public String getIdentifier() {
      return this.getName() + "-id" + id;
    }
    
    public void activateOptions() {
      if (setActive(true)) {
        logger.debug("plugin " + this.getIdentifier() + " activated");
      }
      else {
        logger.debug("plugin " + this.getIdentifier() + " already activated");
      }
    }
    
    public void shutdown() {
      if (setActive(false)) {
        logger.debug("plugin " + this.getIdentifier() + " shutdown");
      }
      else {
        logger.debug("plugin " + this.getIdentifier() + " already shutdown");
      }
    }
  }

  /**
    Class to test the Plugin and PluginRegistry functionality. */
  private static class PluginTester1 extends PluginTester {
    
    public PluginTester1(String _name, int _id) {
      logger = Logger.getLogger(this.getClass());
      setName(_name);
      id = _id;
    }

  }
  
  /**
    Class to test the Plugin and PluginRegistry functionality. */
  private static class PluginTester2 extends PluginTester {
    
    public PluginTester2(String _name, int _id) {
      logger = Logger.getLogger(this.getClass());
      setName(_name);
      id = _id;
    }

  }
}