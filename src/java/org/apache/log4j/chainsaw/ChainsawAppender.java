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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;


/**
 * ChainsawAppender receives LoggingEvents from the local
 * Log4J environment, and appends them into a model that
 * can be used inside a Swing GUI
 * @author Paul Smith
 * @version 1.0
 */
public class ChainsawAppender
    extends AppenderSkeleton{

  private Appender appender;
  
  /**
   * The in-JVM singleton instance of the ChainsawAppender.
   *
   * If somehow Log4j initialises more than one, then the first one to
   * initialise wins!
   */
  private static ChainsawAppender sSharedAppender = null;

  /**
   * The classname of the viewer to create to view the events.
   */
  private String viewerClassname;
  private String hostname = "localhost";
  private String application = "app";

  /**
   * Constructor, initialises the singleton instance of the appender
   */
  public ChainsawAppender() {
    synchronized (ChainsawAppender.class) {
      if (sSharedAppender == null) {
        sSharedAppender = this;
      }
    }
  }

  /**
   * Return the singleton instance of the ChainsawAppender, it should only
   * be initialised once.
   * @return the One and only instance of the ChainsawAppender that is
   * allowed to be referenced by the GUI
   */
  static ChainsawAppender getInstance() {
    return sSharedAppender;
  }

  /**
   * This appender does not require layout and so return false
   * @return false and only false
   */
  public boolean requiresLayout() {
    return false;
  }
  
  public Appender getAppender() {
      return appender;
  } 

  public void setAppender(Appender appender) {
    this.appender = appender;
  }
  
  /**
   * Appends the event
   * @param aEvent the LoggingEvent to append
   */
  protected void append(LoggingEvent aEvent) {
      if (hostname != null) {
        aEvent.setProperty(Constants.HOSTNAME_KEY, hostname);
      }

      if (application != null) {
        aEvent.setProperty(Constants.APPLICATION_KEY, application);
      }

      appender.doAppend(aEvent);
  }

  /**
   * Instantiates and activates an instance of a ChainsawViewer
   * to view the contents of this appender.
   */
  public void activateOptions() {
    if (viewerClassname == null) {
      viewerClassname = "org.apache.log4j.chainsaw.DefaultViewer";
    }
      
    ChainsawViewer viewer = 
      (ChainsawViewer) OptionConverter.instantiateByClassName(viewerClassname, 
        ChainsawViewer.class, null);
        
    if (viewer != null) {
      viewer.activateViewer(this);
    }
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException uhe) {
      try {
        hostname = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException uhe2) {
      }
    }
  }

  /**
   * Close does nothing
   */
  public void close() {
  }

  /**
   * Sets the viewer class to use to view the events.  The class must
   * implement the ChainsawViewer interface.
   *
   * @param classname The class name of the viewer class.
   */
  public void setViewerClass(String classname) {
    viewerClassname = classname;
  }

  /**
   * Gets the viewer class to use to view the events.
   *
   * @return The class name of the viewer class.
   */
  public String getViewerClass() {
    return viewerClassname;
  }

  /**
   * The <b>Application</b> option takes a string value which should be the
   * name of the application getting logged
   */
  public void setApplication(String lapp) {
    this.application = lapp;
  }

  /**
   *  Returns value of the <b>Application</b> option.
   */
  public String getApplication() {
    return application;
  }


}
