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
