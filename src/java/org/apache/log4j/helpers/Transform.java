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

package org.apache.log4j.helpers;

import java.io.IOException;
import java.io.Writer;


/**
   Utility class for transforming strings.

   @author Ceki G&uuml;lc&uuml;
   @author Michael A. McAngus
 */
public class Transform {
  private static final String CDATA_START = "<![CDATA[";
  private static final String CDATA_END = "]]>";
  private static final String CDATA_PSEUDO_END = "]]&gt;";
  private static final String CDATA_EMBEDED_END =
    CDATA_END + CDATA_PSEUDO_END + CDATA_START;
  private static final int CDATA_END_LEN = CDATA_END.length();

  /**
   * This method takes a string which may contain HTML tags (ie,
   * &lt;b&gt;, &lt;table&gt;, etc) and replaces any '<' and '>'
   * characters with respective predefined entity references.
   *
   * @param input The text to be converted.
   * @return The input string with the characters '<' and '>' replaced with
   *  &amp;lt; and &amp;gt; respectively.
   * */
  public static void escapeTags(String input, Writer output) throws IOException {
    //Check if the string is null or zero length -- if so, return
    //what was sent in.
    if ((input == null) || (input.length() == 0)) {
      return;
    }

    char ch = ' ';

    int len = input.length();

    for (int i = 0; i < len; i++) {
      ch = input.charAt(i);

      if (ch == '<') {
        output.write("&lt;");
      } else if (ch == '>') {
		    output.write("&gt;");
      } else {
		output.write(ch);
      }
    }
  }

  //public static void appendEscapingCDATA(StringBuffer buf, String str) {
  //	
  //}

  /**
  * Ensures that embeded CDEnd strings (]]>) are handled properly
  * within message, NDC and throwable tag text.
  *
  * @param output Writer.  The
  * initial CDSutart (<![CDATA[) and final CDEnd (]]>) of the CDATA
  * section are the responsibility of the calling method.
  * 
  * @param str The String that is inserted into an existing CDATA Section.
  * */
  public static void appendEscapingCDATA(Writer output, String str)
    throws IOException {
    if (str == null) {
      return;
    }

    int end = str.indexOf(CDATA_END);

    if (end < 0) {
      output.write(str);

      return;
    }

    int start = 0;

    while (end > -1) {
      output.write(str.substring(start, end));
      output.write(CDATA_EMBEDED_END);
      start = end + CDATA_END_LEN;

      if (start < str.length()) {
        end = str.indexOf(CDATA_END, start);
      } else {
        return;
      }
    }

    output.write(str.substring(start));
  }
}
