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

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Represents the controls for filtering, pausing, exiting, etc.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 */
class ControlPanel extends JPanel {
  /**
   * Name of the preference set for the control panel
   */
  public static final String PREF_SET_NAME = "filter";

  /**
   * Priority filter property
   */
  public static final String PRIORITY_PROPERTY = "priority";

  /**
   * Thread filter property
   */
  public static final String THREAD_PROPERTY = "thread";

  /**
   * Category filter property
   */
  public static final String CATEGORY_PROPERTY = "category";

  /**
   * NDC filter property
   */
  public static final String NDC_PROPERTY = "ndc";

  /**
   * Message filter property
   */
  public static final String MESSAGE_PROPERTY = "message";

  /**
   * Save filters? property
   */
  public static final String SAVE_PROPERTY = "save";

  /**
   * use the log messages
   */
  private static final Category LOG = Category.getInstance(ControlPanel.class);

  /**
   * Creates a new <code>ControlPanel</code> instance.
   *
   * @param aModel the model to control
   */
  ControlPanel(final MyTableModel aModel) {
    final PreferenceSet prefs =
      Preferences.getInstance().getPreferenceSet(
        Preferences.PROP_PREFIX, PREF_SET_NAME);
    final boolean savePrefs = prefs.getBoolean(SAVE_PROPERTY, true);

    setBorder(BorderFactory.createTitledBorder("Controls: "));

    final GridBagLayout gridbag = new GridBagLayout();
    final GridBagConstraints c = new GridBagConstraints();
    final Dimension d = new Dimension(80, 24);
    setLayout(gridbag);

    // Pad everything
    c.ipadx = 5;
    c.ipady = 5;

    // Add the 1st column of labels
    c.gridx = 0;
    c.anchor = GridBagConstraints.EAST;

    c.gridy = 0;

    JLabel label = new JLabel("Filter Level:");
    gridbag.setConstraints(label, c);
    add(label);

    c.gridy++;
    label = new JLabel("Filter Thread:");
    gridbag.setConstraints(label, c);
    add(label);

    c.gridy++;
    label = new JLabel("Filter Category:");
    gridbag.setConstraints(label, c);
    add(label);

    c.gridy++;
    label = new JLabel("Filter NDC:");
    gridbag.setConstraints(label, c);
    add(label);

    c.gridy++;
    label = new JLabel("Filter Message:");
    gridbag.setConstraints(label, c);
    add(label);

    // Add the 2nd column of filters
    c.weightx = 1;

    //c.weighty = 1;
    c.gridx = 1;
    c.anchor = GridBagConstraints.WEST;

    c.gridy = 0;

    final Priority[] allPriorities = Priority.getAllPossiblePriorities();
    final JComboBox priorities = new JComboBox(allPriorities);
    gridbag.setConstraints(priorities, c);
    add(priorities);
    priorities.setEditable(false);
    priorities.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent aEvent) {
          aModel.setPriorityFilter((Priority) priorities.getSelectedItem());
          prefs.setProperty(
            PRIORITY_PROPERTY, priorities.getSelectedItem().toString());
        }
      });

    Priority selected = allPriorities[allPriorities.length - 1];
    final String priorityProp = prefs.getProperty(PRIORITY_PROPERTY);

    if (savePrefs && (priorityProp != null)) {
      for (int i = 0; i < allPriorities.length; i++) {
        if (allPriorities[i].toString().equals(priorityProp)) {
          selected = allPriorities[i];

          break;
        }
      }
    }

    priorities.setSelectedItem(selected);
    aModel.setPriorityFilter(selected);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 2;
    c.gridy++;

    String threadProp = "";

    if (savePrefs) {
      threadProp = prefs.getProperty(THREAD_PROPERTY, "");
    }

    final JTextField threadField = new JTextField(threadProp);
    aModel.setThreadFilter(threadProp);
    threadField.getDocument().addDocumentListener(
      new DocumentChangeListener() {
        public void update(DocumentEvent aEvent) {
          aModel.setThreadFilter(threadField.getText());
          prefs.setProperty(THREAD_PROPERTY, threadField.getText());
        }
      });
    gridbag.setConstraints(threadField, c);
    add(threadField);

    c.gridy++;

    String catProp = "";

    if (savePrefs) {
      catProp = prefs.getProperty(CATEGORY_PROPERTY, "");
    }

    final JTextField catField = new JTextField(catProp);
    aModel.setCategoryFilter(catProp);
    catField.getDocument().addDocumentListener(
      new DocumentChangeListener() {
        public void update(DocumentEvent aEvent) {
          aModel.setCategoryFilter(catField.getText());
          prefs.setProperty(CATEGORY_PROPERTY, catField.getText());
        }
      });
    gridbag.setConstraints(catField, c);
    add(catField);

    c.gridy++;

    String ndcProp = "";

    if (savePrefs) {
      ndcProp = prefs.getProperty(NDC_PROPERTY, "");
    }

    final JTextField ndcField = new JTextField(ndcProp);
    aModel.setNDCFilter(ndcProp);
    ndcField.getDocument().addDocumentListener(
      new DocumentChangeListener() {
        public void update(DocumentEvent aEvent) {
          aModel.setNDCFilter(ndcField.getText());
          prefs.setProperty(NDC_PROPERTY, ndcField.getText());
        }
      });
    gridbag.setConstraints(ndcField, c);
    add(ndcField);

    c.gridy++;

    String msgProp = "";

    if (savePrefs) {
      msgProp = prefs.getProperty(MESSAGE_PROPERTY, "");
    }

    final JTextField msgField = new JTextField(msgProp);
    aModel.setMessageFilter(msgProp);
    msgField.getDocument().addDocumentListener(
      new DocumentChangeListener() {
        public void update(DocumentEvent aEvent) {
          aModel.setMessageFilter(msgField.getText());
          prefs.setProperty(MESSAGE_PROPERTY, msgField.getText());
        }
      });

    gridbag.setConstraints(msgField, c);
    add(msgField);

    // Add the 3rd column of buttons
    c.gridx = 2;
    c.gridy = 0;
    c.gridwidth = 1;
    c.weightx = 0;
    c.weighty = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;

    final JPanel buttonPanel = new JPanel();
    gridbag.setConstraints(buttonPanel, c);
    add(buttonPanel);
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));

    final Insets insets = new Insets(2, 2, 2, 2);
    final JButton toggleButton = new JButton("Pause");
    toggleButton.setMnemonic('p');
    toggleButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent aEvent) {
          aModel.toggle();
          toggleButton.setText(aModel.isPaused() ? "Resume" : "Pause");
        }
      });
    toggleButton.setMargin(insets);
    toggleButton.setPreferredSize(d);
    toggleButton.setMinimumSize(d);
    buttonPanel.add(toggleButton);

    final JButton clearButton = new JButton("Clear");
    clearButton.setMnemonic('c');
    clearButton.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent aEvent) {
          aModel.clear();
        }
      });
    clearButton.setMargin(insets);
    clearButton.setPreferredSize(d);
    clearButton.setMinimumSize(d);
    buttonPanel.add(clearButton);

    final JButton exitButton = new JButton("Exit");
    exitButton.setMnemonic('x');
    exitButton.addActionListener(ExitAction.INSTANCE);
    exitButton.setMargin(insets);
    exitButton.setPreferredSize(d);
    exitButton.setMinimumSize(d);
    buttonPanel.add(exitButton);
  }

  /**
   * Convenience class that filters all document events to one method
   */
  private abstract static class DocumentChangeListener
    implements DocumentListener {
    public abstract void update(DocumentEvent aEvent);

    public void insertUpdate(DocumentEvent aEvent) {
      update(aEvent);
    }

    public void removeUpdate(DocumentEvent aEvent) {
      update(aEvent);
    }

    public void changedUpdate(DocumentEvent aEvent) {
      update(aEvent);
    }
  }
}
