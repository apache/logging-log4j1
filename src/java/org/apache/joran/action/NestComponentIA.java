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
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.spi.OptionHandler;

import org.w3c.dom.Element;


/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class NestComponentIA extends ImplicitAction {
  static final Logger logger = Logger.getLogger(NestComponentIA.class);

  Object nestedComponent;
  int containmentType;
  PropertySetter parentBean;
  
  public boolean isApplicable(Element nestedElement, ExecutionContext ec) {
    inError = false;
    Object o = ec.peekObject();
    parentBean = new PropertySetter(o);

    String nestedElementTagName = nestedElement.getTagName();

    containmentType = parentBean.canContainComponent(nestedElementTagName);

    switch (containmentType) {
    case PropertySetter.NOT_FOUND:
      return false;

    case PropertySetter.AS_COLLECTION:
      return true;

    case PropertySetter.AS_PROPERTY:
      return true;
      
      default: 
      inError= true;
      ec.addError("PropertySetter.canContainComponent returned "+containmentType);
      return false;
    }
  }

  public void begin(ExecutionContext ec, Element e) {
    // inError was reset in isApplicable. It should not be touched here

      String className = e.getAttribute(ActionConst.CLASS_ATTRIBUTE);
      
      String tagName = e.getTagName();
      if(className == null || ActionConst.EMPTY_STR.equals(className)) {
        inError = true;
        String errMsg = "No class name attribute in <"+tagName+">";
        logger.error(errMsg);
        ec.addError(errMsg);
        return;
      }
      
      try {
        logger.debug("About to instantiate component <"+tagName+ "> of type [" + className + "]");

        nestedComponent = Loader.loadClass(className).newInstance();
         
            
        logger.debug("Pushing component <"+tagName+"> on top of the object stack.");
        ec.pushObject(nestedComponent);
      } catch (Exception oops) {
        inError = true;      
        String msg =  "Could not create component <"+tagName+">.";
        logger.error(msg, oops);
        ec.addError(msg);
      }
  }

  public void end(ExecutionContext ec, Element e) {
    if (inError) {
        return;
      }

      if (nestedComponent instanceof OptionHandler) {
        ((OptionHandler) nestedComponent).activateOptions();
      }

      Object o = ec.peekObject();

      if (o != nestedComponent) {
        logger.warn(
          "The object on the top the of the stack is not the component pushed earlier.");
      } else {
        logger.warn("Removing component from the object stack");
        ec.popObject();
       
         
        String tagName =  e.getTagName();
        // Now let us attach the component
        switch (containmentType) {
        case PropertySetter.AS_PROPERTY:
        logger.debug("Setting ["+tagName+"] to parent.");
          parentBean.setComponent(tagName, nestedComponent);
          break;

        case PropertySetter.AS_COLLECTION:
        logger.debug("Adding ["+tagName+"] to parent.");
          parentBean.addComponent(e.getTagName(), nestedComponent);

          break;
        } 
      }
  }

  public void finish(ExecutionContext ec) {
  }
}
