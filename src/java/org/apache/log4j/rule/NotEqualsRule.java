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
 * A Rule class implementing not equals against two strings.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class NotEqualsRule extends AbstractRule {
  private static final LoggingEventFieldResolver resolver =
    LoggingEventFieldResolver.getInstance();
  private final String field;
  private final String value;

  private NotEqualsRule(String field, String value) {
    if (!resolver.isField(field)) {
      throw new IllegalArgumentException(
        "Invalid NOT EQUALS rule - " + field + " is not a supported field");
    }

    this.field = field;
    this.value = value;
  }

  public static Rule getRule(String field, String value) {
    return new NotEqualsRule(field, value);
  }

  public static Rule getRule(Stack stack) {
    if (stack.size() < 2) {
      throw new IllegalArgumentException(
        "Invalid NOT EQUALS rule - expected two parameters but received "
        + stack.size());
    }

    String p2 = stack.pop().toString();
    String p1 = stack.pop().toString();

    return new NotEqualsRule(p1, p2);
  }

  public boolean evaluate(LoggingEvent event) {
    Object p2 = resolver.getValue(field, event);

    return ((p2 != null) && !(p2.toString().equals(value)));
  }
}
