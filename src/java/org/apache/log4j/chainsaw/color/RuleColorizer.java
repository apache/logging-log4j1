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

package org.apache.log4j.chainsaw.color;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.chainsaw.rule.ColorRule;
import org.apache.log4j.spi.LoggingEvent;


/**
 * A colorizer supporting an ordered collection of ColorRules, including support for notification of
 * color rule changes via a propertyChangeListener and the 'colorrule' property.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class RuleColorizer implements Colorizer {
  private Map ruleMap = new HashMap();
  private final PropertyChangeSupport colorChangeSupport =
    new PropertyChangeSupport(this);

  public RuleColorizer() {
  }
  
  public void setColors(Map ruleMap) {
  	this.ruleMap = ruleMap;
  }
  
  public Map getColors() {
  	return ruleMap;
  }

  public void addRules(Map rules) {
  	Iterator iter = rules.entrySet().iterator();
  	while (iter.hasNext()) {
  		Map.Entry entry = (Map.Entry)iter.next();
  		ruleMap.put(entry.getKey(), entry.getValue());
  	}
    colorChangeSupport.firePropertyChange("colorrule", false, true);
  }

  public void addRule(String expression, ColorRule rule) {
    ruleMap.put(expression, rule);
    colorChangeSupport.firePropertyChange("colorrule", false, true);
  }

  public void clear() {
    ruleMap.clear();
  }

  public void removeRule(String expression) {
    ruleMap.remove(expression);
  }

  public Color getBackgroundColor(LoggingEvent event) {
  	Iterator iter = ruleMap.values().iterator();
  	while (iter.hasNext()) {
      ColorRule rule = (ColorRule) iter.next();

      if ((rule.getBackgroundColor() != null) && (rule.evaluate(event))) {
        return rule.getBackgroundColor();
      }
    }

    return null;
  }

  public Color getForegroundColor(LoggingEvent event) {
	Iterator iter = ruleMap.values().iterator();
	while (iter.hasNext()) {
	  ColorRule rule = (ColorRule) iter.next();

      if ((rule.getForegroundColor() != null) && (rule.evaluate(event))) {
        return rule.getForegroundColor();
      }
    }

    return null;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    colorChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    colorChangeSupport.removePropertyChangeListener(listener);
  }
}
