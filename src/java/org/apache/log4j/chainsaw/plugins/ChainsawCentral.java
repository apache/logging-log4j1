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

package org.apache.log4j.chainsaw.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Appender;
import org.apache.log4j.chainsaw.ChainsawAppender;
import org.apache.log4j.chainsaw.ChainsawAppenderHandler;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.helpers.LogLog;


/**
 * @author Paul Smith <psmith@apache.org>
 */
public class ChainsawCentral extends GUIPluginSkeleton {
  private final JLabel dataRateLabel = new JLabel();
  private final NumberFormat nf = NumberFormat.getNumberInstance();
  private ChainsawAppenderHandler handler;
  private LinkedList dataRateModel = new LinkedList();

  /**
   *
   */
  public ChainsawCentral() {
    super();
    setName("ChainsawCentral");
    initComponents();
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#shutdown()
   */
  public void shutdown() {
    // TODO Auto-generated method stub
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  public void activateOptions() {
    Enumeration enumeration =
      getLoggerRepository().getRootLogger().getAllAppenders();

    while (enumeration.hasMoreElements()) {
      Appender appender = (Appender) enumeration.nextElement();

      if (appender instanceof ChainsawAppenderHandler) {
        this.handler = (ChainsawAppenderHandler) appender;

        break;
      }
      if (appender instanceof ChainsawAppender) {
          Appender chainsawAppender = ((ChainsawAppender)appender).getAppender();
          if (chainsawAppender instanceof ChainsawAppenderHandler) {
              handler = (ChainsawAppenderHandler)chainsawAppender;
              break;
          }
      }
    }

    if (this.handler == null) {
      LogLog.error("Failed to find the ChainsawAppenderHandler");
    }

    this.handler.addPropertyChangeListener(
      "dataRate",
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          final Double value = (Double) evt.getNewValue();
          addDataRateToModel(value);
          SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                dataRateLabel.setText(nf.format(value));
              }
            });
        }
      });
  }

  /**
   * @param double1
   */
  protected void addDataRateToModel(Double double1) {
    this.dataRateModel.add(double1);
  }

  /**
   *
   */
  private void initComponents() {
    setLayout(new GridLayout(1, 1));
    nf.setMaximumFractionDigits(1);
    nf.setMinimumFractionDigits(1);

//    JPanel grid1 = new JPanel(new GridLayout(1, 2));
//    grid1.add(new JLabel("Data Rate:"));
//    grid1.add(dataRateLabel);
//
//    RateGraph rateGraph = new RateGraph();
//    rateGraph.setBorder(
//      BorderFactory.createTitledBorder(
//        BorderFactory.createEtchedBorder(), "Data Rate Graph"));
//    grid1.add(rateGraph);
//    add(grid1);

    JPanel mc = new JPanel(new BorderLayout());
    mc.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Message Center"));
    mc.add(MessageCenter.getInstance().getGUIComponent(), BorderLayout.CENTER);

    add(mc);
  }

  private static class RateGraph extends JComponent {
    /**
    *
    */
    public RateGraph() {
      super();
      initCanvas();
    }

    private void initCanvas() {
      setBackground(Color.black);
      setForeground(Color.red);
      setOpaque(true);
    }
  }
}
