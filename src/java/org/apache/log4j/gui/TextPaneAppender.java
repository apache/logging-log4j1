/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.gui;


import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import org.apache.log4j.*;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.helpers.TracerPrintWriter;
import org.apache.log4j.helpers.OptionConverter;


/**
 * <b>Experimental</b> TextPaneAppender. <br>
 *
 *
 * Created: Sat Feb 26 18:50:27 2000 <br>
 *
 * @author Sven Reimers
 */

public class TextPaneAppender extends AppenderSkeleton {
    
  JTextPane textpane;
  StyledDocument doc;
  TracerPrintWriter tp;
  StringWriter sw;
  QuietWriter qw;
  Hashtable attributes;
  Hashtable icons;
  
  private String label;
  
  private boolean fancy;
    
  final String LABEL_OPTION = "Label";
  final String COLOR_OPTION_FATAL = "Color.Emerg";
  final String COLOR_OPTION_ERROR = "Color.Error";
  final String COLOR_OPTION_WARN = "Color.Warn";
  final String COLOR_OPTION_INFO = "Color.Info";
  final String COLOR_OPTION_DEBUG = "Color.Debug";
  final String COLOR_OPTION_BACKGROUND = "Color.Background";
  final String FANCY_OPTION = "Fancy";
  final String FONT_NAME_OPTION = "Font.Name";
  final String FONT_SIZE_OPTION = "Font.Size";
  
  public static Image loadIcon ( String path ) {
    Image img = null;
    try {
      URL url = ClassLoader.getSystemResource(path);
      img = (Image) (Toolkit.getDefaultToolkit()).getImage(url);
    } catch (Exception e) {
      System.out.println("Exception occured: " + e.getMessage() + 
			 " - " + e );   
    }	
    return (img);
  }
  
  public TextPaneAppender(Layout layout, String name) {
    this();
    this.layout = layout;
    this.name = name;
    setTextPane(new JTextPane());
    createAttributes();
    createIcons();
  }
    
  public TextPaneAppender() {
    super();
    setTextPane(new JTextPane());
    createAttributes();
    createIcons();
    this.label="";
    this.sw = new StringWriter();
    this.qw = new QuietWriter(sw, errorHandler);
    this.tp = new TracerPrintWriter(qw);
    this.fancy =true;
  }

  public
  void close() {
    
  }
  
  private void createAttributes() {	
    Priority prio[] = Priority.getAllPossiblePriorities();
    
    attributes = new Hashtable();
    for (int i=0; i<prio.length;i++) {
      MutableAttributeSet att = new SimpleAttributeSet();
      attributes.put(prio[i], att);
      StyleConstants.setFontSize(att,14);
    }
    StyleConstants.setForeground((MutableAttributeSet)attributes.get(Priority.ERROR),Color.red);
    StyleConstants.setForeground((MutableAttributeSet)attributes.get(Priority.WARN),Color.orange);
    StyleConstants.setForeground((MutableAttributeSet)attributes.get(Priority.INFO),Color.gray);
    StyleConstants.setForeground((MutableAttributeSet)attributes.get(Priority.DEBUG),Color.black);
  }

  private void createIcons() {
    Priority prio[] = Priority.getAllPossiblePriorities();
    
    icons = new Hashtable();
    for (int i=0; i<prio.length;i++) {
      if (prio[i].equals(Priority.FATAL))
	icons.put(prio[i],new ImageIcon(loadIcon("icons/RedFlag.gif")));
      if (prio[i].equals(Priority.ERROR))		
	icons.put(prio[i],new ImageIcon(loadIcon("icons/RedFlag.gif")));
      if (prio[i].equals(Priority.WARN))		
	icons.put(prio[i],new ImageIcon(loadIcon("icons/BlueFlag.gif")));
      if (prio[i].equals(Priority.INFO))		
	icons.put(prio[i],new ImageIcon(loadIcon("icons/GreenFlag.gif")));
      if (prio[i].equals(Priority.DEBUG))		
	icons.put(prio[i],new ImageIcon(loadIcon("icons/GreenFlag.gif")));
    }
  }

  public void append(LoggingEvent event) {
    String text = this.layout.format(event);
    String trace="";
    // Print Stacktrace
    // Quick Hack maybe there is a better/faster way?
    if (event.throwable!=null) {
      event.throwable.printStackTrace(tp);
      for (int i=0; i< sw.getBuffer().length(); i++) {
	if (sw.getBuffer().charAt(i)=='\t')
	  sw.getBuffer().replace(i,i+1,"        ");
      }
      trace = sw.toString();
      sw.getBuffer().delete(0,sw.getBuffer().length());
    }
    try {
      if (fancy) {
	textpane.setEditable(true);
	textpane.insertIcon((ImageIcon)icons.get(event.priority));
	textpane.setEditable(false);
      }
      doc.insertString(doc.getLength(),text+trace,
		       (MutableAttributeSet)attributes.get(event.priority));
	}	
    catch (BadLocationException badex) {
      System.err.println(badex);
    }	
    textpane.setCaretPosition(doc.getLength());
  }
  
  public
  JTextPane getTextPane() {
    return textpane;
  }
  
  public String getLabel() {
    return label;
  }

  public
  String[] getOptionStrings() {
    return new String[] {LABEL_OPTION, COLOR_OPTION_FATAL, COLOR_OPTION_ERROR,
			   COLOR_OPTION_WARN, COLOR_OPTION_INFO, COLOR_OPTION_DEBUG,
			   COLOR_OPTION_BACKGROUND, FANCY_OPTION,
			   FONT_NAME_OPTION, FONT_SIZE_OPTION};
  }

  
  private
  Color parseColor (String v) {
    StringTokenizer st = new StringTokenizer(v,",");
    int val[] = {255,255,255,255};
    int i=0;
    while (st.hasMoreTokens()) {
      val[i]=Integer.parseInt(st.nextToken());
      i++;
    }
    return new Color(val[0],val[1],val[2],val[3]);
  }

  public
  void setLayout(Layout layout) {
    this.layout=layout;
  }
  
  public
  void setName(String name) {
    this.name = name;
  }
  
    
  public
  void setTextPane(JTextPane textpane) {
    this.textpane=textpane;
    textpane.setEditable(false);
    textpane.setBackground(Color.lightGray);
    this.doc=textpane.getStyledDocument();
  }
          
  private
  void setColor(Priority p, String v) {
    StyleConstants.setForeground(
		      (MutableAttributeSet)attributes.get(p),parseColor(v));	
  }
    
  private
  void setFontSize(int size) {
    Enumeration e = attributes.elements();
    while (e.hasMoreElements()) {
      StyleConstants.setFontSize((MutableAttributeSet)e.nextElement(),size);
    }
    return;
  }
  
  private
  void setFontName(String name) {
    Enumeration e = attributes.elements();
    while (e.hasMoreElements()) {
      StyleConstants.setFontFamily((MutableAttributeSet)e.nextElement(),name);
    }
    return;
  }
        
  public
  void setOption(String option, String value) {
    if (option.equalsIgnoreCase(LABEL_OPTION))
      this.label=value;
    if (option.equalsIgnoreCase(COLOR_OPTION_FATAL))
      setColor(Priority.FATAL,value);
    if (option.equalsIgnoreCase(COLOR_OPTION_ERROR))
      setColor(Priority.ERROR,value);
    if (option.equalsIgnoreCase(COLOR_OPTION_WARN))
      setColor(Priority.WARN,value);
    if (option.equalsIgnoreCase(COLOR_OPTION_INFO))
      setColor(Priority.INFO,value);
    if (option.equalsIgnoreCase(COLOR_OPTION_DEBUG))
      setColor(Priority.DEBUG,value);
    if (option.equalsIgnoreCase(COLOR_OPTION_BACKGROUND))
      textpane.setBackground(parseColor(value));
    if (option.equalsIgnoreCase(FANCY_OPTION))
      fancy = OptionConverter.toBoolean(value, fancy);
    if (option.equalsIgnoreCase(FONT_SIZE_OPTION))
      setFontSize(Integer.parseInt(value));
    if (option.equalsIgnoreCase(FONT_NAME_OPTION))
      setFontName(value);
    return;
  }

  public
  boolean requiresLayout() {
    return true;
  }
} // TextPaneAppender



