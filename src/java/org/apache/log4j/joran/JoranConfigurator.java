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

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.Interpreter;
import org.apache.joran.Pattern;
import org.apache.joran.RuleStore;
import org.apache.joran.action.ParamAction;
import org.apache.joran.helper.SimpleRuleStore;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.joran.action.ActionConst;
import org.apache.log4j.joran.action.AppenderAction;
import org.apache.log4j.joran.action.AppenderRefAction;
import org.apache.log4j.joran.action.ConversionRuleAction;
import org.apache.log4j.joran.action.LayoutAction;
import org.apache.log4j.joran.action.LevelAction;
import org.apache.log4j.joran.action.LoggerAction;
import org.apache.log4j.joran.action.RootLoggerAction;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JoranConfigurator implements Configurator {
  Interpreter joranInterpreter;

  public JoranConfigurator() {
    selfInitialize();
  }

  public void doConfigure(URL url, LoggerRepository repository) {
    ExecutionContext ec = joranInterpreter.getExecutionContext();
    ec.pushObject(repository);

    String errMsg;

    try {
      InputStream in = url.openStream();
      doConfigure(in, repository);
      in.close();
    } catch (IOException ioe) {
      errMsg = "Could not open [" + url + "].";
      LogLog.error(errMsg, ioe);
      ec.addError(new ErrorItem(errMsg, null, ioe));
    }
  }

  protected void selfInitialize() {
    RuleStore rs = new SimpleRuleStore();
    rs.addRule(new Pattern("log4j:configuration/logger"), new LoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/root"), new RootLoggerAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/level"), new LevelAction());
    rs.addRule(
      new Pattern("log4j:configuration/logger/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("log4j:configuration/root/appender-ref"),
      new AppenderRefAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender"), new AppenderAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender/layout"), new LayoutAction());
    rs.addRule(
      new Pattern("log4j:configuration/appender/layout/conversionRule"),
      new ConversionRuleAction());

    rs.addRule(new Pattern("*/param"), new ParamAction());

    Interpreter jp = new Interpreter(rs);
    ExecutionContext ec = jp.getExecutionContext();

    HashMap omap = ec.getObjectMap();
    omap.put(ActionConst.APPENDER_BAG, new HashMap());
  }

  public void doConfigure(InputStream in, LoggerRepository repository) {
    ExecutionContext ec = joranInterpreter.getExecutionContext();

    ec.pushObject(repository);

    String errMsg;

    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser saxParser = spf.newSAXParser();
      saxParser.parse("file:input/joran/parser2.xml", joranInterpreter);
    } catch (SAXException e) {
      // all exceptions should have been recorded already.
    } catch (ParserConfigurationException pce) {
      errMsg = "Parser configuration error occured";
      LogLog.error(errMsg, pce);
      ec.addError(new ErrorItem(errMsg, null, pce));
    } catch (IOException ie) {
      errMsg = "I/O error occured while parsing xml file";
      ec.addError(
        new ErrorItem("Parser configuration error occured", null, ie));
    }
  }
}
