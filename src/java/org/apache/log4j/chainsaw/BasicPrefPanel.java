/*
 */
package org.apache.log4j.chainsaw;

import javax.swing.JPanel;


/**
 * All of the Preferences panels used in this class extend from
 * this, it is used to provide standard L&F required by all.
 * @author Paul Smith
 *
 */
public abstract class BasicPrefPanel extends JPanel {
  private String title;

  protected BasicPrefPanel(String title) {
    //    	setBorder(BorderFactory.createLineBorder(Color.red));
    this.title = title;
  }
  /**
   * @return Returns the title.
   */
  public final String getTitle()
  {
    return title;
  }
  /**
   * @param title The title to set.
   */
  public final void setTitle(String title)
  {
    this.title = title;
  }
  
  public String toString() {
    return getTitle();
  }
}