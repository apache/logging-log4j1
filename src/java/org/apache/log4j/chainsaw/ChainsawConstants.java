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

import java.net.URL;

/**
 * 
 * Constants used throught Chainsaw.
 * 
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 */
public class ChainsawConstants {
  private ChainsawConstants(){}
  
  public static final URL WELCOME_URL = ChainsawConstants.class.getClassLoader().getResource(
  "org/apache/log4j/chainsaw/WelcomePanel.html");
  
  public static final URL EXAMLE_CONFIG_URL =
  ChainsawConstants.class.getClassLoader().getResource(
	  "org/apache/log4j/chainsaw/log4j-receiver-sample.xml");

  public static final URL TUTORIAL_URL =
  ChainsawConstants.class.getClassLoader().getResource(
	  "org/apache/log4j/chainsaw/help/tutorial.html");
  
  static final String MAIN_PANEL = "panel";
  static final String LOWER_PANEL = "lower";
  static final String UPPER_PANEL = "upper";
  static final String EMPTY_STRING = "";
  static final String FILTERS_EXTENSION = ".filters";
  static final String SETTINGS_EXTENSION = ".settings";

  //COLUMN NAMES
  static final String LOGGER_COL_NAME = "Logger";
  static final String TIMESTAMP_COL_NAME = "Timestamp";
  static final String LEVEL_COL_NAME = "Level";
  static final String THREAD_COL_NAME = "Thread";
  static final String MESSAGE_COL_NAME = "Message";
  static final String NDC_COL_NAME = "NDC";
  static final String MDC_COL_NAME = "MDC";
  static final String THROWABLE_COL_NAME = "Throwable";
  static final String CLASS_COL_NAME = "Class";
  static final String METHOD_COL_NAME = "Method";
  static final String FILE_COL_NAME = "File";
  static final String LINE_COL_NAME = "Line";
  static final String PROPERTIES_COL_NAME = "Properties";
  static final String ID_COL_NAME = "ID";

  //none is not a real column name, but is used by filters as a way to apply no filter for colors or display
  static final String NONE_COL_NAME = "None";
  static final String LOG4J_REMOTEHOST_KEY = "log4j.remoteSourceInfo";
  public static final String LOG4J_ID_KEY = "log4jid";
  static final String UNKNOWN_TAB_NAME = "Unknown";
  static final String GLOBAL_MATCH = "*";
  public static final String DETAIL_CONTENT_TYPE = "text/html";

  static final String EVENT_TYPE_KEY = "log4j.eventtype";
  public static final String LOG4J_EVENT_TYPE = "log4j";
  public static final String UTIL_LOGGING_EVENT_TYPE = "util-logging";

  static final String LEVEL_DISPLAY = "level.display";
  static final String LEVEL_DISPLAY_ICONS = "icons";
  static final String LEVEL_DISPLAY_TEXT = "text";  

  static final String DATETIME_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
}
