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
package org.apache.log4j.chainsaw.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * A Container class used to hold unique LoggingEvent values
 * and provide them as unique ListModels.
 * 
 * @author Paul Smith 
 *
 */
class EventTypeEntryContainer {
  private Set ColumnNames = new HashSet();
  private Set Methods = new HashSet();
  private Set Classes = new HashSet();
  private Set MDCKeys = new HashSet();
  private Set NDCs = new HashSet();
  private Set Levels = new HashSet();
  private Set Loggers = new HashSet();
  private Set Threads = new HashSet();
  private Set FileNames = new HashSet();
  private DefaultListModel columnNameListModel = new DefaultListModel();
  private DefaultListModel methodListModel = new DefaultListModel();
  private DefaultListModel classesListModel = new DefaultListModel();
  private DefaultListModel mdcListModel = new DefaultListModel();
  private DefaultListModel ndcListModel = new DefaultListModel();
  private DefaultListModel levelListModel = new DefaultListModel();
  private DefaultListModel loggerListModel = new DefaultListModel();
  private DefaultListModel threadListModel = new DefaultListModel();
  private DefaultListModel fileNameListModel = new DefaultListModel();

  void addLevel(Object level) {
    if (Levels.add(level)) {
      levelListModel.addElement(level);
    }
  }

  void addLogger(String logger) {
    if (Loggers.add(logger)) {
      loggerListModel.addElement(logger);
    }
  }

  void addFileName(String filename) {
    if (FileNames.add(filename)) {
      fileNameListModel.addElement(filename);
    }
  }

  void addThread(String thread) {
    if (Threads.add(thread)) {
      threadListModel.addElement(thread);
    }
  }

  void addNDC(String ndc) {
    if (NDCs.add(ndc)) {
      ndcListModel.addElement(ndc);
    }
  }

  void addColumnName(String name) {
    if (ColumnNames.add(name)) {
      columnNameListModel.addElement(name);
    }
  }

  void addMethod(String method) {
    if (Methods.add(method)) {
      methodListModel.addElement(method);
    }
  }

  void addClass(String className) {
    if (Classes.add(className)) {
      classesListModel.addElement(className);
    }
  }

  void addMDCKeys(Set keySet) {
    if (MDCKeys.addAll(keySet)) {
      for (Iterator iter = keySet.iterator(); iter.hasNext();) {
        Object element = (Object) iter.next();
        mdcListModel.addElement(element);
      }
    }
  }

  ListModel getColumnListModel() {
    return columnNameListModel;
  }

  /**
   * @return
   */
  DefaultListModel getClassesListModel() {
    return classesListModel;
  }

  /**
   * @return
   */
  DefaultListModel getColumnNameListModel() {
    return columnNameListModel;
  }

  /**
   * @return
   */
  DefaultListModel getFileNameListModel() {
    return fileNameListModel;
  }

  /**
   * @return
   */
  DefaultListModel getLevelListModel() {
    return levelListModel;
  }

  /**
   * @return
   */
  DefaultListModel getLoggerListModel() {
    return loggerListModel;
  }

  /**
   * @return
   */
  DefaultListModel getMdcListModel() {
    return mdcListModel;
  }

  /**
   * @return
   */
  DefaultListModel getMethodListModel() {
    return methodListModel;
  }

  /**
   * @return
   */
  DefaultListModel getNdcListModel() {
    return ndcListModel;
  }

  /**
   * @return
   */
  DefaultListModel getThreadListModel() {
    return threadListModel;
  }
}
