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

package org.apache.log4j.chainsaw.filter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;


/**
 * A Panel that is used for configuring which unique Event elements determine
 * what events to display.
 * 
 * @author Paul Smith
 */
class FilterColorPreferences extends JPanel {
  private final FilterModel filterModel;
  private JTabbedPane tabbedPane = new JTabbedPane();

  FilterColorPreferences(FilterModel filterModel) {
    this.filterModel = filterModel;
    initComponents();
  }

  /**
   *
   */
  private void initComponents() {
    setLayout(new BorderLayout());

    EventTypeEntryContainer container = filterModel.getContainer();
    ListModel[] models =
      new ListModel[] {
        container.getLevelListModel(), container.getLoggerListModel(),
      };
    String[] tabNames = new String[] { "Levels", "Loggers", };

    for (int i = 0; i < tabNames.length; i++) {
      tabbedPane.add(tabNames[i], createTabPanel(tabNames[i], models[i]));
    }

    add(tabbedPane, BorderLayout.CENTER);
  }

  /**
   * @param string
   * @param collection
   * @return
   */
  private Component createTabPanel(String tabName, ListModel listModel) {
    JPanel c = new JPanel();
    c.setLayout(new BorderLayout());

    JList list = new JList(listModel);
    list.setVisibleRowCount(25);
    c.add(new JScrollPane(list), BorderLayout.CENTER);

    return c;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("test bed");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    FilterModel model = new FilterModel();
    LoggingEvent e =
      new LoggingEvent(
        "org.blah.blah", Logger.getLogger("org.blah.blah"),
        new Date().getTime(), Level.DEBUG, "Hello World", null);
    model.processNewLoggingEvent(null, e);
    frame.getContentPane().add(new FilterColorPreferences(model));
    frame.pack();
    frame.setSize(new Dimension(320, 240));
    frame.setVisible(true);
  }
}
