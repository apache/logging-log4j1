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

package org.apache.log4j.lbel.comparator;

import org.apache.log4j.lbel.Operator;
import org.apache.log4j.lbel.ScanError;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Compare the logger of an event passed as parameter to the logger name and
 * comparison operator set in the constructor.
 *
 * <p>Allowed comparison operators are '=', '!=', '>', '>=', '<', '<=', '~' and
 * '!~' where '~' stands for regular expression match.
 *
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * @author Scott Deboy
 */
public class LoggerComparator extends StringComparator {
  public LoggerComparator(final Operator operator, String rightSide)
    throws ScanError {
    super(operator, rightSide);
  }

  protected String getLeftSide(LoggingEvent event) {
    return event.getLoggerName();
  }
}
