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

package org.apache.log4j.chainsaw.help;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;

import org.apache.log4j.chainsaw.ChainsawConstants;
import org.apache.log4j.helpers.LogLog;


/**
 * Singleton help manager where objects can register to display
 * Help for something, an independant viewer can register to
 * be notified when the requested Help URL changes and can display
 * it appropriately. This class effectively decouples the help requester
 * from the help implementation (if any!)
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class HelpManager {
  private static final HelpManager instance = new HelpManager();
  private HelpLocator helpLocator = new HelpLocator();
  private URL helpURL;
  private final PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);

  private HelpManager() {
    
//    TODO setup all the base URLs in the default.properties and configure in ApplicationPreferenceModel
    
    try {
      if (System.getProperty("log4j.chainsaw.localDocs")!=null) {
        LogLog.info("Adding HelpLocator for localDocs property=" +System.getProperty("log4j.chainsaw.localDocs") );
        helpLocator.installLocator(new URL(System.getProperty("log4j.chainsaw.localDocs")));
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
        helpLocator.installClassloaderLocator(this.getClass().getClassLoader());
//      helpLocator.installLocator(new URL());
  }

  /**
   * @return
   */
  public final URL getHelpURL() {
    return helpURL;
  }

  /**
   * The current Help URL that should be displayed, and is
   * a PropertyChangeListener supported property.
   *
   * This method ALWAYS fires property change events
   * even if the value is the same (the oldvalue
   * of the event will be null)
   * @param helpURL
   */
  public final void setHelpURL(URL helpURL) {
    this.helpURL = helpURL;
    firePropertyChange("helpURL", null, this.helpURL);
  }

  /**
   * @param listener
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(listener);
  }

  /**
   * @param propertyName
   * @param listener
   */
  public synchronized void addPropertyChangeListener(
    String propertyName, PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * @param evt
   */
  public void firePropertyChange(PropertyChangeEvent evt) {
    propertySupport.firePropertyChange(evt);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  public void firePropertyChange(
    String propertyName, boolean oldValue, boolean newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  public void firePropertyChange(
    String propertyName, int oldValue, int newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param propertyName
   * @param oldValue
   * @param newValue
   */
  public void firePropertyChange(
    String propertyName, Object oldValue, Object newValue) {
    propertySupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  /**
   * @param listener
   */
  public synchronized void removePropertyChangeListener(
    PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(listener);
  }

  /**
   * @param propertyName
   * @param listener
   */
  public synchronized void removePropertyChangeListener(
    String propertyName, PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   *
   */
  public static HelpManager getInstance() {
    return instance;
  }

  /**
   * Given a class, and that it belongs within the org.apache.log4j project,
   * sets the URL to the JavaDoc for that class.
   *
   * @param c
   */
  public void showHelpForClass(Class c) {
    String name = c.getName();
    name = name.replace('.', '/') + ".html";

    URL url = helpLocator.findResource(name);
    LogLog.debug("located help resource for '" + name +"' at " + (url==null?"":url.toExternalForm()));
    
    if (url != null) {
      setHelpURL(url);
    } else {
      //     TODO Create a resource not found url
      setHelpURL(ChainsawConstants.WELCOME_URL);
    }
  }
}
