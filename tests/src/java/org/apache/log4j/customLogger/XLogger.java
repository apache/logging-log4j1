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

package org.apache.log4j.customLogger;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.xml.XLevel;


/**
   A simple example showing Logger sub-classing. It shows the
   minimum steps necessary to implement one's {@link LoggerFactory}.
   Note that sub-classes follow the hierarchy even if its loggers
   belong to different classes.
 */
public class XLogger extends Logger implements OptionHandler {
  // It's usually a good idea to add a dot suffix to the fully
  // qualified class name. This makes caller localization to work
  // properly even from classes that have almost the same fully
  // qualified class name as XLogger, such as XLogegoryTest.
  private static String FQCN = XLogger.class.getName() + ".";

  // It's enough to instantiate a factory once and for all.
  private static XFactory factory = new XFactory();
  String suffix = "";

  /**
     Just calls the parent constuctor.
   */
  protected XLogger(String name) {
    super(name);
  }

  /**
     Nothing to activate.
   */
  public void activateOptions() {
  }

  /**
     Overrides the standard debug method by appending the value of
     suffix variable to each message.
  */
  public void debug(String message) {
    super.log(FQCN, Level.DEBUG, message + " " + suffix, null);
  }

  /**
     We introduce a new printing method in order to support {@link
     XLevel#LETHAL}.  */
  public void lethal(String message, Throwable t) {
    if (repository.isDisabled(XLevel.LETHAL_INT)) {
      return;
    }

    if (XLevel.LETHAL.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, XLevel.LETHAL, message, t);
    }
  }

  /**
     We introduce a new printing method in order to support {@link
     XLevel#LETHAL}.  */
  public void lethal(String message) {
    if (repository.isDisabled(XLevel.LETHAL_INT)) {
      return;
    }

    if (XLevel.LETHAL.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, XLevel.LETHAL, message, null);
    }
  }

  public static Logger getLogger(String name) {
    return LogManager.getLogger(name, factory);
  }

  public static Logger getLogger(Class clazz) {
    return XLogger.getLogger(clazz.getName());
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  /**
     We introduce a new printing method that takes the TRACE level.
  */
  public void trace(String message, Throwable t) {
    if (repository.isDisabled(XLevel.TRACE_INT)) {
      return;
    }

    if (XLevel.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, XLevel.TRACE, message, t);
    }
  }

  /**
     We introduce a new printing method that takes the TRACE level.
  */
  public void trace(String message) {
    if (repository.isDisabled(XLevel.TRACE_INT)) {
      return;
    }

    if (XLevel.TRACE.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, XLevel.TRACE, message, null);
    }
  }

  // Any sub-class of Logger must also have its own implementation of 
  // CategoryFactory.
  public static class XFactory implements LoggerFactory {
    public XFactory() {
    }

    public Logger makeNewLoggerInstance(String name) {
      return new XLogger(name);
    }
  }
}
