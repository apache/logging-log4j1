package org.apache.joran.action;

import org.apache.joran.ExecutionContext;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

import org.w3c.dom.Element;

public class RootLoggerAction extends Action {
	
  static final String NAME_ATTR = "name";
  static final String CLASS_ATTR = "class";
  static final String ADDITIVITY_ATTR = "additivity";
  static final String EMPTY_STR = "";
  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };
  Logger logger = Logger.getLogger(RootLoggerAction.class);

  public void begin(ExecutionContext ec, Element loggerElement) {

		logger.debug("In begin method");
		
    LoggerRepository repository = (LoggerRepository) ec.getObject(0);
    Logger root = repository.getRootLogger();   
  }

  public void end(ExecutionContext ec, Element e) {
  }

  public void finish(ExecutionContext ec) {
  }
}
