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

import java.awt.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.spi.LoggingEvent;


/**
 * If there are no filters defined, null is returned for color (the table background should then be used).
 * If more than one color is assigned to a column/regexp combination, the last one added is the one used.
 * Columns and regexps define a single entry for color filtering, meaning the same column may be used in
 * multiple filters, as long as the regexp is unique.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 * @deprecated The new Rule structure replaces this class
 * 
 */
public class ColorFilter {
  private Vector filterListeners = new Vector();
  private Vector filters = new Vector();

  public void addFilterChangedListener(FilterChangedListener listener) {
    filterListeners.add(listener);
  }

  private void notifyListeners() {
    Iterator iter = filterListeners.iterator();

    while (iter.hasNext()) {
      ((FilterChangedListener) iter.next()).filterChanged();
    }
  }

  public synchronized void addFilter(
    String columnName, String regExp, Color color) {
    Iterator iter = filters.iterator();
    FilterEntry removable = null;

    while (iter.hasNext()) {
      FilterEntry entry = (FilterEntry) iter.next();

      if (
        (entry.getColumnName().equals(columnName))
          && (entry.getRegExp().equals(regExp))) {
        removable = entry;
      }
    }

    if (removable != null) {
      filters.remove(removable);
    }

    FilterEntry thisEntry = new FilterEntry(columnName, regExp, color);

    if (!filters.contains(thisEntry)) {
      filters.add(thisEntry);
    }

    notifyListeners();
  }

  public synchronized void clear() {
    filters.clear();
    notifyListeners();
  }

  public HashMap getEntriesByColumn(String column) {
    HashMap result = new HashMap();
    Iterator iter = filters.iterator();

    while (iter.hasNext()) {
      FilterEntry entry = (FilterEntry) iter.next();

      if (entry.getColumnName().equals(column)) {
        result.put(entry.getRegExp(), entry.getColor());
      }
    }

    return result;
  }

  public Collection getRegExpByColumn(String column) {
    Vector result = new Vector();
    Iterator iter = filters.iterator();

    while (iter.hasNext()) {
      FilterEntry entry = (FilterEntry) iter.next();

      if (entry.getColumnName().equals(column)) {
        result.add(entry.getRegExp());
      }
    }

    return result;
  }

  //relies on tostring correctly returning the string representation (either the row's value was
  //a string or row.get(i).tostring will return the data in a correct format - as in a date)
  public synchronized Color getColor(List columnNames, LoggingEvent event) {
      
    Color color = null;
    
//    TODO this is broken while the change from Vectors -> LoggingEvents occurs
//    Iterator iter = filters.iterator();
//
//    while (iter.hasNext()) {
//      FilterEntry entry = (FilterEntry) iter.next();
//      int colCount = columnNames.size();
//
//      for (int i = 0; i < colCount; i++) {
//        if (row.get(i) == null) {
//          //NOTE: level was in colnames but wasn't in vector 
//          return null;
//        }
//
//        if (
//          entry.matches(columnNames.get(i).toString(), row.get(i).toString())) {
//          return entry.getColor();
//        }
//      }
//    }

    return color;
  }

  class FilterEntry {
    private final String columnName;
    private final String regExp;
    private ExpressionEvaluator evaluator;
    private final Color color;

    FilterEntry(String columnName, String regExp, Color color) {
      this.columnName = columnName;
      this.regExp = regExp;
      this.color = color;
      evaluator =
        ExpressionEvaluatorFactory.newInstance().getEvaluator(regExp);
    }

    Color getColor() {
      return color;
    }

    String getColumnName() {
      return columnName;
    }

    String getRegExp() {
      return regExp;
    }

    boolean matches(String column, String expression) {
      return ((column != null) && (evaluator != null)
      && column.equals(this.columnName) && evaluator.match(expression));
    }

    public int hashCode() {
      int result = 37;

      if (columnName != null) {
        result = (result * 17) + columnName.hashCode();
      }

      if (regExp != null) {
        result = (result * 17) + regExp.hashCode();
      }

      if (color != null) {
        result = (result * 17) + color.hashCode();
      }

      return result;
    }

    public boolean equals(Object o) {
      if (o instanceof FilterEntry) {
        FilterEntry f = (FilterEntry) o;
        boolean result =
          (((columnName == null) && (f.columnName == null))
          || ((columnName != null) && columnName.equals(f.columnName)));
        result =
          result
          && (((regExp == null) && (f.regExp == null))
          || ((regExp != null) && regExp.equals(f.regExp)));

        return result
        && (((color == null) && (f.color == null))
        || ((color != null) && color.equals(f.color)));
      }

      return false;
    }

    public String toString() {
      return "columnName: " + columnName + ", regExp: " + regExp + ", color: "
      + color;
    }
  }
}