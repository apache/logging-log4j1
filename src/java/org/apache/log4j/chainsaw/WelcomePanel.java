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

import org.apache.log4j.chainsaw.help.Tutorial;
import org.apache.log4j.chainsaw.icons.ChainsawIcons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.net.URL;

import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * An initial Welcome Panel that is used when Chainsaw starts up, displays
 * a HTML page that should be located in the current Classloaders classpath
 * providing some useful tips on startup.
 *
 * @author Paul Smith
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class WelcomePanel extends JPanel {
  private static WelcomePanel instance = new WelcomePanel();
  private Stack urlStack = new Stack();
  private final JEditorPane textInfo = new JEditorPane();
  private final URLToolbar urlToolbar =  new URLToolbar();
  private final URL helpURL;
  private final URL exampleConfigURL;
  private final URL tutorialURL;
  
  private WelcomePanel() {
    setLayout(new BorderLayout());
    setBackground(Color.white);
    add(urlToolbar, BorderLayout.NORTH);

     helpURL =
      getClass().getClassLoader().getResource(
        "org/apache/log4j/chainsaw/WelcomePanel.html");
        
    exampleConfigURL =
    getClass().getClassLoader().getResource(
      "org/apache/log4j/chainsaw/log4j-receiver-sample.xml");

	tutorialURL =    getClass().getClassLoader().getResource(
	"org/apache/log4j/chainsaw/help/tutorial.html");
 
    if (helpURL != null) {
      textInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
      textInfo.setEditable(false);
      textInfo.setPreferredSize(new Dimension(320, 240));

      JScrollPane pane = new JScrollPane(textInfo);
      pane.setBorder(null);
      add(pane, BorderLayout.CENTER);

      try {
        textInfo.setPage(helpURL);
        textInfo.addHyperlinkListener(
          new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
              if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if(e.getDescription().equals("StartTutorial")){
					new Thread(new Tutorial()).start();	
				}else{
                urlStack.add(textInfo.getPage());

                try {
                  textInfo.setPage(e.getURL());
                  urlToolbar.updateToolbar();
                } catch (IOException e1) {
                  e1.printStackTrace();
                }
				}
              }
            }
          });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  void setURL(final URL url) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          try {
            urlStack.push(textInfo.getPage());
            textInfo.setPage(url);
            urlToolbar.updateToolbar();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    
  }

  private class URLToolbar extends JToolBar {
    private final Action previousAction =
      new AbstractAction(null, new ImageIcon(ChainsawIcons.ICON_BACK)) {
        public void actionPerformed(ActionEvent e) {
          if (urlStack.isEmpty()) {
            return;
          }
          setURL((URL)urlStack.pop());

        }
      };
      
    private final Action homeAction =
      new AbstractAction(null, new ImageIcon(ChainsawIcons.ICON_HOME)) {
        public void actionPerformed(ActionEvent e) {
          setURL(helpURL);
          urlStack.clear();
        }
      };

    private URLToolbar() {
    	setFloatable(false);
      updateToolbar();
      previousAction.putValue(Action.SHORT_DESCRIPTION, "Back");
      homeAction.putValue(Action.SHORT_DESCRIPTION, "Home");

      JButton home = new SmallButton(homeAction);
      add(home);
      
	  addSeparator();      
      JButton previous = new SmallButton(previousAction);
      previous.setEnabled(false);
      add(previous);
      
      addSeparator();
      add(new SmallButton(new AbstractAction("Tutorial"){

		public void actionPerformed(ActionEvent e) {
			setURL(tutorialURL);
			
		}}));
	addSeparator();
      final Action exampleConfigAction = new AbstractAction("View example Receiver configuration"){

        public void actionPerformed(ActionEvent e) {
          setURL(exampleConfigURL);
          
        }};
      exampleConfigAction.putValue(Action.SHORT_DESCRIPTION, "Displays an example Log4j configuration file with several Receivers defined.");
      
      JButton exampleButton = new SmallButton(exampleConfigAction);
      add(exampleButton);

      JPanel p = new JPanel();
      add(p);
    }

    void updateToolbar() {
      previousAction.setEnabled(!urlStack.isEmpty());
    }
  }
  /**
   * 
   */
  public static WelcomePanel getInstance() {
    return instance;
    
  }
  
}
