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

/*
 */
package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.prefs.SettingsManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 *  Used to encapsulate all the preferences for a given LogPanel
 * @author Paul Smith
 */
public class LogPanelPreferenceModel {
  public static final String ISO8601 = "ISO8601";
  public static final Collection DATE_FORMATS;

  static {
    Collection list = new ArrayList();

    Properties properties = SettingsManager.getInstance().getDefaultSettings();

    for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();

      if (entry.getKey().toString().startsWith("DateFormat")) {
        list.add(entry.getValue());
      }
    }

    DATE_FORMATS = Collections.unmodifiableCollection(list);
  }

  private final PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);
  private String dateFormatPattern = ISO8601;
  private boolean levelIcons = true;

  /**
   * Returns the Date Pattern string for the alternate date formatter.
   * @return
   */
  public final String getDateFormatPattern() {
    return dateFormatPattern;
  }

  /**
   * @param dateFormatPattern
   */
  public final void setDateFormatPattern(String dateFormatPattern) {
    String oldVal = this.dateFormatPattern;
    this.dateFormatPattern = dateFormatPattern;
    propertySupport.firePropertyChange(
      "dateFormatPattern", oldVal, this.dateFormatPattern);
  }

  /**
   * @param listener
   */
  public synchronized void addPropertyChangeListener(
    PropertyChangeListener listener) {
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
   * Applies all the properties of another model to this model
   *
   * @param uncommitedPreferenceModel the model to copy
   * all the properties from
   */
  public void apply(LogPanelPreferenceModel that) {
    setDateFormatPattern(that.getDateFormatPattern());
    setLevelIcons(that.isLevelIcons());
  }

  /**
   * Returns true if this the fast ISO8601DateFormat object
   * should be used instead of SimpleDateFormat
   * @return
   */
  public boolean isUseISO8601Format() {
    return getDateFormatPattern().equals(ISO8601);
  }

  /**
   * @return
   */
  public boolean isLevelIcons() {
    return levelIcons;
  }

  /**
   * @param levelIcons
   */
  public void setLevelIcons(boolean levelIcons) {
    boolean oldVal = this.levelIcons;
    this.levelIcons = levelIcons;
    propertySupport.firePropertyChange("levelIcons", oldVal, this.levelIcons);
  }
}
