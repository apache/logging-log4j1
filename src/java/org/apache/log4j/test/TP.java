
//      Copyright 1996-2000, International Business Machines 
//      Corporation and others. All Rights Reserved.

package org.apache.log4j.test; 

import org.apache.log4j.*;
import org.apache.log4j.gui.TextPaneAppender;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.awt.Dimension;

public class TP {

  static Category cat = Category.getInstance(TP.class.getName());

  public 
  static 
  void main(String argv[]) {

    JFrame frame = new JFrame("asdasd");
    Category root = Category.getRoot();
    PatternLayout layout = new PatternLayout("%p [%t] $c{2} %m\n");
    TextPaneAppender ap = new TextPaneAppender(layout, "c");
    
    JTextPane textPane = ap.getTextPane();
    textPane.setPreferredSize(new Dimension(200, 200));

    frame.getContentPane().add(textPane);
    frame.pack();

    ap.getTextPane().setVisible(true);    
    frame.setVisible(true);
    root.addAppender(ap);
    cat.debug("Message 1.");
    cat.debug("Message 2.");    
    
  }


  
}
