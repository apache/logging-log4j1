/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.xml;

import java.util.*;

import java.net.URL;

import org.w3c.dom.*;
import java.lang.reflect.Method;
import org.apache.log4j.Category;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.FileWatchdog;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;


/**
   Use this class to initialize the log4j environment using a DOM tree.

   <p>The DTD is specified in <a
   href="doc-files/log4j.dtd"><b>log4j.dtd</b></a>.

   <p>Sometimes it is useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.configDebug</b> variable on the java command line.

   <p>There are sample XML files included in the package.
   
   @author <a href=mailto:cstaylor@pacbell.net>Christopher Taylor</a>
   @author Ceki G&uuml;lc&uuml;

   @since 0.8.3 */
public class DOMConfigurator extends BasicConfigurator implements Configurator {

  static final String CONFIGURATION_TAG = "configuration";
  static final String RENDERER_TAG      = "renderer";
  static final String APPENDER_TAG 	= "appender";
  static final String APPENDER_REF_TAG 	= "appender-ref";  
  static final String PARAM_TAG    	= "param";
  static final String LAYOUT_TAG	= "layout";
  static final String CATEGORY		= "category";
  static final String NAME_ATTR		= "name";
  static final String CLASS_ATTR        = "class";
  static final String VALUE_ATTR	= "value";
  static final String ROOT_TAG		= "root";
  static final String PRIORITY_TAG	= "priority";
  static final String FILTER_TAG	= "filter";
  static final String ERROR_HANDLER_TAG	= "errorHandler";
  static final String REF_ATTR		= "ref";
  static final String ADDITIVITY_ATTR    = "additivity";  
  static final String SCFO_ATTR          = "disableOverride";
  static final String CONFIG_DEBUG_ATTR  = "configDebug";
  static final String RENDERING_CLASS_ATTR = "renderingClass";
  static final String RENDERED_CLASS_ATTR = "renderedClass";

  static final String EMPTY_STR = "";
  static final Class[] ONE_STRING_PARAM = new Class[] {String.class};

  final static String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";

  
  // key: appenderName, value: appender
  Hashtable appenderBag;

  /**
     No argument constructor.
  */
  public
  DOMConfigurator () { 
    appenderBag = new Hashtable();
  }

  /**
     Used internally to parse appenders by IDREF.
   */
  protected
  Appender findAppenderByReference(Element appenderRef) {    
    String appenderName = appenderRef.getAttribute(REF_ATTR);
    Appender appender = (Appender) appenderBag.get(appenderName);
    if(appender != null) {
      return appender;
    } else {
      Document doc = appenderRef.getOwnerDocument();

      // DOESN'T WORK!! :
      // Element element = doc.getElementById(appenderName);
                        
      // Endre's hack:
      Element element = null;
      NodeList list = doc.getElementsByTagName("appender");
      for (int t=0; t<list.getLength(); t++) {
	Node node = list.item(t);
	NamedNodeMap map= node.getAttributes();
	Node attrNode = map.getNamedItem("name");
	if (appenderName.equals(attrNode.getNodeValue())) {
	  element = (Element) node;
	  break;
	}
      }
      // Hack finished.

      if(element == null) {
	LogLog.error("No appender named ["+appenderName+"] could be found."); 
	return null;
      } else {
	appender = parseAppender(element);
	appenderBag.put(appenderName, appender);
	return appender;
      }
    } 
  }

  /**
     Used internally to parse an appender element.
   */
  protected
  Appender parseAppender (Element appenderElement) {
    String className = appenderElement.getAttribute(CLASS_ATTR);
    LogLog.debug("Class name: [" + className+']');    
    try {
      Object instance 	= Class.forName(className).newInstance();
      Appender appender	= (Appender)instance;

      appender.setName(appenderElement.getAttribute(NAME_ATTR));
      
      NodeList children	= appenderElement.getChildNodes();
      final int length 	= children.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = children.item(loop);

	/* We're only interested in Elements */
	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	  Element currentElement = (Element)currentNode;

	  // Parse appender parameters 
	  if (currentElement.getTagName().equals(PARAM_TAG)) {
	    if(appender instanceof OptionHandler) {
	      parseParameters(currentElement, (OptionHandler) appender);
	    }
	  }
	  // Set appender layout
	  else if (currentElement.getTagName().equals(LAYOUT_TAG)) {
	    appender.setLayout(parseLayout(currentElement));
	  }
	  // Add filters
	  else if (currentElement.getTagName().equals(FILTER_TAG)) {
	    parseFilters(currentElement, appender);
	  }
	  else if (currentElement.getTagName().equals(ERROR_HANDLER_TAG)) {
	    parseErrorHandler(currentElement, appender);
	  }
	  else if (currentElement.getTagName().equals(APPENDER_REF_TAG)) {
	    String refName = currentElement.getAttribute(REF_ATTR);
	    if(appender instanceof AppenderAttachable) {
	      AppenderAttachable aa = (AppenderAttachable) appender;
	      LogLog.debug("Attaching appender named ["+ refName+
			   "] to appender named ["+ appender.getName()+"].");
	      aa.addAppender(findAppenderByReference(currentElement));
	    } else {
	      LogLog.error("Requesting attachment of appender named ["+
			   refName+ "] to appender named ["+ appender.getName()+
                "] which does not implement org.apache.log4j.spi.AppenderAttachable.");
	    }
	  }
	}
      }
      if(appender instanceof OptionHandler) {
	((OptionHandler) appender).activateOptions();
      }
      return appender;
    }
    /* Yes, it's ugly.  But all of these exceptions point to the same
       problem: we can't create an Appender */
    catch (Exception oops) {
      LogLog.error("Could not create an Appender. Reported error follows.",
		   oops);
      return null;
    }
  }

  /**
     Used internally to parse an {@link ErrorHandler} element.
   */
  protected
  void parseErrorHandler(Element element, Appender appender) {
    ErrorHandler eh = (ErrorHandler) OptionConverter.instantiateByClassName(
                                       element.getAttribute(CLASS_ATTR),
                                       org.apache.log4j.spi.ErrorHandler.class, 
 				       null);
    if(eh != null) {
      NodeList children = element.getChildNodes();
      final int length 	= children.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = children.item(loop);
	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	  Element currentElement = (Element) currentNode;
	  String tagName = currentElement.getTagName();
	  if(tagName.equals(PARAM_TAG)) {
	    parseParameters(currentElement, eh);
	  }
	}
      }
      appender.setErrorHandler(eh);
    }
  }
  
  /**
     Used internally to parse an <code>param</code> element.
   */
  protected
  void parseParameters(Element elem, OptionHandler oh) {
    String name = elem.getAttribute(NAME_ATTR);
    String value = elem.getAttribute(VALUE_ATTR);
    LogLog.debug("Handling parameter \""+name+ "="+value+'\"');	   
    if(oh instanceof OptionHandler && value != null) {
      oh.setOption(name, OptionConverter.convertSpecialChars(value));
    }
  }

  /**
     Used internally to parse a filter element.
   */
  protected
  void parseFilters(Element element, Appender appender) {
    String clazz = element.getAttribute(CLASS_ATTR);
    Filter filter = (Filter) OptionConverter.instantiateByClassName(clazz,
                                                org.apache.log4j.spi.Filter.class, 
					        null);
    
    if(filter != null) {
      NodeList children = element.getChildNodes();
      final int length 	= children.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = children.item(loop);
	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	  Element currentElement = (Element) currentNode;
	  String tagName = currentElement.getTagName();
	  if(tagName.equals(PARAM_TAG)) {
	    parseParameters(currentElement, filter);
	  }
	}
      }
      appender.addFilter(filter);
    }    
  }
  
  /**
     Used internally to parse an category element.
  */
  protected
  void parseCategory (Element categoryElement, Hierarchy hierarchy) {
    // Create a new org.apache.log4j.Category object from the <category> element.
    String catName = categoryElement.getAttribute(NAME_ATTR);

    Category cat;    

    String className = categoryElement.getAttribute(CLASS_ATTR);


    if(EMPTY_STR.equals(className)) {
      LogLog.debug("Retreiving an instance of org.apache.log4j.Category.");
      cat = hierarchy.getInstance(catName);
    }
    else {
      LogLog.debug("Desired category sub-class: ["+className+']');
       try {	 
	 Class clazz = Class.forName(className);
	 Method getInstanceMethod = clazz.getMethod("getInstance", 
						    ONE_STRING_PARAM);
	 cat = (Category) getInstanceMethod.invoke(null, new Object[] {catName});
       } catch (Exception oops) {
	 LogLog.error("Could not retrieve category ["+catName+
		      "]. Reported error follows.", oops);
	 return;
       }
    }

    // Setting up a category needs to be an atomic operation, in order
    // to protect potential log operations while category
    // configuration is in progress.
    synchronized(cat) {
      cat.setAdditivity(OptionConverter.toBoolean(
                  categoryElement.getAttribute(ADDITIVITY_ATTR), true));
      parseChildrenOfCategoryElement(categoryElement, cat, false);
    }
  }


  /**
     Used internally to parse the roor category element.
  */
  protected
  void parseRoot (Element rootElement, Hierarchy hierarchy) {
    Category root = hierarchy.getRoot();
    // category configuration needs to be atomic
    synchronized(root) {    
      parseChildrenOfCategoryElement(rootElement, root, true);
    }
  }


  /**
     Used internally to parse the children of a category element.
  */
  protected
  void parseChildrenOfCategoryElement(Element catElement,
				      Category cat, boolean isRoot) {
				      
    // Remove all existing appenders from cat. They will be
    // reconstructed if need be.
    cat.removeAllAppenders();


    NodeList children 	= catElement.getChildNodes();
    final int length 	= children.getLength();
    
    for (int loop = 0; loop < length; loop++) {
      Node currentNode = children.item(loop);

      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	Element currentElement = (Element) currentNode;
	String tagName = currentElement.getTagName();
	
	if (tagName.equals(APPENDER_REF_TAG)) {
	  Element appenderRef = (Element) currentNode;
	  Appender appender = findAppenderByReference(appenderRef);
	  String refName =  appenderRef.getAttribute(REF_ATTR);
	  if(appender != null)
	    LogLog.debug("Adding appender named ["+ refName+ 
			 "] to category ["+cat.getName()+"].");
	  else 
	    LogLog.debug("Appender named ["+ refName + "] not found.");
	    
	  cat.addAppender(appender);
	  
	} else if(tagName.equals(PRIORITY_TAG)) {
	  parsePriority(currentElement, cat, isRoot);	
	} else if(tagName.equals(PARAM_TAG)) {
	  if(cat instanceof OptionHandler) {
	    OptionHandler oh = (OptionHandler) cat;
	    parseParameters(currentElement, oh);
	    oh.activateOptions();
	  }
	}
      }
    }
  }

  /**
     Used internally to parse a layout element.
  */  
  protected
  Layout parseLayout (Element layout_element) {
    String className = layout_element.getAttribute(CLASS_ATTR);
    LogLog.debug("Parsing layout of class: \""+className+"\"");		 
    try {
      Object instance 	= Class.forName(className).newInstance();
      Layout layout   	= (Layout)instance;
      
      NodeList params 	= layout_element.getChildNodes();
      final int length 	= params.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = (Node)params.item(loop);
	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	  Element currentElement = (Element) currentNode;
	  String tagName = currentElement.getTagName();
	  if(tagName.equals(PARAM_TAG)) {
	    parseParameters(currentElement, layout);
	  }
	}
      }

      /* Now make those options take effect */
      layout.activateOptions();
      return layout;
    }
    catch (Exception oops) {
      LogLog.error("Could not create the Layout. Reported error follows.",
		   oops);
      return null;
    }
  }

  protected 
  void parserRenderer(Element element) {
    String renderingClass = element.getAttribute(RENDERING_CLASS_ATTR);
    String renderedClass = element.getAttribute(RENDERED_CLASS_ATTR);
    addRenderer(renderedClass, renderingClass);
  }

  /**
     Used internally to parse a priority  element.
  */
  protected
  void parsePriority(Element element, Category cat, boolean isRoot) {
    String catName = cat.getName();
    if(isRoot) {
      catName = "root";
    }

    String priStr = element.getAttribute(VALUE_ATTR);
    LogLog.debug("Priority value for "+catName+" is  ["+priStr+"].");
    
    if(BasicConfigurator.INHERITED.equals(priStr)) {
      if(isRoot) {
	LogLog.error("Root priority cannot be inherited. Ignoring directive.");
      } else {
	cat.setPriority(null);
      }
    }
    else {
      String className = element.getAttribute(CLASS_ATTR);      
      if(EMPTY_STR.equals(className)) {      
	
	cat.setPriority(Priority.toPriority(priStr));
      } else {
	LogLog.debug("Desired Priority sub-class: ["+className+']');
	try {	 
	  Class clazz = Class.forName(className);
	  Method toPriorityMethod = clazz.getMethod("toPriority", 
						    ONE_STRING_PARAM);
	  Priority pri = (Priority) toPriorityMethod.invoke(null, 
						    new Object[] {priStr});
	  cat.setPriority(pri);
	} catch (Exception oops) {
	  LogLog.error("Could not create priority ["+priStr+
		       "]. Reported error follows.", oops);
	  return;
	}
      }
    }
    LogLog.debug(catName +" priority set to " +cat.getPriority());    
  }


  /**
     Configure log4j using a <code>configuration</code> element as
     defined in the log4j.dtd. 

  */
  static
  public
  void configure (Element element) {
    DOMConfigurator configurator = new DOMConfigurator();
    configurator.parse(element, Category.getDefaultHierarchy());
  }

 /**
     Like {@link #configureAndWatch(String, long)} except that the
     default delay as defined by {@link FileWatchdog#DEFAULT_DELAY} is
     used. 

     @param configFilename A log4j configuration file in XML format.

  */
  static
  public
  void configureAndWatch(String configFilename) {
    configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
  }



  /**
     Read the configuration file <code>configFilename</code> if it
     exists. Moreover, a thread will be created that will periodically
     check if <code>configFilename</code> has been created or
     modified. The period is determined by the <code>delay</code>
     argument. If a change or file creation is detected, then
     <code>configFilename</code> is read to configure log4j.  

      @param configFilename A log4j configuration file in XML format.
      @param delay The delay in milliseconds to wait between each check.
  */
  static
  public
  void configureAndWatch(String configFilename, long delay) {
    XMLWatchdog xdog = new XMLWatchdog(configFilename);
    xdog.setDelay(delay);
    xdog.start();
  }

  public
  void doConfigure(String filename, Hierarchy hierarchy) {
    try {
      doConfigure(new FileInputStream(filename), hierarchy);
    } catch(IOException e) {
      LogLog.error("Could not open ["+filename+"].", e);
    }
  }

  public
  void doConfigure(URL url, Hierarchy hierarchy) {
    try {
      doConfigure(url.openStream(), hierarchy);
    } catch(IOException e) {
      LogLog.error("Could not open ["+url+"].", e);
    }
  }


  /**
     Configure log4j by reading in a log4j.dtd compliant XML
     configuration file.

  */
  public
  void doConfigure(InputStream input, Hierarchy hierarchy) 
                                          throws FactoryConfigurationError {
    DocumentBuilderFactory dbf = null;
    try { 
      LogLog.debug("System property is :"+System.getProperty(dbfKey));      
      dbf = DocumentBuilderFactory.newInstance();
      LogLog.debug("Standard DocumentBuilderFactory search succeded.");
      LogLog.debug("DocumentBuilderFactory is: "+dbf.getClass().getName());
    } catch(FactoryConfigurationError fce) {
      Exception e = fce.getException();
      LogLog.debug("Could not instantiate a DocumentBuilderFactory.", e);
      throw fce;
    }
      
    try {
      // This makes ID/IDREF attributes to have a meaning. Don't ask
      // me why.
      dbf.setValidating(true);

      DocumentBuilder docBuilder = dbf.newDocumentBuilder();
      //docBuilder.setErrorHandler(new ReportParserError());

      InputSource inputSource = new InputSource(input);
      URL dtdURL = DOMConfigurator.class.getResource("log4j.dtd");
      if(dtdURL == null) {
	LogLog.error("Could not find log4j.dtd.");
      }
      else {
	LogLog.debug("URL to log4j.dtd is [" + dtdURL.toString()+"].");
	inputSource.setSystemId(dtdURL.toString());
      }
      Document doc = docBuilder.parse(inputSource);
      parse(doc.getDocumentElement(), hierarchy);
    } catch (Exception e) {
      // I know this is miserable...
      LogLog.error("Could not parse input stream ["+input+"].", e);
    }
  }

  

  /**
     This is the static version of {@link #doConfigure(String, Hierarchy)}.x
   */
  static
  public
  void configure(String filename) throws FactoryConfigurationError {
    new DOMConfigurator().doConfigure(filename, Category.getDefaultHierarchy());
  }

  /**
     Used internally to configure the log4j framework by parsing a DOM
     tree of XML elements based on <a
     href="doc-files/log4j.dtd">log4j.dtd</a>.
     
  */
  protected
  void parse(Element element, Hierarchy hierarchy) {
    
    if (!element.getTagName().equals(CONFIGURATION_TAG)) {
      LogLog.error("DOM element is not a <configuration> element");
      return;
    }

    String confDebug = element.getAttribute(CONFIG_DEBUG_ATTR);
    LogLog.debug("configDebug attribute= \"" + confDebug +"\".");
    // if the log4j.dtd is not specified in the XML file, then the
    // configDebug attribute is returned as the empty string when it
    // is not specified in the XML file.
    if(!confDebug.equals("") && !confDebug.equals("null")) {      
      LogLog.setInternalDebugging(OptionConverter.toBoolean(confDebug, true));
    }
    else 
      LogLog.debug("Ignoring " + CONFIG_DEBUG_ATTR + " attribute.");
      
    String override = element.getAttribute(SCFO_ATTR);
    LogLog.debug("Disable override=\"" + override +"\".");
    // if the log4j.dtd is not specified in the XML file, then the
    // SCFO_ATTR attribute is returned as the empty string when it
    // is not specified in the XML file.
    if(!override.equals("") && !override.equals("null")) {
      // overrideAsNeeded is defined in BasicConfigurator.
      overrideAsNeeded(override);
    }
    else 
      LogLog.debug("Ignoring " + SCFO_ATTR + " attribute.");
    
    //Hashtable appenderBag = new Hashtable(11);

    /* Building Appender objects, placing them in a local namespace
       for future reference */
    NodeList children = element.getChildNodes();
    final int length = children.getLength();
    
    for (int loop = 0; loop < length; loop++) {
      Node currentNode = children.item(loop);
      if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	Element currentElement = (Element) currentNode;
	String tagName = currentElement.getTagName();

	if (tagName.equals(CATEGORY)) {
	  parseCategory(currentElement, hierarchy);
	} else if (tagName.equals(ROOT_TAG)) {
	  parseRoot(currentElement, hierarchy);
	} else if(tagName.equals(RENDERER_TAG)) {
	  parserRenderer(currentElement);
	}
      }
    }
  }
}


class XMLWatchdog extends FileWatchdog {

  XMLWatchdog(String filename) {
    super(filename);
  }

  /**
     Call {@link PropertyConfigurator#configure(String)} with the
     <code>filename</code> to reconfigure log4j. */
  public
  void doOnChange() {
    new DOMConfigurator().doConfigure(filename, Category.getDefaultHierarchy());
  }
}
