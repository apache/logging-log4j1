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

import org.apache.joran.ExecutionContext;
import org.apache.joran.Interpreter;
import org.apache.joran.Pattern;
import org.apache.joran.RuleStore;
import org.apache.joran.action.NestComponentIA;
import org.apache.joran.action.NewRuleAction;
import org.apache.joran.action.ParamAction;
import org.apache.joran.helper.SimpleRuleStore;

import org.apache.log4j.config.ConfiguratorBase;
import org.apache.log4j.joran.action.ActionConst;
import org.apache.log4j.joran.action.AppenderAction;
import org.apache.log4j.joran.action.AppenderRefAction;
import org.apache.log4j.joran.action.ConfigurationAction;
import org.apache.log4j.joran.action.ConversionRuleAction;
import org.apache.log4j.joran.action.JndiSubstitutionPropertyAction;
import org.apache.log4j.joran.action.LayoutAction;
import org.apache.log4j.joran.action.LevelAction;
import org.apache.log4j.joran.action.LoggerAction;
import org.apache.log4j.joran.action.PluginAction;
import org.apache.log4j.joran.action.PriorityAction;
import org.apache.log4j.joran.action.RepositoryPropertyAction;
import org.apache.log4j.joran.action.RootLoggerAction;
import org.apache.log4j.joran.action.SubstitutionPropertyAction;
import org.apache.log4j.joran.util.JoranDocument;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 *
 * A JoranConfigurator instance should not be used more than once to
 * configure a LoggerRepository.
 *
 * @author Curt Arnold
 * @author Ceki G&uuml;lc&uuml;  
 */
public class JoranConfigurator extends ConfiguratorBase {
  Interpreter joranInterpreter;
  LoggerRepository repository;
  boolean listAppnderAttached = false;

  public JoranConfigurator() {
  }
  
  protected interface ParseAction {
      void parse(final SAXParser parser, final DefaultHandler handler) throws SAXException, IOException;
  }
  
  final public void doConfigure(final URL url, final LoggerRepository repository) {
      ParseAction action = new ParseAction() {
          public void parse(final SAXParser parser, final DefaultHandler handler) throws SAXException, IOException {
              parser.parse(url.toString(), handler);
          }
      };
      doConfigure(action, repository);
  }
  
  final public void doConfigure(final String filename, final LoggerRepository repository) {
      ParseAction action = new ParseAction() {
          public void parse(final SAXParser parser, final DefaultHandler handler) throws SAXException, IOException {
              parser.parse(new File(filename), handler);
          }
      };
      doConfigure(action, repository);
  }

  final public void doConfigure(final File file, final LoggerRepository repository) {
      ParseAction action = new ParseAction() {
          public void parse(final SAXParser parser, final DefaultHandler handler) throws SAXException, IOException {
              parser.parse(file, handler);
          }
      };
      doConfigure(action, repository);
  }

  final public void doConfigure(final InputSource source, final LoggerRepository repository) {
      ParseAction action = new ParseAction() {
          public void parse(final SAXParser parser, final DefaultHandler handler) throws SAXException, IOException {
              parser.parse(source, handler);
          }
      };
      doConfigure(action, repository);
  }

  final public void doConfigure(final InputStream stream, final LoggerRepository repository) {
      ParseAction action = new ParseAction() {
          public void parse(final SAXParser parser, final DefaultHandler handler) throws SAXException, IOException {
              parser.parse(stream, handler);
          }
      };
      doConfigure(action, repository);
  }
  
  protected void doConfigure(final ParseAction action, final LoggerRepository repository) {
    // This line is needed here because there is logging from inside this method.
    this.repository = repository;
    selfInitialize(this.repository);
    
    ExecutionContext ec = joranInterpreter.getExecutionContext();
    List errorList = ec.getErrorList();

    SAXParser saxParser = null;
    try {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(false);
        spf.setNamespaceAware(true);
        saxParser = spf.newSAXParser();
    } catch (Exception pce) {
      final String errMsg = "Parser configuration error occured";
      getLogger(repository).error(errMsg, pce);
      ec.addError(new ErrorItem(errMsg, pce));
      return;
    }
    
    JoranDocument document = new JoranDocument(errorList);
    
    try {
        action.parse(saxParser, document);
    } catch(IOException ie) {
      final String errMsg = "I/O error occured while parsing xml file";
      getLogger(repository).error(errMsg, ie);
      ec.addError(new ErrorItem(errMsg, ie));
    } catch (Exception ex) {
      final String errMsg = "Problem parsing XML document. See previously reported errors. Abandoning all further processing.";
      getLogger(repository).error(errMsg, ex);
      errorList.add(
        new ErrorItem(errMsg));
      return;
    }
    
    ec.pushObject(repository);
    String errMsg;
    try {
      attachListAppender(repository);
      
      document.replay(joranInterpreter);

      getLogger(repository).debug("Finished parsing.");
    } catch (SAXException e) {
      // all exceptions should have been recorded already.
    } finally {
      detachListAppender(repository);
    }
    
    
  }

  public List getErrorList() {
    return getExecutionContext().getErrorList();
  }


  protected void selfInitialize(LoggerRepository repository) {
    RuleStore rs = new SimpleRuleStore(repository);
    rs.addRule(new Pattern("configuration"), new ConfigurationAction());
    rs.addRule(
      new Pattern("configuration/substitutionProperty"),
      new SubstitutionPropertyAction());
    rs.addRule(
      new Pattern("configuration/repositoryProperty"),
      new RepositoryPropertyAction());
    rs.addRule(
        new Pattern("configuration/conversionRule"),
        new ConversionRuleAction());
    rs.addRule(new Pattern("configuration/plugin"), new PluginAction());
    rs.addRule(new Pattern("configuration/logger"), new LoggerAction());
    rs.addRule(
      new Pattern("configuration/logger/level"), new LevelAction());
    rs.addRule(
      new Pattern("configuration/logger/priority"), new PriorityAction());
    rs.addRule(
      new Pattern("configuration/root"), new RootLoggerAction());
    rs.addRule(
      new Pattern("configuration/root/level"), new LevelAction());
    rs.addRule(
      new Pattern("configuration/root/priority"), new PriorityAction());
    rs.addRule(
      new Pattern("configuration/logger/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("configuration/root/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("configuration/appender"), new AppenderAction());
    rs.addRule(new Pattern("configuration/appender/appender-ref"), 
        new AppenderRefAction());
    rs.addRule(
      new Pattern("configuration/appender/layout"), new LayoutAction());
    rs.addRule( 
         new Pattern("configuration/jndiSubstitutionProperty"), 
         new JndiSubstitutionPropertyAction());
    rs.addRule(
      new Pattern("configuration/newRule"), new NewRuleAction());
    rs.addRule(new Pattern("*/param"), new ParamAction());

    joranInterpreter = new Interpreter(rs);

    // The following line adds the capability to parse nested components
    joranInterpreter.addImplicitAction(new NestComponentIA());
    ExecutionContext ec = joranInterpreter.getExecutionContext();

    Map omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
  }

  public ExecutionContext getExecutionContext() {
    return joranInterpreter.getExecutionContext();
  }
}
