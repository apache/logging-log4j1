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

package org.apache.joran;

import org.apache.joran.action.*;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

// TODO Errors should be reported in Error objects instead of just strings.
// TODO Interpreter should set its own ErrorHander for XML parsing errors.

public class Interpreter extends DefaultHandler {
  static final Logger logger = Logger.getLogger(Interpreter.class);
  private static List EMPTY_LIST = new Vector(0);
  
  private RuleStore ruleStore;
  private ExecutionContext ec;
  private ArrayList implicitActions;
  Pattern pattern;
  Locator locator;
  /**
   * The <code>actionListStack</code> contains a list of actions that are 
   * executing for the given XML element.
   * 
   * A list of actions is pushed by the {link #startElement} and popped by
   * {@link #endElement}. 
   * 
   */
  Stack actionListStack;
  
  Interpreter(RuleStore rs) {
    ruleStore = rs;
    ec = new ExecutionContext(this);
    implicitActions = new ArrayList(3);
    pattern = new Pattern();
    actionListStack = new Stack();
  }

  public ExecutionContext getExecutionContext() {
    return ec;
  }

  public void startDocument() {
    System.out.println(" in JP startDocument");
  }

  public void startElement(
    String namespaceURI, String localName, String qName, Attributes atts) {
    String x = null;
 
    String tagName = getTagName(localName, qName);

    logger.debug("in startElement <" + tagName + ">");
      
    pattern.push(tagName);

    List applicableActionList = getapplicableActionList(pattern);

    if (applicableActionList != null) {
      actionListStack.add(applicableActionList);
      callBeginAction(applicableActionList, tagName, atts);
    } else {
      actionListStack.add(EMPTY_LIST);
      logger.debug("no applicable action for <"+tagName+">.");
    }
  }

  public void endElement(String namespaceURI, String localName, String qName) {
    List applicableActionList = (List) actionListStack.pop();

    if (applicableActionList != EMPTY_LIST) {
      callEndAction(applicableActionList, getTagName(localName, qName));
    }

    // given that we always push, we must also pop the pattern
    pattern.pop();
  }


  public Locator getDocumentLocator() {
    return locator;
  }
  public void setDocumentLocator(Locator l) {
    locator = l;
  }
  
  String getTagName(String localName, String qName) {
    String tagName = localName;

    if ((tagName == null) || (tagName.length() < 1)) {
      tagName = qName;
    }

    return tagName;
  }

  public void addImplcitAction(ImplicitAction ia) {
    implicitActions.add(ia);
  }

  /**
   * Check if any implicit actions are applicable. As soon as an applicable
   * action is found, it is returned. Thus, the returned list will have at most
   * one element.
   */
  List lookupImplicitAction(ExecutionContext ec, Pattern pattern) {
    int len = implicitActions.size();

    for (int i = 0; i < len; i++) {
      ImplicitAction ia = (ImplicitAction) implicitActions.get(i);

      if (ia.isApplicable(ec, pattern.peekLast())) {
        List actionList = new ArrayList(1);
        actionList.add(ia);

        return actionList;
      }
    }

    return null;
  }

  /**
   * Return the list of applicable patterns for this
  */
  List getapplicableActionList(Pattern pattern) {
    List applicableActionList = ruleStore.matchActions(pattern);

    //logger.debug("set of applicable patterns: " + applicableActionList);
    if (applicableActionList == null) {
      applicableActionList = lookupImplicitAction(ec, pattern);
    }

    return applicableActionList;
  }

  void callBeginAction(
    List applicableActionList, String tagName, Attributes atts) {
    if (applicableActionList == null) {
      return;
    }

    Iterator i = applicableActionList.iterator();

    while (i.hasNext()) {
      Action action = (Action) i.next();
      action.begin(ec, tagName, atts);
    }
  }

  void callEndAction(List applicableActionList, String tagName) {
    if (applicableActionList == null) {
      return;
    }

    //logger.debug("About to call end actions on node: <" + localName + ">");
    Iterator i = applicableActionList.iterator();

    while (i.hasNext()) {
      Action action = (Action) i.next();
      action.end(ec, tagName);
    }
  }

  public RuleStore getRuleStore() {
    return ruleStore;
  }

  public void setRuleStore(RuleStore ruleStore) {
    this.ruleStore = ruleStore;
  }
}
