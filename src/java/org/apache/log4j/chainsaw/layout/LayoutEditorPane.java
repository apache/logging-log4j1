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

package org.apache.log4j.chainsaw.layout;

import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.ChainsawConstants;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Date;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * An editor Pane that allows a user to Edit a Pattern Layout and preview the output it would
 * generate with an example LoggingEvent
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class LayoutEditorPane extends JPanel {
  private final Action copyAction;
  private final Action cutAction;
  private final JToolBar editorToolbar = new JToolBar();
  private final JToolBar okCancelToolbar = new JToolBar();
  private final JButton okButton = new JButton("Ok");
  private final JButton cancelButton = new JButton("Cancel");

  //  private final JButton applyButton = new JButton();
  private final JEditorPane patternEditor = new JEditorPane("text/plain", "");
  private final JEditorPane previewer =
    new JEditorPane(ChainsawConstants.DETAIL_CONTENT_TYPE, "");
  private final JScrollPane patternEditorScroll =
    new JScrollPane(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  private final JScrollPane previewEditorScroll =
    new JScrollPane(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  private LoggingEvent event;
  private EventDetailLayout layout = new EventDetailLayout();

  /**
   *
   */
  public LayoutEditorPane() {
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    createEvent();
    copyAction = createCopyAction();
    cutAction = createCutAction();
    initComponents();
    setupListeners();
  }

  /**
  * @return
  */
  private Action createCutAction() {
    final Action action =
      new AbstractAction("Cut", ChainsawIcons.ICON_CUT) {
        public void actionPerformed(ActionEvent e) {
          // TODO Auto-generated method stub
        }
      };

    action.setEnabled(false);

    return action;
  }

  /**
   * @return
   */
  private Action createCopyAction() {
    final Action action =
      new AbstractAction("Copy", ChainsawIcons.ICON_COPY) {
        public void actionPerformed(ActionEvent e) {
          // TODO Auto-generated method stub
        }
      };

    action.setEnabled(false);

    return action;
  }

  /**
    *
    */
  private void setupListeners() {
    patternEditor.getDocument().addDocumentListener(
      new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
          updatePreview();
        }

        public void insertUpdate(DocumentEvent e) {
          updatePreview();
        }

        public void removeUpdate(DocumentEvent e) {
          updatePreview();
        }
      });

    patternEditor.addCaretListener(
      new CaretListener() {
        public void caretUpdate(CaretEvent e) {
          updateTextActions(e.getMark() != e.getDot());
        }
      });
  }

  private void updatePreview() {
    String pattern = patternEditor.getText();
    layout.setConversionPattern(pattern);

    previewer.setText(layout.format(event));
  }

  /**
  *
  */
  private void updateTextActions(boolean enabled) {
    cutAction.setEnabled(enabled);
    copyAction.setEnabled(enabled);
  }

  /**
      *
      */
  private void createEvent() {
    Hashtable hashTable = new Hashtable();
    hashTable.put("key1", "val1");
    hashTable.put("key2", "val2");
    hashTable.put("key3", "val3");

    LocationInfo li =
      new LocationInfo(
        "myfile.java", "com.mycompany.util.MyClass", "myMethod", "321");

    ThrowableInformation tsr = new ThrowableInformation(new Exception());

    event =
      new LoggingEvent(
        "com.mycompany.mylogger", Logger.getLogger("com.mycompany.mylogger"),
        new Date().getTime(), org.apache.log4j.Level.DEBUG, "Thread-1",
        "The quick brown fox jumped over the lazy dog", "NDC string", hashTable,
        tsr.getThrowableStrRep(), li, hashTable);
  }

  /**
     *
     */
  private void initComponents() {
    editorToolbar.setFloatable(false);
    okCancelToolbar.setFloatable(false);
    okButton.setToolTipText("Accepts the current Pattern layout and will apply it to the Log Panel");
    cancelButton.setToolTipText("Closes this dialog and discards your changes");
    
    previewer.setEditable(false);
    patternEditor.setPreferredSize(new Dimension(240, 240));
    patternEditor.setMaximumSize(new Dimension(320, 240));
    previewer.setPreferredSize(new Dimension(360, 240));
    patternEditorScroll.setViewportView(patternEditor);
    previewEditorScroll.setViewportView(previewer);

    patternEditor.setToolTipText("Edit the Pattern here");
    previewer.setToolTipText(
      "The result of the layout of the pattern is shown here");

    patternEditorScroll.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Pattern Editor"));
    previewEditorScroll.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Pattern Preview"));

//    editorToolbar.add(new JButton(copyAction));
//    editorToolbar.add(new JButton(cutAction));

    editorToolbar.add(Box.createHorizontalGlue());

    okCancelToolbar.add(Box.createHorizontalGlue());
    okCancelToolbar.add(okButton);
    okCancelToolbar.addSeparator();
    okCancelToolbar.add(cancelButton);

    //    okCancelToolbar.addSeparator();
    //    okCancelToolbar.add(applyButton);
    add(editorToolbar);
    add(patternEditorScroll);
    add(previewEditorScroll);
    add(okCancelToolbar);
  }

  public void setConversionPattern(String pattern) {
    patternEditor.setText(pattern);
  }

  public String getConversionPattern() {
    return patternEditor.getText();
  }

  public void addOkActionListener(ActionListener l) {
    okButton.addActionListener(l);
  }

  public void addCancelActionListener(ActionListener l) {
    cancelButton.addActionListener(l);
  }

  public static void main(String[] args) {
    JDialog dialog = new JDialog((Frame) null, "Pattern Editor");
    dialog.getContentPane().add(new LayoutEditorPane());
    dialog.setResizable(true);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    //    dialog.pack();
    dialog.setSize(new Dimension(640, 480));
    dialog.setVisible(true);
  }
}
