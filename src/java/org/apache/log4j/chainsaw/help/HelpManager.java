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

package org.apache.log4j.chainsaw.help;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.ChainsawConstants;


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
    private final Logger logger = LogManager.getLogger(HelpManager.class);

    private HelpManager() {

//    TODO setup all the base URLs in the default.properties and configure in ApplicationPreferenceModel

        try {

            if (System.getProperty("log4j.chainsaw.localDocs") != null) {
                logger.info("Adding HelpLocator for localDocs property=" +
                    System.getProperty("log4j.chainsaw.localDocs"));
                helpLocator.installLocator(new URL(
                        System.getProperty("log4j.chainsaw.localDocs")));
            }else if(new File("docs/api").exists()) {
            	File dir = new File("docs/api");
            	logger.info("Detected Local JavaDocs at " + dir.toString());
            	helpLocator.installLocator(dir.toURL());
            } else {
            	logger.warn("Could not find any local JavaDocs, you might want to consider running 'ant javadoc'. The release version will be able to access Javadocs from the Apache website.");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        helpLocator.installClassloaderLocator(this.getClass().getClassLoader());
//      helpLocator.installLocator(new URL());
    }

    /**
     * @return URL
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
    public synchronized void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
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
    public void firePropertyChange(String propertyName, boolean oldValue,
        boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public void firePropertyChange(String propertyName, int oldValue,
        int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public void firePropertyChange(String propertyName, Object oldValue,
        Object newValue) {
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
    public synchronized void removePropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
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

        URL url = getHelpForClass(c);
        setHelpURL(url);
    }

    /**
     * Determines the most appropriate Help resource for a particular class
     * or returns ChainsawConstants.URL_PAGE_NOT_FOUND if there is no resource located.
     *
     * @return URL
     */
    public URL getHelpForClass(Class c) {

        String name = c.getName();
        name = name.replace('.', '/') + ".html";

        URL url = helpLocator.findResource(name);
        logger.debug("located help resource for '" + name + "' at " +
            ((url == null) ? "" : url.toExternalForm()));

        return (url != null) ? url : ChainsawConstants.URL_PAGE_NOT_FOUND;

    }
}
