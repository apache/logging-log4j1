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

package org.apache.log4j.chainsaw.plugins;

import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.spi.LoggerRepository;

import java.awt.LayoutManager;

import javax.swing.JPanel;


/**
 */
public abstract class GUIPluginSkeleton extends JPanel implements Plugin {
  private LoggerRepository loggerRepository;
  private boolean active;

  /**
   *
   */
  public GUIPluginSkeleton() {
    super();
  }

  /**
   * @param isDoubleBuffered
   */
  public GUIPluginSkeleton(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  /**
   * @param layout
   */
  public GUIPluginSkeleton(LayoutManager layout) {
    super(layout);
  }

  /**
   * @param layout
   * @param isDoubleBuffered
   */
  public GUIPluginSkeleton(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#getLoggerRepository()
   */
  public LoggerRepository getLoggerRepository() {
    return this.loggerRepository;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#setLoggerRepository(org.apache.log4j.spi.LoggerRepository)
   */
  public void setLoggerRepository(LoggerRepository repository) {
    this.loggerRepository = repository;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#isActive()
   */
  public boolean isActive() {
    return active;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#isEquivalent(org.apache.log4j.plugins.Plugin)
   */
  public boolean isEquivalent(Plugin testPlugin) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * @param active The active to set.
   */
  public final void setActive(boolean active) {
    boolean oldValue = this.active;
    this.active = active;
    firePropertyChange("active", oldValue, this.active);
  }
}
