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
    setLayout(new GridLayout(2, 1));
    nf.setMaximumFractionDigits(1);
    nf.setMinimumFractionDigits(1);

    JPanel grid1 = new JPanel(new GridLayout(1, 2));
    grid1.add(new JLabel("Data Rate:"));
    grid1.add(dataRateLabel);

    RateGraph rateGraph = new RateGraph();
    rateGraph.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "Data Rate Graph"));
    grid1.add(rateGraph);
    add(grid1);

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
      setBackground(Color.BLACK);
      setForeground(Color.RED);
      setOpaque(true);
    }
  }
}
