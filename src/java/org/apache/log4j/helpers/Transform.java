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
