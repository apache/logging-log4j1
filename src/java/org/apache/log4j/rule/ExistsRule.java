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

package org.apache.log4j.rule;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;

import java.util.Stack;


/**
 * A Rule class implementing a not null (and not empty string) check.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class ExistsRule extends AbstractRule {
  private static final LoggingEventFieldResolver resolver =
    LoggingEventFieldResolver.getInstance();
  private final String field;

  private ExistsRule(String field) {
    if (!resolver.isField(field)) {
      throw new IllegalArgumentException(
        "Invalid EXISTS rule - " + field + " is not a supported field");
    }

    this.field = field;
  }

  public static Rule getRule(String field) {
    return new ExistsRule(field);
  }

  public static Rule getRule(Stack stack) {
    if (stack.size() < 1) {
      throw new IllegalArgumentException(
        "Invalid EXISTS rule - expected one parameter but received "
        + stack.size());
    }

    return new ExistsRule(stack.pop().toString());
  }

  public boolean evaluate(LoggingEvent event) {
    Object p2 = resolver.getValue(field, event);

    return (!((p2 == null) || ((p2 != null) && p2.toString().equals(""))));
  }
}
