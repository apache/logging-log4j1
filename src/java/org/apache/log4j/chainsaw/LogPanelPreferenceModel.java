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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 *  Used to encapsulate all the preferences for a given LogPanel
 * @author Paul Smith
 */
public class LogPanelPreferenceModel implements Serializable{
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

  private transient final PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);
  private String dateFormatPattern = ISO8601;
  private boolean levelIcons = true;
  private Set visibleColumns = new HashSet(ChainsawColumns.getColumnsNames());
  private boolean detailPaneVisible = true;
  private boolean toolTips = false;
  private boolean scrollToBottom = true;
  private boolean logTreePanelVisible = true;
  private String loggerPrecision = "";

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
    setLoggerPrecision(that.getLoggerPrecision());
    setDateFormatPattern(that.getDateFormatPattern());
    setLevelIcons(that.isLevelIcons());

    /**
     * First, iterate and ADD new columns, (this means notifications of adds go out first
     * add to the end
     */
    for (Iterator iter = that.visibleColumns.iterator(); iter.hasNext();) {
      String column = (String) iter.next();

      if (!this.visibleColumns.contains(column)) {
        setColumnVisible(column, true);
      }
    }

    /**
     * Now go through and apply removals
     */
    /**
     * this copy is needed to stop ConcurrentModificationException
     */
    Set thisSet = new HashSet(this.visibleColumns);

    for (Iterator iter = thisSet.iterator(); iter.hasNext();) {
      String column = (String) iter.next();

      if (!that.visibleColumns.contains(column)) {
        setColumnVisible(column, false);
      }
    }
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

  /**
   * @param loggerPrecision - an integer representing the number of packages to display, 
   * or an empty string representing 'display all packages' 
   */
  public void setLoggerPrecision(String loggerPrecision) {
    String oldVal = this.loggerPrecision;
    this.loggerPrecision = loggerPrecision;
    propertySupport.firePropertyChange("loggerPrecision", oldVal, this.loggerPrecision);      
  }
  
  /**
   * Returns the Logger precision.
   * @return
   */
  public final String getLoggerPrecision() {
    return loggerPrecision;
  }

  /**
   * Returns true if the named column should be made visible otherwise
   * false.
   * @param columnName
   * @return
   */
  public boolean isColumnVisible(String columnName) {
    return visibleColumns.contains(columnName);
  }

  public void setColumnVisible(String columnName, boolean isVisible) {
    boolean oldValue = visibleColumns.contains(columnName);
    boolean newValue = isVisible;

    if (isVisible) {
      visibleColumns.add(columnName);
    } else {
      visibleColumns.remove(columnName);
    }

    propertySupport.firePropertyChange(
      new PropertyChangeEvent(
        this, "visibleColumns", new Boolean(oldValue), new Boolean(newValue)));
  }

  /**
   * Toggles the state between visible, non-visible for a particular Column name
   * @param string
   */
  public void toggleColumn(String column) {
    setColumnVisible(column, !isColumnVisible(column));
  }

  /**
   * @return
   */
  public final boolean isDetailPaneVisible() {
    return detailPaneVisible;
  }

  /**
   * @param detailPaneVisible
   */
  public final void setDetailPaneVisible(boolean detailPaneVisible) {
    boolean oldValue = this.detailPaneVisible;
    this.detailPaneVisible = detailPaneVisible;
    propertySupport.firePropertyChange(
      "detailPaneVisible", oldValue, this.detailPaneVisible);
  }

  /**
   * @return
   */
  public final boolean isScrollToBottom() {
    return scrollToBottom;
  }

  /**
   * @param scrollToBottom
   */
  public final void setScrollToBottom(boolean scrollToBottom) {
    boolean oldValue = this.scrollToBottom;
    this.scrollToBottom = scrollToBottom;
    propertySupport.firePropertyChange(
      "scrollToBottom", oldValue, this.scrollToBottom);
  }

  /**
   * @return
   */
  public final boolean isToolTips() {
    return toolTips;
  }

  /**
   * @param toolTips
   */
  public final void setToolTips(boolean toolTips) {
    boolean oldValue = this.toolTips;
    this.toolTips = toolTips;
    propertySupport.firePropertyChange("toolTips", oldValue, this.toolTips);
  }

  /**
   * @return
   */
  public final boolean isLogTreePanelVisible() {
    return logTreePanelVisible;
  }

  /**
   * @param logTreePanelVisible
   */
  public final void setLogTreePanelVisible(boolean logTreePanelVisible) {
    boolean oldValue = this.logTreePanelVisible;
    this.logTreePanelVisible = logTreePanelVisible;
    propertySupport.firePropertyChange(
      "logTreePanelVisible", oldValue, this.logTreePanelVisible);
  }

  /**
   * @return
   */
  public boolean isCustomDateFormat()
  {
    return !DATE_FORMATS.contains(getDateFormatPattern()) && !isUseISO8601Format();
  }
}
