
package org.apache.log4j.joran.action;

/**
 *
 * This class contains costants used by other Actions.
 * 
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public abstract class ActionConst {
	
  public static final String APPENDER_TAG = "appender";
  public static final String REF_ATTRIBUTE = "ref";
  public static final String ADDITIVITY_ATTRIBUTE = "additivity";
  public static final String CONVERTER_CLASS_ATTRIBUTE = "converterClass";
  public static final String CONVERSION_WORD_ATTRIBUTE = "conversionWord";
  public static final String PATTERN_ATTRIBUTE = "pattern";
  public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";

  static final String INHERITED = "INHERITED";
  static final String NULL = "NULL";
  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };

  public static final String APPENDER_BAG = "APPENDER_BAG";
  public static final String FILTER_CHAIN_BAG = "FILTER_CHAIN_BAG";
}
