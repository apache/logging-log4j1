/*
 * @author Paul Smith <psmith@apache.org>
 *
*/
package org.apache.log4j.chainsaw;

/**
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 * 
 */
public class ChainsawConstants {
  private ChainsawConstants(){}
  
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
  static final String LOG4J_APP_KEY = "log4japp";
  static final String LOG4J_MACHINE_KEY = "log4jmachinename";
  static final String LOG4J_REMOTEHOST_KEY = "log4j.remoteSourceInfo";
  public static final String LOG4J_ID_KEY = "log4jid";
  static final String UNKNOWN_TAB_NAME = "Unknown";
  static final String GLOBAL_MATCH = "*";
  public static final String DETAIL_CONTENT_TYPE = "text/html";

  static final String EVENT_TYPE_KEY = "log4j.eventtype";
  static final String LOG4J_EVENT_TYPE = "log4j";
  static final String UTIL_LOGGING_EVENT_TYPE = "util-logging";

  static final String LEVEL_DISPLAY = "level.display";
  static final String LEVEL_DISPLAY_ICONS = "icons";
  static final String LEVEL_DISPLAY_TEXT = "text";  

  static final String DATETIME_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
}
