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

import org.apache.log4j.helpers.LogLog;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;


/**
 * A panel used by the user to modify any application-wide preferences.
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ApplicationPreferenceModelPanel extends AbstractPreferencePanel {

    private ApplicationPreferenceModel committedPreferenceModel;
    private ApplicationPreferenceModel uncommittedPreferenceModel =
        new ApplicationPreferenceModel();

    ApplicationPreferenceModelPanel(ApplicationPreferenceModel model) {
        this.committedPreferenceModel = model;
        initComponents();
        getOkButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    committedPreferenceModel.apply(uncommittedPreferenceModel);
                    hidePanel();
                }
            });

        getCancelButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    hidePanel();
                }
            });
    }

    public static void main(String[] args) {

        JFrame f = new JFrame("App Preferences Panel Test Bed");
        ApplicationPreferenceModel model = new ApplicationPreferenceModel();
        ApplicationPreferenceModelPanel panel =
            new ApplicationPreferenceModelPanel(model);
        f.getContentPane().add(panel);

        model.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    LogLog.warn(evt.toString());

                }
            });
        panel.setOkCancelActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(1);
                }
            });

        f.setSize(640, 480);
        f.show();
    }

    /**
     * Ensures this panels DISPLAYED model is in sync with
     * the model initially passed to the constructor.
     *
     */
    public void updateModel() {
        this.uncommittedPreferenceModel.apply(committedPreferenceModel);
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.chainsaw.AbstractPreferencePanel#createTreeModel()
     */
    protected TreeModel createTreeModel() {

        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                "Preferences");
        DefaultTreeModel model = new DefaultTreeModel(rootNode);

        DefaultMutableTreeNode general = new DefaultMutableTreeNode(
                new GeneralAllPrefPanel());


        rootNode.add(general);

        return model;

    }

    /**
     * @author psmith
     *
     */
    public class GeneralAllPrefPanel extends BasicPrefPanel {

        private final JCheckBox showNoReceiverWarning = new JCheckBox(
                "Prompt me on startup if there are no Receivers defined");
        private final JSlider responsiveSlider = new JSlider(JSlider.HORIZONTAL,
                1, 4, 2);

        Dictionary sliderLabelMap = new Hashtable();

        /**
         * @param title
         */
        public GeneralAllPrefPanel() {
            super("General");

            GeneralAllPrefPanel.this.initComponents();

        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


            Box p = new Box(BoxLayout.X_AXIS);

            p.add(showNoReceiverWarning);
            p.add(Box.createHorizontalGlue());

            setupInitialValues();
            setupListeners();

            initSliderComponent();

            add(responsiveSlider);
            add(p);
            add(Box.createVerticalGlue());


        }

        private void initSliderComponent() {
            responsiveSlider.setToolTipText(
                "Adjust to set the responsiveness of the app.  How often the view is updated.");
            responsiveSlider.setSnapToTicks(true);
            responsiveSlider.setLabelTable(sliderLabelMap);
            responsiveSlider.setPaintLabels(true);
            responsiveSlider.setPaintTrack(true);

            responsiveSlider.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Responsiveness"));

//            responsiveSlider.setAlignmentY(0);
//            responsiveSlider.setAlignmentX(0);
        }

        private void setupListeners() {
            uncommittedPreferenceModel.addPropertyChangeListener(
                "showNoReceiverWarning", new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        showNoReceiverWarning.setSelected(
                            ((Boolean) evt.getNewValue()).booleanValue());

                    }
                });
            uncommittedPreferenceModel.addPropertyChangeListener(
                "responsiveness", new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {

                        int value = ((Integer) evt.getNewValue()).intValue();

                        if (value >= 1000) {

                            int newValue = (value - 750) / 1000;
                            LogLog.debug(
                                "Adjusting old Responsiveness value from " +
                                value + " to " + newValue);
                            value = newValue;
                        }

                        responsiveSlider.setValue(value);
                    }
                });
            showNoReceiverWarning.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        uncommittedPreferenceModel.setShowNoReceiverWarning(
                            showNoReceiverWarning.isSelected());
                    }
                });

            responsiveSlider.getModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {

                        if (responsiveSlider.getValueIsAdjusting()) {

                            /**
                             * We'll wait until it stops.
                             */
                        } else {

                            int value = responsiveSlider.getValue();

                            if (value == 0) {
                                value = 1;
                            }

                            LogLog.debug("Adjust responsiveness to " + value);
                            uncommittedPreferenceModel.setResponsiveness(
                                value);
                        }
                    }
                });
        }

        private void setupInitialValues() {
            sliderLabelMap.put(new Integer(1), new JLabel("Fastest"));
            sliderLabelMap.put(new Integer(2), new JLabel("Fast"));
            sliderLabelMap.put(new Integer(3), new JLabel("Medium"));
            sliderLabelMap.put(new Integer(4), new JLabel("Slow"));

//          
            showNoReceiverWarning.setSelected(
                uncommittedPreferenceModel.isShowNoReceiverWarning());
        }

    }

}
