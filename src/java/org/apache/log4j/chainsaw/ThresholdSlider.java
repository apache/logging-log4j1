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

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JSlider;


/**
 * A Slider implementation that allows a user to
 * choose a particular Threshold
 * .
 * @author Paul Smith <psmith@apache.org>
 *
 */
final class ThresholdSlider extends JSlider {
  final List priorityList;
  ThresholdSlider() {
    
    Priority[] priorities =
      new Level[] {
        Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO,
        Level.DEBUG, Level.ALL
      };

    priorityList = Arrays.asList(priorities);

    Collections.sort(
      priorityList,
      new Comparator() {
        public int compare(Object o1, Object o2) {
          Priority p1 = (Priority) o1;
          Priority p2 = (Priority) o2;

          if (p1.toInt() == p2.toInt()) {
            return 0;
          } else if (p1.toInt() < p2.toInt()) {
            return -1;
          }

          return 1;
        }
      });

    Level min = (Level) priorityList.get(0);
    Level max = (Level) priorityList.get(priorityList.size() - 1);

    setModel(
      new DefaultBoundedRangeModel(
        priorityList.indexOf(Level.DEBUG), 0, 0, priorityList.size() - 1));

        
    Hashtable labelMap = new Hashtable();
    for (Iterator iter = priorityList.iterator(); iter.hasNext();) {
      Priority item = (Priority) iter.next();
      labelMap.put(new Integer(priorityList.indexOf(item)), new JLabel(item.toString()));
//      System.out.println("creating levels for :: " + item.toInt() + "," + item.toString());
    }
    
    setOrientation(JSlider.VERTICAL);
    setInverted(true);
    setLabelTable(labelMap);

    setPaintLabels(true);
//    setPaintTicks(true);
    setSnapToTicks(true);
//    setMajorTickSpacing(10000);
    
//    setPaintTrack(true);
    
  }
  
  void setChosenLevel(Level level){
    setValue(priorityList.indexOf(level));
  }
  
  /**
   * Returns the Log4j Level that is currently selected in this slider
   * @return
   */
  Level getSelectedLevel() {
    Level level = (Level) priorityList.get(getValue());
    
    if(level==null){
      level = Level.DEBUG;
    }
    return level;
  }
}
