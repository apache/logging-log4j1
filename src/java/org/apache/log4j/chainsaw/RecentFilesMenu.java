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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;


/**
 *  Recent files menu.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Raymond DeCampo</a>
 */
public class RecentFilesMenu extends JMenu {
  /** Logger for class */
  private static final Logger LOG = Logger.getLogger(RecentFilesMenu.class);
  private final MyTableModel mModel;
  private final XMLFileHandler mHandler;

  /**
   *  Construct a RecentFilesMenu object based on the given model.  When a
   *  file is selected from the menu, it will be loaded to the given model.
   *
   *  @param model  the table model
   */
  public RecentFilesMenu(MyTableModel model) {
    super("Recent Files");
    mModel = model;
    mHandler = new XMLFileHandler(model);
  }

  /**
   *  Rebuild the menu based on the data in the
   *  {@link org.apache.log4j.chainsaw.Preferences}.
   */
  public void rebuild() {
    removeAll();

    int order = 1;
    final Iterator fIter =
        Preferences.getInstance().getRecentFiles().iterator();

    // Menu is enabled when we have files
    setEnabled(fIter.hasNext());

    while (fIter.hasNext()) {
      String filename = (String) fIter.next();
      JMenuItem menuItem = new JMenuItem(order + " - " + filename);
      menuItem.addActionListener(new LoadRecentFileAction(filename, order));
      add(menuItem);

      if (order < 10) {
        menuItem.setMnemonic('0' + order);
      }

      order++;
    }

    updateUI();
  }

  /** Handler for menu items */
  private class LoadRecentFileAction extends AbstractAction {
    /** File to load */
    private final String mFilename;

    public LoadRecentFileAction(String filename, int order) {
      mFilename = filename;
      putValue(NAME, order + " - " + filename);
    }

    /* Load the file */
    public void actionPerformed(ActionEvent ae) {
      try {
        final File f = new File(mFilename);
        final int num = mHandler.loadFile(f);
        JOptionPane.showMessageDialog(
          RecentFilesMenu.this, "Loaded " + num + " events.", "CHAINSAW",
          JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        LOG.warn("caught an exception loading the file", e);
        JOptionPane.showMessageDialog(
          RecentFilesMenu.this, "Error parsing file - " + e.getMessage(),
          "CHAINSAW", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
