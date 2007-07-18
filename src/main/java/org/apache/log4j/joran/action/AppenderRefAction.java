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


import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.Option;
import org.apache.log4j.joran.spi.ExecutionContext;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.ErrorItem;

import org.xml.sax.Attributes;

import java.util.HashMap;


public class AppenderRefAction extends Action {
  
  public void begin(
    ExecutionContext ec, String tagName, Attributes attributes) {

    Object o = ec.peekObject();

    if (!(o instanceof AppenderAttachable)) {
      String errMsg =
        "Could not find an AppenderAttachable at the top of execution stack. Near <"
        + tagName + "> line " + getLineNumber(ec);

      getLogger().warn(errMsg);
      ec.addError(new ErrorItem(errMsg));

      return;
    }

    AppenderAttachable appenderAttachable = (AppenderAttachable) o;

    String appenderName = attributes.getValue(ActionConst.REF_ATTRIBUTE);

    if (Option.isEmpty(appenderName)) {
      // print a meaningful error message and return
      String errMsg = "Missing appender ref attribute in <appender-ref> tag.";

      getLogger().warn(errMsg);
      ec.addError(new ErrorItem(errMsg));

      return;
    }

    HashMap appenderBag =
      (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
    Appender appender = (Appender) appenderBag.get(appenderName);

    if (appender == null) {
      String msg = "Could not find an appender named ["+appenderName+
      "]. Did you define it below in the config file?";
      getLogger().warn(msg);
      getLogger().warn("See {}#appender_order for more details.", Constants.CODES_HREF);
      ec.addError(new ErrorItem(msg));

      return;
    }

    if (appenderAttachable instanceof Logger) {
      getLogger().debug(
        "Attaching appender named [{}] to logger named [{}].", appenderName, (
            (Logger) appenderAttachable).getName());
    } else {
      getLogger().debug(
        "Attaching appender named [{}] to {}.", appenderName, appenderAttachable);
    }

    appenderAttachable.addAppender(appender);
  }

  public void end(ExecutionContext ec, String n) {
  }

  public void finish(ExecutionContext ec) {
  }
}
