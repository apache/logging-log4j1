/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.xml;

import java.util.*;

import java.net.URL;

import org.w3c.dom.*;
import java.lang.reflect.Method;
import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.helpers.*;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginRegistry;

import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;

// Contributors:   Mark Womack
//                 Arun Katkere 

/**
   Use this class to initialize the log4j environment using a DOM tree.

   <p>The DTD is specified in <a
   href="doc-files/log4j.dtd"><b>log4j.dtd</b></a>.

   <p>Sometimes it is useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.debug</b> variable on the java command
   line. Alternatively, set the <code>debug</code> attribute in the
   <code>log4j:configuration</code> element. As in
<pre>
   &lt;log4j:configuration <b>debug="true"</b> xmlns:log4j="http://jakarta.apache.org/log4j/">
   ...
   &lt;/log4j:configuration>
</pre>

   <p>There are sample XML files included in the package.
   
   @author Christopher Taylor
   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen

   @since 0.8.3 */
public class DOMConfigurator implements Configurator {

  static Logger logger = Logger.getLogger("LOG4J."+DOMConfigurator.class.getName());

  static final String CONFIGURATION_TAG = "log4j:configuration";
  static final String OLD_CONFIGURATION_TAG = "configuration";
  static final String RENDERER_TAG      = "renderer";
  static final String APPENDER_TAG 	= "appender";
  static final String APPENDER_REF_TAG 	= "appender-ref";  
  static final String PARAM_TAG    	= "param";
  static final String LAYOUT_TAG	= "layout";
  static final String CATEGORY		= "category";
  static final String LOGGER		= "logger";
  static final String LOGGER_REF	= "logger-ref";
  static final String CATEGORY_FACTORY_TAG  = "categoryFactory";
  static final String NAME_ATTR		= "name";
  static final String CLASS_ATTR        = "class";
  static final String VALUE_ATTR	= "value";
  static final String ROOT_TAG		= "root";
  static final String ROOT_REF		= "root-ref";
  static final String LEVEL_TAG	        = "level";
  static final String PRIORITY_TAG      = "priority";
  static final String FILTER_TAG	= "filter";
  static final String ERROR_HANDLER_TAG	= "errorHandler";
  static final String REF_ATTR		= "ref";
  static final String ADDITIVITY_ATTR    = "additivity";  
  static final String THRESHOLD_ATTR       = "threshold";
  static final String CONFIG_DEBUG_ATTR  = "configDebug";
  static final String INTERNAL_DEBUG_ATTR  = "debug";
  static final String RENDERING_CLASS_ATTR = "renderingClass";
  static final String RENDERED_CLASS_ATTR = "renderedClass";
  static final String PLUGIN_TAG = "plugin";

  static final String EMPTY_STR = "";
  static final Class[] ONE_STRING_PARAM = new Class[] {String.class};

  final static String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";

  
  // key: appenderName, value: appender
  Hashtable appenderBag;

  Properties props;
  LoggerRepository repository;

  /**
     No argument constructor.
  */
  public
  DOMConfigurator () { 
    appenderBag = new Hashtable();
  }

  /**
     Used internally to parse appenders by IDREF name.
  */
  protected
  Appender findAppenderByName(Document doc, String appenderName)  {      
    Appender appender = (Appender) appenderBag.get(appenderName);

    if(appender != null) {
      return appender;
    } else {
      // Doesn't work on DOM Level 1 :
      // Element element = doc.getElementById(appenderName);
                        
      // Endre's hack:
      Element element = null;
      NodeList list = doc.getElementsByTagName("appender");
      for (int t=0; t < list.getLength(); t++) {
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
     Used internally to parse appenders by IDREF element.
   */
  protected
  Appender findAppenderByReference(Element appenderRef) {    
    String appenderName = subst(appenderRef.getAttribute(REF_ATTR));    
    Document doc = appenderRef.getOwnerDocument();
    return findAppenderByName(doc, appenderName);
  }

  /**
     Used internally to parse an appender element.
   */
  protected
  Appender parseAppender (Element appenderElement) {
    String className = subst(appenderElement.getAttribute(CLASS_ATTR));
    LogLog.debug("Class name: [" + className+']');    
    try {
      Object instance 	= Loader.loadClass(className).newInstance();
      Appender appender	= (Appender)instance;
      PropertySetter propSetter = new PropertySetter(appender);

      appender.setName(subst(appenderElement.getAttribute(NAME_ATTR)));
      
      NodeList children	= appenderElement.getChildNodes();
      final int length 	= children.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = children.item(loop);

	/* We're only interested in Elements */
	if (!isElement(currentNode)) {
	  continue;
	}
	
	Element currentElement = (Element)currentNode;

	// Parse appender parameters 
	if (currentElement.getTagName().equals(PARAM_TAG)) {
	  setParameter(currentElement, propSetter);	
	} else if (currentElement.getTagName().equals(ERROR_HANDLER_TAG)) {
	  parseErrorHandler(currentElement, appender);
	} else if (currentElement.getTagName().equals(APPENDER_REF_TAG)) {
	  String refName = subst(currentElement.getAttribute(REF_ATTR));
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
	} else {
	  logger.debug("Handling nested <"+ currentElement.getTagName() 
			     + "> for appender "+appender.getName());
	  configureNestedComponent(propSetter, currentElement);	    
	}
      }
      propSetter.activate();
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
  
  protected void configureNestedComponent(PropertySetter parentBean, Element nestedElement) {
    
    String nestedElementTagName = nestedElement.getTagName();
    
    int containmentType = parentBean.canContainComponent(nestedElementTagName);
    
    if(containmentType == PropertySetter.NOT_FOUND) {
      logger.warn("A component with tag name ["+nestedElementTagName
		  +"] cannot be contained within an object of class ["
		  + parentBean.getObjClass().getName()+"].");
      return; // nothing can be done
    }
    
    boolean debug = logger.isDebugEnabled();

    String className = subst(nestedElement.getAttribute(CLASS_ATTR));
    if(debug)
      logger.debug("Will instantiate instance of class ["+className+']');    
    
    Object nestedComponent = null;
    try {
      nestedComponent = Loader.loadClass(className).newInstance();      
    } catch(Exception e) {
      logger.warn("Could not instantiate object of type ["+className+"].", e);
      return;
    } 

    NodeList children	= nestedElement.getChildNodes();
    final int length 	= children.getLength();
    PropertySetter nestedBean = new PropertySetter(nestedComponent);	

    for (int loop = 0; loop < length; loop++) {
      Node currentNode = children.item(loop);

      /* We're only interested in Elements */
      if (!isElement(currentNode)) {
	continue;
      }

      Element currentElement = (Element)currentNode;
      if(hasParamTag(currentElement)) {
	if(debug) {
	  logger.debug("Configuring parameter ["+currentElement.getAttribute("name")
		       + "] for <"+nestedElementTagName+">.");
	}
	setParameter(currentElement, nestedBean);
      } else {
	if(debug) {
	  logger.debug("Configuring component "+nestedComponent+ " with tagged as <"
		       +currentElement.getTagName()+">.");
	}
	configureNestedComponent(nestedBean, currentElement);
      }
    }
    
    // Now let us attach the component
    switch(containmentType) {
    case PropertySetter.AS_PROPERTY:  
      parentBean.setComponent(nestedElementTagName, nestedComponent);
      break;
    case PropertySetter.AS_COLLECTION:  
      parentBean.addComponent(nestedElementTagName, nestedComponent);
      break;
    }
  }


  /**
   * Returns <code>true</code> if the node passed as parameter is an
   * Element, returns <code>false</code> otherwise.  
   * */
  protected boolean isElement(Node node) {
    return (node.getNodeType() == Node.ELEMENT_NODE);
  }

  /**
   * Returns <code>true</code> if the element has the param tag,
   * returns false otherwise.
   * */
  protected boolean hasParamTag(Element element) {
    return element.getTagName().equals(PARAM_TAG);
  }

  /**
     Used internally to parse an {@link ErrorHandler} element.
   */
  protected
  void parseErrorHandler(Element element, Appender appender) {
    ErrorHandler eh = (ErrorHandler) OptionConverter.instantiateByClassName(
                                       subst(element.getAttribute(CLASS_ATTR)),
                                       org.apache.log4j.spi.ErrorHandler.class, 
 				       null);
    
    if(eh != null) {
      eh.setAppender(appender);

      PropertySetter propSetter = new PropertySetter(eh);
      NodeList children = element.getChildNodes();
      final int length 	= children.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = children.item(loop);
	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	  Element currentElement = (Element) currentNode;
	  String tagName = currentElement.getTagName();
	  if(tagName.equals(PARAM_TAG)) {
            setParameter(currentElement, propSetter);
	  } else if(tagName.equals(APPENDER_REF_TAG)) {
	    eh.setBackupAppender(findAppenderByReference(currentElement));
	  } else if(tagName.equals(LOGGER_REF)) {
	    String loggerName = currentElement.getAttribute(REF_ATTR);	    
	    Logger logger = repository.getLogger(loggerName);
	    eh.setLogger(logger);
	  } else if(tagName.equals(ROOT_REF)) {
	    Logger root = repository.getRootLogger();
	    eh.setLogger(root);
	  }
	}
      }
      propSetter.activate();
      appender.setErrorHandler(eh);
    }
  }
  
  /**
     Used internally to parse a filter element.
   */
  protected
  void parseFilters(Element element, Appender appender) {
    String clazz = subst(element.getAttribute(CLASS_ATTR));
    Filter filter = (Filter) OptionConverter.instantiateByClassName(clazz,
                                                Filter.class, null);
    
    if(filter != null) {
      PropertySetter propSetter = new PropertySetter(filter);
      NodeList children = element.getChildNodes();
      final int length 	= children.getLength();

      for (int loop = 0; loop < length; loop++) {
	Node currentNode = children.item(loop);
	if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	  Element currentElement = (Element) currentNode;
	  String tagName = currentElement.getTagName();
	  if(tagName.equals(PARAM_TAG)) {
            setParameter(currentElement, propSetter);
	  } 
	}
      }
      propSetter.activate();
      LogLog.debug("Adding filter of type ["+filter.getClass()
		   +"] to appender named ["+appender.getName()+"].");
      appender.addFilter(filter);
    }    
  }
  
  /**
     Used internally to parse an category element.
  */
  protected
  void parseCategory (Element loggerElement) {
    // Create a new org.apache.log4j.Category object from the <category> element.
    String catName = subst(loggerElement.getAttribute(NAME_ATTR));

    Logger cat;    

    String className = subst(loggerElement.getAttribute(CLASS_ATTR));


    if(EMPTY_STR.equals(className)) {
      LogLog.debug("Retreiving an instance of org.apache.log4j.Logger.");
      cat = repository.getLogger(catName);
    }
    else {
      LogLog.debug("Desired logger sub-class: ["+className+']');
       try {	 
	 Class clazz = Loader.loadClass(className);
	 Method getInstanceMethod = clazz.getMethod("getLogger", 
						    ONE_STRING_PARAM);
	 cat = (Logger) getInstanceMethod.invoke(null, new Object[] {catName});
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
      boolean additivity = OptionConverter.toBoolean(
                           subst(loggerElement.getAttribute(ADDITIVITY_ATTR)),
			   true);
    
      LogLog.debug("Setting ["+cat.getName()+"] additivity to ["+additivity+"].");
      cat.setAdditivity(additivity);
      parseChildrenOfLoggerElement(loggerElement, cat, false);
    }
  }

  /**
     Used internally to parse the roor category element.
  */
  protected void parseRoot (Element rootElement) {
    Logger root = repository.getRootLogger();
    // category configuration needs to be atomic
    synchronized(root) {    
      parseChildrenOfLoggerElement(rootElement, root, true);
    }
  }


  /**
     Used internally to parse the children of a category element.
  */
  protected void parseChildrenOfLoggerElement(Element catElement,
				      Logger cat, boolean isRoot) {
    
    PropertySetter propSetter = new PropertySetter(cat);
    
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
	  String refName =  subst(appenderRef.getAttribute(REF_ATTR));
	  if(appender != null)
	    LogLog.debug("Adding appender named ["+ refName+ 
			 "] to category ["+cat.getName()+"].");
	  else 
	    LogLog.debug("Appender named ["+ refName + "] not found.");
	    
	  cat.addAppender(appender);
	  
	} else if(tagName.equals(LEVEL_TAG)) {
	  parseLevel(currentElement, cat, isRoot);	
	} else if(tagName.equals(PRIORITY_TAG)) {
	  parseLevel(currentElement, cat, isRoot);
	} else if(tagName.equals(PARAM_TAG)) {
          setParameter(currentElement, propSetter);
	}
      }
    }
    propSetter.activate();
  }

  protected void parseRenderer(Element element) {
    String renderingClass = subst(element.getAttribute(RENDERING_CLASS_ATTR));
    String renderedClass = subst(element.getAttribute(RENDERED_CLASS_ATTR));
    if(repository instanceof RendererSupport) {
      RendererMap.addRenderer((RendererSupport) repository, renderedClass, 
			      renderingClass);
    }
  }

  /**
     Used internally to parse a level  element.
  */
  protected void parseLevel(Element element, Logger logger, boolean isRoot) {
    String catName = logger.getName();
    if(isRoot) {
      catName = "root";
    }

    String priStr = subst(element.getAttribute(VALUE_ATTR));
    LogLog.debug("Level value for "+catName+" is  ["+priStr+"].");
    
    if(INHERITED.equalsIgnoreCase(priStr) || NULL.equalsIgnoreCase(priStr)) {
      if(isRoot) {
	LogLog.error("Root level cannot be inherited. Ignoring directive.");
      } else {
	logger.setLevel(null);
      }
    } else {
      String className = subst(element.getAttribute(CLASS_ATTR));      
      if(EMPTY_STR.equals(className)) {	
	logger.setLevel(OptionConverter.toLevel(priStr, Level.DEBUG));
      } else {
	LogLog.debug("Desired Level sub-class: ["+className+']');
	try {	 
	  Class clazz = Loader.loadClass(className);
	  Method toLevelMethod = clazz.getMethod("toLevel", 
						    ONE_STRING_PARAM);
	  Level pri = (Level) toLevelMethod.invoke(null, 
						    new Object[] {priStr});
	  logger.setLevel(pri);
	} catch (Exception oops) {
	  LogLog.error("Could not create level ["+priStr+
		       "]. Reported error follows.", oops);
	  return;
	}
      }
    }
    LogLog.debug(catName + " level set to " + logger.getLevel());    
  }

  protected Plugin parsePlugin(Element pluginElement) {
    String className = subst(pluginElement.getAttribute(CLASS_ATTR));
    LogLog.debug("Creating plugin: [" + className+']');    
    try {
      Plugin plugin = (Plugin)Loader.loadClass(className).newInstance();
      PropertySetter propSetter = new PropertySetter(plugin);

      plugin.setName(subst(pluginElement.getAttribute(NAME_ATTR)));
      
      NodeList children	= pluginElement.getChildNodes();
      final int length 	= children.getLength();
      for (int loop = 0; loop < length; loop++) {
        Node currentNode = children.item(loop);

        /* We're only interested in Elements */
        if (!isElement(currentNode)) {
          continue;
        }
  	
        Element currentElement = (Element)currentNode;
  
      	// Parse appender parameters 
      	if (currentElement.getTagName().equals(PARAM_TAG)) {
      	  setParameter(currentElement, propSetter);	
      	}
      }
      return plugin;
    } catch (Exception e) {
      LogLog.error("Could not create plugin. Reported error follows.",
		   e);
      return null;
    }
  }
  
  protected void setParameter(Element elem, PropertySetter propSetter) {
    String name = subst(elem.getAttribute(NAME_ATTR));
    String value = (elem.getAttribute(VALUE_ATTR));
    value = subst(OptionConverter.convertSpecialChars(value));
    propSetter.setProperty(name, value);
  }


  /**
     Configure log4j using a <code>configuration</code> element as
     defined in the log4j.dtd. 

  */
  static public void configure (Element element) {
    DOMConfigurator configurator = new DOMConfigurator();
    configurator.doConfigure(element,  LogManager.getLoggerRepository());
  }

 /**
     Like {@link #configureAndWatch(String, long)} except that the
     default delay as defined by {@link FileWatchdog#DEFAULT_DELAY} is
     used. 

     @param configFilename A log4j configuration file in XML format.

  */
  static public void configureAndWatch(String configFilename) {
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
  static public void configureAndWatch(String configFilename, long delay) {
    XMLWatchdog xdog = new XMLWatchdog(configFilename);
    xdog.setDelay(delay);
    xdog.start();
  }

  public void doConfigure(String filename, LoggerRepository repository) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filename);
      doConfigure(fis, repository);
    } catch(IOException e) {
      LogLog.error("Could not open ["+filename+"].", e);
    } finally {
      if (fis != null) {
	try {
	  fis.close();
	} catch(java.io.IOException e) {
	  LogLog.error("Could not close ["+filename+"].", e);
	}
      }
    }
  }
  

  public void doConfigure(URL url, LoggerRepository repository) {
    try {
      doConfigure(url.openStream(), repository);
    } catch(IOException e) {
      LogLog.error("Could not open ["+url+"].", e);
    }
  }

  /**
     Configure log4j by reading in a log4j.dtd compliant XML
     configuration file.

  */
  public void doConfigure(InputStream inputStream, LoggerRepository repository) 
                                          throws FactoryConfigurationError {
    doConfigure(new InputSource(inputStream), repository);
  }

  /**
     Configure log4j by reading in a log4j.dtd compliant XML
     configuration file.

  */
  public
  void doConfigure(Reader reader, LoggerRepository repository) 
                                          throws FactoryConfigurationError {
    doConfigure(new InputSource(reader), repository);
  }

  /**
     Configure log4j by reading in a log4j.dtd compliant XML
     configuration file.

  */
  protected
  void doConfigure(InputSource inputSource, LoggerRepository repository) 
                                          throws FactoryConfigurationError {
    DocumentBuilderFactory dbf = null;
    this.repository = repository;
    try { 
      LogLog.debug("System property ["+dbfKey+"] is: "+
  	                        OptionConverter.getSystemProperty(dbfKey, 
								  null)); 
      dbf = DocumentBuilderFactory.newInstance();
      LogLog.debug("Search for the standard DocumentBuilderFactory succeded.");
      LogLog.debug("DocumentBuilderFactory is: "+dbf.getClass().getName());
    } catch(FactoryConfigurationError fce) {
      Exception e = fce.getException();
      LogLog.debug("Could not instantiate a DocumentBuilderFactory.", e);
      throw fce;
    }
      
    try {
      dbf.setValidating(true);

      DocumentBuilder docBuilder = dbf.newDocumentBuilder();
      docBuilder.setErrorHandler(new SAXErrorHandler());      
      docBuilder.setEntityResolver(new Log4jEntityResolver());        
      // we change the system ID to a valid URI so that Crimson won't
      // complain. Indeed, "log4j.dtd" alone is not a valid URI which
      // causes Crimson to barf. The Log4jEntityResolver only cares
      // about the "log4j.dtd" ending.
      inputSource.setSystemId("dummy://log4j.dtd");
      Document doc = docBuilder.parse(inputSource); 
      parse(doc.getDocumentElement());
    } catch (Exception e) {
      // I know this is miserable...
      LogLog.error("Could not parse input source ["+inputSource+"].", e);
    }
  }

    /**
       Configure by taking in an DOM element. 
     */
    public void doConfigure(Element element, LoggerRepository repository) {
	this.repository = repository;
	parse(element);
    }

  
  /**
     A static version of {@link #doConfigure(String, LoggerRepository)}.  */
  static public void configure(String filename) throws FactoryConfigurationError {
    new DOMConfigurator().doConfigure(filename, 
				      LogManager.getLoggerRepository());
  }

  /**
     A static version of {@link #doConfigure(URL, LoggerRepository)}.
   */
  static
  public
  void configure(URL url) throws FactoryConfigurationError {
    new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
  }

  /**
     Used internally to configure the log4j framework by parsing a DOM
     tree of XML elements based on <a
     href="doc-files/log4j.dtd">log4j.dtd</a>.
     
  */
  protected void parse(Element element) {

    String rootElementName = element.getTagName();

    if (!rootElementName.equals(CONFIGURATION_TAG)) {
      if(rootElementName.equals(OLD_CONFIGURATION_TAG)) {
	LogLog.warn("The <"+OLD_CONFIGURATION_TAG+
		     "> element has been deprecated.");
	LogLog.warn("Use the <"+CONFIGURATION_TAG+"> element instead.");
      } else {
	LogLog.error("DOM element is - not a <"+CONFIGURATION_TAG+"> element.");
	return;
      }
    }

    String debugAttrib = subst(element.getAttribute(INTERNAL_DEBUG_ATTR));
      
    LogLog.debug("debug attribute= \"" + debugAttrib +"\".");
    // if the log4j.dtd is not specified in the XML file, then the
    // "debug" attribute is returned as the empty string.
    if(!debugAttrib.equals("") && !debugAttrib.equals("null")) {      
      LogLog.setInternalDebugging(OptionConverter.toBoolean(debugAttrib, true));
    } else {
      LogLog.debug("Ignoring " + INTERNAL_DEBUG_ATTR + " attribute.");
    }

    String confDebug = subst(element.getAttribute(CONFIG_DEBUG_ATTR));
    if(!confDebug.equals("") && !confDebug.equals("null")) {      
      LogLog.warn("The \""+CONFIG_DEBUG_ATTR+"\" attribute is deprecated.");
      LogLog.warn("Use the \""+INTERNAL_DEBUG_ATTR+"\" attribute instead.");
      LogLog.setInternalDebugging(OptionConverter.toBoolean(confDebug, true));
    }

    String thresholdStr = subst(element.getAttribute(THRESHOLD_ATTR));
    LogLog.debug("Threshold =\"" + thresholdStr +"\".");
    if(!"".equals(thresholdStr) && !"null".equals(thresholdStr)) {
      repository.setThreshold(thresholdStr);
    }

    //Hashtable appenderBag = new Hashtable(11);

    /* Building Appender objects, placing them in a local namespace
       for future reference */

    // First configure each category factory under the root element.
    // Category factories need to be configured before any of
    // categories they support.
    //
    String   tagName = null;
    Element  currentElement = null;
    Node     currentNode = null;
    NodeList children = element.getChildNodes();
    final int length = children.getLength();

    for (int loop = 0; loop < length; loop++) {
      currentNode = children.item(loop);
      if (!isElement(currentNode)) {
        continue;
      }
      currentElement = (Element) currentNode;
      tagName = currentElement.getTagName();
      
      if (tagName.equals(CATEGORY) || tagName.equals(LOGGER)) {
        parseCategory(currentElement);
      } else if (tagName.equals(ROOT_TAG)) {
        parseRoot(currentElement);
      } else if(tagName.equals(RENDERER_TAG)) {
        parseRenderer(currentElement);
      } else if (tagName.equals(PLUGIN_TAG)) {
        Plugin plugin = parsePlugin(currentElement);
        if (plugin != null) {
          PluginRegistry.startPlugin(plugin, repository);
        }
      }
    }
        
    // let listeners know the configuration just changed
    repository.fireConfigurationChangedEvent();
  }

  
  protected String subst(String value) {
    try {
      return OptionConverter.substVars(value, props);
    } catch(IllegalArgumentException e) {
      LogLog.warn("Could not perform variable substitution.", e);
      return value;
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
    new DOMConfigurator().doConfigure(filename, 
				      LogManager.getLoggerRepository());
  }
}
