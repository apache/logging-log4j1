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

package org.apache.log4j.helpers.mcompose;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class MessageComposer {
  static char DELIM_START = '{';
  static char DELIM_STOP = '}';

  public static String compose(String message, Object param) {
    int j = message.indexOf(DELIM_START);
    int len = message.length();
    char escape = 'x';

    // if there are no { characters or { is the last character of the messsage
    // then we just return message
    if (j == -1 || (j+1 == len)) {
      return message;
    } else {
      if(j+1 == len) {
      }
      
      char delimStop = message.charAt(j + 1);
      if (j > 0) {
        escape = message.charAt(j - 1);
      }
      if ((delimStop != DELIM_STOP) || (escape == '\\')) {
        // invalid DELIM_START/DELIM_STOP pair or espace character is
        // present
        return message;
      } else {
        StringBuffer sbuf = new StringBuffer(len + 20);
        sbuf.append(message.substring(0, j));
        sbuf.append(param);
        sbuf.append(message.substring(j + 2));
        return sbuf.toString();
      }
    }
  }

  public static String compose(String message, Object param1, Object param2) {
    int i = 0;
    int len = message.length();
    int j = message.indexOf(DELIM_START);

    StringBuffer sbuf = new StringBuffer(message.length() + 50);

    for (int L = 0; L < 2; L++) {
      j = message.indexOf(DELIM_START, i);

      if (j == -1 || (j+1 == len)) {
        // no more variables
        if (i == 0) { // this is a simple string
          return message;
        } else { // add the tail string which contains no variables and return the result.
          sbuf.append(message.substring(i, message.length()));
          return sbuf.toString();
        }
      } else {
        char delimStop = message.charAt(j + 1);
        if ((delimStop != DELIM_STOP)) {
          // invalid DELIM_START/DELIM_STOP pair
          sbuf.append(message.substring(i, message.length()));
          return sbuf.toString();
        }
        sbuf.append(message.substring(i, j));
        sbuf.append((L == 0) ? param1 : param2);
        i = j + 2;
      }
    }
    // append the characters following the second {} pair.
    sbuf.append(message.substring(i, message.length()));
    return sbuf.toString();
  }
}
