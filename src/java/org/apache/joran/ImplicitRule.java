package org.apache.joran;

import org.apache.joran.action.*;
import org.w3c.dom.Element;

public abstract class ImplicitRule {

  Action action;
  
	ImplicitRule(Action a) {
		action = a;
	}
	
	public Action getAction() {
	  return action;
	}
	
	public abstract boolean isApplicable(Element e, ExecutionContext ec);
	
}
