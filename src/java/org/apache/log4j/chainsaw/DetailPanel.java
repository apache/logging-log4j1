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

import java.awt.BorderLayout;

import java.text.MessageFormat;

import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * A panel for showing a stack trace.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 */
class DetailPanel extends JPanel implements ListSelectionListener {
  /** used to log events **/
  private static final Category LOG = Category.getInstance(DetailPanel.class);

  /** used to format the logging event **/
  private static final MessageFormat FORMATTER =
    new MessageFormat(
      "<b>Time:</b> <code>{0,time,medium}</code>"
      + "&nbsp;&nbsp;<b>Priority:</b> <code>{1}</code>"
      + "&nbsp;&nbsp;<b>Thread:</b> <code>{2}</code>"
      + "&nbsp;&nbsp;<b>NDC:</b> <code>{3}</code>"
      + "<br><b>Category:</b> <code>{4}</code>"
      + "<br><b>Location:</b> <code>{5}</code>" + "<br><b>Message:</b>"
      + "<pre>{6}</pre>" + "<b>Throwable:</b>" + "<pre>{7}</pre>");

  /** the model for the data to render **/
  private final MyTableModel mModel;

  /** pane for rendering detail **/
  private final JEditorPane mDetails;

  /**
 * Creates a new <code>DetailPanel</code> instance.
 *
 * @param aTable the table to listen for selections on
 * @param aModel the model backing the table
 */
  DetailPanel(JTable aTable, final MyTableModel aModel) {
    mModel = aModel;
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Details: "));

    mDetails = new JEditorPane();
    mDetails.setEditable(false);
    mDetails.setContentType("text/html");
    add(new JScrollPane(mDetails), BorderLayout.CENTER);

    final ListSelectionModel rowSM = aTable.getSelectionModel();
    rowSM.addListSelectionListener(this);
  }

  /** @see ListSelectionListener **/
  public void valueChanged(ListSelectionEvent aEvent) {
    //Ignore extra messages.
    if (aEvent.getValueIsAdjusting()) {
      return;
    }

    final ListSelectionModel lsm = (ListSelectionModel) aEvent.getSource();

    if (lsm.isSelectionEmpty()) {
      mDetails.setText("Nothing selected");
    } else {
      final int selectedRow = lsm.getMinSelectionIndex();
      final EventDetails e = mModel.getEventDetails(selectedRow);
      final Object[] args =
      {
        new Date(e.getTimeStamp()), e.getPriority(), escape(e.getThreadName()),
        escape(e.getNDC()), escape(e.getCategoryName()),
        escape(e.getLocationDetails()), escape(e.getMessage()),
        escape(getThrowableStrRep(e))
      };
      mDetails.setText(FORMATTER.format(args));
      mDetails.setCaretPosition(0);
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // Private methods
  ////////////////////////////////////////////////////////////////////////////

  /**
 * Returns a string representation of a throwable.
 *
 * @param aEvent contains the throwable information
 * @return a <code>String</code> value
 */
  private static String getThrowableStrRep(EventDetails aEvent) {
    final String[] strs = aEvent.getThrowableStrRep();

    if (strs == null) {
      return null;
    }

    final StringBuffer sb = new StringBuffer();

    for (int i = 0; i < strs.length; i++) {
      sb.append(strs[i]).append("\n");
    }

    return sb.toString();
  }

  /**
 * Escape &lt;, &gt; &amp; and &quot; as their entities. It is very
 * dumb about &amp; handling.
 * @param aStr the String to escape.
 * @return the escaped String
 */
  private String escape(String aStr) {
    if (aStr == null) {
      return null;
    }

    final StringBuffer buf = new StringBuffer();

    for (int i = 0; i < aStr.length(); i++) {
      char c = aStr.charAt(i);

      switch (c) {
      case '<':
        buf.append("&lt;");

        break;

      case '>':
        buf.append("&gt;");

        break;

      case '\"':
        buf.append("&quot;");

        break;

      case '&':
        buf.append("&amp;");

        break;

      default:
        buf.append(c);

        break;
      }
    }

    return buf.toString();
  }
}
