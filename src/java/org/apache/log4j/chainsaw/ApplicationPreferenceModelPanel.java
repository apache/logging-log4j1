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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.log4j.helpers.LogLog;


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
  private JTextField identifierExpression;
  private JTextField toolTipDisplayMillis;
  private JTextField cyclicBufferSize;    
  private final JTextField configurationURL = new JTextField(25);

  ApplicationPreferenceModelPanel(ApplicationPreferenceModel model) {
    this.committedPreferenceModel = model;
    initComponents();
    getOkButton().addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          uncommittedPreferenceModel.setConfigurationURL(configurationURL.getText());
          uncommittedPreferenceModel.setIdentifierExpression(
            identifierExpression.getText());
            try {
                int millis = Integer.parseInt(toolTipDisplayMillis.getText());
                if (millis >= 0) {
                    uncommittedPreferenceModel.setToolTipDisplayMillis(millis);
                }
            } catch (NumberFormatException nfe) {}
            try {
                int bufferSize = Integer.parseInt(cyclicBufferSize.getText());
                if (bufferSize >= 0) {
                    uncommittedPreferenceModel.setCyclicBufferSize(bufferSize);
                }
            } catch (NumberFormatException nfe) {}
          committedPreferenceModel.apply(uncommittedPreferenceModel);
          hidePanel();
        }
      });

    getCancelButton().addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          uncommittedPreferenceModel.apply(committedPreferenceModel);
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

    model.addPropertyChangeListener(
      new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          LogLog.warn(evt.toString());
        }
      });
    panel.setOkCancelActionListener(
      new ActionListener() {
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
    final DefaultMutableTreeNode rootNode =
      new DefaultMutableTreeNode("Preferences");
    DefaultTreeModel model = new DefaultTreeModel(rootNode);

    DefaultMutableTreeNode general =
      new DefaultMutableTreeNode(new GeneralAllPrefPanel());

    DefaultMutableTreeNode visuals =
      new DefaultMutableTreeNode(new VisualsPrefPanel());

    rootNode.add(general);
    rootNode.add(visuals);

    return model;
  }

  public class VisualsPrefPanel extends BasicPrefPanel {
    private final JRadioButton topPlacement = new JRadioButton("Top");
    private final JRadioButton bottomPlacement = new JRadioButton("Bottom");
    private final JCheckBox statusBar = new JCheckBox("Show Status bar");
    private final JCheckBox toolBar = new JCheckBox("Show Toolbar");
    private final JCheckBox receivers = new JCheckBox("Show Receivers");
    private UIManager.LookAndFeelInfo[] lookAndFeels =
      UIManager.getInstalledLookAndFeels();
    private final ButtonGroup lookAndFeelGroup = new ButtonGroup();

    private VisualsPrefPanel() {
      super("Visuals");
      setupComponents();
      setupListeners();
    }

    /**
     *
     */
    private void setupListeners() {
      topPlacement.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setTabPlacement(SwingConstants.TOP);
          }
        });
      bottomPlacement.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setTabPlacement(SwingConstants.BOTTOM);
          }
        });

      statusBar.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setStatusBar(statusBar.isSelected());
          }
        });

      toolBar.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setToolbar(toolBar.isSelected());
          }
        });

      receivers.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setReceivers(receivers.isSelected());
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "tabPlacement",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            int value = ((Integer) evt.getNewValue()).intValue();

            switch (value) {
            case SwingConstants.TOP:
              topPlacement.setSelected(true);

              break;

            case SwingConstants.BOTTOM:
              bottomPlacement.setSelected(true);

              break;

            default:
              break;
            }
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "statusBar",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            statusBar.setSelected(
              ((Boolean) evt.getNewValue()).booleanValue());
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "toolbar",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            toolBar.setSelected(((Boolean) evt.getNewValue()).booleanValue());
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "receivers",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            receivers.setSelected(
              ((Boolean) evt.getNewValue()).booleanValue());
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "lookAndFeelClassName",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            String lf = evt.getNewValue().toString();

            Enumeration enumeration = lookAndFeelGroup.getElements();

            while (enumeration.hasMoreElements()) {
              JRadioButton button = (JRadioButton) enumeration.nextElement();

              if (button.getName()!=null && button.getName().equals(lf)) {
                button.setSelected(true);

                break;
              }
            }
          }
        });
    }

    /**
     *
     */
    private void setupComponents() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JPanel tabPlacementBox = new JPanel();
      tabPlacementBox.setLayout(
        new BoxLayout(tabPlacementBox, BoxLayout.Y_AXIS));

      tabPlacementBox.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Tab Placement"));

      ButtonGroup tabPlacementGroup = new ButtonGroup();

      tabPlacementGroup.add(topPlacement);
      tabPlacementGroup.add(bottomPlacement);

      tabPlacementBox.add(topPlacement);
      tabPlacementBox.add(bottomPlacement);

      add(tabPlacementBox);
      add(statusBar);
      add(receivers);
      add(toolBar);

      JPanel lfPanel = new JPanel();
      lfPanel.setLayout(new BoxLayout(lfPanel, BoxLayout.Y_AXIS));
      lfPanel.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Look & Feel"));

      for (int i = 0; i < lookAndFeels.length; i++) {
        final UIManager.LookAndFeelInfo lfInfo = lookAndFeels[i];
        final JRadioButton lfItem = new JRadioButton(lfInfo.getName());
        lfItem.setName(lfInfo.getClassName());
        lfItem.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              uncommittedPreferenceModel.setLookAndFeelClassName(
                lfInfo.getClassName());
            }
          });
        lookAndFeelGroup.add(lfItem);
        lfPanel.add(lfItem);
      }

      try {
        final Class gtkLF =
          Class.forName("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        final JRadioButton lfIGTK = new JRadioButton("GTK+ 2.0");
        lfIGTK.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              uncommittedPreferenceModel.setLookAndFeelClassName(
                gtkLF.getName());
            }
          });
        lookAndFeelGroup.add(lfIGTK);
        lfPanel.add(lfIGTK);
      } catch (Exception e) {
        LogLog.debug("Can't find new GTK L&F, might be Windows, or <JDK1.4.2");
      }

      add(lfPanel);

      add(
        new JLabel(
          "Look and Feel change will apply the next time you start Chainsaw"));
    }
  }

  /**
   * @author psmith
   *
   */
  public class GeneralAllPrefPanel extends BasicPrefPanel {
    private final JCheckBox showNoReceiverWarning =
      new JCheckBox("Prompt me on startup if there are no Receivers defined");
    private final JCheckBox showSplash = new JCheckBox("Show Splash screen at startup");
    private final JSlider responsiveSlider =
      new JSlider(JSlider.HORIZONTAL, 1, 4, 2);
    private final JCheckBox confirmExit = new JCheckBox("Confirm Exit");
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

      identifierExpression = new JTextField(20);
      toolTipDisplayMillis = new JTextField(8);
      cyclicBufferSize = new JTextField(8);
      Box p = new Box(BoxLayout.X_AXIS);

      p.add(showNoReceiverWarning);
      p.add(Box.createHorizontalGlue());

      confirmExit.setToolTipText("Is set, you will be prompted to confirm the exit Chainsaw");
      
      setupInitialValues();
      setupListeners();

      initSliderComponent();
      add(responsiveSlider);

      JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      p1.add(new JLabel("Tab identifier"));
      p1.add(Box.createHorizontalStrut(5));
      p1.add(identifierExpression);
      add(p1);
      add(p);
      
      Box p2 = new Box(BoxLayout.X_AXIS);
      p2.add(confirmExit);
      p2.add(Box.createHorizontalGlue());
      
      Box p3 = new Box(BoxLayout.X_AXIS);
      p3.add(showSplash);
      p3.add(Box.createHorizontalGlue());
      
      add(p2);
      add(p3);

      JPanel p4 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      p4.add(new JLabel("ToolTip Display (millis)"));
      p4.add(Box.createHorizontalStrut(5));
      p4.add(toolTipDisplayMillis);
      add(p4);

      JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      p5.add(new JLabel("Cyclic buffer size"));
      p5.add(Box.createHorizontalStrut(5));
      p5.add(cyclicBufferSize);
      add(p5);

      JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      p6.add(new JLabel("Automatic Configuration"));
      p6.add(Box.createHorizontalStrut(5));
      p6.add(configurationURL);
      add(p6);

      JPanel p7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      p7.add(
        new JLabel(
          "Cyclic buffer size change will apply the next time you start Chainsaw"));
      add(p7);

      add(Box.createVerticalGlue());
      
      configurationURL.setToolTipText("A complete and valid URL identifying the location of a valid log4.xml file to auto-configure Receivers and other Plugins");
      configurationURL.setInputVerifier(new InputVerifier() {

        public boolean verify(JComponent input)
        {
            try {
                new URL(configurationURL.getText());
            } catch (Exception e) {
                return false;
            }
            return true;
        }});
    }

    private void initSliderComponent() {
      responsiveSlider.setToolTipText(
        "Adjust to set the responsiveness of the app.  How often the view is updated.");
      responsiveSlider.setSnapToTicks(true);
      responsiveSlider.setLabelTable(sliderLabelMap);
      responsiveSlider.setPaintLabels(true);
      responsiveSlider.setPaintTrack(true);

      responsiveSlider.setBorder(
        BorderFactory.createTitledBorder(
          BorderFactory.createEtchedBorder(), "Responsiveness"));

      //            responsiveSlider.setAlignmentY(0);
      //            responsiveSlider.setAlignmentX(0);
    }

    private void setupListeners() {
      uncommittedPreferenceModel.addPropertyChangeListener(
        "showNoReceiverWarning",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            showNoReceiverWarning.setSelected(
              ((Boolean) evt.getNewValue()).booleanValue());
          }
        });
      
      uncommittedPreferenceModel.addPropertyChangeListener("showSplash", new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent evt) {
          boolean value = ((Boolean)evt.getNewValue()).booleanValue();
          showSplash.setSelected(value);
        }});
      
      uncommittedPreferenceModel.addPropertyChangeListener(
        "identifierExpression",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            identifierExpression.setText(evt.getNewValue().toString());
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "responsiveness",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            int value = ((Integer) evt.getNewValue()).intValue();

            if (value >= 1000) {
              int newValue = (value - 750) / 1000;
              LogLog.debug(
                "Adjusting old Responsiveness value from " + value + " to "
                + newValue);
              value = newValue;
            }

            responsiveSlider.setValue(value);
          }
        });

        uncommittedPreferenceModel.addPropertyChangeListener(
          "toolTipDisplayMillis",
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
              toolTipDisplayMillis.setText(evt.getNewValue().toString());
            }
          });

        uncommittedPreferenceModel.addPropertyChangeListener(
          "cyclicBufferSize",
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
              cyclicBufferSize.setText(evt.getNewValue().toString());
            }
          });

      showNoReceiverWarning.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setShowNoReceiverWarning(
              showNoReceiverWarning.isSelected());
          }
        });

      showSplash.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          uncommittedPreferenceModel.setShowSplash(showSplash.isSelected());
        }});
      
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
              uncommittedPreferenceModel.setResponsiveness(value);
            }
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener(
        "confirmExit",
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            boolean value = ((Boolean) evt.getNewValue()).booleanValue();
            confirmExit.setSelected(value);
          }
        });

      uncommittedPreferenceModel.addPropertyChangeListener("configurationURL", new PropertyChangeListener() {

          public void propertyChange(PropertyChangeEvent evt) {
            String value = evt.getNewValue().toString();
            configurationURL.setText(value);
          }});
      confirmExit.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            uncommittedPreferenceModel.setConfirmExit(
              confirmExit.isSelected());
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
      identifierExpression.setText(
        uncommittedPreferenceModel.getIdentifierExpression());
    }
  }
}
