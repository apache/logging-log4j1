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

import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * A singleton helper utility which accepts a field name and a LoggingEvent and returns the
 * String value of that field.
 *
 * This class defines a grammar used in creation of an expression-based Rule.
 *
 * The only available method is Object getField(String fieldName, LoggingEvent event).
 *
 * Here is a description of the mapping of field names in the grammar
 * to fields on the logging event.  While the getField method returns an Object, the
 * individual types returned per field are described here:
 *
 * Field Name                Field value (String representation                Return type
 * LOGGER                    category name (logger)                                        String
 * LEVEL                     level                                                                        Level
 * CLASS                     locationInformation's class name                String
 * FILE                      locationInformation's file name                String
 * LINE                      locationInformation's line number                String
 * METHOD                    locationInformation's method name                String
 * MSG                       message                                                                Object
 * NDC                       NDC                                                                        String
 * EXCEPTION                 throwable string representation                ThrowableInformation
 * TIMESTAMP                 timestamp                                                                Long
 * THREAD                    thread                                                                        String
 * MDC.keyName               entry in the MDC hashtable                         Object
 *                                                          mapped to key 'keyName'
 * PROP.keyName              entry in the Property hashtable                 String
 *                                                          mapped to the key 'keyName'

 * NOTE:  the values for the 'keyName' portion of the MDC and PROP mappings must
 * be an exact match to the key in the hashTable (case sensitive).
 *
 * If the passed-in field is null or doesn't match an entry in the above-described
 * mapping, an exception is thrown.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class LoggingEventFieldResolver {
  private static final List keywordList = new ArrayList();
  private static final String LOGGER_FIELD = "LOGGER";
  private static final String LEVEL_FIELD = "LEVEL";
  private static final String CLASS_FIELD = "CLASS";
  private static final String FILE_FIELD = "FILE";
  private static final String LINE_FIELD = "LINE";
  private static final String METHOD_FIELD = "METHOD";
  private static final String MSG_FIELD = "MSG";
  private static final String NDC_FIELD = "NDC";
  private static final String EXCEPTION_FIELD = "EXCEPTION";
  private static final String TIMESTAMP_FIELD = "TIMESTAMP";
  private static final String THREAD_FIELD = "THREAD";
  private static final String MDC_FIELD = "MDC.";
  private static final String PROP_FIELD = "PROP.";
  private static final String EMPTY_STRING = "";
  private static final LoggingEventFieldResolver resolver =
    new LoggingEventFieldResolver();

  private LoggingEventFieldResolver() {
    keywordList.add(LOGGER_FIELD);
    keywordList.add(LEVEL_FIELD);
    keywordList.add(CLASS_FIELD);
    keywordList.add(FILE_FIELD);
    keywordList.add(LINE_FIELD);
    keywordList.add(METHOD_FIELD);
    keywordList.add(MSG_FIELD);
    keywordList.add(NDC_FIELD);
    keywordList.add(EXCEPTION_FIELD);
    keywordList.add(TIMESTAMP_FIELD);
    keywordList.add(THREAD_FIELD);
    keywordList.add(MDC_FIELD);
    keywordList.add(PROP_FIELD);
  }

  public static LoggingEventFieldResolver getInstance() {
    return resolver;
  }

  public List getKeywords() {
    return keywordList;
  }

  public boolean isField(String fieldName) {
    return keywordList.contains(fieldName);
  }

  public Object getValue(String fieldName, LoggingEvent event) {
    if (fieldName == null) {
        throw new RuntimeException("null field name");
    }

    String upperField = fieldName.toUpperCase();

    if (LOGGER_FIELD.equals(upperField)) {
      return event.getLoggerName();
    } else if (LEVEL_FIELD.equals(upperField)) {
      return event.getLevel();
    } else if (CLASS_FIELD.equals(upperField)) {
      return event.getLocationInformation().getClassName();
    } else if (FILE_FIELD.equals(upperField)) {
      return event.getLocationInformation().getFileName();
    } else if (LINE_FIELD.equals(upperField)) {
      return event.getLocationInformation().getLineNumber();
    } else if (METHOD_FIELD.equals(upperField)) {
      return event.getLocationInformation().getMethodName();
    } else if (MSG_FIELD.equals(upperField)) {
      return event.getMessage();
    } else if (NDC_FIELD.equals(upperField)) {
      String ndcValue = event.getNDC();

      return ((ndcValue == null) ? "" : ndcValue);
    } else if (EXCEPTION_FIELD.equals(upperField)) {
      return event.getThrowableInformation();
    } else if (TIMESTAMP_FIELD.equals(upperField)) {
      return new Long(event.timeStamp);
    } else if (THREAD_FIELD.equals(upperField)) {
      return event.getThreadName();
    } else if (upperField.startsWith(MDC_FIELD)) {
      //note: need to use actual fieldname since case matters
      Object mdcValue = event.getMDC(fieldName.substring(4));

      return ((mdcValue == null) ? EMPTY_STRING : mdcValue.toString());
    } else if (upperField.startsWith(PROP_FIELD)) {
      //note: need to use actual fieldname since case matters
      String propValue = event.getProperty(fieldName.substring(5));

      return ((propValue == null) ? EMPTY_STRING : propValue);
    }

    //there wasn't a match, so throw a runtime exception
    throw new RuntimeException("Unsupported field name: " + fieldName);
  }
}
