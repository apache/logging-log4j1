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
