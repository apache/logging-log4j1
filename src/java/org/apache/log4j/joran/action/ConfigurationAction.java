package org.apache.log4j.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import org.xml.sax.Attributes;


public class ConfigurationAction extends Action {
  static final Logger logger = Logger.getLogger(ConfigurationAction.class);
  static final String INTERNAL_DEBUG_ATTR = "debug";

  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    String debugAttrib = attributes.getValue(INTERNAL_DEBUG_ATTR);
    logger.debug("debug attribute= \"" + debugAttrib + "\".");
    if (debugAttrib == null || debugAttrib.equals("") || debugAttrib.equals("null")) {
      LogLog.debug("Ignoring " + INTERNAL_DEBUG_ATTR + " attribute.");
    } else {
      LogLog.setInternalDebugging(
          OptionConverter.toBoolean(debugAttrib, true));
    }
   }

  public void finish(ExecutionContext ec) {
  }

  public void end(ExecutionContext ec, String e) {
  }
}
