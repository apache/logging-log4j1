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
package org.apache.log4j.chainsaw.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
public class EventTypeEntryContainer {
  private Set ColumnNames = new HashSet();
  private Set Methods = new HashSet();
  private Set Classes = new HashSet();
  private Set propertyKeys = new HashSet();
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
  private Map modelMap = new HashMap();
  private static final String LOGGER_FIELD = "LOGGER";
  private static final String LEVEL_FIELD = "LEVEL";
  private static final String CLASS_FIELD = "CLASS";
  private static final String FILE_FIELD = "FILE";
  private static final String THREAD_FIELD = "THREAD";
  private static final String METHOD_FIELD = "METHOD";
  private static final String MDC_FIELD = "MDC";
  private static final String NDC_FIELD = "NDC";

  public EventTypeEntryContainer() {
      modelMap.put(LOGGER_FIELD, loggerListModel);
      modelMap.put(LEVEL_FIELD, levelListModel);
      modelMap.put(CLASS_FIELD, classesListModel);
      modelMap.put(FILE_FIELD, fileNameListModel);
      modelMap.put(THREAD_FIELD, threadListModel);
      modelMap.put(METHOD_FIELD, methodListModel);
      modelMap.put(NDC_FIELD, ndcListModel);
      modelMap.put(MDC_FIELD, mdcListModel);
  }
  
  public boolean modelExists(String fieldName) {
      if (fieldName != null) {
          return (modelMap.keySet().contains(fieldName.toUpperCase()));
      }
      return false;
  }
  
  public ListModel getModel(String fieldName) {
      if (fieldName != null) {
          return (ListModel)modelMap.get(fieldName.toUpperCase());
      }
      return null;
  } 
  
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

  void addPropertyKeys(Set keySet) {
    if (propertyKeys.addAll(keySet)) {
      for (Iterator iter = keySet.iterator(); iter.hasNext();) {
        Object element = iter.next();
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
  DefaultListModel getPropertiesListModel() {
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
