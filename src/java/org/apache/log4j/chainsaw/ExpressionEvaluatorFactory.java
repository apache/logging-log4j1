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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * A Factory class that automatically provides a ExpressionEvaluator.
 *
 * This factory and related impl classes wrap different regexp implementations to ensure 
 * filtering works correctly.  The factory provides support for the ORO reg expression package
 * and a simple fallback expression evaluator (case-insensitive partial-string match support).  
 *
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class ExpressionEvaluatorFactory {
  private static final ExpressionEvaluatorFactory factory =
    new ExpressionEvaluatorFactory();
  private Class expressionEvaluatorClass;
  private Class expressionEvaluatorImplementationClass;

  private ExpressionEvaluatorFactory() {
    try {
      //first try ORO regexp support
      expressionEvaluatorImplementationClass =
        Class.forName("org.apache.oro.text.regex.Perl5Pattern");
      expressionEvaluatorClass =
        Class.forName("org.apache.log4j.chainsaw.OroRegExpEvaluator");
    } catch (ClassNotFoundException cnfe1) {
        //use simple string version if ORO not found
        try {
          expressionEvaluatorClass =
            Class.forName("org.apache.log4j.chainsaw.StringExpEvaluator");
          expressionEvaluatorImplementationClass = expressionEvaluatorClass;
        } catch (ClassNotFoundException cnfe3) {
            //should not happen
            cnfe3.printStackTrace();
        }
    }
  }

  public String getEvaluatorClassName() {
    return expressionEvaluatorImplementationClass.getName();
  }

  public static ExpressionEvaluatorFactory newInstance() {
    return factory;
  }

  public ExpressionEvaluator getEvaluator(String expression) {
    ExpressionEvaluator evaluator = null;

    try {
      Class[] params = { String.class };
      Constructor constructor =
        expressionEvaluatorClass.getConstructor(params);
      Object[] args = { expression };
      evaluator = (ExpressionEvaluator) constructor.newInstance(args);
    } catch (IllegalAccessException iae) {
    } catch (InstantiationException ie) {
    } catch (NoSuchMethodException nme) {
    } catch (InvocationTargetException ite) {
    }

    return evaluator;
  }
}
