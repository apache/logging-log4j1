/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
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
