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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *  A PreferenceSet represents a set of properties within a
 *  {@link org.apache.log4j.chainsaw.Preferences} object that share a given
 *  prefix.  The PrefenceSet gives convenient access to the properties
 *  without need to explicitly specify the prefix.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Raymond DeCampo</a>
 */
class PreferenceSet {
  /** the prefix */
  private String mPropPrefix;

  /** the name of the set */
  private String mName;

  /** the Preferences object containing the properties */
  private Preferences mPrefs;

  /**
   *  Create a PreferenceSet with the given prefix, name and
   *  {@link org.apache.log4j.chainsaw.Preferences}.  The prefix and name
   *  are concatenated to obtain the full prefix.
   *  <p>
   *  For example, if the propPrefix is &quot;org.apache.log4j.chainsaw&quot;
   *  and the name is &quot;table&quot; the full prefix is
   *  &quot;org.apache.log4j.chainsaw.table&quot;.  The full prefix is
   *  automatically prepended to any property name passed to any method.
   *
   *  @param propPrefix   the prefix to all the properties
   *  @param name         the name of the set; supplements the prefix
   *  @param prefs        the Preferences object containing the properties
   */
  public PreferenceSet(String propPrefix, String name, Preferences prefs) {
    mPropPrefix = propPrefix;
    mName = name;
    mPrefs = prefs;
  }

  /**
   *  Get the name of the set.
   *
   *  @return the name of the set
   */
  public String getName() {
    return mName;
  }

  /**
   *  Get the value of a property.  The full prefix is prepended to the
   *  property and then the value is obtained from the
   *  {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to retrieve
   *
   *  @return  the value of the property
   */
  public String getProperty(String property) {
    return mPrefs.getProperty(getFullPrefix() + property);
  }

  /**
   *  Get the value of a given property.  If the property is not defined then
   *  the default value is returned.  The full prefix is prepended to the
   *  property and then the value is obtained from the
   *  {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to retrieve
   *  @param def       the default value
   *
   *  @return the value of the property or def if the property is not defined
   */
  public String getProperty(String property, String def) {
    return mPrefs.getProperty(getFullPrefix() + property, def);
  }

  /**
   *  Set the value of a property.  The full prefix is prepended to the
   *  property and then the value is set in the
   *  {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to set
   *  @param value     the new value
   */
  public void setProperty(String property, String value) {
    if (value == null) {
      value = "";
    }

    mPrefs.setProperty(getFullPrefix() + property, value);
  }

  /**
   *  Get the value of the given property as an integer.  If the property is
   *  not defined or cannot be parsed into an integer then def is returned.
   *  The full prefix is prepended to the property and then the value is
   *  obtained from the {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to retrieve
   *  @param def       the default value
   *
   *  @return the value of the property or def if the property cannot be
   *          expressed as an integer
   */
  public int getInteger(String property, int def) {
    return mPrefs.getInteger(getFullPrefix() + property, def);
  }

  /**
   *  Set the value of a property.  The full prefix is prepended to the
   *  property and then the value is set in the
   *  {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to set
   *  @param value     the new value
   */
  public void setInteger(String property, int value) {
    mPrefs.setInteger(getFullPrefix() + property, value);
  }

  /**
   *  Get the value of the given property as a boolean.  If the property is
   *  not defined or cannot be parsed into a boolean then def is returned.
   *  The full prefix is prepended to the property and then the value is
   *  obtained from the {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to retrieve
   *  @param def       the default value
   *
   *  @return the value of the property or def if the property cannot be
   *          expressed as a boolean
   */
  public boolean getBoolean(String property, boolean def) {
    return mPrefs.getBoolean(getFullPrefix() + property, def);
  }

  /**
   *  Set the value of a property.  The full prefix is prepended to the
   *  property and then the value is set in the
   *  {@link org.apache.log4j.chainsaw.Preferences} object.
   *
   *  @param property  the property to set
   *  @param value     the new value
   */
  public void setBoolean(String property, boolean value) {
    mPrefs.setBoolean(getFullPrefix() + property, value);
  }

  /**
   *  Remove this set of preferences from the
   *  {@link org.apache.log4j.chainsaw.Preferences} object.
   */
  public void remove() {
    List toRemove = new ArrayList(6);

    for (Iterator keys = mPrefs.keySet().iterator(); keys.hasNext();) {
      String key = (String) keys.next();

      if (key.startsWith(getFullPrefix())) {
        toRemove.add(key);
      }
    }

    for (Iterator keys = toRemove.iterator(); keys.hasNext();) {
      mPrefs.remove(keys.next());
    }
  }

  /**
   *  Get the full prefix for properties
   *
   *  @return the full prefix
   */
  private String getFullPrefix() {
    return mPropPrefix + "." + mName + ".";
  }
}
