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

package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;


/**
  This is the central class in the log4j package. Most logging
  operations, except configuration, are done through this class.

  @since log4j 1.2

  @author Ceki G&uuml;lc&uuml; */
public class Logger extends Category {
  protected Logger(String name) {
    super(name);
  }

  /**
    Log a message object with the {@link Level#FINE FINE} level which
    is just an alias for the {@link Level#DEBUG DEBUG} level.

    <p>This method first checks if this category is <code>DEBUG</code>
    enabled by comparing the level of this category with the {@link
    Level#DEBUG DEBUG} level. If this category is
    <code>DEBUG</code> enabled, then it converts the message object
    (passed as parameter) to a string by invoking the appropriate
    {@link org.apache.log4j.or.ObjectRenderer}. It then proceeds to call all the
    registered appenders in this category and also higher in the
    hierarchy depending on the value of the additivity flag.

    <p><b>WARNING</b> Note that passing a {@link Throwable} to this
    method will print the name of the <code>Throwable</code> but no
    stack trace. To print a stack trace use the {@link #debug(Object,
    Throwable)} form instead.

    @param message the message object to log. */

  //public
  //void fine(Object message) {
  //  if(repository.isDisabled(Level.DEBUG_INT))
  //    return;
  //  if(Level.DEBUG.isGreaterOrEqual(this.getChainedLevel())) {
  //    forcedLog(FQCN, Level.DEBUG, message, null);
  //  }
  //}

  /**
   Log a message object with the <code>FINE</code> level including
   the stack trace of the {@link Throwable} <code>t</code> passed as
   parameter.

   <p>See {@link #fine(Object)} form for more detailed information.

   @param message the message object to log.
   @param t the exception to log, including its stack trace.  */

  //public
  //void fine(Object message, Throwable t) {
  //  if(repository.isDisabled(Level.DEBUG_INT))
  //    return;
  //  if(Level.DEBUG.isGreaterOrEqual(this.getChainedLevel()))
  //    forcedLog(FQCN, Level.FINE, message, t);
  //}

  /**
     Retrieve a logger by name.
  */
  public static Logger getLogger(String name) {
    return LogManager.getLogger(name);
  }

  /**
     Same as calling <code>getLogger(clazz.getName())</code>.
   */
  public static Logger getLogger(Class clazz) {
    return LogManager.getLogger(clazz.getName());
  }

  /**
     Retrieve the root logger.
   */
  public static Logger getRootLogger() {
    return LogManager.getRootLogger();
  }

  /**
     Like {@link #getLogger(String)} except that the type of logger
     instantiated depends on the type returned by the {@link
     LoggerFactory#makeNewLoggerInstance} method of the
     <code>factory</code> parameter.

     <p>This method is intended to be used by sub-classes.

     @param name The name of the logger to retrieve.

     @param factory A {@link LoggerFactory} implementation that will
     actually create a new Instance.

     @since 0.8.5 */
  public static Logger getLogger(String name, LoggerFactory factory) {
    return LogManager.getLogger(name, factory);
  }
}
