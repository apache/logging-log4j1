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

/*
 */
package org.apache.log4j.chainsaw;

import java.awt.Container;
import java.awt.Dimension;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.log4j.helpers.LogLog;


/**
 *
 * A TreeModel that represents the Loggers for a given LogPanel
 *
 * @author Paul Smith <psmith@apaceh.org>
 */
class LogPanelLoggerTreeModel extends DefaultTreeModel
  implements LoggerNameListener {
  LogPanelLoggerTreeModel() {
    super(new LogPanelTreeNode("Root Logger"));
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.chainsaw.LoggerNameListener#loggerNameAdded(java.lang.String)
   */
  public void loggerNameAdded(final String loggerName) {
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          addLoggerNameInDispatchThread(loggerName);
        }
      });
  }

  private void addLoggerNameInDispatchThread(final String loggerName) {
    String[] packages = tokenize(loggerName);

    /**
     * The packages array is effectively the tree
     * path that must exist within the tree, so
     * we walk the tree ensuring each level is present
     */
    DefaultMutableTreeNode current = (DefaultMutableTreeNode) getRoot();


/**
 * This label is used to break out when descending the
 * current tree hierachy, and it has matched a package name
 * with an already existing TreeNode.
 */
outerFor: 
    for (int i = 0; i < packages.length; i++) {
      String packageName = packages[i];
      Enumeration enum = current.children();

      while (enum.hasMoreElements()) {
        DefaultMutableTreeNode child =
          (DefaultMutableTreeNode) enum.nextElement();
        String childName = child.getUserObject().toString();

        if (childName.equals(packageName)) {
          /**
           * This the current known branch to descend
           */
          current = child;

          /**
           * we've found it, so break back to the outer
           * for loop to continue processing further
           * down the tree
           */
          continue outerFor;
        }
      }

      /*
       * So we haven't found this index in the current children,
       * better create the child
       */
      final LogPanelTreeNode newChild = new LogPanelTreeNode(packageName);

      final DefaultMutableTreeNode changedNode = current;

      changedNode.add(newChild);

      final int[] changedIndices = new int[changedNode.getChildCount()];

      for (int j = 0; j < changedIndices.length; j++) {
        changedIndices[j] = j;
      }

	  nodesWereInserted(changedNode, new int[]{changedNode.getIndex(newChild)});
      nodesChanged(changedNode, changedIndices);
      current = newChild;
    }
  }

  /**
     * Takes the loggerName and tokenizes it into it's
     * package name lements returning the elements
     * via the Stirng[]
     * @param loggerName
     * @return array of strings representing the package hierarchy
     */
  private String[] tokenize(String loggerName) {
    StringTokenizer tok = new StringTokenizer(loggerName, ".");

    String[] tokens = new String[tok.countTokens()];

    int index = 0;

    while (tok.hasMoreTokens()) {
      tokens[index++] = tok.nextToken();
    }

    return tokens;
  }

  private static class LogPanelTreeNode extends DefaultMutableTreeNode {
    protected static Comparator nodeComparator =
      new Comparator() {
        public int compare(Object o1, Object o2) {
          return o1.toString().compareToIgnoreCase(o2.toString());
        }

        public boolean equals(Object obj) {
          return false;
        }
      };

    private LogPanelTreeNode(String logName) {
      super(logName);
    }

    public void insert(MutableTreeNode newChild, int childIndex) {
//      LogLog.debug("[" + this.getUserObject() + "] inserting child " + newChild + " @ index " + childIndex);
//      LogLog.debug("Children now: " + this.children);
      super.insert(newChild, childIndex);
//	  LogLog.debug("Children after insert: " + this.children);
      Collections.sort(this.children, nodeComparator);
//	  LogLog.debug("Children after sort: " + this.children);
    }
  }
}
