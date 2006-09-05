/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
package org.apache.log4j.test;

public class SpacePad {

  static String[] SPACES = {" ", "  ", "    ", "        ", //1,2,4,8 spaces
			    "                ", // 16 spaces
			    "                                " }; // 32

  static public void main(String[] args) {
    StringBuffer sbuf = new StringBuffer();

    for(int i = 0; i < 35; i++) {
      sbuf.setLength(0);
      sbuf.append("\"");
      spacePad(sbuf, i);
      sbuf.append("\"");
      System.out.println(sbuf.toString());
    }
    
    sbuf.setLength(0);
    sbuf.append("\"");
    spacePad(sbuf, 67);
    sbuf.append("\"");
    System.out.println(sbuf.toString());
    
  }
  static
  public
  void spacePad(StringBuffer sbuf, int length) {
    //LogLog.debug("Padding with " + length + " spaces.");
    while(length >= 32) {
      sbuf.append(SPACES[5]);
      length -= 32;
    }
    
    for(int i = 4; i >= 0; i--) {	
      if((length & (1<<i)) != 0) {
	sbuf.append(SPACES[i]);
      }
    }
  }
}
