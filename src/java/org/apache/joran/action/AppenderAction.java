package org.apache.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.spi.OptionHandler;
import org.w3c.dom.Element;


public class AppenderAction extends Action {

  final static Logger logger = Logger.getLogger(AppenderAction.class);

  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };

  Logger logger2Attach = null;

  Appender appender;
  
  public void begin(ExecutionContext ec, Element appenderElement) {

    Object o = ec.peekObject();

    if (!(o instanceof Logger)) {
      logger.warn("Could not find a logger at the top of execution stack.");
      inError = true;
      ec.addError(
        "For element <level>, could not find a logger at the top of execution stack.");
      return;
    }

    logger2Attach = (Logger) o;

    String className =
      appenderElement.getAttribute(ActionConst.CLASS_ATTRIBUTE);
    logger.debug("Class name: [" + className + ']');

    try {
      Object instance = Loader.loadClass(className).newInstance();
      appender = (Appender) instance;
			String appenderName = appenderElement.getAttribute(ActionConst.NAME_ATTRIBUTE);
			if(appenderName == null) {
				logger.warn("No appender name given for appender of type "+className
					+"].");
			} else {
        appender.setName(appenderName);
			}
      ec.pushObject(appender);
    } catch (Exception oops) {
    	inError = true;
      logger.error(
        "Could not create an Appender. Reported error follows.", oops);
        ec.addError("Could not create appender of type "+className+"].");
    }

  }

  public void end(ExecutionContext ec, Element e) {
    if (inError)
      return;
		if(appender instanceof OptionHandler) {
		 ((OptionHandler)appender).activateOptions();
		}
  }

  public void finish(ExecutionContext ec) {
  }
}
