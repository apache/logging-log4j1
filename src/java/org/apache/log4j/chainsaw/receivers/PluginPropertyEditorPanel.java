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
package org.apache.log4j.chainsaw.receivers;

import org.apache.log4j.Level;
import org.apache.log4j.chainsaw.Generator;
import org.apache.log4j.chainsaw.helper.TableCellEditorFactory;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketHubReceiver;
import org.apache.log4j.plugins.Plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;


/**
 * A panel that allows the user to edit a particular Plugin, by using introspection
 * this class discovers the modifiable properties of the Plugin
 * @author Paul Smith <psmith@apache.org>
 * @since 1.3
 */
public class PluginPropertyEditorPanel extends JPanel {

    private final JScrollPane scrollPane = new JScrollPane();
    private final JTable propertyTable = new JTable();

    private Plugin plugin;
    private TableModel defaultModel = new DefaultTableModel(
            new String[] { "Property", "Value" }, 1);

    /**
     *
     */
    public PluginPropertyEditorPanel() {
        super();
        initComponents();
        setupListeners();
    }

    /**
     *
     */
    private void initComponents() {
        setPreferredSize(new Dimension(160, 120));
        setLayout(new BorderLayout());
        scrollPane.setViewportView(propertyTable);

        add(scrollPane, BorderLayout.CENTER);

        propertyTable.setModel(
            defaultModel = new DefaultTableModel(
                    new String[] { "Property", "Value" }, 1));

    }

    /**
     *
     */
    private void setupListeners() {
        addPropertyChangeListener("plugin", new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {

                    final Plugin p = (Plugin) evt.getNewValue();

                    if (p != null) {

                        try {

                            PluginPropertyTableModel model =
                                new PluginPropertyTableModel(p);
                            propertyTable.setModel(model);
                            propertyTable.getColumnModel().getColumn(1)
                            .setCellEditor(new PluginTableCellEditor());
                            propertyTable.setEnabled(true);
                        } catch (Throwable e) {
                            LogLog.error("Failed to introspect the Plugin", e);
                        }
                    } else {
                        propertyTable.setModel(defaultModel);
                        propertyTable.setEnabled(false);
                    }

                }
            });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Property Editor Test bed");
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    System.exit(1);
                }
            });

        PluginPropertyEditorPanel panel = new PluginPropertyEditorPanel();


        frame.getContentPane().add(panel);
        frame.pack();

        frame.setVisible(true);

        SocketHubReceiver r = new SocketHubReceiver();

        panel.setPlugin(r);

        try {
            Thread.sleep(3000);

            panel.setPlugin(new Generator("MyPlugin"));
        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    /**
     * @return Returns the plugin.
     */
    public final Plugin getPlugin() {

        return plugin;
    }

    /**
     * @param plugin The plugin to set.
     */
    public final void setPlugin(Plugin plugin) {

        Plugin oldValue = this.plugin;
        this.plugin = plugin;
        firePropertyChange("plugin", oldValue, this.plugin);
    }

    /**
     * @author psmith
     *
     */
    private class PluginTableCellEditor extends AbstractCellEditor
        implements TableCellEditor {

        private Map editorMap = new HashMap();
        private DefaultCellEditor defaultEditor = new DefaultCellEditor(
                new JTextField());
        private DefaultCellEditor currentEditor = defaultEditor;

        private PluginTableCellEditor() {

            editorMap.put(Boolean.class,
                TableCellEditorFactory.createBooleanTableCellEditor());
            editorMap.put(Level.class,
                TableCellEditorFactory.createLevelTableCellEditor());
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

            if (editorMap.containsKey(value.getClass())) {

                DefaultCellEditor editor =
                    (DefaultCellEditor) editorMap.get(value.getClass());
                LogLog.debug("Located CellEditor for " + value.getClass());
                currentEditor = editor;

                return currentEditor.getTableCellEditorComponent(table, value,
                    isSelected, row, column);
            }

            currentEditor = defaultEditor;
            LogLog.debug("Cell value class " + value.getClass() +
                " not know, using default editor");

            return defaultEditor.getTableCellEditorComponent(table, value,
                isSelected, row, column);
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#getCellEditorValue()
         */
        public Object getCellEditorValue() {

            return currentEditor.getCellEditorValue();
        }

    }

    private static class PluginPropertyTableModel extends AbstractTableModel {

        private final PropertyDescriptor[] descriptors;
        private final Plugin plugin;

        private PluginPropertyTableModel(Plugin p)
            throws IntrospectionException {
            super();

            BeanInfo beanInfo = Introspector.getBeanInfo(p.getClass());

            List list = new ArrayList(Arrays.asList(
                        beanInfo.getPropertyDescriptors()));

            Collections.sort(list, new Comparator() {

                    public int compare(Object o1, Object o2) {

                        PropertyDescriptor d1 = (PropertyDescriptor) o1;
                        PropertyDescriptor d2 = (PropertyDescriptor) o2;

                        return d1.getDisplayName().compareToIgnoreCase(
                            d2.getDisplayName());
                    }
                });
            this.plugin = p;
            this.descriptors = (PropertyDescriptor[]) list.toArray(
                    new PropertyDescriptor[0]);
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int col) {

            PropertyDescriptor d = descriptors[row];

            switch (col) {

            case 1:

                try {

                    Object object = d.getReadMethod().invoke(plugin,
                            new Object[0]);

                    if (object != null) {

                        return object;
                    }
                } catch (Exception e) {
                    LogLog.error(
                        "Error reading value for PropertyDescriptor " + d);
                }

                return "";

            case 0:
                return d.getName();
            }

            return null;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnCount()
         */
        public int getColumnCount() {

            return 2;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getRowCount()
         */
        public int getRowCount() {

            return descriptors.length;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {

//        TODO Determine if the property is one of the ones a User could edit
            if (columnIndex == 1) {

                return descriptors[rowIndex].getWriteMethod() != null;
            }

            return false;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int column) {

            return (column == 0) ? "Property" : "Value";
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {


            if (columnIndex == 1) {
                aValue = translateValueIfNeeded(rowIndex, aValue);
                LogLog.debug(
                    "setValueAt, " + rowIndex + ", " + columnIndex +
                    ", value=" + aValue + ", valueClass" + aValue.getClass());

                try {
                    descriptors[rowIndex].getWriteMethod().invoke(plugin,
                        new Object[] { aValue });
                    fireTableCellUpdated(rowIndex, columnIndex);
                } catch (IllegalArgumentException e) {
                    // ignore
                } catch (Exception e) {
                    LogLog.error(
                        "Failed to modify the Plugin because of Exception", e);
                }

            } else {
                super.setValueAt(aValue, rowIndex, columnIndex);
            }
        }

        /**
         * @param columnIndex
         * @param value
         * @return
         */
        private Object translateValueIfNeeded(int row, Object value) {

            if ((descriptors[row].getPropertyType() == int.class) ||
                    (descriptors[row].getPropertyType() == Integer.class)) {

                try {

                    return Integer.valueOf(value.toString());
                } catch (Exception e) {
                    LogLog.error("Failed to convert to Integer type");
                }
            }

            return value;
        }
    }
}
