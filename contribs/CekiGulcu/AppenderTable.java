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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;


/**
 * The AppenderTable illustrates one possible implementation of an Table
 * possibly containing a great many number of rows.
 * 
 * <p>
 * In this particular example we use a fixed size buffer (CyclicBuffer)
 * although this data structure could be easily replaced by dynamically
 * growing one, such as a Vector. The required properties of the data
 * structure is 1) support for indexed element access 2) support for the
 * insertion of new elements at the end.
 * </p>
 * 
 * <p>
 * Experimentation on my 1400Mhz AMD machine show that it takes about 45
 * micro-seconds to insert an element in the table. This number does not
 * depend on the size of the buffer. It takes as much (or as little) time to
 * insert one million elements to a buffer of size 10 as to a buffer of size
 * 10'000. It takes about 4 seconds to insert a total of 100'000 elements
 * into the table.
 * </p>
 * 
 * <p>
 * On windows NT the test will run about twice as fast if you give the focus
 * to the window that runs "java AppenderTable" and not the window that
 * contains the Swing JFrame.
 * </p>
 */
public class AppenderTable extends JTable {
  static Logger logger = Logger.getLogger(AppenderTable.class);

  public AppenderTable() {
    this.setDefaultRenderer(Object.class, new Renderer());
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println(
        "Usage: java AppenderTable bufferSize runLength\n"
        + "  where bufferSize is the size of the cyclic buffer in the TableModel\n"
        + "  and runLength is the total number of elements to add to the table in\n"
        + "  this test run.");

      return;
    }

    JFrame frame = new JFrame("JTableAppennder test");
    Container container = frame.getContentPane();

    AppenderTable tableAppender = new AppenderTable();

    int bufferSize = Integer.parseInt(args[0]);
    AppenderTableModel model = new AppenderTableModel(bufferSize);
    tableAppender.setModel(model);

    int runLength = Integer.parseInt(args[1]);

    JScrollPane sp = new JScrollPane(tableAppender);
    sp.setPreferredSize(new Dimension(250, 80));

    container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
    container.add(sp);

    // The "ADD" button is intended for manual testing. It will
    // add one new logging event to the table.
    JButton button = new JButton("ADD");
    container.add(button);
    button.addActionListener(new JTableAddAction(tableAppender));

    frame.setSize(new Dimension(500, 300));
    frame.setVisible(true);

    long before = System.currentTimeMillis();

    int i = 0;

    while (i++ < runLength) {
      LoggingEvent event =
        new LoggingEvent("x", logger, Level.ERROR, "Message " + i, null);
      tableAppender.doAppend(event);
    }

    long after = System.currentTimeMillis();

    long totalTime = (after - before);

    System.out.println(
      "Total time :" + totalTime + " milliseconds for "
      + "runLength insertions.");
    System.out.println(
      "Average time per insertion :" + ((totalTime * 1000) / runLength)
      + " micro-seconds.");
  }

  /**
   * When asked to append we just insert directly into the model. In a real
   * appender we would use two buffers one for accumulating events and
   * another to accumalte events but after filtering. Only the second buffer
   * would be displayed in the table and made visible to the user.
   */
  public void doAppend(LoggingEvent event) {
    ((AppenderTableModel) getModel()).insert(event);
  }

  /**
   * The Renderer is required to display object in a friendlier from. This
   * particular renderer is just a JTextArea.
   * 
   * <p>
   * The important point to note is that we only need one renderer.
   * </p>
   */
  class Renderer extends JTextArea implements TableCellRenderer {
    PatternLayout layout;

    public Renderer() {
      layout = new PatternLayout("%r %p %c [%t] -  %m");
    }

    public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
      int column) {
      // If its a LoggingEvent than format it using our layout.
      if (value instanceof LoggingEvent) {
        LoggingEvent event = (LoggingEvent) value;
        String str = layout.format(event);
        setText(str);
      } else {
        setText(value.toString());
      }

      return this;
    }
  }
}


class AppenderTableModel extends AbstractTableModel {
  CyclicBuffer cb;

  AppenderTableModel(int size) {
    cb = new CyclicBuffer(size);
  }

  /**
   * Insertion to the model always results in a fireTableDataChanged method
   * call. Suprisingly enough this has no crippling impact on performance.
   */
  public void insert(LoggingEvent event) {
    cb.add(event);
    fireTableDataChanged();
  }

  /**
   * We assume only one column.
   */
  public int getColumnCount() {
    return 1;
  }

  /**
   * The row count is given by the number of elements in the buffer. This
   * number is guaranteed to be between 0 and the buffer size (inclusive).
   */
  public int getRowCount() {
    return cb.length();
  }

  /**
   * Get the value in a given row and column. We suppose that there is only
   * one colemn so we are only concerned with the row.
   * 
   * <p>
   * Interesting enough this method returns an object. This leaves the door
   * open for a TableCellRenderer to render the object in a variety of ways.
   * </p>
   */
  public Object getValueAt(int row, int col) {
    return cb.get(row);
  }
}


/**
 * The JTableAddAction is called when the user clicks on the "ADD" button.
 */
class JTableAddAction implements ActionListener {
  AppenderTable appenderTable;
  Logger dummy = Logger.getLogger("x");
  int counter = 0;

  public JTableAddAction(AppenderTable appenderTable) {
    this.appenderTable = appenderTable;
  }

  public void actionPerformed(ActionEvent e) {
    counter++;

    LoggingEvent event =
      new LoggingEvent("x", dummy, Level.DEBUG, "Message " + counter, null);
    appenderTable.doAppend(event);
  }
}
