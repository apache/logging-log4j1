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

package org.apache.log4j.chainsaw.prefs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.EventListener;
import java.util.Properties;

import javax.swing.event.EventListenerList;


/**
 * SettingManager allows components to register interest in Saving/Loading
 * of general application preferences/settings.
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 */
public final class SettingsManager {
  private static final SettingsManager instance = new SettingsManager();
  private static final String SETTINGS_FILE_NAME =
    "chainsaw.settings.properties";
  private static final String HEADER = "Chainsaws Settings Files";
  private EventListenerList listenerList = new EventListenerList();
  private Properties defaultProperties = new Properties();

  /**
   * Initialises the SettingsManager by loading the default Properties from
   * a resource
   *
   */
  private SettingsManager() {
    //	load the default properties as a Resource
    InputStream is = null;

    try {
      is =
        this.getClass().getClassLoader()
            .getResource("org/apache/log4j/chainsaw/prefs/default.properties")
            .openStream();
      defaultProperties.load(is);

      //      defaultProperties.list(System.out);
      is.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception e) {
        }
      }
    }
  }

  /**
   * Returns the singleton instance of the SettingsManager
   * @return
   */
  public static final SettingsManager getInstance() {
    return instance;
  }

  /**
   * Registers the listener with the manager
   * @param listener
   */
  public void addSettingsListener(SettingsListener listener) {
    listenerList.add(SettingsListener.class, listener);
  }

  /**
   * Requests that the settings be loaded, all listeners will be notified of
   * this call, and configure themselves according to the values found in the
   * loaded settings
   *
   */
  public void loadSettings() {
    /*
     * Ok, note we ensure we have a .chainsaw directory in the users
     * home folder, and create a chainsaw.settings.properties file..
     */
    File settingsDir = getSettingsDirectory();

    if (!settingsDir.exists()) {
      settingsDir.mkdir();
    }
      
    EventListener[] listeners =
      listenerList.getListeners(SettingsListener.class);
    LoadSettingsEvent event = null;

    for (int i = 0; i < listeners.length; i++) {
      SettingsListener settingsListener = (SettingsListener) listeners[i];

      if (event == null) {
        Properties loadedProperties = loadCurrentUserProperties();

        //        loadedProperties.list(System.out);
        event = new LoadSettingsEvent(this, loadedProperties);
      }

      settingsListener.loadSettings(event);
    }
  }

  /**
   * Creates a SaveSettingsEvent and calls all the SettingsListeners
   * to populate the properties with configuration information
   *
   */
  public void saveSettings() {
    /*
     * Ok, note we ensure we have a .chainsaw directory in the users
     * home folder, and create a chainsaw.settings.properties file..
     */
    File settingsDir = getSettingsDirectory();

    if (!settingsDir.exists()) {
      settingsDir.mkdir();
    }

    EventListener[] listeners =
      listenerList.getListeners(SettingsListener.class);
    SaveSettingsEvent event = null;

    for (int i = 0; i < listeners.length; i++) {
      SettingsListener settingsListener = (SettingsListener) listeners[i];

      if (event == null) {
        event = new SaveSettingsEvent(this);
      }

      settingsListener.saveSettings(event);
    }

    OutputStream os = null;

    try {
      os =
        new BufferedOutputStream(
          new FileOutputStream(new File(settingsDir, SETTINGS_FILE_NAME)));
      event.getProperties().store(os, HEADER);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  public File getSettingsDirectory() {
    return new File(System.getProperty("user.home"), ".chainsaw");
  }

  public void configure(SettingsListener listener) {
      Properties loadedProperties = loadCurrentUserProperties();
      LoadSettingsEvent event = new LoadSettingsEvent(this, loadedProperties);
      listener.loadSettings(event);
  }
  
  /**
   * Returns the current Properties settings for this user
   * by merging the default Properties with the ones we find in their directory.
   *
   * @return
   */
  private Properties loadCurrentUserProperties() {
    Properties properties = new Properties(defaultProperties);
    InputStream is = null;

    File f = new File(getSettingsDirectory(), SETTINGS_FILE_NAME);
    if (f.exists()) {
      try {
        is =
          new BufferedInputStream(
            new FileInputStream(f));

        Properties toLoad = new Properties();
        toLoad.load(is);
        properties.putAll(toLoad);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    }

    return properties;
  }

  /**
   * Returns the loaded default settings, which can be used by
   * other classes within this package.
   * @return Properties defaults
   */
  Properties getDefaultSettings() {
    return defaultProperties;
  }
}
