/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.joran;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.Interpreter;
import org.apache.joran.Pattern;
import org.apache.joran.RuleStore;
import org.apache.joran.action.NestComponentIA;
import org.apache.joran.action.NewRuleAction;
import org.apache.joran.action.ParamAction;
import org.apache.joran.helper.SimpleRuleStore;
import org.apache.log4j.Logger;
//import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.joran.action.ActionConst;
import org.apache.log4j.joran.action.AppenderAction;
import org.apache.log4j.joran.action.AppenderRefAction;
import org.apache.log4j.joran.action.ConfigurationAction;
import org.apache.log4j.joran.action.ConversionRuleAction;
import org.apache.log4j.joran.action.LayoutAction;
import org.apache.log4j.joran.action.LevelAction;
import org.apache.log4j.joran.action.LoggerAction;
import org.apache.log4j.joran.action.PluginAction;
import org.apache.log4j.joran.action.PriorityAction;
import org.apache.log4j.joran.action.RepositoryPropertyAction;
import org.apache.log4j.joran.action.RootLoggerAction;
import org.apache.log4j.joran.action.SubstitutionPropertyAction;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * A JoranConfigurator instance should not be used more than once to 
 * configure a LoggerRepository.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class JoranConfigurator
       implements Configurator {

  Interpreter joranInterpreter;
  LoggerRepository repository;
  
  // The logger will be retreived form the logger repository being
  // configured
  private Logger logger;
  
  public JoranConfigurator() {
    selfInitialize();
  }

  public void doConfigure(URL url, LoggerRepository repository) {
    // This line is needed here because there is logging from inside this method.
    this.repository = repository;

    ExecutionContext ec = joranInterpreter.getExecutionContext();
    String errMsg;
    try {
      InputStream in = url.openStream();
      doConfigure(in, repository);
      in.close();
    } catch (IOException ioe) {
      errMsg = "Could not open [" + url + "].";
      getLogger().error(errMsg, ioe);
      ec.addError(new ErrorItem(errMsg, ioe));
    }
  }

  /**
   * Configure a repository from a configuration file passed as parameter.
   */
  public void doConfigure(String filename, LoggerRepository repository) {
    // This line is needed here because there is logging from inside this method.
    this.repository = repository;
    
    FileInputStream fis = null;
    ExecutionContext ec = joranInterpreter.getExecutionContext();
    getLogger().info("in JoranConfigurator doConfigure "+filename);
    try {
      fis = new FileInputStream(filename);
      doConfigure(fis, repository);
    } catch (IOException ioe) {
      String errMsg = "Could not open [" + filename + "].";
      getLogger().error(errMsg, ioe);
      ec.addError(new ErrorItem(errMsg, ioe));
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (java.io.IOException e) {
          getLogger().error("Could not close [" + filename + "].", e);
        }
      }
    }
  }
  
  /**
   * Configure a repository from the input stream passed as parameter
   */
  public void doConfigure(InputStream in, LoggerRepository repository) {
    doConfigure(new InputSource(in), repository);
  }

  /**
   * All doConfigure methods evenually call this form.
   * */  
  public void doConfigure(InputSource inputSource, LoggerRepository repository) {
    this.repository = repository;
    ExecutionContext ec = joranInterpreter.getExecutionContext();
    ec.pushObject(repository);
    String errMsg;
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser saxParser = spf.newSAXParser();

      // we change the system ID to a valid URI so that Crimson won't
      // complain. Indeed, "log4j.dtd" alone is not a valid URI which
      // causes Crimson to barf. The Log4jEntityResolver only cares
      // about the "log4j.dtd" ending.
      inputSource.setSystemId("dummy://log4j.dtd");
      
      saxParser.parse(inputSource, joranInterpreter);
    } catch (SAXException e) {
      // all exceptions should have been recorded already.
    } catch (ParserConfigurationException pce) {
      errMsg = "Parser configuration error occured";
      getLogger().error(errMsg, pce);
      ec.addError(new ErrorItem(errMsg, pce));
    } catch (IOException ie) {
      errMsg = "I/O error occured while parsing xml file";
      ec.addError(new ErrorItem("Parser configuration error occured", ie));
    }
  }


  protected void selfInitialize() {
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("log4j:configuration"), new ConfigurationAction());
    rs.addRule(new Pattern("log4j:configuration/substitutionProperty"), new SubstitutionPropertyAction());
    rs.addRule(new Pattern("log4j:configuration/repositoryProperty"), new RepositoryPropertyAction());
    rs.addRule(new Pattern("log4j:configuration/plugin"), new PluginAction());
    rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
    rs.addRule(new Pattern("log4j:configuration/logger/level"), new LevelAction());
    rs.addRule(new Pattern("log4j:configuration/logger/priority"), new PriorityAction());
    rs.addRule(new Pattern("log4j:configuration/root"), new RootLoggerAction());
    rs.addRule(new Pattern("log4j:configuration/root/level"), new LevelAction());
    rs.addRule(new Pattern("log4j:configuration/root/priority"), new PriorityAction());
    rs.addRule(new Pattern("log4j:configuration/logger/appender-ref"), new AppenderRefAction());
    rs.addRule(new Pattern("log4j:configuration/root/appender-ref"), new AppenderRefAction());
    rs.addRule(new Pattern("log4j:configuration/appender"), new AppenderAction());
    rs.addRule(new Pattern("log4j:configuration/appender/layout"), new LayoutAction());
    rs.addRule(new Pattern("log4j:configuration/appender/layout/conversionRule"), new ConversionRuleAction());
    rs.addRule(new Pattern("log4j:configuration/newRule"), new NewRuleAction());
    rs.addRule(new Pattern("*/param"), new ParamAction());

    joranInterpreter = new Interpreter(rs);
    joranInterpreter.setEntityResolver(new Log4jEntityResolver());
    
    // The following line adds the capability to parse nested components
    joranInterpreter.addImplcitAction(new NestComponentIA());
    ExecutionContext ec = joranInterpreter.getExecutionContext();

    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
  }
  
  public ExecutionContext getExecutionContext() {
    return joranInterpreter.getExecutionContext();
  }
  
  public void logErrors() {
    List errorList = getExecutionContext().getErrorList();
    if(errorList.size() == 0) {
      return;
    }
    getLogger().warn("Errors occured while parsing the XML configuration file");
    for(int i = 0; i < errorList.size(); i++) {
      getLogger().warn(""+errorList.get(i));
    }
  }
  
  Logger getLogger() {
    if(logger == null) {
      logger = repository.getLogger(JoranConfigurator.class.getName());
    }
    return logger;
  }
  
}

