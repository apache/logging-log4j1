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

package org.apache.log4j.helpers;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;
import java.util.Vector;


/**
   A straightforward implementation of the {@link AppenderAttachable}
   interface.

   @author Ceki G&uuml;lc&uuml;
   @since version 0.9.1 */
public class AppenderAttachableImpl implements AppenderAttachable {
  /** Array of appenders. */
  protected Vector appenderList;

  /**
     Attach an appender. If the appender is already in the list in
     won't be added again.
  */
  public void addAppender(Appender newAppender) {
    // Null values for newAppender parameter are strictly forbidden.
    if (newAppender == null) {
      return;
    }

    if (appenderList == null) {
      appenderList = new Vector(1);
    }

    if (!appenderList.contains(newAppender)) {
      appenderList.addElement(newAppender);
    }
  }

  /**
     Call the <code>doAppend</code> method on all attached appenders.  */
  public int appendLoopOnAppenders(LoggingEvent event) {
    int size = 0;
    Appender appender;

    if (appenderList != null) {
      size = appenderList.size();

      for (int i = 0; i < size; i++) {
        appender = (Appender) appenderList.elementAt(i);
        appender.doAppend(event);
      }
    }

    return size;
  }

  /**
     Get all attached appenders as an Enumeration. If there are no
     attached appenders <code>null</code> is returned.

     @return Enumeration An enumeration of attached appenders.
   */
  public Enumeration getAllAppenders() {
    if (appenderList == null) {
      return null;
    } else {
      return appenderList.elements();
    }
  }

  /**
     Look for an attached appender named as <code>name</code>.

     <p>Return the appender with that name if in the list. Return null
     otherwise.

   */
  public Appender getAppender(String name) {
    if ((appenderList == null) || (name == null)) {
      return null;
    }

    int size = appenderList.size();
    Appender appender;

    for (int i = 0; i < size; i++) {
      appender = (Appender) appenderList.elementAt(i);

      if (name.equals(appender.getName())) {
        return appender;
      }
    }

    return null;
  }

  /**
     Returns <code>true</code> if the specified appender is in the
     list of attached appenders, <code>false</code> otherwise.

     @since 1.2 */
  public boolean isAttached(Appender appender) {
    if ((appenderList == null) || (appender == null)) {
      return false;
    }

    int size = appenderList.size();
    Appender a;

    for (int i = 0; i < size; i++) {
      a = (Appender) appenderList.elementAt(i);

      if (a == appender) {
        return true;
      }
    }

    return false;
  }

  /**
   * Remove and close all previously attached appenders.
   * */
  public void removeAllAppenders() {
    if (appenderList != null) {
      int len = appenderList.size();

      for (int i = 0; i < len; i++) {
        Appender a = (Appender) appenderList.elementAt(i);
        a.close();
      }

      appenderList.removeAllElements();
      appenderList = null;
    }
  }

  /**
     Remove the appender passed as parameter form the list of attached
     appenders.  */
  public void removeAppender(Appender appender) {
    if ((appender == null) || (appenderList == null)) {
      return;
    }

    appenderList.removeElement(appender);
  }

  /**
     Remove the appender with the name passed as parameter form the
     list of appenders.
   */
  public void removeAppender(String name) {
    if ((name == null) || (appenderList == null)) {
      return;
    }

    int size = appenderList.size();

    for (int i = 0; i < size; i++) {
      if (name.equals(((Appender) appenderList.elementAt(i)).getName())) {
        appenderList.removeElementAt(i);

        break;
      }
    }
  }
}
