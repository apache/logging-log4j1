/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.selectors;

import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * ContextClassLoaderSelector
 *
 * <p>This class is provided as an example of a <code>RepositorySelector</code>
 * that uses ClassLoaders as a context to select a </code>LoggerRepository
 * </code> within a J2EE container, such as Tomcat.  Because the implementation
 * of class loading can vary greatly between J2EE containers, this class is
 * considered <bold>experimental</bold>.  No guarantees are made that this
 * class will work in every J2EE container, and the implementation of this
 * class may change greatly in the future.  While it useful, it is primarily
 * provided as a "point of discussion" for creating other repository
 * selectors for specific J2EE containers.  It has been tested primarily
 * with Tomcat 4.0.x.
 *
 * <p>Based primarily on Ceki Gülcü's article <h3>Supporting the Log4j
 * <code>RepositorySelector</code> in Servlet Containers</h3> at:
 * http://qos.ch/containers/sc.html</p>
 *
 * <p>By default, the class static <code>RepositorySelector</code> variable
 * of the <code>LogManager</code> class is set to a trivial
 * <code>RepositorySelector</code> implementation which always
 * returns the same logger repository, which also happens to be a
 * <code>Hierarchy</code> instance. In other words, by default log4j will use
 * one hierarchy, the default hierarchy. This behavior can be overridden via 
 * the <code>LogManager</code>'s
 * <code>setRepositorySelector(RepositorySelector, Object)</code> method.</p>
 *
 * <p>That is where this class enters the picture.  It can be used to define a
 * custom logger repository.  It makes use of the fact that each webapp runs
 * in its own classloader.  This means we can track hierachies using the
 * webapp classloader as the key to each individual hierarchy.  That is what
 * is meant by "contextual" repository selector.  Each classloader provides
 * a unique context.</p>
 *
 * <p>Of course, this means that this class will only work in containers which
 * provide for separate classloaders, so that is something to keep in mind.
 * This methodology will certainly work in containers such as Tomcat 4.x.x and
 * probably a multitude of others.  However, Tomcat 4.x.x is the only container
 * currently tested.</p>
 *
 * @author  Jacob Kjome <hoju@visi.com>
 * @since   1.3
 */
public class ContextClassLoaderSelector implements RepositorySelector {
      
    // key: current thread's ContextClassLoader, 
    // value: Hierarchy instance
    final private static Map hierMap =
      Collections.synchronizedMap(new WeakHashMap());
    
    // key: current thread's ContextClassLoader, 
    // value: Log4jCRS instance
    final private static Map crsMap =
      Collections.synchronizedMap(new WeakHashMap());
    
    // static initializer
    static {
        // This should only ever be called once in this class' entire existence.
        // The only way to override it is to pass in the exact same guard object
        // that we set here...which is next to impossible unless we store a
        // reference to it.  We will not do that unless it becomes a requirement
        // in the future.
        Object guard = new Object();
        LogManager.setRepositorySelector(new ContextClassLoaderSelector(), guard);    
    }
    
    /**
     * default constructor
     */
    public ContextClassLoaderSelector() {}

    /**
     * This provides access to the hierarchy object which is associated with
     * the current webapp.
     *
     * @param the contextual classloader of the current webapp.
     *
     * @return the Hierarchy instance associated with the current webapp.
     *         May be null.
     */
    public static Hierarchy getHierarchy(ClassLoader cl) {
        return (Hierarchy) hierMap.get(cl);
    }

    /**
     * This provides access to the ContextClassLoaderSelector object which 
     * was used to create the hierarchy for the current web-application.  
     * This can be used to remove the instances of both the hierarchy and
     * the Log4jCRS objects at application shutdown.
     *
     * @param the contextual classloader of the current webapp.
     *
     * @return the ContextClassLoaderSelector instance associated with the
     *         current webapp. May be null.
     */
    public static ContextClassLoaderSelector getCRS(ClassLoader cl) {
        return (ContextClassLoaderSelector) crsMap.get(cl);    
    }

    /**
     * This provides access to the LoggerRepository.  Generally, this method 
     * isn't called directly by the developer, rather, it is called
     * automatically by Log4j because setRepositorySelector was called on the
     * LogManager with an instance of this class.
     * 
     * <p>the returned value is guaranteed to be non-null</p>
     *
     * @return the LoggerRepository, or Hierachy, associated with the current
     *         webapp.  Guaranteed to be non-null.
     */
    public LoggerRepository getLoggerRepository() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Hierarchy hierarchy = (Hierarchy) hierMap.get(cl);
  
        if(hierarchy == null) {
            hierarchy = setGetLoggerRepository(cl);
        } 
        return hierarchy;
    }

    /**
     * This generates the new hierachy and stores the instances of both the
     * hierarchy and ContextClassLoaderSelector in Maps in order to make
     * them retrievable at a later date by each individual webapp.
     *
     * @param the contextual classloader of the current webapp.
     *
     * @return a generated Hierarchy instance
     */
    private Hierarchy setGetLoggerRepository(ClassLoader cl) {
        Hierarchy hierarchy = new Hierarchy(new RootCategory((Level) Level.DEBUG));
        hierMap.put(cl, hierarchy);
        crsMap.put(cl, this);
        return hierarchy;
    }

    /** 
     * The Container should initialize the logger repository for each
     * webapp upon startup or reload.  In this case, it is controllable
     * via each webapp.
     */
    public void initLoggerRepository() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        setGetLoggerRepository(cl);
    }

    /** 
     * The Container should remove the entry when the webapp is removed or
     * restarted.  In this case, it is controllable via each webapp.
     *
     * @param the contextual classloader of the current webapp.
     */
    public void remove(ClassLoader cl) {
        hierMap.remove(cl);
        crsMap.remove(cl); 
    }

}
