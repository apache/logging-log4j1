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

package org.apache.joran.helper;

import org.apache.joran.*;
import org.apache.joran.RuleStore;
import org.apache.joran.action.*;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class SimpleRuleStore implements RuleStore {
  final static Logger logger = Logger.getLogger(SimpleRuleStore.class);

  HashMap rules = new HashMap();

  public void addRule(Pattern pattern, Action action) {
    //System.out.println("pattern to add is:" + pattern + "hashcode:" + pattern.hashCode());
    List a4p = (List) rules.get(pattern);

    if (a4p == null) {
      a4p = new ArrayList();
      rules.put(pattern, a4p);
    }

    a4p.add(action);
  }

  public void addRule(Pattern pattern, String actionClassName) {
    Action action =
      (Action) OptionConverter.instantiateByClassName(
        actionClassName, Action.class, null);

    if (action != null) {
      addRule(pattern, action);
    } else {
      logger.warn("Could not intantiate Action of class ["+actionClassName+"].");
    }
  }

  public List matchActions(Pattern pattern) {
    //System.out.println("pattern to search for:" + pattern + ", hashcode: " + pattern.hashCode());
    //System.out.println("rules:" + rules);
    ArrayList a4p = (ArrayList) rules.get(pattern);

    if (a4p != null) {
      return a4p;
    } else {
      Iterator patternsIterator = rules.keySet().iterator();
      int max = 0;
      Pattern longestMatch = null;

      while (patternsIterator.hasNext()) {
        Pattern p = (Pattern) patternsIterator.next();

        if ((p.size() > 1) && p.get(0).equals("*")) {
          int r = pattern.tailMatch(p);

          //System.out.println("tailMatch " +r);
          if (r > max) {
            //System.out.println("New longest match "+p);
            max = r;
            longestMatch = p;
          }
        }
      }

      if (longestMatch != null) {
        return (ArrayList) rules.get(longestMatch);
      } else {
        return null;
      }
    }
  }
}
