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

package org.apache.log4j.chainsaw;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.rule.AbstractRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.LoggingEvent;


/**
 * A mediator class that implements the Rule interface, by combining several optional
 * rules used by Chainsaw's filtering GUI's into a single Rule.
 *
 * This class is based upon the concept of Inclusion, Exclusion and Refinement.
 * By default, this class accepts all events by returning true as part of the Rule interface, unless
 * the Inclusion/Exclusion/Refinement sub-rules have been configured.
 *
 * The sub-rules are queried in this order: Inclusion, Refinement, Exclusion.  If any are null, that particular
 * sub-rule is not queried.  If any of the sub-rules returns false, this mediator returns false immediately, otherwise
 * they are queried in that order to ensure the overall rule evaluates.
 *
 * Setting the individual sub-rules propagates a PropertyChangeEvent as per standard Java beans principles.
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class RuleMediator extends AbstractRule implements Rule {
  private Rule inclusionRule;
  private Rule loggerRule;
  private Rule refinementRule;
  private Rule exclusionRule;
  private final PropertyChangeListener ruleChangerNotifier =
    new RuleChangerNotifier();

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.rule.Rule#evaluate(org.apache.log4j.spi.LoggingEvent)
   */
  public boolean evaluate(LoggingEvent e) {
    boolean accepts = true;

    if (inclusionRule != null) {
      accepts = inclusionRule.evaluate(e);
    }

    if (!accepts) {
      return false;
    }

    if (loggerRule != null) {
      accepts = loggerRule.evaluate(e);
    }

    if (!accepts) {
      return false;
    }

    if (refinementRule != null) {
      accepts = refinementRule.evaluate(e);
    }

    if (!accepts) {
      return false;
    }

    if (exclusionRule != null) {
      accepts = exclusionRule.evaluate(e);
    }

    return accepts;
  }

  /**
   * Sets the Inclusion rule to be used, and fires a PropertyChangeEvent to listeners
   * @param r
   */
  public void setInclusionRule(Rule r) {
    Rule oldRule = this.inclusionRule;
    this.inclusionRule = r;
    firePropertyChange("inclusionRule", oldRule, this.inclusionRule);
  }

  /**
   * Sets the Refinement rule to be used, and fires a PropertyChangeEvent to listeners
   * @param r
   */
  public void setRefinementRule(Rule r) {
    Rule oldRefinementRule = this.refinementRule;
    this.refinementRule = r;
    firePropertyChange(
      "refinementRule", oldRefinementRule, this.refinementRule);
  }

  public void setLoggerRule(Rule r) {
    Rule oldLoggerRule = this.loggerRule;
    this.loggerRule = r;
    if(oldLoggerRule!=null){
      oldLoggerRule.removePropertyChangeListener(ruleChangerNotifier);
    }
    this.loggerRule.addPropertyChangeListener(ruleChangerNotifier);
    firePropertyChange("loggerRule", oldLoggerRule, this.loggerRule);
  }

  /**
   * Sets the Exclusion rule to be used, and fires a PropertyChangeEvent to listeners.
   *
   * @param r
   */
  public void setExclusionRule(Rule r) {
    Rule oldExclusionRule = this.exclusionRule;
    this.exclusionRule = r;
    firePropertyChange("exclusionRule", oldExclusionRule, this.exclusionRule);
  }

  /**
   * @return exclusion rule
   */
  public final Rule getExclusionRule() {
    return exclusionRule;
  }

  /**
   * @return inclusion rule
   */
  public final Rule getInclusionRule() {
    return inclusionRule;
  }

  /**
   * @return logger rule
   */
  public final Rule getLoggerRule() {
    return loggerRule;
  }

  /**
   * @return refinement rule
   */
  public final Rule getRefinementRule() {
    return refinementRule;
  }

  /**
   * Helper class that propagates internal Rules propertyChange events
   * to external parties, since an internal rule changing really means
   * this outter rule is going to change too.
   */
  private class RuleChangerNotifier implements PropertyChangeListener {
    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
      RuleMediator.this.firePropertyChange(evt);
    }
  }
}
