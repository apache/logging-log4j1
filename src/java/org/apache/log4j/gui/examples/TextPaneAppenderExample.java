/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.gui.examples;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.*;
import org.apache.log4j.gui.*;



public class TextPaneAppenderExample implements ActionListener {

    JFrame mainframe;
    ButtonGroup priorities;
    TextPaneAppender tpa;
    Category gui;
    Priority prio[];
    JTabbedPane logview;
    
    
    public TextPaneAppenderExample () {
	mainframe = new JFrame("Testing the TextPaneAppender...");
	mainframe.setSize(300,300);
	logview = new JTabbedPane();
	createLogger();
	createMenuBar();
	mainframe.setVisible(true);
	mainframe.getContentPane().add(logview);
    }

    public void createLogger() {
	tpa = new TextPaneAppender(new PatternLayout("%-5p %d [%t]:  %m%n"),"Debug");
	logview.addTab("Events ...",new JScrollPane(tpa.getTextPane()));
	gui = Category.getInstance(this.getClass().getName());
	gui.addAppender(tpa);
    }
    
    public void createMenuBar() {
	JMenu file = new JMenu("File");
	JMenuItem exit = new JMenuItem("Exit");
	exit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    System.exit(0);
		}
	    });
	file.add(exit);
	JMenuBar mb = new JMenuBar();
	mb.add(file);
	
	JMenu logevent = new JMenu("LoggingEvents");
	JMenu selectprio = new JMenu("Priority");
	
	prio = Priority.getAllPossiblePriorities();
	JRadioButtonMenuItem priority[]= new JRadioButtonMenuItem[prio.length];
	priorities = new ButtonGroup();
	
	for (int i=0; i<prio.length;i++) {
	    if (i==0)
		priority[i] = new JRadioButtonMenuItem(prio[i].toString(),true);
	    else
		priority[i] = new JRadioButtonMenuItem(prio[i].toString());
	    priority[i].setActionCommand(prio[i].toString());
	    selectprio.add(priority[i]);
	    priorities.add(priority[i]);

	}
	
	logevent.add(selectprio);
	
	JMenuItem lognow = new JMenuItem("LogIt!");
	lognow.addActionListener(this);
	logevent.add(lognow);
	
	mb.add(logevent);
	
	mainframe.setJMenuBar(mb);
	
    }
    
    public void actionPerformed(ActionEvent ae){
	String logtext = JOptionPane.showInputDialog("Text to log");
	if (logtext == null) logtext="NO Input";
	int i=0;
	String name = priorities.getSelection().getActionCommand();
	while (!prio[i].toString().equals(name))
	    i=i+1;
	gui.log(prio[i],logtext);
    }

    static public void main(String args[]) {
	TextPaneAppenderExample tpex = new TextPaneAppenderExample();
    }
    
}
