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
package org.apache.log4j.chainsaw.prefs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
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
    private static final String GLOBAL_SETTINGS_FILE_NAME = "chainsaw.settings.properties";
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
            is = this.getClass().getClassLoader()
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

        loadGlobalSettings();
        loadProfileableSettings();
    }

    /**
     *
     */
    private void loadProfileableSettings() {
        EventListener[] listeners = listenerList.getListeners(SettingsListener.class);

        for (int i = 0; i < listeners.length; i++) {
            SettingsListener settingsListener = (SettingsListener) listeners[i];

            if (settingsListener instanceof Profileable) {
                Profileable p = (Profileable) settingsListener;
                loadProfileble(p);
            }
        }
    }

    private void loadProfileble(Profileable p) {
        LoadSettingsEvent event = createProfilebleEvent(p);
        p.loadSettings(event);
    }

    private LoadSettingsEvent createProfilebleEvent(Profileable p) {
        Properties loadedProperties = new Properties();
        loadedProperties.putAll(getDefaultSettings());
        loadedProperties.putAll(loadProperties(p));
        

        LoadSettingsEvent event = new LoadSettingsEvent(this, loadedProperties);

        return event;
    }

    /**
     * @param p
     * @return
     */
    private Properties loadProperties(Profileable p) {
        Properties properties = new Properties(defaultProperties);
        InputStream is = null;

        File f = new File(getSettingsDirectory(),
        		URLEncoder.encode(p.getNamespace() + ".properties"));
        
        if (!f.exists()) {
        	f = new File(getSettingsDirectory(),
            		p.getNamespace() + ".properties");        	
        }

        if (f.exists()) {
            try {
                is = new BufferedInputStream(new FileInputStream(f));

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

    private void loadGlobalSettings() {
        EventListener[] listeners = listenerList.getListeners(SettingsListener.class);
        LoadSettingsEvent event = null;

        for (int i = 0; i < listeners.length; i++) {
            SettingsListener settingsListener = (SettingsListener) listeners[i];

            if (event == null) {
                Properties loadedProperties = loadGlobalProperties();

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

        saveGlobalSettings(settingsDir);
        saveProfileableSetting(settingsDir);
    }

    /**
     * Looks up all the Profileable's that have been registered
     * and creates a new event for each of them, and ensures that they
     * are saved within a separate external store
     * @param settingsDir
     */
    private void saveProfileableSetting(File settingsDir) {
        EventListener[] listeners = listenerList.getListeners(SettingsListener.class);
        SaveSettingsEvent event = null;

        for (int i = 0; i < listeners.length; i++) {
            SettingsListener settingsListener = (SettingsListener) listeners[i];

            if (settingsListener instanceof Profileable) {
                Profileable profileable = (Profileable) settingsListener;
                event = new SaveSettingsEvent(this, getSettingsDirectory());

                profileable.saveSettings(event);

                OutputStream os = null;

                try {
                    os = new BufferedOutputStream(new FileOutputStream(
                                new File(settingsDir,
                                		 URLEncoder.encode(profileable.getNamespace()) + ".properties")));
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
        }
    }

    private void saveGlobalSettings(File settingsDir) {
        EventListener[] listeners = listenerList.getListeners(SettingsListener.class);
        SaveSettingsEvent event = null;

        for (int i = 0; i < listeners.length; i++) {
            SettingsListener settingsListener = (SettingsListener) listeners[i];

            if (!(settingsListener instanceof Profileable)) {
                if (event == null) {
                    event = new SaveSettingsEvent(this, getSettingsDirectory());
                }

                settingsListener.saveSettings(event);
            }
        }

        OutputStream os = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(
                        new File(settingsDir, GLOBAL_SETTINGS_FILE_NAME)));
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
        if (listener instanceof Profileable) {
            loadProfileble((Profileable) listener);
        } else {
            Properties loadedProperties = loadGlobalProperties();
            LoadSettingsEvent event = new LoadSettingsEvent(this,
                    loadedProperties);
            listener.loadSettings(event);
        }
    }

    /**
     * Returns the current Properties settings for this user
     * by merging the default Properties with the ones we find in their directory.
     *
     * @return
     */
    private Properties loadGlobalProperties() {
        Properties properties = new Properties(defaultProperties);
        InputStream is = null;

        File f = new File(getSettingsDirectory(), GLOBAL_SETTINGS_FILE_NAME);

        if (f.exists()) {
            try {
                is = new BufferedInputStream(new FileInputStream(f));

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
    public Properties getDefaultSettings() {
        return defaultProperties;
    }
}
