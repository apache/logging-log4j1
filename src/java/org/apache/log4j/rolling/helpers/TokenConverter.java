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

package org.apache.log4j.rolling.helpers;


/**
 *
 * The TokenConverter offer some basic functionality for more specific token converters. 
 * It basically sets up the chained architecture.
 * 
 * @author Ceki
 *
 */
public class TokenConverter {
  /**
   * @author Ceki
   *
   * To change the template for this generated type comment go to
   * Window>Preferences>Java>Code Generation>Code and Comments
   */
  protected class IndentityTokenConverter {

  }
  static final int IDENTITY = 0;
  static final int INTEGER = 1;
  static final int DATE = 1;
  int type;
  TokenConverter next;

  TokenConverter(int t) {
    type = t;
  }

  public TokenConverter getNext() {
    return next;
  }

  public void setNext(TokenConverter next) {
    this.next = next;
  }
 
  public int getType() {
    return type;
  }

  public void setType(int i) {
    type = i;
  }

}
