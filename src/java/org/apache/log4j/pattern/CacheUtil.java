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


/**
 * Utility methods to detect patterns unsafe for caching by {@ CacheDateFormat},
 * and other utility methods.
 * 
 * @author Ceki Gulcu
 */
public class CacheUtil {
  private static final int REGULAR_STATE = 0;
  private static final int IN_QUOTE_STATE = 1;
  
  /**
   * Remove all literal text from the pattern, return only the letters a-z or 
   * A-Z placed ouside quotes.
   * @param pattern
   * @return
   */
  public static String removeLiterals(String pattern) {
    StringBuffer pbuf = new StringBuffer(pattern.length());
    int state = REGULAR_STATE;
    for(int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      switch(state) {
      case REGULAR_STATE:
        if(c == '\'') {
          state = IN_QUOTE_STATE;
        } else if( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
          pbuf.append(c);
        }
        break;
      case IN_QUOTE_STATE:
        if(c == '\'') {
          state = REGULAR_STATE;
        }
        break;
      }
    }
    return pbuf.toString();
  }
  
  /**
   * Pattern unsafe only in case both the long text form of E (4 or more 
   * successive Es) and the long text form of M (4 or more successive Ms) are 
   * present. 
   * 
   * Another uncacheable pattern is that of disjoint Ss, e.g. "YYYY-MM SSE E SSS"
   * a non-sensical pattern, but unsafe nonetheless.
   */
  public static boolean isPatternSafeForCaching(String pattern) {
 
    String cleanedPattern = CacheUtil.removeLiterals(pattern);
 
    if(cleanedPattern.indexOf("EEEE") != -1 && cleanedPattern.indexOf("MMMM") != -1) {
      return false;
    }
    if(disjointS(cleanedPattern)) {
      return false;
    }
    int successiveS = CacheUtil.computeSuccessiveS(cleanedPattern);
    if(successiveS == 1 || successiveS == 2) {
      return false;
    }
    return true;
  }  
  
  public static int computeSuccessiveS(String pattern) {
    // this code assumes that literals have been removed from the pattern
    int len = pattern.length();
    int i = pattern.indexOf('S');
    
    if(i == -1)
      return 0;
    
    int count = 0;
    while(i < len && pattern.charAt(i++) == 'S') {
      count++;
    }
    return count;
  }
  
  /**
   * Are there any disjoint S in the pattern? Examples of disjointS:
   * <p>YYYY SSS EE SSS
   * <p>SSS EE SSSS
   * 
   * @param pattern
   * @return
   */
  static boolean disjointS(String pattern) {
    int len = pattern.length();
    int i = pattern.indexOf('S');
    
    if(i == -1)
      return false;
    
    // skip any  ajoining S
    while( i < len && pattern.charAt(i++) == 'S') {
    }
    
    if(i >= len )
      return false;
    else {
      // if there are other S present, then we are in the presence of
      // disjoint S
      return pattern.indexOf('S', i) != 1;
    }
  }
}
