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

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


/**
 *  Manages user preferences for the Chainsaw application.
 *  <p>
 *  Unless overriden by the {@link #PROP_FILE} system property, the properties
 *  file used is ${user.home}/.chainsaw/preferences.properties, where
 *  ${user.home} is the value of the system property &quot;user.home&quot;.
 *  If necessary, the file and the .chainsaw directory will be created.
 *  <p>
 *  Any property in the file may be overridden by defining the property as
 *  a system property (either programmtaically or by using the -D option to
 *  java).
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Raymond DeCampo</a>
 */
class Preferences extends Properties {
  /** Prefix to all properties related to Chainsaw. */
  public static final String PROP_PREFIX = "org.apache.log4j.chainsaw";

  /** System property used to override the default property file. */
  public static final String PROP_FILE = PROP_PREFIX + ".properties";

  /** Property defining the list of recent files. */
  public static final String FILES_PROPERTY = PROP_PREFIX + ".files";

  /** Property defining the maximum number of recent files. */
  public static final String MAX_FILES_PROPERTY = PROP_PREFIX + ".max.files";

  /** used to log messages **/
  private static final Logger LOG = Logger.getLogger(Preferences.class);

  /**
    * Single global instance based on property file in user.home or by file
    * indicated by {@link #PROP_FILE} system property.
   **/
  private static Preferences sInstance = null;

  /** The actual preferences file */
  private File prefFile = null;

  /** List of Strings, the recent files list */
  private final LinkedList files = new LinkedList();

  /** Maximum number of entries in th recent files menu */
  private int mMaxFiles = 5;

  /** The recent files menu */
  private RecentFilesMenu mRecentFilesMenu = null;

  /** Private constructor for when no preference file is found */
  private Preferences() {
  }

  /**
   *  Private constructor accepting a file
   *
   *  @param preferencesFile  file containing preferences
   *
   *  @throws IOException if the preferencesFile could not be read
   */
  private Preferences(File preferencesFile) throws IOException {
    prefFile = preferencesFile;

    InputStream in = null;

    try {
      LOG.info("Loading properties from " + prefFile);
      in = new BufferedInputStream(new FileInputStream(prefFile));
      load(in);
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   *  Get the global instance of the Preferences class.
   *
   *  @return the global instance of the Preferences class
   */
  public static Preferences getInstance() {
    if (sInstance == null) {
      sInstance = createInstance();
    }

    return sInstance;
  }

  /**
   *  Get the value of the given property.  If the property is defined as a
   *  system property, that value will be returned.
   *
   *  @param property  the property to retrieve
   *
   *  @return the value of the given property
   */
  public String getProperty(String property) {
    String result = System.getProperty(property);

    if (result == null) {
      result = super.getProperty(property);
    }

    return result;
  }

  /**
   *  Get the value of a given property.  If the property is not defined then
   *  the default value is returned.
   *
   *  @param property  the property to retrieve
   *  @param def       the default value
   *
   *  @return the value of the property or def if the property is not defined
   */
  public String getProperty(String property, String def) {
    String result = getProperty(property);

    if (result == null) {
      result = def;
    }

    return result;
  }

  /**
   *  Get the value of the given property as an integer.  If the property is
   *  not defined or cannot be parsed into an integer then def is returned.
   *
   *  @param property  the property to retrieve
   *  @param def       the default value
   *
   *  @return the value of the property or def if the property cannot be
   *          expressed as an integer
   */
  public int getInteger(String property, int def) {
    int result = def;
    String strValue = getProperty(property);

    try {
      if (strValue != null) {
        result = Integer.parseInt(strValue);
      }
    } catch (final NumberFormatException nfe) {
      // just use default value
    }

    return result;
  }

  /**
   *  Set the value of a property.
   *
   *  @param property  the property to set
   *  @param value     the value to set it to
   */
  public void setInteger(String property, int value) {
    setProperty(property, String.valueOf(value));
  }

  /**
   *  Get the value of the given property as a boolean.  If the property is
   *  not defined or cannot be parsed into a boolean then def is returned.
   *
   *  @param property  the property to retrieve
   *  @param def       the default value
   *
   *  @return the value of the property or def if the property cannot be
   *          expressed as a boolean
   */
  public boolean getBoolean(String property, boolean def) {
    boolean result = def;
    String strValue = getProperty(property);

    if (strValue != null) {
      result = Boolean.valueOf(strValue).booleanValue();
    }

    return result;
  }

  /**
   *  Set the value of a property.
   *
   *  @param property  the property to set
   *  @param value     the value to set it to
   */
  public void setBoolean(String property, boolean value) {
    setProperty(property, String.valueOf(value));
  }

  /**
   *  Save the preferences to the preferences file.
   *
   *  @throws IOException  if an error occurs while writing to the file
   */
  public void save() throws IOException {
    if (prefFile != null) {
      OutputStream out = null;

      try {
        LOG.info("Saving preferences to " + prefFile);
        out = new BufferedOutputStream(new FileOutputStream(prefFile));
        store(out, "LOG4J Chainsaw property file");
      } finally {
        try {
          if (out != null) {
            out.close();
          }
        } catch (final IOException ioe) {
          LOG.warn(
            "Error closing preferences file " + prefFile.getPath(), ioe);
        }
      }
    }
  }

  /**
   *  Notify the preferences that a file has been loaded for integration into
   *  the recent files list.
   *
   *  @param filename  the file that was loaded
   */
  public void fileLoaded(String filename) {
    files.remove(filename);
    files.addFirst(filename);

    if (files.size() > mMaxFiles) {
      files.removeLast();
    }

    rebuildRecentFilesData();
  }

  /**
   *  Get the maximum number files in the recent files list.
   *
   *  @return the maximum number files in the recent files list
   */
  public int getMaxFiles() {
    return mMaxFiles;
  }

  /**
   *  Set the maximum number of files in the recent files list.  If the value
   *  is less than the current size of the list, the list will be truncated.
   *
   *  @param newMaxFiles  the new value for the maximum number of files in the
   *                      recent files list
   */
  public void setMaxFiles(int newMaxFiles) {
    mMaxFiles = newMaxFiles;
    setInteger(MAX_FILES_PROPERTY, mMaxFiles);

    while (files.size() > mMaxFiles) {
      files.removeLast();
    }

    rebuildRecentFilesData();
  }

  /**
   *  Set the recent files menu.  The menu registered will be notified of
   *  changes in the recent files list.
   *
   *  @param newMenu  the new recent files menu
   */
  public void setRecentFilesMenu(RecentFilesMenu newMenu) {
    mRecentFilesMenu = newMenu;
  }

  /**
   *  Get a {@link PreferenceSet} based on this Preferences object.
   *
   *  @param prefix  the prefix for the set
   *  @param name    the name of the set
   *
   *  @return the {@link PreferenceSet} based on this Preferences object with
   *          the given prefix and name
   */
  public PreferenceSet getPreferenceSet(String prefix, String name) {
    return new PreferenceSet(prefix, name, this);
  }

  /**
   *  Get an unmodifiable list of the recent files.  Entries in the list are
   *  {@link java.lang.String}s.
   *
   *  @return an unmodifiable list of strings representing the recent files.
   */
  public List getRecentFiles() {
    return Collections.unmodifiableList(files);
  }

  /* Create the single instance */
  private static Preferences createInstance() {
    String filename = getPropertyFilename();
    File file = new File(filename);

    if (!file.exists()) {
      LOG.debug("Creating preferences file " + filename);

      try {
        file.createNewFile();
      } catch (final IOException ioe) {
        LOG.warn("Could not create file " + filename, ioe);
      }
    }

    if (file.exists()) {
      try {
        sInstance = new Preferences(file);
      } catch (final IOException ioe) {
        LOG.warn("Could not read file " + filename, ioe);
      }
    }

    if (sInstance == null) {
      sInstance = new Preferences();
    }

    sInstance.load();

    return sInstance;
  }

  /* Determine the property file to use */
  private static String getPropertyFilename() {
    String filename = System.getProperty(PROP_FILE);

    if (filename == null) {
      String userHome = System.getProperty("user.home", "");
      String dirName = userHome + File.separator + ".chainsaw";
      File dir = new File(dirName);

      if (!dir.exists()) {
        LOG.debug("Creating preferences directory.");

        if (!dir.mkdir()) {
          LOG.warn("Could not create directory " + dir.getPath());
        }
      }

      filename = dirName + File.separator + "preferences.properties";
    }

    return filename;
  }

  /** Load the preferences from the file. */
  private void load() {
    mMaxFiles = getInteger(MAX_FILES_PROPERTY, 5);
    loadFiles();
  }

  /** Load the recent files list. */
  private void loadFiles() {
    final char[] ch = getProperty(FILES_PROPERTY, "").toCharArray();
    final StringBuffer filename = new StringBuffer(ch.length);
    int fileCount = 0;

    // The recent files list is kept as a comma-separated list of filenames
    // Commas are escaped by doubling
    for (int i = 0; (i < ch.length) && (fileCount < mMaxFiles); i++) {
      if (ch[i] == ',') {
        if (((i + 1) < ch.length) && (ch[i + 1] == ',')) {
          // Double comma equals one escaped comma
          filename.append(',');
          i++;
        } else {
          // denotes the end of a filename
          files.add(filename.toString());
          fileCount++;
          filename.setLength(0);
        }
      } else {
        filename.append(ch[i]);
      }
    }

    if ((filename.length() > 0) && (fileCount < mMaxFiles)) {
      // handle last file
      files.add(filename.toString());
    }

    if (mRecentFilesMenu != null) {
      mRecentFilesMenu.rebuild();
    }
  }

  /** Rebuild the recent files list property and menu */
  private void rebuildRecentFilesData() {
    StringBuffer fileList = new StringBuffer();
    boolean first = true;
    Iterator fileIter = files.iterator();

    while (fileIter.hasNext()) {
      String filename = (String) fileIter.next();

      if (!first) {
        fileList.append(',');
      }

      // Add the file name to the list
      int index = 0;
      int lastIndex = 0;

      while ((index >= 0) && (lastIndex < filename.length())) {
        index = filename.indexOf(',', lastIndex);

        if (index >= 0) {
          // Append the filename, up to the and including the comma
          fileList.append(filename.substring(lastIndex, index + 1));
          fileList.append(',');
          lastIndex = index + 1;
        } else {
          // Append the rest of the filename
          fileList.append(filename.substring(lastIndex));
        }
      }

      first = false;
    }

    setProperty(FILES_PROPERTY, fileList.toString());

    if (mRecentFilesMenu != null) {
      mRecentFilesMenu.rebuild();
    }
  }
}
