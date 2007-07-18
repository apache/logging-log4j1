/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// WARNING This class MUST not have references to the Category or
// WARNING RootLogger classes in its static initiliazation neither
// WARNING directly nor indirectly.
package org.apache.log4j;

import org.apache.log4j.helpers.IntializationUtil;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.scheduler.Scheduler;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.spi.LoggerRepositoryEventListener;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.HierarchyEventListenerAdapter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


// Contributors: Luke Blanshard <luke@quiq.com>
//               Mario Schomburg - IBM Global Services/Germany
//               Anders Kristensen
//               Igor Poteryaev


/**
   This class is specialized in retrieving loggers by name and also
   maintaining the logger hierarchy.

   <p><em>The casual user does not have to deal with this class
   directly.</em>

   <p>The structure of the logger hierarchy is maintained by the
   {@link #getLogger} method. The hierarchy is such that children link
   to their parent but parents do not have any pointers to their
   children. Moreover, loggers can be instantiated in any order, in
   particular descendant before ancestor.

   <p>In case a descendant is created before a particular ancestor,
   then it creates a provision node for the ancestor and adds itself
   to the provision node. Other descendants of the same ancestor add
   themselves to the previously created provision node.

   @author Ceki G&uuml;lc&uuml;
   @author Mark Womack

*/
public class Hierarchy implements LoggerRepositoryEx, RendererSupport {

    /**
     * Logger factory.
     */
  private LoggerFactory loggerFactory;
    /**
     * List of repository event listeners.
     */
  private final ArrayList repositoryEventListeners;
    /**
     * List of logger event listeners.
     */
  private final ArrayList loggerEventListeners;
    /**
     * Name of hierarchy.
     */
  String name;
    /**
     * Loggers by name.
     */
  Hashtable ht;
    /**
     * Root logger.
     */
  Logger root;
    /**
     * Map of ObjectRenderer by Class.
     */
  RendererMap rendererMap;
    /**
     * Numeric value for threshold.
     */
  int thresholdInt;
    /**
     * Threshold.
     */
  Level threshold;
    /**
     * Plug in registry.
     */
  PluginRegistry pluginRegistry;
    /**
     * Properties.
     */
  Map properties;
    /**
     * Scheduler.
     */
  private Scheduler scheduler;

  /** The repository can also be used as an object store
   * for various objects used by log4j components.
   */
  private Map objectMap;

    /**
     * The internal logger used by this instance of
     * Hierarchy for its own reporting.
     */
  private Logger myLogger;

    /**
     * Error list.
     */
  private List errorList = new Vector();

    /**
     * True if no appender warning has already been issued.
     */
  boolean emittedNoAppenderWarning = false;
    /**
     * True if hierarchy has not been modified.
     */
  boolean pristine = true;

  /**
     Constructs a new logger hierarchy.

     @param rootLogger The root of the new hierarchy.

   */
  public Hierarchy(final Logger rootLogger) {
    ht = new Hashtable();
    repositoryEventListeners = new ArrayList(1);
    loggerEventListeners = new ArrayList(1);
    this.root = rootLogger;
    this.objectMap = new HashMap();
    // Enable all level levels by default.
    setThreshold(Level.ALL);
    this.root.setHierarchy(this);
    rendererMap = new RendererMap();
    rendererMap.setLoggerRepository(this);
    properties = new Hashtable();
    loggerFactory = new DefaultLoggerFactory();
  }

  /**
   * Constructs a new logger hierarchy with a default {@link RootLogger}.
   */
  public Hierarchy() {
    this(new RootLogger());
  }

  /**
     Add an object renderer for a specific class.
     @param classToRender class to render
     @param or renderer
   */
  public void addRenderer(final Class classToRender,
                          final ObjectRenderer or) {
    rendererMap.put(classToRender, or);
  }

  /**
    Add a {@link LoggerRepositoryEventListener} to the repository. The
    listener will be called when repository events occur.
    @param listener listener
    @since 1.3*/
  public void addLoggerRepositoryEventListener(
    final LoggerRepositoryEventListener listener) {
    synchronized (repositoryEventListeners) {
      if (repositoryEventListeners.contains(listener)) {
        getMyLogger().warn(
          "Ignoring attempt to add a previously "
                  + "registered LoggerRepositoryEventListener.");
      } else {
        repositoryEventListeners.add(listener);
      }
    }
  }

    /**
     * Gets logger to be used for internal diagnostic messages.
     * @return logger
     */
  private Logger getMyLogger() {
    if (myLogger == null) {
      myLogger = getLogger(this.getClass().getName());
    }
    return myLogger;
  }

  /**
    Remove a {@link LoggerRepositoryEventListener} from the repository.
    @param listener listener
    @since 1.3*/
  public void removeLoggerRepositoryEventListener(
    final LoggerRepositoryEventListener listener) {
    synchronized (repositoryEventListeners) {
      if (!repositoryEventListeners.contains(listener)) {
        getMyLogger().warn(
          "Ignoring attempt to remove a "
                  + "non-registered LoggerRepositoryEventListener.");
      } else {
        repositoryEventListeners.remove(listener);
      }
    }
  }

  /**
    Add a {@link LoggerEventListener} to the repository. The  listener
    will be called when repository events occur.
    @param listener listener
    @since 1.3
   */
  public void addLoggerEventListener(final LoggerEventListener listener) {
    synchronized (loggerEventListeners) {
      if (loggerEventListeners.contains(listener)) {
        getMyLogger().warn(
         "Ignoring attempt to add a previously registerd LoggerEventListener.");
      } else {
        loggerEventListeners.add(listener);
      }
    }
  }

    /**
       Add a {@link org.apache.log4j.spi.HierarchyEventListener}
     event to the repository.
     @param listener listener
       @deprecated Superceded by addLoggerEventListener
    */
    public
    void addHierarchyEventListener(final HierarchyEventListener listener) {
        addLoggerEventListener(new HierarchyEventListenerAdapter(listener));
    }


  /**
    Remove a {@link LoggerEventListener} from the repository.
    @param listener listener to be removed
    @since 1.3*/
  public void removeLoggerEventListener(final LoggerEventListener listener) {
    synchronized (loggerEventListeners) {
      if (!loggerEventListeners.contains(listener)) {
        getMyLogger().warn(
          "Ignoring attempt to remove a non-registered LoggerEventListener.");
      } else {
        loggerEventListeners.remove(listener);
      }
    }
  }

  /**
     This call will clear all logger definitions from the internal
     hashtable. Invoking this method will irrevocably mess up the
     logger hierarchy.

     <p>You should <em>really</em> know what you are doing before
     invoking this method.

     @since 0.9.0 */
  public void clear() {
    //System.out.println("\n\nAbout to clear internal hash table.");
    ht.clear();
  }

    /**
     * Issue warning that there are no appenders in hierarchy.
     * @param cat logger, not currently used.
     */
  public void emitNoAppenderWarning(final Category cat) {
    // No appenders in hierarchy, warn user only once.
    if (!this.emittedNoAppenderWarning) {
      //LogLog.warn(
      //  "No appenders could be found for logger (" + cat.getName() + ").");
      //LogLog.warn("Please initialize the log4j system properly.");
      this.emittedNoAppenderWarning = true;
    }
  }

  /**
     Check if the named logger exists in the hierarchy. If so return
     its reference, otherwise returns <code>null</code>.

     @param loggerName The name of the logger to search for.
     @return true if logger exists.
  */
  public Logger exists(final String loggerName) {
    Object o = ht.get(new CategoryKey(loggerName));

    if (o instanceof Logger) {
      return (Logger) o;
    } else {
      return null;
    }
  }

  /**
   * Return the name of this hierarchy.
   * @return name of hierarchy
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of this repository.
   *
   * Note that once named, a repository cannot be rerenamed.
   * @since 1.3
   * @param repoName name of hierarchy
   */
  public void setName(final String repoName) {
    if (name == null) {
      name = repoName;
    } else if (!name.equals(repoName)) {
      throw new IllegalStateException(
        "Repository [" + name + "] cannot be renamed as [" + repoName + "].");
    }
  }

  /**
   * {@inheritDoc}
   */
  public Map getProperties() {
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  public String getProperty(final String key) {
     return (String) properties.get(key);
  }

  /**
   * Set a property by key and value. The property will be shared by all
   * events in this repository.
   * @param key property name
   * @param value property value
   */
  public void setProperty(final String key,
                          final String value) {
   properties.put(key, value);
  }

  /**
     The string form of {@link #setThreshold(Level)}.
   @param levelStr symbolic name for level
  */
  public void setThreshold(final String levelStr) {
    Level l = Level.toLevel(levelStr, null);

    if (l != null) {
      setThreshold(l);
    } else {
      getMyLogger().warn("Could not convert [" + levelStr + "] to Level.");
    }
  }

  /**
     Enable logging for logging requests with level <code>l</code> or
     higher. By default all levels are enabled.

     @param l The minimum level for which logging requests are sent to
     their appenders.  */
  public void setThreshold(final Level l) {
    if (l != null) {
      thresholdInt = l.level;
      threshold = l;
    }
  }

  /**
   * {@inheritDoc}
   * @since 1.3
   */
  public PluginRegistry getPluginRegistry() {
   if (pluginRegistry == null) {
     pluginRegistry = new PluginRegistry(this);
   }
   return pluginRegistry;
  }


    /**
      Requests that a appender added event be sent to any registered
      {@link LoggerEventListener}.
      @param logger The logger to which the appender was added.
      @param appender The appender added to the logger.
     */
    public void fireAddAppenderEvent(final Category logger,
                                     final Appender appender) {
        if (logger instanceof Logger) {
            fireAddAppenderEvent((Logger) logger, appender);
        }
    }

    /**
      Requests that a appender added event be sent to any registered
      {@link LoggerEventListener}.
      @param logger The logger to which the appender was added.
      @param appender The appender added to the logger.
      @since 1.3
     */
    public void fireAddAppenderEvent(final Logger logger,
                                     final Appender appender) {
        ArrayList list = copyListenerList(loggerEventListeners);
        int size = list.size();

        for (int i = 0; i < size; i++) {
          ((LoggerEventListener) list.get(i)).
                  appenderAddedEvent(logger, appender);
        }
    }


    /**
      Requests that a appender removed event be sent to any registered
      {@link LoggerEventListener}.
      @param logger The logger from which the appender was removed.
      @param appender The appender removed from the logger.
      */
    public void fireRemoveAppenderEvent(final Category logger,
                                        final Appender appender) {
       if (logger instanceof Logger) {
           fireRemoveAppenderEvent((Logger) logger, appender);
       }
    }

  /**
    Requests that a appender removed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger from which the appender was removed.
    @param appender The appender removed from the logger.
    @since 1.3*/
  public void fireRemoveAppenderEvent(final Logger logger,
                                      final Appender appender) {
    ArrayList list = copyListenerList(loggerEventListeners);
    int size = list.size();

    for (int i = 0; i < size; i++) {
      ((LoggerEventListener) list.get(i)).appenderRemovedEvent(
        logger, appender);
    }
  }

  /**
    Requests that a level changed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger which changed levels.
    @since 1.3*/
  public void fireLevelChangedEvent(final Logger logger) {
    ArrayList list = copyListenerList(loggerEventListeners);
    int size = list.size();

    for (int i = 0; i < size; i++) {
      ((LoggerEventListener) list.get(i)).levelChangedEvent(logger);
    }
  }

  /**
    Requests that a configuration changed event be sent to any registered
    {@link LoggerRepositoryEventListener}.
    @since 1.3*/
  public void fireConfigurationChangedEvent() {
    ArrayList list = copyListenerList(repositoryEventListeners);
    int size = list.size();

    for (int i = 0; i < size; i++) {
      ((LoggerRepositoryEventListener) list.get(i)).configurationChangedEvent(
        this);
    }
  }

  /**
    Returns a copy of the given listener vector.
    @param list original list
    @return copy of list
   */
  private ArrayList copyListenerList(final ArrayList list) {
    ArrayList listCopy = null;

    synchronized (list) {
      int size = list.size();
      listCopy = new ArrayList(size);

      for (int x = 0; x < size; x++) {
        listCopy.add(list.get(x));
      }
    }

    return listCopy;
  }

  /**
     Returns a {@link Level} representation of the <code>enable</code>
     state.
     @return current threshold level

     @since 1.2 */
  public Level getThreshold() {
    return threshold;
  }

  /**
     Returns an integer representation of the this repository's
     threshold.

     @since 1.2 */

  //public
  //int getThresholdInt() {
  //  return thresholdInt;
  //}


  /**
     Return a new logger instance named as the first parameter using
     the default factory.

     <p>If a logger of that name already exists, then it will be
     returned.  Otherwise, a new logger will be instantiated and
     then linked with its existing ancestors as well as children.

     @param loggerName The name of the logger to retrieve.
     @return logger

  */
  public Logger getLogger(final String loggerName) {
    return getLogger(loggerName, loggerFactory);
  }

  /**
      Return a new logger instance named as the first parameter using
      <code>factory</code>.

      <p>If a logger of that name already exists, then it will be
      returned.  Otherwise, a new logger will be instantiated by the
      <code>factory</code> parameter and linked with its existing
      ancestors as well as children.

      @param loggerName The name of the logger to retrieve.
      @param factory The factory that will make the new logger instance.
      @return logger

  */
  public Logger getLogger(final String loggerName,
                          final LoggerFactory factory) {
    //System.out.println("getInstance("+name+") called.");
    CategoryKey key = new CategoryKey(loggerName);


    // Synchronize to prevent write conflicts. Read conflicts (in
    // getChainedLevel method) are possible only if variable
    // assignments are non-atomic.
    Logger logger;

    synchronized (ht) {
      Object o = ht.get(key);

      if (o == null) {
        LogLog.debug(
            "Creating new logger [" + loggerName
                    + "] in repository [" + getName() + "].");
        logger = factory.makeNewLoggerInstance(loggerName);
        logger.setHierarchy(this);
        ht.put(key, logger);
        updateParents(logger);

        return logger;
      } else if (o instanceof Logger) {
          LogLog.debug(
            "Returning existing logger [" + loggerName
                    + "] in repository [" + getName() + "].");
        return (Logger) o;
      } else if (o instanceof ProvisionNode) {
        //System.out.println("("+name+") ht.get(this) returned ProvisionNode");
        logger = factory.makeNewLoggerInstance(loggerName);
        logger.setHierarchy(this);
        ht.put(key, logger);
        updateChildren((ProvisionNode) o, logger);
        updateParents(logger);

        return logger;
      } else {
        // It should be impossible to arrive here
        return null; // but let's keep the compiler happy.
      }
    }
  }

  /**
     Returns all the currently defined categories in this hierarchy as
     an {@link java.util.Enumeration Enumeration}.

     <p>The root logger is <em>not</em> included in the returned
     {@link Enumeration}.
     @return enumerator of current loggers
   */
  public Enumeration getCurrentLoggers() {
    // The accumlation in v is necessary because not all elements in
    // ht are Logger objects as there might be some ProvisionNodes
    // as well.
    Vector v = new Vector(ht.size());

    Enumeration elems = ht.elements();

    while (elems.hasMoreElements()) {
      Object o = elems.nextElement();

      if (o instanceof Logger) {
        v.addElement(o);
      }
    }

    return v.elements();
  }

  /**
   * Return the the list of previously encoutered {@link ErrorItem error items}.
   * @return list of errors
   */
  public List getErrorList() {
    return errorList;
  }

  /**
   * Add an error item to the list of previously encountered errors.
   * @param errorItem error to add to list of errors.
   * @since 1.3
   */
  public void addErrorItem(final ErrorItem errorItem) {
    getErrorList().add(errorItem);
  }

  /**
   * Get enumerator over current loggers.
   * @return enumerator over current loggers
     @deprecated Please use {@link #getCurrentLoggers} instead.
   */
  public Enumeration getCurrentCategories() {
    return getCurrentLoggers();
  }

  /**
     Get the renderer map for this hierarchy.
   @return renderer map
  */
  public RendererMap getRendererMap() {
    return rendererMap;
  }

  /**
     Get the root of this hierarchy.

     @since 0.9.0
   @return root of hierarchy
   */
  public Logger getRootLogger() {
    return root;
  }

  /**
     This method will return <code>true</code> if this repository is
     disabled for <code>level</code> value passed as parameter and
     <code>false</code> otherwise. See also the {@link
     #setThreshold(Level) threshold} method.
   @param level numeric value for level.
   @return true if disabled for specified level
   */
  public boolean isDisabled(final int level) {
    return thresholdInt > level;
  }

  /**
     Reset all values contained in this hierarchy instance to their
     default.  This removes all appenders from all categories, sets
     the level of all non-root categories to <code>null</code>,
     sets their additivity flag to <code>true</code> and sets the level
     of the root logger to {@link Level#DEBUG DEBUG}.  Moreover,
     message disabling is set its default "off" value.

     <p>Existing categories are not removed. They are just reset.

     <p>This method should be used sparingly and with care as it will
     block all logging until it is completed.</p>

     @since 0.8.5 */
  public void resetConfiguration() {
    getRootLogger().setLevel(Level.DEBUG);
    root.setResourceBundle(null);
    setThreshold(Level.ALL);


    // the synchronization is needed to prevent JDK 1.2.x hashtable
    // surprises
    synchronized (ht) {
      shutdown(true); // nested locks are OK

      Enumeration cats = getCurrentLoggers();

      while (cats.hasMoreElements()) {
        Logger c = (Logger) cats.nextElement();
        c.setLevel(null);
        c.setAdditivity(true);
        c.setResourceBundle(null);
      }
    }

    rendererMap.clear();

    // inform the listeners that the configuration has been reset
    ArrayList list = copyListenerList(repositoryEventListeners);
    int size = list.size();

    for (int i = 0; i < size; i++) {
      ((LoggerRepositoryEventListener) list.get(i)).configurationResetEvent(
        this);
    }
  }

  /**
     Used by subclasses to add a renderer to the hierarchy passed as parameter.
   @param renderedClass class
   @param renderer object used to render class.
   */
  public void setRenderer(final Class renderedClass,
                          final ObjectRenderer renderer) {
    rendererMap.put(renderedClass, renderer);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isPristine() {
    return pristine;
  }

  /**
   * {@inheritDoc}
   */
  public void setPristine(final boolean state) {
    pristine = state;
  }

  /**
     Shutting down a hierarchy will <em>safely</em> close and remove
     all appenders in all categories including the root logger.

     <p>Some appenders such as {@link org.apache.log4j.net.SocketAppender}
     and {@link AsyncAppender} need to be closed before the
     application exists. Otherwise, pending logging events might be
     lost.

     <p>The <code>shutdown</code> method is careful to close nested
     appenders before closing regular appenders. This is allows
     configurations where a regular appender is attached to a logger
     and again to a nested appender.

     @since 1.0 */
  public void shutdown() {
    shutdown(false);
  }

    /**
     * Shutdown hierarchy.
     * @param doingReset true is resetting hierarchy
     */
  private void shutdown(final boolean doingReset) {

    // stop this repo's scheduler if it has one
     if (scheduler != null) {
       scheduler.shutdown();
       scheduler = null;
     }

    // let listeners know about shutdown if this is
    // not being done as part of a reset.
    if (!doingReset) {
      ArrayList list = copyListenerList(repositoryEventListeners);
      int size = list.size();

      for (int i = 0; i < size; i++) {
        ((LoggerRepositoryEventListener) list.get(i)).shutdownEvent(this);
      }
    }

    Logger rootLogger = getRootLogger();

    // begin by closing nested appenders
    rootLogger.closeNestedAppenders();

    synchronized (ht) {
      Enumeration cats = this.getCurrentLoggers();

      while (cats.hasMoreElements()) {
        Logger c = (Logger) cats.nextElement();
        c.closeNestedAppenders();
      }

      // then, remove all appenders
      rootLogger.removeAllAppenders();
      cats = this.getCurrentLoggers();

      while (cats.hasMoreElements()) {
        Logger c = (Logger) cats.nextElement();
        c.removeAllAppenders();
      }
    }

    // log4j self configure
    IntializationUtil.log4jInternalConfiguration(this);
  }

  /**
     This method loops through all the *potential* parents of
     'cat'. There 3 possible cases:

     1) No entry for the potential parent of 'cat' exists

        We create a ProvisionNode for this potential parent and insert
        'cat' in that provision node.

     2) There entry is of type Logger for the potential parent.

        The entry is 'cat's nearest existing parent. We update cat's
        parent field with this entry. We also break from the loop
        because updating our parent's parent is our parent's
        responsibility.

     3) There entry is of type ProvisionNode for this potential parent.

        We add 'cat' to the list of children for this potential parent.
        @param cat logger whose parents are updated
   */
  private void updateParents(final Logger cat) {
    String loggerName = cat.name;
    int length = loggerName.length();
    boolean parentFound = false;


    //System.out.println("UpdateParents called for " + name);
    // if name = "w.x.y.z",
    // loop through "w.x.y", "w.x" and "w", but not "w.x.y.z"
    for (
      int i = loggerName.lastIndexOf('.', length - 1); i >= 0;
        i = loggerName.lastIndexOf('.', i - 1)) {
      String substr = loggerName.substring(0, i);

      //System.out.println("Updating parent : " + substr);
      CategoryKey key = new CategoryKey(substr); // simple constructor
      Object o = ht.get(key);

      // Create a provision node for a future parent.
      if (o == null) {
    //System.out.println("No parent "+substr+" found. Creating ProvisionNode.");
        ProvisionNode pn = new ProvisionNode(cat);
        ht.put(key, pn);
      } else if (o instanceof Logger) {
        parentFound = true;
        cat.parent = (Logger) o;

       //System.out.println("Linking " + cat.name + " -> " + ((Logger) o).name);
        break; // no need to update the ancestors of the closest ancestor
      } else if (o instanceof ProvisionNode) {
        ((ProvisionNode) o).addElement(cat);
      } else {
        Exception e =
          new IllegalStateException(
            "unexpected object type " + o.getClass() + " in ht.");
        e.printStackTrace();
      }
    }

    // If we could not find any existing parents, then link with root.
    if (!parentFound) {
      cat.parent = root;
    }
  }

  /**
      We update the links for all the children that placed themselves
      in the provision node 'pn'. The second argument 'cat' is a
      reference for the newly created Logger, parent of all the
      children in 'pn'

      We loop on all the children 'c' in 'pn':

         If the child 'c' has been already linked to a child of
         'cat' then there is no need to update 'c'.

         Otherwise, we set cat's parent field to c's parent and set
         c's parent field to cat.
   @param pn provisional node
   @param logger parent logger

  */
  private void updateChildren(final ProvisionNode pn,
                                    final Logger logger) {
    //System.out.println("updateChildren called for " + logger.name);
    final int last = pn.size();

    for (int i = 0; i < last; i++) {
      Logger l = (Logger) pn.elementAt(i);


      //System.out.println("Updating child " +p.name);
      // Unless this child already points to a correct (lower) parent,
      // make cat.parent point to l.parent and l.parent to cat.
      if (!l.parent.name.startsWith(logger.name)) {
        logger.parent = l.parent;
        l.parent = logger;
      }
    }
  }
  /**
   * Return this repository's own scheduler.
   * The scheduler is lazily instantiated.
   * @return this repository's own scheduler.
   */
  public Scheduler getScheduler() {
    if (scheduler == null) {
      scheduler = new Scheduler();
      scheduler.setDaemon(true);
      scheduler.start();
    }
    return scheduler;
  }

    /**
     * Puts object by key.
     * @param key key, may not be null.
     * @param value object to associate with key.
     */
  public void putObject(final String key,
                        final Object value) {
    objectMap.put(key, value);
  }

    /**
     * Get object by key.
     * @param key key, may not be null.
     * @return object associated with key or null.
     */
  public Object getObject(final String key) {
    return objectMap.get(key);
  }

    /**
     * Set logger factory.
     * @param factory logger factory.
     */
  public void setLoggerFactory(final LoggerFactory factory) {
    if (factory == null) {
      throw new NullPointerException();
    }
    this.loggerFactory = factory;
  }

    /**
     * Get logger factory.
     * @return logger factory.
     */
  public LoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

}
