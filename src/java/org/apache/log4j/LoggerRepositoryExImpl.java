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

package org.apache.log4j;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.scheduler.Scheduler;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEventListener;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.spi.RendererSupport;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * This class implements LoggerRepositoryEx by
 *   wrapping an existing LoggerRepository implementation
 *   and implementing the newly added capabilities.
*/
public final class LoggerRepositoryExImpl
        implements LoggerRepositoryEx, RendererSupport {

    /**
     * Wrapped logger repository.
     */
  private final LoggerRepository repo;

    /**
     * Logger factory.  Does not affect class of logger
     * created by underlying repository.
     */
  private LoggerFactory loggerFactory;

    /**
     *  Renderer support.
     */
  private final RendererSupport rendererSupport;

    /**
     * List of repository event listeners.
     */
  private final ArrayList repositoryEventListeners = new ArrayList();
    /**
     * Map of HierarchyEventListener keyed by LoggingEventListener.
     */
  private final Map loggerEventListeners = new HashMap();
    /**
     * Name of hierarchy.
     */
  private String name;
    /**
     * Plug in registry.
     */
  private PluginRegistry pluginRegistry;
    /**
     * Properties.
     */
  private final Map properties = new Hashtable();
    /**
     * Scheduler.
     */
  private Scheduler scheduler;

  /** The repository can also be used as an object store
   * for various objects used by log4j components.
   */
  private Map objectMap = new HashMap();


    /**
     * Error list.
     */
  private List errorList = new Vector();

    /**
     * True if hierarchy has not been modified.
     */
  private boolean pristine = true;

  /**
     Constructs a new logger hierarchy.

     @param repository Base implementation of repository.

   */
  public LoggerRepositoryExImpl(final LoggerRepository repository) {
    super();
    if (repository == null) {
        throw new NullPointerException("repository");
    }
    repo = repository;
    if (repository instanceof RendererSupport) {
        rendererSupport = (RendererSupport) repository;
    } else {
        rendererSupport = new RendererSupportImpl();
    }
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
        LogLog.warn(
          "Ignoring attempt to add a previously "
                  + "registered LoggerRepositoryEventListener.");
      } else {
        repositoryEventListeners.add(listener);
      }
    }
  }


  /**
    Remove a {@link LoggerRepositoryEventListener} from the repository.
    @param listener listener
    @since 1.3*/
  public void removeLoggerRepositoryEventListener(
    final LoggerRepositoryEventListener listener) {
    synchronized (repositoryEventListeners) {
      if (!repositoryEventListeners.contains(listener)) {
        LogLog.warn(
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
      if (loggerEventListeners.get(listener) != null) {
        LogLog.warn(
         "Ignoring attempt to add a previously registerd LoggerEventListener.");
      } else {
        HierarchyEventListenerProxy proxy =
                new HierarchyEventListenerProxy(listener);
        loggerEventListeners.put(listener, proxy);
        repo.addHierarchyEventListener(proxy);
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
        repo.addHierarchyEventListener(listener);
    }


  /**
    Remove a {@link LoggerEventListener} from the repository.
    @param listener listener to be removed
    @since 1.3*/
  public void removeLoggerEventListener(final LoggerEventListener listener) {
    synchronized (loggerEventListeners) {
      HierarchyEventListenerProxy proxy =
              (HierarchyEventListenerProxy) loggerEventListeners.get(listener);
      if (proxy == null) {
        LogLog.warn(
          "Ignoring attempt to remove a non-registered LoggerEventListener.");
      } else {
        loggerEventListeners.remove(listener);
        proxy.disable();
      }
    }
  }

    /**
     * Issue warning that there are no appenders in hierarchy.
     * @param cat logger, not currently used.
     */
  public void emitNoAppenderWarning(final Category cat) {
    repo.emitNoAppenderWarning(cat);
  }

  /**
     Check if the named logger exists in the hierarchy. If so return
     its reference, otherwise returns <code>null</code>.

     @param loggerName The name of the logger to search for.
     @return true if logger exists.
  */
  public Logger exists(final String loggerName) {
    return repo.exists(loggerName);
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
    repo.setThreshold(levelStr);
  }

  /**
     Enable logging for logging requests with level <code>l</code> or
     higher. By default all levels are enabled.

     @param l The minimum level for which logging requests are sent to
     their appenders.  */
  public void setThreshold(final Level l) {
    repo.setThreshold(l);
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
        repo.fireAddAppenderEvent(logger, appender);
    }


    /**
      Requests that a appender removed event be sent to any registered
      {@link LoggerEventListener}.
      @param logger The logger from which the appender was removed.
      @param appender The appender removed from the logger.
      */
    public void fireRemoveAppenderEvent(final Category logger,
                                        final Appender appender) {
       if (repo instanceof Hierarchy) {
           ((Hierarchy) repo).fireRemoveAppenderEvent(logger, appender);
       }
    }


  /**
    Requests that a level changed event be sent to any registered
    {@link LoggerEventListener}.
    @param logger The logger which changed levels.
    @since 1.3*/
  public void fireLevelChangedEvent(final Logger logger) {
  }

  /**
   * @TODO
    Requests that a configuration changed event be sent to any registered
    {@link LoggerRepositoryEventListener}.
    @since 1.3*/
  public void fireConfigurationChangedEvent() {
  }


  /**
     Returns a {@link Level} representation of the <code>enable</code>
     state.
     @return current threshold level

     @since 1.2 */
  public Level getThreshold() {
    return repo.getThreshold();
  }


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
    return repo.getLogger(loggerName);
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
    return repo.getLogger(loggerName, factory);
  }

  /**
     Returns all the currently defined categories in this hierarchy as
     an {@link java.util.Enumeration Enumeration}.

     <p>The root logger is <em>not</em> included in the returned
     {@link Enumeration}.
     @return enumerator of current loggers
   */
  public Enumeration getCurrentLoggers() {
    return repo.getCurrentLoggers();
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
    return repo.getCurrentCategories();
  }

  /**
     Get the renderer map for this hierarchy.
   @return renderer map
  */
  public RendererMap getRendererMap() {
    return rendererSupport.getRendererMap();
  }

  /**
     Get the root of this hierarchy.

     @since 0.9.0
   @return root of hierarchy
   */
  public Logger getRootLogger() {
    return repo.getRootLogger();
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
    return repo.isDisabled(level);
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
    repo.resetConfiguration();
  }

  /**
     Used by subclasses to add a renderer to the hierarchy passed as parameter.
   @param renderedClass class
   @param renderer object used to render class.
   */
  public void setRenderer(final Class renderedClass,
                          final ObjectRenderer renderer) {
    rendererSupport.setRenderer(renderedClass, renderer);
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
    repo.shutdown();
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

    /**
     * Implementation of RendererSupportImpl if not
     * provided by LoggerRepository.
     */
   private static final class RendererSupportImpl implements RendererSupport {
        /**
         * Renderer map.
         */
       private final RendererMap renderers = new RendererMap();

        /**
         * Create new instance.
         */
       public RendererSupportImpl() {
           super();
       }

        /** {@inheritDoc} */
       public RendererMap getRendererMap() {
           return renderers;
       }

        /** {@inheritDoc} */
       public void setRenderer(final Class renderedClass,
                               final ObjectRenderer renderer) {
           renderers.put(renderedClass, renderer);
       }
   }

    /**
     * Proxy that implements HierarchyEventListener
     * and delegates to LoggerEventListener.
     */
   private static final class HierarchyEventListenerProxy
        implements HierarchyEventListener {
        /**
         * Wrapper listener.
         */
       private LoggerEventListener listener;

        /**
         * Creates new instance.
         * @param l listener
         */
       public HierarchyEventListenerProxy(final LoggerEventListener l) {
           super();
           if (l == null) {
               throw new NullPointerException("l");
           }
           listener = l;
       }

        /** {@inheritDoc} */
       public void addAppenderEvent(final Category cat,
                                    final Appender appender) {
           if (isEnabled() && cat instanceof Logger) {
                listener.appenderAddedEvent((Logger) cat, appender);
           }
       }

        /** {@inheritDoc} */
       public void removeAppenderEvent(final Category cat,
                                    final Appender appender) {
           if (isEnabled() && cat instanceof Logger) {
                listener.appenderRemovedEvent((Logger) cat, appender);
           }
       }

        /**
         * Disable forwarding of notifications to
         * simulate removal of listener.
         */
       public synchronized void disable() {
           listener = null;
       }

        /**
         * Gets whether proxy is enabled.
         * @return true if proxy is enabled.
         */
       private synchronized boolean isEnabled() {
           return listener != null;
       }
   }

}
