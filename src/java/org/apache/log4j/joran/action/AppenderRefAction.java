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

package org.apache.log4j.joran.action;

import org.apache.joran.ErrorItem;
import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;

import org.xml.sax.Attributes;

import java.util.HashMap;


public class AppenderRefAction extends Action {
  //static final Logger logger = Logger.getLogger(AppenderRefAction.class);

  boolean inError = false;
  
  public void begin(
    ExecutionContext ec, String tagName, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    //logger.debug("begin called");

    Object o = ec.peekObject();

    if (!(o instanceof AppenderAttachable)) {
      String errMsg =
        "Could not find an AppenderAttachable at the top of execution stack. Near <"
        + tagName + "> line " + getLineNumber(ec);

      LogLog.warn(errMsg);
      inError = true;
      ec.addError(new ErrorItem(errMsg));

      return;
    }

    AppenderAttachable appenderAttachable = (AppenderAttachable) o;

    String appenderName = attributes.getValue(ActionConst.REF_ATTRIBUTE);

    if (Option.isEmpty(appenderName)) {
      // print a meaningful error message and return
      String errMsg = "Missing appender ref attribute in <appender-ref> tag.";

      LogLog.warn(errMsg);
      inError = true;
      ec.addError(new ErrorItem(errMsg));

      return;
    }

    HashMap appenderBag =
      (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
    Appender appender = (Appender) appenderBag.get(appenderName);

    if (appender == null) {
      LogLog.warn("Could not find an appender named [" + appenderName + "]");
      inError = true;
      ec.addError(new ErrorItem("Could not find an appender named [" + appenderName + "]"));

      return;
    }

    if (appenderAttachable instanceof Logger) {
      LogLog.debug(
        "Attaching appender named [" + appenderName + "] to logger named ["
        + ((Logger) appenderAttachable).getName() + "].");
    } else {
      LogLog.debug(
        "Attaching appender named [" + appenderName + "] to "
        + appenderAttachable);
    }

    appenderAttachable.addAppender(appender);
  }

  public void end(ExecutionContext ec, String n) {
  }

  public void finish(ExecutionContext ec) {
  }
}
