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

/*
 * @author Paul Smith <psmith@apache.org>
 *
*/
package org.apache.log4j.chainsaw;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;


/**
 * The only reason this class is needed is because
 * of a stupid 'issue' with the JTabbedPane.
 *
 * If the currently selected tab is the first tab,
 * and we insert a new tab at the front, then as
 * far as the JTabbedPane is concerned, NO STATE has
 * changed, as the currently selected tab index is still
 * the same (even though the TAB is different - go figure)
 * and therefore no ChangeEvent is generated and sent
 * to listeners.  Thanks very much Sun!
 *
 * @see http://developer.java.sun.com/developer/bugParade/bugs/4253819.html
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
class ChainsawTabbedPane extends JTabbedPane {
  /**
   *
   */
  public ChainsawTabbedPane() {
    super();
  }

  /**
   * @param tabPlacement
   */
  public ChainsawTabbedPane(int tabPlacement) {
    super(tabPlacement);
    setBorder(null);
  }

  /**
   * Returns true if this TabbedPane has an instance of the WelcomePanel
   * in it
   * @return true/false
   */
  boolean containsWelcomePanel() {
    return indexOfTab("Welcome") > -1;
  }

  /**
   * Our custom implementation of inserting a new tab,
   * this method ALWAYS inserts it at the front because
   * we get an ArrayIndexOutOfBoundsException otherwise
   * under some JDK implementations.
   *
   * This method also causes a fireStateChange() to be
   * called so that listeners get notified of the event.
   * See the class level comments for the reason why...
   * @param name
   * @param component
   */
  public void addANewTab(String name, JComponent component, Icon icon) {
    int selectedIndex = getSelectedIndex();
    super.insertTab(name, icon, component, null, 0);

    if (getTabCount() >= (selectedIndex + 1)) {
      setSelectedIndex(selectedIndex + 1);
    }

    super.fireStateChanged();
  }

  public void setSelectedTab(int index) {
    if (getTabCount() >= index) {
      setSelectedIndex(index);
    }

    getSelectedComponent().setVisible(true);
    getSelectedComponent().validate();
    super.fireStateChanged();
  }

  public void addANewTab(
    String name, JComponent component, Icon icon, String tooltip) {
    int selectedIndex = getSelectedIndex();
    super.insertTab(name, icon, component, tooltip, 0);

    if (getTabCount() >= (selectedIndex + 1)) {
      setSelectedIndex(selectedIndex + 1);
    }

    super.fireStateChanged();
  }

  public void remove(Component component) {
    super.remove(component);
    super.fireStateChanged();
  }
}
