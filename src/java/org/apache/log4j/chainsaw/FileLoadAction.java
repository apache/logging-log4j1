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

package org.apache.log4j.chainsaw;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Decoder;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Allows the user to specify a particular file to open
 * and import the events into a new tab.
 *
 * @author Paul Smith <psmith@apache.org>
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
class FileLoadAction extends AbstractAction {
  private static final Logger LOG = Logger.getLogger(FileLoadAction.class);

  /**
   * This action must have a reference to a LogUI
   * window so that it can append the events it loads
   *
   */
  Decoder decoder = null;
  private LogUI parent;
  private JFileChooser chooser = null;
  private boolean remoteURL = false;

  public FileLoadAction(
    LogUI parent, Decoder decoder, String title, boolean isRemoteURL) {
    super(title);
    remoteURL = isRemoteURL;
    this.decoder = decoder;
    this.parent = parent;
  }

  /*
   * When the user chooses the Load action,
   * a File chooser is presented to allow them to
   * find an XML file to load events from.
   *
   * Any events decoded from this file are added to
   * one of the tabs.
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    String name = "";
    URL url = null;

    if (!remoteURL) {
      if (chooser == null) {
        chooser = new JFileChooser();
      }

      chooser.setDialogTitle("Load Events from XML file...");

      chooser.setAcceptAllFileFilterUsed(true);

      chooser.setFileFilter(
        new FileFilter() {
          public boolean accept(File f) {
            return (f.getName().toLowerCase().endsWith(".xml")
            || f.isDirectory());
          }

          public String getDescription() {
            return "XML files (*.xml)";
          }
        });

      chooser.showOpenDialog(parent);

      File selectedFile = chooser.getSelectedFile();

      try {
        url = selectedFile.toURL();
        name = selectedFile.getName();
      } catch (Exception ex) {
        // TODO: handle exception
      }
    } else {
      String urltext =
        JOptionPane.showInputDialog(
          parent,
          "<html>Please type in the <b>complete</b> URL to the remote XML source.</html>");

      if (urltext != null) {
        try {
          url = new URL(urltext);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
            parent, "'" + urltext + "' is not a valid URL.");
        }
      }
    }

    if (url != null) {
      Map additionalProperties = new HashMap();
      additionalProperties.put(Constants.HOSTNAME_KEY, "file");
      additionalProperties.put(Constants.APPLICATION_KEY, name);
      decoder.setAdditionalProperties(additionalProperties);

      final URL urlToUse = url;
      new Thread(
        new Runnable() {
          public void run() {
            try {
              Vector events = decoder.decode(urlToUse);
              Iterator iter = events.iterator();
              while (iter.hasNext()) {
                  parent.handler.append((LoggingEvent)iter.next());
              }
            } catch (IOException e1) {
              // TODO Handle the error with a nice msg
              LOG.error(e1);
            }
          }
        }).start();
    }
  }
}
