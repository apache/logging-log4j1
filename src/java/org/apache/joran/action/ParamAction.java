package org.apache.joran.action;

import org.apache.joran.ExecutionContext;

import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.OptionConverter;
import org.w3c.dom.Element;


public class ParamAction extends Action {

  final static Logger logger = Logger.getLogger(ParamAction.class);

	static String NO_NAME = "No name attribute in <param> element";
	static String NO_VALUE = "No name attribute in <param> element";
	
  public void begin(ExecutionContext ec, Element element) {
		String name = element.getAttribute(NAME_ATTRIBUTE);
		String value = (element.getAttribute(VALUE_ATTRIBUTE));

    if(name==null) {
			inError = true;
			logger.error(NO_NAME);
			ec.addError(NO_NAME);	
    	return;
    }

		if(value==null) {
			inError = true;
			logger.error(NO_VALUE);
			ec.addError(NO_VALUE);	
			return;
		}
    
    logger.debug("Setting parameter ["+name+"] to value ["+value+"].");
		Object o = ec.peekObject();
		PropertySetter propSetter = new PropertySetter(o);		
		value = ec.subst(OptionConverter.convertSpecialChars(value));
		propSetter.setProperty(name, value);
  }

  public void end(ExecutionContext ec, Element e) {
  }

  public void finish(ExecutionContext ec) {
  }
}
