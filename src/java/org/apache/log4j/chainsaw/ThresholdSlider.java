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

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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

    setModel(
      new DefaultBoundedRangeModel(
        priorityList.indexOf(Level.DEBUG), 0, 0, priorityList.size() - 1));

    Hashtable labelMap = new Hashtable();

    for (Iterator iter = priorityList.iterator(); iter.hasNext();) {
      Priority item = (Priority) iter.next();
      labelMap.put(
        new Integer(priorityList.indexOf(item)), new JLabel(item.toString()));

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

  void setChosenLevel(Level level) {
    setValue(priorityList.indexOf(level));
  }

  /**
   * Returns the Log4j Level that is currently selected in this slider
   * @return
   */
  Level getSelectedLevel() {
    Level level = (Level) priorityList.get(getValue());

    if (level == null) {
      level = Level.DEBUG;
    }

    return level;
  }
}
