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

package org.apache.joran;

import java.util.ArrayList;


public class Pattern {
  //String patternStr;
  ArrayList components;

  public Pattern() {
    components = new ArrayList();
  }

  public Pattern(String p) {
    this();

    if (p == null) {
      return;
    }

    int lastIndex = 0;

    //System.out.println("p is "+ p);
    while (true) {
      int k = p.indexOf('/', lastIndex);

      //System.out.println("k is "+ k);
      if (k == -1) {
        components.add(p.substring(lastIndex));

        break;
      } else {
        String c = p.substring(lastIndex, k);

        if (c.length() > 0) {
          components.add(c);
        }

        lastIndex = k + 1;
      }
    }

    //System.out.println(components);
  }

  void push(String s) {
    components.add(s);
  }

  public int size() {
    return components.size();
  }

  public String get(int i) {
    return (String) components.get(i);
  }

  void pop() {
    if (!components.isEmpty()) {
      components.remove(components.size() - 1);
    }
  }

  /**
   * Returns the number of "tail" components that this pattern has in common
   * with the pattern p passed as parameter. By "tail" components we mean the
   * components at the end of the pattern.
   */
  public int tailMatch(Pattern p) {
    if (p == null) {
      return 0;
    }

    int lSize = this.components.size();
    int rSize = p.components.size();

    // no match possible for empty sets
    if ((lSize == 0) || (rSize == 0)) {
      return 0;
    }

    int minLen = (lSize <= rSize) ? lSize : rSize;
    int match = 0;

    // loop from the end to the front
    for (int i = 1; i <= minLen; i++) {
      String l = (String) this.components.get(lSize - i);
      String r = (String) p.components.get(rSize - i);

      if (l.equals(r)) {
        match++;
      } else {
        break;
      }
    }

    return match;
  }

  public boolean equals(Object o) {
    //System.out.println("in equals:" +this+ " vs. " + o);
    if ((o == null) || !(o instanceof Pattern)) {
      return false;
    }

    //System.out.println("both are Patterns");
    Pattern r = (Pattern) o;

    if (r.size() != size()) {
      return false;
    }

    //System.out.println("both are size compatible");
    int len = size();

    for (int i = 0; i < len; i++) {
      if (!(get(i).equals(r.get(i)))) {
        return false;
      }
    }

    // if everything matches, then the twp patterns are equal
    return true;
  }

  public int hashCode() {
    int hc = 0;
    int len = size();

    for (int i = 0; i < len; i++) {
      hc ^= get(i).hashCode();

      //System.out.println("i = "+i+", hc="+hc);
    }

    return hc;
  }

  public String toString() {
    return components.toString();
  }
}
