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

package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.Writer;


/**

   <p>PatternConverter is an abtract class that provides the
   formatting functionality that derived classes need.

   <p>Conversion specifiers in a conversion patterns are parsed to
   individual PatternConverters. Each of which is responsible for
   converting a logging event in a converter specific manner.

   @author <a href="mailto:cakalijp@Maritz.com">James P. Cakalic</a>
   @author Ceki G&uuml;lc&uuml;
   @author Chris Nokes

   @since 0.8.2
 */
public abstract class PatternConverter {
  static String[] SPACES =
  {
    " ", "  ", "    ", "        ", //1,2,4,8 spaces
    "                ", // 16 spaces
    "                                " // 32 spaces
  };
  public PatternConverter next;
  int min = -1;
  int max = 0x7FFFFFFF;
  boolean leftAlign = false;
  String option;

  protected PatternConverter() {
  }

  protected PatternConverter(FormattingInfo fi) {
    min = fi.min;
    max = fi.max;
    leftAlign = fi.leftAlign;
  }

  public void setFormattingInfo(FormattingInfo fi) {
    min = fi.min;
    max = fi.max;
    leftAlign = fi.leftAlign;
  }

  /**
     Derived pattern converters must override this method in order to
     convert conversion specifiers in the correct way.
     
     IMPORTANT: Note that an implementing class may always return the same
     StringBuffer instance in order to avoid superflous object creation.
  */
  protected abstract StringBuffer convert(LoggingEvent event);

  /**
     A template method for formatting in a converter specific way.
   */
  public void format(Writer output, LoggingEvent e) throws IOException {
    StringBuffer s = convert(e);

    if (s == null) {
      if (0 < min) {
        spacePad(output, min);
      }

      return;
    }

    int len = s.length();

    if (len > max) {
      output.write(s.substring(len - max));
    } else if (len < min) {
      if (leftAlign) {
        output.write(s.toString());
        spacePad(output, min - len);
      } else {
        spacePad(output, min - len);
        output.write(s.toString());
      }
    } else {
      output.write(s.toString());
    }
  }

  /**
     Fast space padding method.
  */
  public void spacePad(Writer output, int length) throws IOException {
    while (length >= 32) {
      output.write(SPACES[5]);
      length -= 32;
    }

    for (int i = 4; i >= 0; i--) {
      if ((length & (1 << i)) != 0) {
        output.write(SPACES[i]);
      }
    }
  }

  public String getOption() {
    return option;
  }

  public void setOption(String string) {
    option = string;
  }
  
  /**
   * This method returns the name of the conversion pattern.
   * 
   * The name can be useful to certain Layouts such as HTMLLayout.
   * 
   * @return	the name of the conversion pattern
   */
  public abstract String getName();
}
