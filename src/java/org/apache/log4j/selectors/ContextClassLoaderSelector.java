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
 * @author  Jacob Kjome 
 */
public class ContextClassLoaderSelector implements RepositorySelector {
      
  // key: current thread's ContextClassLoader, 
  // value: Hierarchy instance
  final private static Map hierMap = Collections.synchronizedMap(new WeakHashMap());
  
  final private static ContextClassLoaderSelector singleton = new ContextClassLoaderSelector();
  private static boolean initialized = false;
  
  private ContextClassLoaderSelector() {}
  
  public LoggerRepository getLoggerRepository() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Hierarchy hierarchy = (Hierarchy) hierMap.get(cl);
    
    if(hierarchy == null) {
      hierarchy = new Hierarchy(new RootCategory((Level) Level.DEBUG));
      hierMap.put(cl, hierarchy);
    } 
    return hierarchy;
  }
  
  /** 
   * The Container should initialize the logger repository for each
   * webapp upon startup or reload.  In this case, it is controllable
   * via each webapp.
   */
  public static void doIdempotentInitialization() {
    if(!initialized) {
      try {      
        Object guard = new Object();
        LogManager.setRepositorySelector(singleton, guard);   
        initialized = true;
      } catch (IllegalArgumentException iae) {
        //either ignore the exception or log the fact that the setting of this 
        //custom repository selector failed because another had been set previously
        // and maybe we should set "initialized" to "true" in here so this exception doesn't
        // occur again in this class
      }
    }
  }

}


