package org.apache.joran.action;

import org.apache.joran.ExecutionContext;

import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.OptionConverter;
import org.w3c.dom.Element;


public class ParamAction extends Action {

  final static Logger logger = Logger.getLogger(ParamAction.class);

  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };

  public void begin(ExecutionContext ec, Element element) {
    logger.info("begin called");
		Object o = ec.peekObject();
		PropertySetter propSetter = new PropertySetter(o);
		String name = element.getAttribute(ActionConst.NAME_ATTRIBUTE);
		String value = (element.getAttribute(ActionConst.VALUE_ATTRIBUTE));
		value = ec.subst(OptionConverter.convertSpecialChars(value));
		propSetter.setProperty(name, value);
  }

  public void end(ExecutionContext ec, Element e) {
  }

  public void finish(ExecutionContext ec) {
  }
}
