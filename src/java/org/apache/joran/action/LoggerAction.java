package org.apache.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.joran.helper.Option;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggerRepository;

import org.w3c.dom.Element;

import java.lang.reflect.Method;

public class LoggerAction extends Action {
	
  Logger logger = Logger.getLogger(LoggerAction.class);

  public void begin(ExecutionContext ec, Element loggerElement) {
  	// Let us forget about previous errors (in this object)
  	inError = false; 
  	
    LoggerRepository repository = (LoggerRepository) ec.getObject(0);

    // Create a new org.apache.log4j.Category object from the <category> element.
    String loggerName = loggerElement.getAttribute(ActionConst.NAME_ATTRIBUTE);
    if(Option.isEmpty(loggerName)) {
      inError = true;
			String errorMsg = "No 'name' attribute in element "
				+loggerElement.getTagName();
			logger.warn(errorMsg);
      ec.addError(errorMsg);
      return;
    }
    
		logger.debug("Logger name is ["+loggerName+"].");

    Logger l;

    String className = loggerElement.getAttribute(ActionConst.CLASS_ATTRIBUTE);

    if (ActionConst.EMPTY_STR.equals(className)) {
      logger.debug("Retreiving an instance of org.apache.log4j.Logger.");
      l = repository.getLogger(loggerName);
    } else {
      logger.debug("Desired logger sub-class: [" + className + ']');

      try {
        Class clazz = Loader.loadClass(className);
        Method getInstanceMethod =
          clazz.getMethod("getLogger", ActionConst.ONE_STRING_PARAM);
        l = (Logger) getInstanceMethod.invoke(null, new Object[] { loggerName });
      } catch (Exception oops) {
        logger.error(
          "Could not retrieve category ["
            + loggerName
            + "]. Reported error follows.",
          oops);
        return;
      }     
    }

    boolean additivity =
      OptionConverter.toBoolean(
        loggerElement.getAttribute(ActionConst.ADDITIVITY_ATTRIBUTE),
        true);
    logger.debug(
      "Setting [" + l.getName() + "] additivity to [" + additivity + "].");
    l.setAdditivity(additivity);
    
    logger.debug("Pushing logger named ["+loggerName+"].");
		ec.pushObject(l);
  }

  public void end(ExecutionContext ec, Element e) {
  	logger.debug("end() called.");
  	if(!inError) {
			logger.debug("Removing logger from stack.");
  	  ec.popObject();
  	}
  }

  public void finish(ExecutionContext ec) {
  }
  
}
