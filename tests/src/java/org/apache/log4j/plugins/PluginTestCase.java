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

package org.apache.log4j.plugins;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.util.Compare;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;


public class PluginTestCase extends TestCase {
  static String FILE = "output/plugins.PluginTestCase";
  static String WITNESS = "witness/plugins.PluginTestCase";
  private static boolean verbosePluginOutput = true;
  private static HashMap repositoryMap = new HashMap();

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
    FileAppender appender =
      new FileAppender(new SimpleLayout(), getOutputFile(caseName), false);

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

    repositoryMap.clear();
    repositoryMap.put(LogManager.getLoggerRepository(), "default repository");

    // test basic starting/stopping
    logger.info("test 1.1 - basic starting/stopping");
    logger.info("starting " + plugin1.getIdentifier());
    PluginRegistry.startPlugin(plugin1);
    logger.info(
      "stopping " + plugin1.getIdentifier() + " using plugin object");
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
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin2.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + retPlugin.getIdentifier());
    PluginRegistry.stopPlugin(retPlugin);

    // test starting an "equal" plugin after original stopped
    logger.info(
      "test 1.6 - starting an \"equal\" plugin after original stopped");
    logger.info("starting " + plugin2.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + retPlugin.getIdentifier());
    PluginRegistry.stopPlugin(retPlugin);

    // test starting of an "unequal" plugin with same name
    logger.info("test 1.7 - starting of an \"unequal\" plugin with same name");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin3.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin3);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("stopping " + retPlugin.getIdentifier());
    PluginRegistry.stopPlugin(retPlugin);

    // test starting of multiple plugins and stopAll
    logger.info("test 1.8 - starting of multiple plugins and stopAll");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin4.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin4);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    verbosePluginOutput = false;
    logger.info("stopping all plugins");
    PluginRegistry.stopAllPlugins();
    verbosePluginOutput = true;
    logger.info(
      plugin1.getIdentifier() + " is "
      + (plugin1.isActive() ? "active" : "inactive"));
    logger.info(
      plugin4.getIdentifier() + " is "
      + (plugin4.isActive() ? "active" : "inactive"));
    logger.info("stopping all plugins again");
    PluginRegistry.stopAllPlugins();

    // test starting of multiple plugins and stopAll
    logger.info(
      "test 1.9 - starting of multiple plugins, stopping, and stopAll");
    logger.info("starting " + plugin1.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info("starting " + plugin4.getIdentifier());
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin4);
    logger.info("returned plugin is " + retPlugin.getIdentifier());
    logger.info(
      "stopping " + plugin1.getIdentifier() + " using plugin object");
    PluginRegistry.stopPlugin(plugin1);
    verbosePluginOutput = false;
    logger.info("stopping all plugins");
    PluginRegistry.stopAllPlugins();
    verbosePluginOutput = true;
    logger.info(
      plugin1.getIdentifier() + " is "
      + (plugin1.isActive() ? "active" : "inactive"));
    logger.info(
      plugin4.getIdentifier() + " is "
      + (plugin4.isActive() ? "active" : "inactive"));
    logger.info("stopping all plugins again");
    PluginRegistry.stopAllPlugins();

    assertTrue(
      Compare.compare(getOutputFile(testName), getWitnessFile(testName)));
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
    repositoryMap.clear();
    repositoryMap.put(repo1, "repository1");
    repositoryMap.put(repo2, "repository2");

    logger.info("test 2.1 - starting plugins in multiple repositories");
    logger.info(
      "starting " + plugin1.getIdentifier() + " in "
      + repositoryMap.get(repo1));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1, repo1);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "starting " + plugin2.getIdentifier() + " in "
      + repositoryMap.get(repo2));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2, repo2);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.2 - stopping plugins in multiple repositories");
    logger.info(
      "stopping " + plugin1.getIdentifier() + " in "
      + repositoryMap.get(plugin1.getLoggerRepository()));
    retPlugin = (PluginTester) PluginRegistry.stopPlugin(plugin1);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "stopping " + plugin2.getIdentifier() + " in "
      + repositoryMap.get(plugin2.getLoggerRepository()));
    retPlugin = (PluginTester) PluginRegistry.stopPlugin(plugin2);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.3 - restarting plugins in different repositories");
    logger.info(
      "starting " + plugin1.getIdentifier() + " in "
      + repositoryMap.get(repo2));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1, repo2);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "starting " + plugin2.getIdentifier() + " in "
      + repositoryMap.get(repo1));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2, repo1);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.4 - stopping plugins using stopAll");
    logger.info("stopping all plugins in " + repositoryMap.get(repo1));
    PluginRegistry.stopAllPlugins(repo1);
    logger.info("stopping all plugins in " + repositoryMap.get(repo2));
    PluginRegistry.stopAllPlugins(repo2);

    logger.info(
      "test 2.5 - starting a plugin already active in another repository");
    logger.info(
      "starting " + plugin1.getIdentifier() + " in "
      + repositoryMap.get(repo1));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1, repo1);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "starting " + plugin2.getIdentifier() + " in "
      + repositoryMap.get(repo2));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2, repo2);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "restarting " + plugin1.getIdentifier() + " in "
      + repositoryMap.get(repo2));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1, repo2);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "restarting " + plugin2.getIdentifier() + " in "
      + repositoryMap.get(repo1));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2, repo1);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));

    logger.info("test 2.6 - handle repository reset");
    logger.info("resetting " + repositoryMap.get(repo1));
    repo1.resetConfiguration();
    logger.info("resetting " + repositoryMap.get(repo2));
    repo2.resetConfiguration();

    logger.info("test 2.7 - handle repository shutdown");
    logger.info(
      "starting " + plugin1.getIdentifier() + " in "
      + repositoryMap.get(repo1));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin1, repo1);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info(
      "starting " + plugin2.getIdentifier() + " in "
      + repositoryMap.get(repo2));
    retPlugin = (PluginTester) PluginRegistry.startPlugin(plugin2, repo2);
    logger.info(
      "returned plugin is " + retPlugin.getIdentifier() + " in "
      + repositoryMap.get(retPlugin.getLoggerRepository()));
    logger.info("shutting down " + repositoryMap.get(repo1));
    repo1.shutdown();
    logger.info("shutting down " + repositoryMap.get(repo2));
    repo2.shutdown();

    assertTrue(
      Compare.compare(getOutputFile(testName), getWitnessFile(testName)));
  }

	public void testPluginListeners(){
		Plugin p = new PluginTester1("MyNewPlugin",1);
		PluginListenerLatch l = new PluginListenerLatch();
		PluginRegistry.stopAllPlugins();
		PluginRegistry.addPluginListener(l);
		PluginRegistry.startPlugin(p);
		
		PluginEvent e = l.LastEvent;
		
		assertTrue("PluginListener should have been notified of start", l.StartLatch);
		assertTrue("PluginListener stop latch should not be activated", !l.StopLatch);
		assertTrue("PluginListener should be given reference to Plugin", e.getPlugin() == p);

		PluginRegistry.stopAllPlugins();
		assertTrue("PluginListener should have been notified of stop", l.StopLatch);
		assertTrue("PluginListener should not have been notified of start", !l.StartLatch);
		assertTrue("PluginListener should be given reference to Plugin", l.LastEvent.getPlugin() == p);
		assertTrue("PluginListener should have received a distinct event object", l.LastEvent != e);
	}
	
  public void testPropertyChangeListeners() {
    Plugin plugin = new PluginTester1("PluginTest1", 1);

    final PropertyChangeListenerLatch l = new PropertyChangeListenerLatch();
    plugin.addPropertyChangeListener(l);

    /**
     * Test the basic properties and ensure they get latched by notification
     */
    plugin.setName("NewName");
    assertTrue(
      "PropertyChange latch should have been detected", l.isLatched());
    assertTrue(
      "Old value unexpected: '" + l.getLastEvent().getOldValue() + "'",
      l.getLastEvent().getOldValue().equals("PluginTest1"));
    assertTrue(
      "New value unexpected: '" + l.getLastEvent().getNewValue() + "'",
      l.getLastEvent().getNewValue().equals("NewName"));

    l.reset();

    plugin.removePropertyChangeListener(l);
    plugin.setName("SecondNewName");

    assertTrue("Should not have been notified/latched", !l.isLatched());

    l.reset();

    /**
     * Test when only listening for specific property
     */
    plugin.addPropertyChangeListener("name", l);

    plugin.setName("NewName2");
    assertTrue(
      "PropertyChange latch should have been detected", l.isLatched());
    assertTrue(
      "Old value unexpected: '" + l.getLastEvent().getOldValue() + "'",
      l.getLastEvent().getOldValue().equals("SecondNewName"));
    assertTrue(
      "New value unexpected: '" + l.getLastEvent().getNewValue() + "'",
      l.getLastEvent().getNewValue().equals("NewName2"));

    plugin.removePropertyChangeListener("name", l);

    l.reset();

    /**
     * setup some assertions before continuing testing to make sure the test code isn't broken
     */
    assertTrue("Plugin should not be active just yet", !plugin.isActive());
    assertTrue("Latch should not be latched", !l.isLatched());

    plugin.addPropertyChangeListener("active", l);

    PluginRegistry.startPlugin(plugin);
    assertTrue(
      "Should have been notified of activation when PluginRegistry.start(plugin)",
      l.isLatched());
    assertTrue(
      "Active old value should have been false",
      l.getLastEvent().getOldValue().equals(Boolean.FALSE));
    assertTrue(
      "Active New value should have been true",
      l.getLastEvent().getNewValue().equals(Boolean.TRUE));
      
      PluginRegistry.stopAllPlugins();
      l.reset();
      assertTrue("Latch should have been reset", !l.isLatched());
      /**
       * start afresh
       */
     plugin = new PluginTester1("LoggerRepositoryProperty", 2);
     LoggerRepository oldValue = plugin.getLoggerRepository();
     plugin.addPropertyChangeListener("loggerRepository", l);
     LoggerRepository rep = new Hierarchy(new RootCategory(Level.DEBUG));
     plugin.setLoggerRepository(rep);
     
     assertTrue("Should be notified of LoggerRepository property change", l.isLatched());
     assertTrue("LoggerRepository Old value mismatch", l.getLastEvent().getOldValue() == oldValue);
     assertTrue("LoggerRepository New vale mismatch", l.getLastEvent().getNewValue() == rep);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new PluginTestCase("test1"));
    suite.addTest(new PluginTestCase("test2"));
    suite.addTest(new PluginTestCase("testPropertyChangeListeners"));
    suite.addTest(new PluginTestCase("testPluginListeners"));

    return suite;
  }

	private static class PluginListenerLatch implements PluginListener{
		private boolean StartLatch;
		private boolean StopLatch;
		private PluginEvent LastEvent;
		/* (non-Javadoc)
		 * @see org.apache.log4j.plugins.PluginListener#pluginStarted(org.apache.log4j.plugins.PluginEvent)
		 */
		public void pluginStarted(PluginEvent e) {
			StartLatch = true;
			LastEvent = e;
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.plugins.PluginListener#pluginStopped(org.apache.log4j.plugins.PluginEvent)
		 */
		public void pluginStopped(PluginEvent e) {
			StopLatch = true;
			LastEvent = e;
		}
		void reset(){
			StartLatch = false;
			StopLatch = false;
			LastEvent = null;
		}
	}
	
  private static class PropertyChangeListenerLatch
    implements PropertyChangeListener {
    boolean latch = false;
    PropertyChangeEvent lastEvent = null;

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
      latch = true;
      lastEvent = evt;
    }

    boolean isLatched() {
      return latch;
    }

    void reset() {
      latch = false;
      lastEvent = null;
    }

    PropertyChangeEvent getLastEvent() {
      return lastEvent;
    }
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
        logger.debug(
          "plugin not equal, different class: " + this.getClass().getName()
          + " != " + obj.getClass().getName());

        return false;
      }

      Plugin plugin = (PluginTester) obj;

      if (!this.getName().equals(plugin.getName())) {
        logger.debug(
          "plugin not equal, different name: " + this.getName() + " != "
          + plugin.getName());

        return false;
      }

      if (!this.getLoggerRepository().equals(plugin.getLoggerRepository())) {
        logger.debug(
          "plugin not equal, different repository: "
          + repositoryMap.get(this.getLoggerRepository()) + " != "
          + repositoryMap.get(plugin.getLoggerRepository()));

        return false;
      }

      logger.debug("plugin equal");

      return true;
    }

    public synchronized boolean isActive() {
      logger.debug(
        this.getIdentifier() + " is " + (active ? "active" : "inactive"));

      return active;
    }

    private synchronized boolean setActive(boolean _active) {
      boolean oldValue = active;

      if (active != _active) {
        active = _active;
        firePropertyChange("active", oldValue, active);

        return true;
      } else {
        return false;
      }
    }

    public String getIdentifier() {
      if (verbosePluginOutput) {
        return this.getName() + "-id" + id;
      } else {
        return "plugin in " + repositoryMap.get(this.getLoggerRepository());
      }
    }

    public void activateOptions() {
      if (setActive(true)) {
        logger.debug(this.getIdentifier() + " activated");
      } else {
        logger.debug(this.getIdentifier() + " already activated");
      }
    }

    public void shutdown() {
      if (setActive(false)) {
        logger.debug(this.getIdentifier() + " shutdown");
      } else {
        logger.debug(this.getIdentifier() + " already shutdown");
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
