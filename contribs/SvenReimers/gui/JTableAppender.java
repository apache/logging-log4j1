/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */


package org.apache.log4j.gui;

import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.Priority;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.awt.Rectangle;

public class JTableAppender extends JTable {


  static Category cat = Category.getInstance(JTableAppender.class.getName());

  PatternLayout layout;

  public
  JTableAppender() {
    layout = new PatternLayout("%r %p %c [%t] -  %m");
    this.setDefaultRenderer(Object.class, new Renderer());

  }

  public
  void add(LoggingEvent event) {
    ((JTableAppenderModel)getModel()).add(event);
  }

  public
  Dimension getPreferredSize() {
    System.out.println("getPreferredSize() called");
    return super.getPreferredSize();
  }

  static public void main(String[] args) {

    JFrame frame = new JFrame("JListView test");
    Container container = frame.getContentPane();

    JTableAppender appender = new JTableAppender();
    
    JTableAppenderModel model = new 
                              JTableAppenderModel(Integer.parseInt(args[0]));
    appender.setModel(model);
    //appender.createDefaultColumnsFromModel();    


    JScrollPane sp = new JScrollPane(appender);
    sp.setPreferredSize(new Dimension(250, 80));
    
    container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
    //container.add(view);
    container.add(sp);

    JButton button = new JButton("ADD");
    container.add(button);
    

    button.addActionListener(new JTableAddAction(appender));

    frame.setVisible(true);
    frame.setSize(new Dimension(700,700));

    long before = System.currentTimeMillis();

    int RUN = 10000;
    int i = 0;
    while(i++ < RUN) {      
      LoggingEvent event = new LoggingEvent("x", cat, Priority.ERROR, 
					    "Message "+i, null);
      event.getThreadName();
      if(i % 10 == 0) {
	//event.throwable = new Exception("hello "+i);
      }
      appender.add(event);
    }

    long after = System.currentTimeMillis();
    System.out.println("Time taken :"+ ((after-before)*1000/RUN));

  }

  class Renderer extends JTextArea implements TableCellRenderer {

    Object o = new Object();
    int i = 0;

    public
    Renderer() {
      System.out.println("Render() called ----------------------");      
    }

    public Component getTableCellRendererComponent(JTable table,
						   Object value,
						   boolean isSelected,
						   boolean hasFocus,
						   int row,
						   int column) {

      System.out.println(o + " ============== " + i++);
      //LogLog.error("=======", new Exception());
      //setIcon(longIcon);
      if(value instanceof LoggingEvent) {
	LoggingEvent event = (LoggingEvent) value;
	String str = layout.format(event);
	String t = event.getThrowableInformation();
	
	if(t != null) {
	  System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
	  setText(str + Layout.LINE_SEP + t);
	} else {	
	  setText(str);
	}
	
      } else {
	setText(value.toString());
      }


      return this;
    }
  }
}



class JTableAppenderModel extends AbstractTableModel {

  CyclicBuffer cb;
  
  JTableAppenderModel(int size) {
    cb = new CyclicBuffer(size);
  }

  public
  void add(LoggingEvent event) {
    //System.out.println("JListViewModel.add called");
    cb.add(event);
    int j = cb.length();

    fireTableDataChanged();

  }
  public 
  int getColumnCount() { 
    return 1; 
  }

  public int getRowCount() { 
    return cb.length();
  }

  //public
  //Class getColumnClass(int index) {
  //  System.out.println("getColumnClass called " + index);
  //  return LoggingEvent.class;
  //}

  public 
  Object getValueAt(int row, int col) {
    return cb.get(row);
  }
}


class JTableAddAction implements ActionListener {
    
  int j;
  JTableAppender appender;

  Category cat = Category.getInstance("x");
  
  public
  JTableAddAction(JTableAppender appender) {
    this.appender = appender;
    j = 0;
  }
    
  public
  void actionPerformed(ActionEvent e) {
    System.out.println("Action occured");

    LoggingEvent event = new LoggingEvent("x", cat, Priority.DEBUG, 
					    "Message "+j, null);
    
    if(j % 5 == 0) {
      //event.throwable = new Exception("hello "+j);
    }
    j++;
    appender.add(event);
  }
}
