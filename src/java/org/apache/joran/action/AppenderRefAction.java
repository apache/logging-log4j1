package org.apache.joran.action;

import java.util.HashMap;

import org.apache.joran.ExecutionContext;
import org.apache.joran.JoranParser;
import org.apache.joran.Pattern;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class AppenderRefAction extends Action {

  final static Logger logger = Logger.getLogger(AppenderRefAction.class);
  
  public void begin(ExecutionContext ec, Element appenderRef) {  
	   logger.debug("begin called");

		 Object o = ec.peekObject();
		
		 if(!(o instanceof Logger)) {
		  logger.warn("Could not find a logger at the top of execution stack.");
			inError = true; 
			ec.addError("For element <appender-ref>, could not find a logger at the top of execution stack.");
			return;
		}
		Logger l = (Logger) o;
		
		String appenderName = appenderRef.getAttribute(ActionConst.REF_ATTRIBUTE);
		if(appenderName == null) {
			// print a meaningful error message and return
			Node parentNode = appenderRef.getParentNode();
			String errMsg = "Missing appender ref attribute in <appender-ref> tag.";
			if(parentNode instanceof Element){
				Element parentElement = (Element) parentNode;
				String parentTag = parentElement.getTagName();
				
				if("logger".equals(parentTag)) {
					String loggerName = parentElement.getAttribute("name");
					errMsg = errMsg + " Within <"+parentTag+">"
					 + " named ["+loggerName+"].";				 
				} {
					errMsg = errMsg + " Within <"+parentTag+">"; 			
				}
			}
			parentNode.getAttributes();
			logger.warn(errMsg);
			inError = true; 
			ec.addError(errMsg);
      return;
		}
		
		// let's get the appender
		getAppenderByRef(ec, appenderRef, appenderName);
		Appender appender = (Appender) ec.peekObject();
    
    if(appender == null) {
			logger.warn("Could not find an appender named ["+appenderName+"]");
			inError = true; 
			ec.addError("Could not find an appender named ["+appenderName+"]");

    }
  	
		//			cat.addAppender(appender);
  }


  void getAppenderByRef(ExecutionContext ec, Element appenderRef, 
  String appenderName) {

  	HashMap appenderBag = (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);  	
		Appender appender = (Appender) appenderBag.get(appenderName);
	  if (appender != null) {
	  	ec.pushObject(appender);
		} else {
			// find the right elenet with appender tag and name 'appenderName'
		  Element root = appenderRef.getOwnerDocument().getDocumentElement();
		  Element targetElement = null;
		  NodeList nodeList = root.getElementsByTagName(ActionConst.APPENDER_TAG);
		  for(int i = 0; i < nodeList.getLength(); i++) {
		  	Node n = nodeList.item(i);
		  	if(n instanceof Element) {
		  	  Element e = (Element) n;
		  	  if(appenderName.equals(e.getAttribute(ActionConst.NAME_ATTRIBUTE))) {
						targetElement = e; // we found the appender element we were looking for
		  	  	break;
		  	  }
		  	}
		  }
		  
			if(targetElement == null) {
				logger.warn("Could not find <appender> elemt named ["+appenderName+"]");
				inError = true; 
				ec.addError("Could not find an <appender> element named ["
				+appenderName+"]");
        return;
			}
	
	    JoranParser jp = ec.getJoranParser();
		  jp.loop(targetElement, new Pattern("-"));
	  
	  }
  }

  public void end(ExecutionContext ec, Element e) {
  }

  public void finish(ExecutionContext ec) {
  }
}
