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
package org.apache.log4j.chainsaw;

import org.apache.log4j.chainsaw.rule.AbstractRule;
import org.apache.log4j.chainsaw.rule.Rule;
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
	
	/* (non-Javadoc)
	 * @see org.apache.log4j.chainsaw.rule.Rule#evaluate(org.apache.log4j.spi.LoggingEvent)
	 */
	public boolean evaluate(LoggingEvent e) {
		boolean accepts = true;
		
		if(inclusionRule!=null) {
			accepts = inclusionRule.evaluate(e);
		}
		if(!accepts) return false;
		if(loggerRule!=null) {
			accepts = loggerRule.evaluate(e);
		}
		if(!accepts) return false;
		if(refinementRule!=null) {
			accepts = refinementRule.evaluate(e);
		}
		if(!accepts) return false;
		
		if(exclusionRule!=null) {
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
		firePropertyChange("refinementRule", oldRefinementRule, this.refinementRule);
	}
	
	public void setLoggerRule(Rule r) {
		Rule oldLoggerRule = this.loggerRule;
		this.loggerRule = r;
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
	 * @return
	 */
	public final Rule getExclusionRule() {
		return exclusionRule;
	}

	/**
	 * @return
	 */
	public final Rule getInclusionRule() {
		return inclusionRule;
	}

	/**
	 * @return
	 */
	public final Rule getLoggerRule() {
		return loggerRule;
	}

	/**
	 * @return
	 */
	public final Rule getRefinementRule() {
		return refinementRule;
	}

}
