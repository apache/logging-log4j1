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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * A Frame that allows the user to decide which Columns are important
 * to include in the Detail Panel, or tooltip text when displaying a
 * particular selected LoggingEvent.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class DetailFieldSelector extends JFrame {
  private HashMap displayFieldBoxMap = new HashMap();

  public DetailFieldSelector(String ident, 
    final Vector columnNames, final DisplayFilter displayFilter) {
  	super("Select detail/tooltip columns for " + ident);    	
    setLocation(150, 150);

    JPanel detailFieldPanel = new JPanel(new GridLayout(0, 1));
    detailFieldPanel.setBorder(
      BorderFactory.createTitledBorder("Select tooltip/detail columns"));

    Vector detailColumns = displayFilter.getDetailColumns();
    detailFieldPanel.setPreferredSize(new Dimension(175, 340));

    int colCount = columnNames.size();

    for (int i = 0; i < colCount; i++) {
      final String key = (String) columnNames.get(i);
      JCheckBox box = new JCheckBox(key, detailColumns.contains(key));
      displayFieldBoxMap.put(box, key);
      box.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Vector v = new Vector();
            Collection c = displayFieldBoxMap.entrySet();
            Iterator iter = c.iterator();

            while (iter.hasNext()) {
              Map.Entry entry = (Map.Entry) iter.next();
              JCheckBox b = (JCheckBox) entry.getKey();

              if (b.isSelected()) {
                v.add(entry.getValue());
              }
            }

            //apply ordering by using original column names order..may be some other way
            Vector endVector = new Vector();
            Iterator x = columnNames.iterator();

            while (x.hasNext()) {
              String thiscol = (String) x.next();

              if (v.contains(thiscol)) {
                endVector.add(thiscol);
              }
            }

            displayFilter.setDetailColumns(endVector);
          }
        });
      detailFieldPanel.add(box);
      getContentPane().add(detailFieldPanel);
      pack();
    }
  }
}
