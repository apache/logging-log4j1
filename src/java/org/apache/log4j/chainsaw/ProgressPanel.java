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

import java.awt.BorderLayout;
import java.awt.Color;

import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * A simple ProgressPanel that can be used, a little more flexible
 * than ProgressMonitor when you want it to be shown REGARDLESS
 * of any timeouts etc.
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ProgressPanel extends JPanel {
  private final JLabel messageLabel = new JLabel();
  private final JProgressBar progressBar;

  ProgressPanel(int min, int max, String msg) {
    this.progressBar = new JProgressBar(min, max);
    setBorder(BorderFactory.createLineBorder(Color.black, 1));
    messageLabel.setHorizontalAlignment(JLabel.HORIZONTAL);
    messageLabel.setText(msg);
    setLayout(new BorderLayout());

    add(progressBar, BorderLayout.CENTER);
    add(messageLabel, BorderLayout.SOUTH);
  }

  public void setMessage(final String string) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          messageLabel.setText(string);
        }
      });
  }

  public void setProgress(final int progress) {
    try {
      SwingUtilities.invokeAndWait(
        new Runnable() {
          public void run() {
            progressBar.setValue(progress);
          }
        });
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
