/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
   * Create the tabbed pane.  
   *
   */
  public ChainsawTabbedPane() {
    super();
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

    //only select the previously existing tab if there is more than one tab
    if (getTabCount() > 1) {
      setSelectedIndex(Math.min(selectedIndex + 1, getTabCount() - 1));
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
