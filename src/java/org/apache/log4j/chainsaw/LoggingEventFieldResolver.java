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
 * Field Name                Field value (String representation		Return type
 * LOGGER                    category name (logger)					String
 * LEVEL                     level									Level
 * CLASS                     locationInformation's class name		String
 * FILE                      locationInformation's file name		String
 * LINE                      locationInformation's line number		String
 * METHOD                    locationInformation's method name		String
 * MSG                       message								Object
 * NDC                       NDC									String
 * EXCEPTION                 throwable string representation		ThrowableInformation
 * TIMESTAMP                 timestamp								Long
 * THREAD                    thread									String
 * MDC.keyName               entry in the MDC hashtable 			Object
 * 							 mapped to key 'keyName' 			
 * PROP.keyName              entry in the Property hashtable 		String
 * 							 mapped to the key 'keyName' 	

 * NOTE:  the values for the 'keyName' portion of the MDC and PROP mappings must
 * be an exact match to the key in the hashTable (case sensitive).
 *
 * If the passed-in field is null or does not match an entry in the above-described
 * mapping, an empty string is returned.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class LoggingEventFieldResolver {
  private static final LoggingEventFieldResolver resolver =
    new LoggingEventFieldResolver();

  private LoggingEventFieldResolver() {
  }

  public static LoggingEventFieldResolver getInstance() {
    return resolver;
  }

  public Object getValue(String fieldName, LoggingEvent event) {
    if (fieldName == null) {
      return "";
    }

    String lowerProp = fieldName.toUpperCase();

    if ("LOGGER".equals(fieldName)) {
      return event.getLoggerName();
    } else if ("LEVEL".equals(fieldName)) {
      return event.getLevel();
    } else if ("CLASS".equals(fieldName)) {
      return event.getLocationInformation().getClassName();
    } else if ("FILE".equals(fieldName)) {
      return event.getLocationInformation().getFileName();
    } else if ("LINE".equals(fieldName)) {
      return event.getLocationInformation().getLineNumber();
    } else if ("METHOD".equals(fieldName)) {
      return event.getLocationInformation().getMethodName();
    } else if ("MSG".equals(fieldName)) {
      return event.getMessage();
    } else if ("NDC".equals(fieldName)) {
      return event.getNDC();
    } else if ("EXCEPTION".equals(fieldName)) {
	  return event.getThrowableInformation();
    } else if ("TIMESTAMP".equals(fieldName)) {
      return new Long(event.timeStamp);
    } else if ("THREAD".equals(fieldName)) {
      return event.getThreadName();
    } else if (fieldName.startsWith("MDC.")) {
      return event.getMDC(fieldName.substring(4));
    } else if (fieldName.startsWith("PROP.")) {
      return event.getProperty(fieldName.substring(5));
    }

    return "";
  }
}
