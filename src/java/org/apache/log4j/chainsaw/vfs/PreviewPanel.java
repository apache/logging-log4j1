package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.chainsaw.SmallButton;
import org.apache.log4j.chainsaw.icons.LineIconFactory;

public class PreviewPanel extends JPanel {

    private Actions previewActions = new Actions();
    
    private SmallButton closeButton = new SmallButton();

    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(textArea);
    
    public PreviewPanel() {
        initGUI();
        initListeners();
    }

    Actions getActions() {
        return previewActions;
    }
    
    /**
     * 
     */
    private void initListeners() {
    }

    void initGUI() {
        setLayout(new BorderLayout());

        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getActions().TOGGLE_PREVIEW_PANEL.actionPerformed(e);
            }});
        closeButton.setText("");
        closeButton.setToolTipText(getActions().TOGGLE_PREVIEW_PANEL.getValue(Action.SHORT_DESCRIPTION).toString());
        closeButton.setIcon(LineIconFactory.createCloseIcon());
        textArea.setEditable(false);
        
        Box box = Box.createHorizontalBox();
        box.add(new JLabel("Preview"));
        box.add(Box.createHorizontalGlue());
        box.add(closeButton);

        add(box, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    }
    
    class Actions{
      Action TOGGLE_PREVIEW_PANEL = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
          boolean oldValue = PreviewPanel.this.isVisible();
          boolean newValue = !oldValue;
          PreviewPanel.this.setVisible(newValue);
          PreviewPanel.this.firePropertyChange("visible", oldValue, newValue);
          PreviewPanel.this.invalidate();
        }};
        
        private Actions() {
            TOGGLE_PREVIEW_PANEL.putValue(Action.SHORT_DESCRIPTION, "Hides the Preview Panel");
            TOGGLE_PREVIEW_PANEL.putValue(Action.NAME, "Hide Preview Panel");

            PreviewPanel.this.addPropertyChangeListener("visible", new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    boolean newValue = ((Boolean)evt.getNewValue()).booleanValue();
                    if(newValue) {
                        TOGGLE_PREVIEW_PANEL.putValue(Action.SHORT_DESCRIPTION, "Hides the Preview Panel");
                        TOGGLE_PREVIEW_PANEL.putValue(Action.NAME, "Hide Preview Panel");
                    }else {
                        TOGGLE_PREVIEW_PANEL.putValue(Action.SHORT_DESCRIPTION, "Shows the Preview Panel");
                        TOGGLE_PREVIEW_PANEL.putValue(Action.NAME, "Show Preview Panel");
                    }
                }});
        }
    }

    /**
     * @param string
     */
    public void setPreviewText(String string) {
        textArea.setText(string);
    }
}