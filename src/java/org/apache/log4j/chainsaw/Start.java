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

import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


/**
 * Main initialization point of Chainsaw
 * @author Paul Smith
 * @version 1.0
 */
public class Start {
  private static final String LOG4J_CONFIG_FILE = "log4j.xml";

  public static void main(String[] args) {
    initLog4J();
  }

  private static void initLog4J() {
    /** initialise log4j **/
    final FinderStrategies strategies = new FinderStrategies();
    final URL url = strategies.findConfiguration();
    
    // if a configuration file is specified, use it.
    if (url != null) {
      DOMConfigurator.configure(url);
    // else no configuration specified, create an instance to use
    } else {
      LogManager.getLogger("org.apache.log4j.chainsaw").setLevel(Level.INFO);
      ChainsawAppender appender = new ChainsawAppender();
      LogManager.getRootLogger().addAppender(appender);
      appender.activateOptions();
    }
  }

  private static class FinderStrategies implements Log4JConfigurationFinder {
    private final Collection mStrategies = new ArrayList();

    public FinderStrategies() {
      mStrategies.add(new ResourceLoaderFinder());
      mStrategies.add(new CurrentDirectoryFinder());
      mStrategies.add(new FileOpenFinder());

      // TODO: add any more stategies
    }

    public URL findConfiguration() {
      for (Iterator i = mStrategies.iterator(); i.hasNext();) {
        final Log4JConfigurationFinder finder =
          (Log4JConfigurationFinder) i.next();
        final URL resource = finder.findConfiguration();

        if (resource != null) {
          return resource;
        }
      }

      return null;
    }
  }

  /**
   * Finds the config file by looking for it using a Classloader.getResource()
   * @author Paul Smith
   * @version 1.0
   */
  private static class ResourceLoaderFinder implements Log4JConfigurationFinder {
    public URL findConfiguration() {
      return this.getClass().getClassLoader().getResource(LOG4J_CONFIG_FILE);
    }
  }

  /**
   * Finds the config file by looking for it in the current directory
   * @author Mark Womack
   * @version 1.0
   */
  private static class CurrentDirectoryFinder implements Log4JConfigurationFinder {
    public URL findConfiguration() {
      File configFile = new File("./" + LOG4J_CONFIG_FILE);
      if (configFile.exists()) {
        try {
          return new URL("file:" + LOG4J_CONFIG_FILE);
        } catch (Exception e) {
          return null;
        }
      } else {
        return null;
      }
    }
  }

  /**
   * Allows the user to locate the Log4J initialization file
   * by showing a JFileChooser
   * @author Paul Smith
   * @version 1.0
   */
  private static class FileOpenFinder implements Log4JConfigurationFinder {
    private static FileFilter LOG4J_FILE_FILTER =
      new FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().equals(LOG4J_CONFIG_FILE);
        }

        /**
         * The description of this filter. For example: "JPG and GIF Images"
         * @see FileView#getName
         */
        public String getDescription() {
          return "Log4J Configuration File";
        }
      };

    public URL findConfiguration() {
      final JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(LOG4J_FILE_FILTER);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      final int returnVal = chooser.showOpenDialog(null);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = chooser.getSelectedFile();

        try {
          return f.toURL();
        } catch (MalformedURLException ex) {
          ex.printStackTrace();
        }
      }

      return null;
    }
  }
}
