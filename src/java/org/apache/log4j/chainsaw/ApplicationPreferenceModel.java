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
package org.apache.log4j.chainsaw;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.chainsaw.prefs.LoadSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SaveSettingsEvent;
import org.apache.log4j.chainsaw.prefs.SettingsListener;
import org.apache.log4j.helpers.Constants;


/**
 * Encapsulates the Chainsaw Application wide properties
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
class ApplicationPreferenceModel implements SettingsListener {

    private boolean showNoReceiverWarning = true;
    private String identifierExpression = Constants.HOSTNAME_KEY + " - " + Constants.APPLICATION_KEY; 

    private final PropertyChangeSupport propertySupport =
        new PropertyChangeSupport(this);

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
    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * @param evt
     */
    private void firePropertyChange(PropertyChangeEvent evt) {
        propertySupport.firePropertyChange(evt);
    }

    /**
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    private void firePropertyChange(String propertyName, boolean oldValue,
        boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    private void firePropertyChange(String propertyName, int oldValue,
        int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    private void firePropertyChange(String propertyName, Object oldValue,
        Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @return
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {

        return propertySupport.getPropertyChangeListeners();
    }

    /**
     * @param propertyName
     * @return
     */
    public PropertyChangeListener[] getPropertyChangeListeners(
        String propertyName) {
        return propertySupport.getPropertyChangeListeners(propertyName);
    }

    /**
     * @param propertyName
     * @return
     */
    public boolean hasListeners(String propertyName) {
        return propertySupport.hasListeners(propertyName);
    }

    /**
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * @return Returns the showNoReceiverWarning.
     */
    public final boolean isShowNoReceiverWarning() {

        return showNoReceiverWarning;
    }
    
    public final String getIdentifierExpression() {
        return identifierExpression;
    }

    public final void setIdentifierExpression(String newIdentifierExpression) {
        String oldIdentifierExpression=identifierExpression;
        this.identifierExpression = newIdentifierExpression;
        firePropertyChange("identifierExpression", oldIdentifierExpression, newIdentifierExpression);
    }
    
    /**
     * @param showNoReceiverWarning The showNoReceiverWarning to set.
     */
    public final void setShowNoReceiverWarning(boolean newShowNoReceiverWarning) {
        boolean oldShowNoReceiverWarning=showNoReceiverWarning;
        this.showNoReceiverWarning = newShowNoReceiverWarning;
        firePropertyChange("showNoReceiverWarning", oldShowNoReceiverWarning, newShowNoReceiverWarning);
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.prefs.SettingsListener#loadSettings(org.apache.log4j.chainsaw.prefs.LoadSettingsEvent)
     */
    public void loadSettings(LoadSettingsEvent event) {
       setShowNoReceiverWarning(event.asBoolean("showNoReceiverWarning"));
       setIdentifierExpression(event.getSetting("identifierExpression"));
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.prefs.SettingsListener#saveSettings(org.apache.log4j.chainsaw.prefs.SaveSettingsEvent)
     */
    public void saveSettings(SaveSettingsEvent event) {
        event.saveSetting("showNoReceiverWarning", isShowNoReceiverWarning());
        event.saveSetting("identifierExpression", getIdentifierExpression());
    }
}
