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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class JoranParser {
  static final Logger logger = Logger.getLogger(JoranParser.class);
  private RuleStore ruleStore;
  private ExecutionContext ec;
  private ArrayList implicitActions;
  
  JoranParser(RuleStore rs) {
    ruleStore = rs;
    ec = new ExecutionContext(this);
    implicitActions = new ArrayList(3);
  }

  public ExecutionContext getExecutionContext() {
    return ec;
  }

  public void addImplcitAction(ImplicitAction ia) {
    implicitActions.add(ia);
  }
  
  public void parse(Document document) {
    Pattern currentPattern = new Pattern();
    Element e = document.getDocumentElement();
    loop(e, currentPattern);
  }

  public void loop(Node n, Pattern currentPattern) {
    if (n == null) {
      return;
    }


    //logger.debug("Node type is "+n.getNodeType()+", name is "+n.getNodeName()+", value "+n.getNodeValue());

       
    try {
     // Element currentElement = (Element) n;
            
      currentPattern.push(n.getNodeName());
      // only print the pattern for ELEMENT NODES
      if(n.getNodeType() == Node.ELEMENT_NODE) {
        logger.debug("pattern is " + currentPattern);
      }
      List applicableActionList = ruleStore.matchActions(currentPattern);

      //logger.debug("set of applicable patterns: " + applicableActionList);

      if (applicableActionList == null) {
        if(n instanceof Element)
        applicableActionList = lookupImplicitAction((Element)n, ec);
      }

      if (applicableActionList != null) {
        callBeginAction(applicableActionList, n);
      }

      if (n.hasChildNodes()) {
        for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling()) {
          loop(c, currentPattern);
        }
      }

      if (applicableActionList != null) {
        callEndAction(applicableActionList, n);
      }
    } finally {
      currentPattern.pop();
    }
  }

  /**
   * Check if any implicit actions are applicable. As soon as an applicable
   * action is found, it is returned. Thus, the returned list will have at most
   * one element.
   */
  List lookupImplicitAction(Element element, ExecutionContext ec) {
    int len = implicitActions.size();
    for(int i = 0; i < len; i++) {
      ImplicitAction ia = (ImplicitAction) implicitActions.get(i);
      if(ia.isApplicable(element, ec)) {
        List actionList = new ArrayList(1);
        actionList.add(ia);
        return actionList;
      }
      
    }
    return null;
  }

  void callBeginAction(List applicableActionList, Node n) {
    if (applicableActionList == null) {
      return;
    }

    short type = n.getNodeType();

    if (type != Node.ELEMENT_NODE) {
      return;
    }

    Element e = (Element) n;
    String localName = n.getNodeName();

    Iterator i = applicableActionList.iterator();

    while (i.hasNext()) {
      Action action = (Action) i.next();
      action.begin(ec, e);
    }
  }

  void callEndAction(List applicableActionList, Node n) {
    if (applicableActionList == null) {
      return;
    }

    short type = n.getNodeType();

    if (type != Node.ELEMENT_NODE) {
      return;
    }

    Element e = (Element) n;
    String localName = n.getNodeName();
    //logger.debug("About to call end actions on node: <" + localName + ">");

    Iterator i = applicableActionList.iterator();

    while (i.hasNext()) {
      Action action = (Action) i.next();
      action.end(ec, e);
    }
  }

  public RuleStore getRuleStore() {
    return ruleStore;
  }

  public void setRuleStore(RuleStore ruleStore) {
    this.ruleStore = ruleStore;
  }

}
