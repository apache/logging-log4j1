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

import org.apache.log4j.chainsaw.prefs.SettingsManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * If there are no filters defined, null is returned for color (the table background should then be used).
 * If more than one color is assigned to a column/regexp combination, the last one added is the one used.
 * Columns and regexps define a single entry for color filtering, meaning the same column may be used in
 * multiple filters, as long as the regexp is unique.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class DisplayFilter implements Serializable {
  private static final DateFormat DATE_FORMATTER =
    new SimpleDateFormat(ChainsawConstants.DATETIME_FORMAT);
  private transient Vector filterListeners = new Vector();
  private transient Vector filters = new Vector();
  private transient DisplayFilterEntry customFilter;
  private transient boolean customFilterOverride = false;
  private List colNames = ChainsawColumns.getColumnsNames();
  private Vector toolTipDetailColumns;
  private Boolean toolTipsEnabled = Boolean.FALSE;
  private String ident;

  public DisplayFilter(String ident) {
    this.ident = ident;
    toolTipDetailColumns = new Vector(colNames);
  }

  public void save() {
    ObjectOutputStream o2 = null;

    try {
      o2 =
        new ObjectOutputStream(
          new BufferedOutputStream(
            new FileOutputStream(
              new File(
                SettingsManager.getInstance().getSettingsDirectory()
                + File.separator + ident
                + ChainsawConstants.SETTINGS_EXTENSION))));
      o2.writeObject(this);
      o2.flush();
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    try {
      if (o2 != null) {
        o2.close();
      }
    } catch (IOException ioe) {ioe.printStackTrace();}
  }

  public void setCustomFilterOverride(boolean customFilterOverride) {
    this.customFilterOverride = customFilterOverride;
    notifyListeners();
  }

  public void setCustomFilter(DisplayFilterEntry customFilter) {
    this.customFilter = customFilter;
    notifyListeners();
  }

  public boolean isDisplayed(Vector v) {
    boolean passesCustomFilter = true;

    if (customFilter != null) {
      if (
        customFilter.matches(
            customFilter.getColumnName(),
            formatField(v.get(colNames.indexOf(customFilter.getColumnName())))
                .toString(),
            (String) v.get(colNames.indexOf(ChainsawConstants.LEVEL_COL_NAME)))) {
        passesCustomFilter = true;
      } else {
        passesCustomFilter = false;
      }
    }

    if (customFilterOverride) {
      return passesCustomFilter;
    } else {
      if (filters.size() == 0) {
        return passesCustomFilter;
      }

      Iterator iter = filters.iterator();

      while (iter.hasNext()) {
        DisplayFilterEntry entry = (DisplayFilterEntry) iter.next();

        if (
          entry.matches(
              entry.getColumnName(),
              (String) v.get(colNames.indexOf(entry.getColumnName())),
              (String) v.get(
                colNames.indexOf(ChainsawConstants.LEVEL_COL_NAME)))) {
          return passesCustomFilter;
        }
      }

      return false;
    }
  }

  public void setDetailColumns(Vector v) {
    toolTipDetailColumns = v;
    notifyListeners();
  }

  public void enableToolTips(boolean enabled) {
    toolTipsEnabled = new Boolean(enabled);
  }

  public boolean isToolTipsEnabled() {
    return toolTipsEnabled.booleanValue();
  }

  public Vector getDetailColumns() {
    return toolTipDetailColumns;
  }

  public void addFilterChangedListener(FilterChangedListener listener) {
    filterListeners.add(listener);

    //make sure the listener has updated state
    listener.filterChanged();
  }

  private void notifyListeners() {
    Iterator iter = filterListeners.iterator();

    while (iter.hasNext()) {
      ((FilterChangedListener) iter.next()).filterChanged();
    }
  }

  public synchronized void addFilters(Vector v) {
    if (v != null) {
      filters.addAll(v);
      notifyListeners();
    }
  }

  public synchronized void clear() {
    filters.clear();
    notifyListeners();
  }

  public synchronized Vector getEntriesByColumn(String column) {
    Vector result = new Vector();
    Iterator iter = filters.iterator();

    while (iter.hasNext()) {
      DisplayFilterEntry entry = (DisplayFilterEntry) iter.next();

      if (entry.getColumnName().equals(column)) {
        result.add(entry);
      }
    }

    return result;
  }

  public synchronized Collection getValuesByColumn(String column) {
    Vector result = new Vector();
    Iterator iter = filters.iterator();

    while (iter.hasNext()) {
      DisplayFilterEntry entry = (DisplayFilterEntry) iter.next();

      if (entry.getColumnName().equals(column)) {
        result.add(entry.getColumnValue());
      }
    }

    return result;
  }

  /**
   *Format date field
   *
   * @param o object
   *
   * @return formatted object
   */
  private Object formatField(Object o) {
    if (!(o instanceof Date)) {
      return o;
    } else {
      return DATE_FORMATTER.format((Date) o);
    }
  }

  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
    ident = (String) in.readObject();
    toolTipDetailColumns = (Vector) in.readObject();
    toolTipsEnabled = (Boolean) in.readObject();
    colNames = ChainsawColumns.getColumnsNames();
    filterListeners = new Vector();
    filters = new Vector();
    customFilterOverride = false;
  }

  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException {
    out.writeObject(ident);
    out.writeObject(toolTipDetailColumns);
    out.writeObject(toolTipsEnabled);
  }
}
