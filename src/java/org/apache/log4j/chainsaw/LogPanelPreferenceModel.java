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
  private boolean levelIcons;
  private Set visibleColumns = new HashSet(ChainsawColumns.getColumnsNames());
  private boolean detailPaneVisible;
  private boolean toolTips;
  private boolean scrollToBottom;
  private boolean logTreePanelVisible;
  private String loggerPrecision = "";

  /**
   * Returns the Date Pattern string for the alternate date formatter.
   * @return date pattern
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
   * @param model the model to copy
   * all the properties from
   */
  public void apply(LogPanelPreferenceModel model) {
    setLoggerPrecision(model.getLoggerPrecision());
    setDateFormatPattern(model.getDateFormatPattern());
    setLevelIcons(model.isLevelIcons());
    setToolTips(model.isToolTips());
    setScrollToBottom(model.isScrollToBottom());
    setDetailPaneVisible(model.isDetailPaneVisible());
    setLogTreePanelVisible(model.isLogTreePanelVisible());

    /**
     * First, iterate and ADD new columns, (this means notifications of adds go out first
     * add to the end
     */
    for (Iterator iter = model.visibleColumns.iterator(); iter.hasNext();) {
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

      if (!model.visibleColumns.contains(column)) {
        setColumnVisible(column, false);
      }
    }
  }

  /**
   * Returns true if this the fast ISO8601DateFormat object
   * should be used instead of SimpleDateFormat
   * @return use ISO8601 format flag
   */
  public boolean isUseISO8601Format() {
    return getDateFormatPattern().equals(ISO8601);
  }

  /**
   * @return level icons flag
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
   * @return logger precision
   */
  public final String getLoggerPrecision() {
    return loggerPrecision;
  }

  /**
   * Returns true if the named column should be made visible otherwise
   * false.
   * @param columnName
   * @return column visible flag
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
   * @param column
   */
  public void toggleColumn(String column) {
    setColumnVisible(column, !isColumnVisible(column));
  }

  /**
   * @return detail pane visible flag
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
   * @return scroll to bottom flag
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
   * @return tool tips enabled flag
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
   * @return log tree panel visible flag
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
   * @return custom date format flag
   */
  public boolean isCustomDateFormat()
  {
    return !DATE_FORMATS.contains(getDateFormatPattern()) && !isUseISO8601Format();
  }
}
