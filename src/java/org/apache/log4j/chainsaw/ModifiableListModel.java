/*
 * Created on Dec 12, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.log4j.chainsaw;

import javax.swing.DefaultListModel;

/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ModifiableListModel extends DefaultListModel {
  public void fireContentsChanged(){
    fireContentsChanged(this,0, this.size());
  }

}
