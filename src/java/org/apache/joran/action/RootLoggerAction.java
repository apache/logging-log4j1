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

package org.apache.joran.action;

import org.apache.joran.ExecutionContext;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

import org.w3c.dom.Element;


public class RootLoggerAction extends Action {
  static final String NAME_ATTR = "name";
  static final String CLASS_ATTR = "class";
  static final String ADDITIVITY_ATTR = "additivity";
  static final String EMPTY_STR = "";
  static final Class[] ONE_STRING_PARAM = new Class[] { String.class };
  Logger logger = Logger.getLogger(RootLoggerAction.class);
  Logger root;

  public void begin(ExecutionContext ec, Element loggerElement) {
    inError = false;
    logger.debug("In begin method");

    LoggerRepository repository = (LoggerRepository) ec.getObject(0);
    root = repository.getRootLogger();

    logger.debug("Pushing root logger on stack");
    ec.pushObject(root);
  }

  public void end(ExecutionContext ec, Element e) {
    logger.debug("end() called.");

    if (inError) {
      return;
    }

    Object o = ec.peekObject();

    if (o != root) {
      logger.warn(
        "The object on the top the of the stack is not the root logger");
        logger.warn("It is: "+o);
    } else {
      logger.debug("Removing root logger from top of stack.");
      ec.popObject();
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
