/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.logging.pattern;

import org.apache.logging.core.Converter;
import org.apache.logging.core.LogEvent;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;


/**
 * Appends a line separator to the destination.
 *
 * @PatternSpecifier("n")
 * @Immutable
 */
public final class LineSeparatorConverter<T extends Appendable>
        implements Converter<T>, Serializable {
  /**
   * Line separator.
   */
  private final transient String lineSep;

  /**
   * Create a new instance.
   */
  public LineSeparatorConverter() {
    String sep = "\n";
    try {
        sep = System.getProperty("line.separator");
    } catch(Exception ex) {
    }
    lineSep = sep;
  }

  /** {@inheritDoc} */
  public Object extract(final LogEvent record) {
      return lineSep;
  }

    /**
     * {@inheritDoc}
     */
    public void render(Object extract, Locale locale, T destination)
        throws IOException {
        destination.append(lineSep);
    }

    /**
     * {@inheritDoc}
     */
    public void format(LogEvent record, Locale locale, T destination)
        throws IOException {
        destination.append(lineSep);
    }

}
