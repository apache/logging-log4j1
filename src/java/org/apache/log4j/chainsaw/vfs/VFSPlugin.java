package org.apache.log4j.chainsaw.vfs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.SystemInfo;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.PopupListener;
import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.chainsaw.plugins.GUIPluginSkeleton;
import org.apache.log4j.helpers.LogLog;

/**
 * GUI interface to the Jarkata Commons VFS project.
 * 
 * This is currently a Work In Progress
 * 
 * @see http://jakarta.apache.org/commons/
 * 
 * @author psmith
 *  
 */
public class VFSPlugin extends GUIPluginSkeleton {

    private static final Logger USER_MESSAGE_LOGGER = MessageCenter
            .getInstance().getLogger();

    private final FileSystemTreePanel fileSystemTree = new FileSystemTreePanel();

    private final FileObjectTable fileObjectTable = new FileObjectTable();

    private final JSplitPane splitPane = new JSplitPane();

    private final PreviewPanel previewPane = new PreviewPanel();

    private final JSplitPane rightSplit = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT);

    private VFSPluginPreferenceModel prefModel;

    private StandardFileSystemManager fileSystemManager;

    private final AOFileTablePopulater aoTablePopulator = new AOFileTablePopulater(
            fileObjectTable.getTableModel());

    private final AOChildDirectorScanner aoDirectoryScanner = new AOChildDirectorScanner();

    private Set supportedSchemes = new HashSet();

    public VFSPlugin() {
        setName("VFS");
        initGUI();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.log4j.plugins.Plugin#shutdown()
     */
    public void shutdown() {
        if (fileSystemManager != null) {
            fileSystemManager.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.log4j.spi.OptionHandler#activateOptions()
     */
    public void activateOptions() {
        try {
            this.fileSystemManager = (StandardFileSystemManager) VFS
                    .getManager();

            //            TODO load the pref model from preference storage
            this.prefModel = new VFSPluginPreferenceModel();

        } catch (FileSystemException e) {
            LogLog.error("Failed to initialise VFS", e);
            e.printStackTrace();
            setActive(false);
            return;
        }

        determineSupportedFileSystems();
        loadLocalFileSystem();
        setActive(true);
    }

    /**
     * Ensures that there is at least a Local FileSystem with the Current
     * directory loaded.
     * 
     * TODO This probably shouldn't be here after we've completed all the VFS
     * preference loading stuff.
     *  
     */
    private void loadLocalFileSystem() {
        if (prefModel.isLoadAllRootsOnStart()) {
            try {

                File[] roots = File.listRoots();
                for (int i = 0; i < roots.length; i++) {
                    File root = roots[i];

                    // Add the authors of the java.io.File class to the list of
                    // people to "have a word" with... This is ridiculous...
                    if (!(root.getAbsolutePath().toLowerCase().startsWith("a:") || root
                            .getAbsolutePath().toLowerCase().startsWith("b:"))) {
                        if (root.exists() && root.canRead()) {
                            FileObject fileObject = this.fileSystemManager
                                    .resolveFile(root.toURL().toExternalForm());
                            DefaultMutableTreeNode node = this.fileSystemTree
                                    .addFileObject("local:"
                                            + root.getAbsolutePath(),
                                            fileObject);
                            USER_MESSAGE_LOGGER.info("Adding "
                                    + root.getAbsolutePath());
                        }
                    }
                }

            } catch (Exception e) {
                LogLog.error("error creating local VFS", e);
            }
        }
    }

    /**
     * Works out which of the supported File Systems are available.
     */
    private void determineSupportedFileSystems() {
        SystemInfo info = fileSystemManager.getSystemInfo();
        String[] schemes = info.getSchemes();
        supportedSchemes.addAll(Arrays.asList(schemes));

        LogLog.info("Supported schemes: " + supportedSchemes);
    }

    /**
     *  
     */
    private void initGUI() {

        setLayout(new BorderLayout());
        rightSplit.add(this.fileObjectTable, JSplitPane.TOP);
        rightSplit.add(this.previewPane, JSplitPane.BOTTOM);
        rightSplit.setResizeWeight(0.75);
        splitPane.add(this.fileSystemTree, JSplitPane.LEFT);
        splitPane.add(rightSplit, JSplitPane.RIGHT);

        add(splitPane, BorderLayout.CENTER);

        fileSystemTree.getTree()
                .addTreeSelectionListener(this.aoTablePopulator);
        fileSystemTree.getTree().addTreeSelectionListener(
                this.aoDirectoryScanner);

        fileObjectTable.getTable().addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    final FileObject fileObject = (FileObject) fileObjectTable
                            .getFileObject(fileObjectTable.getTable()
                                    .rowAtPoint(e.getPoint()));
                    if (fileObject != null) {
                        Thread thread = new Thread(new AOGeneratePreview(
                                previewPane, fileObject, fileObjectTable));

                        thread.setPriority(Thread.MIN_PRIORITY);
                        thread.start();

                    }
                }
            }
        });
        initMenus();
    }

    /**
     *  
     */
    private void initMenus() {

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(previewPane.getActions().TOGGLE_PREVIEW_PANEL);
        PopupListener popupListener = new PopupListener(popupMenu);
        rightSplit.addMouseListener(popupListener);
        this.addMouseListener(popupListener);
        previewPane.addMouseListener(popupListener);
        fileObjectTable.getTable().addMouseListener(popupListener);
        //        TODO Work out WTF is going on with this PopupListener not being
        // picked up....
        //        TODO Work out WTF is going on with the split pane and the setVisible
        // like it used to do in LogPanel
    }

    /**
     * Reads in a certain number of lines from the FileObject and sets the
     * Preview pane's preview text from what it reads
     * 
     * @author psmith
     *  
     */
    private final class AOGeneratePreview implements Runnable {

        private final PreviewPanel previewPane;

        private final FileObject fileObject;

        private final FileObjectTable fileObjectTable;

        private AOGeneratePreview(PreviewPanel previewPane,
                FileObject fileObject, FileObjectTable fileObjectTable) {
            super();
            this.previewPane = previewPane;
            this.fileObject = fileObject;
            this.fileObjectTable = fileObjectTable;
        }

        public void run() {
            synchronized (fileObject) {
                try {
                    LineNumberReader reader = new LineNumberReader(
                            new InputStreamReader(
                                    new ProgressMonitorInputStream(
                                            fileObjectTable,
                                            "Generating Preview....",
                                            fileObject.getContent()
                                                    .getInputStream())));

                    StringBuffer buf = new StringBuffer(256);
                    String line;
                    int lineCount = 0;
                    final int MAX = prefModel.getPreviewSize();
                    while (lineCount < MAX
                            && (line = reader.readLine()) != null) {
                        buf.append(line).append("\n");
                        lineCount++;
                    }
                    previewPane.setPreviewText(buf.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * This class will scan a VFSNode for it's children and automatically add
     * them as nodes to the tree
     * 
     * @author psmith
     *  
     */
    private static class AOChildDirectorScanner implements
            TreeSelectionListener {

        private Logger USER_MESSAGE_LOGGER = MessageCenter.getInstance()
                .getLogger();

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e) {
            final JTree sourceTree = (JTree) e.getSource();
            final DefaultTreeModel treeModel = (DefaultTreeModel) sourceTree
                    .getModel();

            TreePath path = e.getPath();

            if (path == null) { return; }
            final DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) path
                    .getLastPathComponent();

            // TODO this method will NEVER remove the children and repopulates
            // if the node already has
            // children, be nice to find out
            // whether VFS can help determine whether it should 'relook' for
            // changes or something.

            if (lastPathComponent == null) {
                return;
            } else if (lastPathComponent.getChildCount() > 0) {
                // already determined Children
                return;
            }

            Object userObject = lastPathComponent.getUserObject();
            if (userObject == null || !(userObject instanceof VFSNode)) { return; }
            final VFSNode vfsNode = (VFSNode) userObject;
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    USER_MESSAGE_LOGGER.info("Scanning directory...");
                    sourceTree.setCursor(Cursor
                            .getPredefinedCursor(Cursor.WAIT_CURSOR));
                    lastPathComponent.removeAllChildren();
                    new BackgroundChildFileObjectPopulator(lastPathComponent,
                            vfsNode).run();
                    treeModel.reload(lastPathComponent);
                    sourceTree.setCursor(Cursor.getDefaultCursor());
                }
            });
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }

    }

    /**
     * Triggered when the user selects a node in the tree, it automatically
     * populates all the child node information in the table
     * 
     * @author psmith
     *  
     */
    private static class AOFileTablePopulater implements TreeSelectionListener {

        private DirectoryListTableModel tableModel;

        private Logger USER_MESSAGE_LOGGER = MessageCenter.getInstance()
                .getLogger();

        /**
         * @param tableModel
         */
        public AOFileTablePopulater(DirectoryListTableModel tableModel) {
            this.tableModel = tableModel;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e) {
            Object object = e.getSource();
            TreePath path = e.getNewLeadSelectionPath();
            // if there is no path, then there is nothing selected, so we need
            // to clear the table model... that's it!
            if (path == null) {
                this.tableModel.clear();
                return;
            }
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path
                    .getLastPathComponent();

            Object userObject = treeNode.getUserObject();
            if (!(userObject instanceof VFSNode)) { return; }
            final VFSNode vfsNode = (VFSNode) userObject;

            // IN a background thread, we now populate the children in the
            // tableModel
            Runnable runnable = new Runnable() {

                public void run() {
                    try {
                        FileObject fileObject = vfsNode.getFileObject();
                        FileObject[] fos = null;
                        synchronized (fileObject) {
                            fos = fileObject.getChildren();
                        }
                        Collection objects = new ArrayList(Arrays.asList(fos));
                        for (Iterator iter = objects.iterator(); iter.hasNext();) {
                            FileObject fo = (FileObject) iter.next();
                            synchronized (fo) {
                                if (fo.isReadable()
                                        && fo.getType().hasChildren()) {
                                    iter.remove();
                                }
                            }
                        }
                        tableModel.setFiles(objects);
                    } catch (FileSystemException ex) {
                        USER_MESSAGE_LOGGER.error(
                                "Failed to retrieve children for " + vfsNode,
                                ex);
                        return;
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();

        }
    }

    /**
     * Given a tree node, first determine if the user object is a VFSNode, and
     * if not, ignores the request and returns immediately. Otherwise a new,
     * low-priority thread is started to go look for any potential children of
     * the fileObject. As each child is located, an new child TreeNode is added
     * to the passed in node, and done within the Swing's EventDispatchThread.
     * 
     * Child TreeNodes' that are added to this node will have a VFSNode as it's
     * UserObject.
     * 
     * @param node
     */
    private static final class BackgroundChildFileObjectPopulator implements
            Runnable {

        private final VFSNode vfsNode;

        private final DefaultMutableTreeNode node;

        private Logger USER_MESSAGE_LOGGER = MessageCenter.getInstance()
                .getLogger();

        /**
         * @param node
         * @param vfsNode
         */
        public BackgroundChildFileObjectPopulator(DefaultMutableTreeNode node,
                VFSNode vfsNode) {
            this.node = node;
            this.vfsNode = vfsNode;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            USER_MESSAGE_LOGGER.debug("Looking for children of node "
                    + vfsNode.getName());
            // first, lets add a tempopary node that says "Pending..." in it
            // while we work out what's going on.
            final DefaultMutableTreeNode pendingNode = new DefaultMutableTreeNode(
                    "Pending...");
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    node.add(pendingNode);
                }
            });
            try {
                FileObject fileObject = this.vfsNode.getFileObject();
                List children = null;
                synchronized (fileObject) {
                    children = new ArrayList(Arrays.asList(fileObject
                            .getChildren()));
                }
                Collections.sort(children, VFSUtils.FILE_OBJECT_COMPARATOR);
                USER_MESSAGE_LOGGER.debug("Found " + children.size()
                        + " children");
                for (Iterator iter = children.iterator(); iter.hasNext();) {
                    FileObject child = (FileObject) iter.next();
                    // we only add non-leaf nodes, as the leaf nodes get
                    // displayed in the table
                    synchronized (child) {
                        if (child.getType().hasChildren()) {
                            final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                                    new VFSNode(child.getName().getBaseName(),
                                            child));
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    node.add(childNode);
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                USER_MESSAGE_LOGGER.error("Failed to populate Children", e);
            } finally {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        node.remove(pendingNode);
                    }
                });
            }
        }
    }
}