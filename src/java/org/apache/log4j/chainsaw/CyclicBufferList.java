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

package org.apache.log4j.chainsaw;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * CyclicBuffer implementation that is Object generic, and implements the List interface.
 *
 * Original CyclicBuffer @author Ceki G&uuml;lc&uuml;
 *
 * This implementation (although there's very little change) @author Paul Smith <psmith@apache.org>
 *
 */
public class CyclicBufferList extends AbstractList implements List {
  Object[] ea;
  int first;
  int last;
  int numElems;
  int maxSize;

  /**
     Instantiate a new CyclicBuffer of at most <code>maxSize</code> events.

     The <code>maxSize</code> argument must a positive integer.

     @param maxSize The maximum number of elements in the buffer.
  */
  public CyclicBufferList(int maxSize) {
    if (maxSize < 1) {
      throw new IllegalArgumentException(
        "The maxSize argument (" + maxSize + ") is not a positive integer.");
    }
    this.maxSize = maxSize;
    clear();
  }

  public CyclicBufferList() {
    this(5000);
  }

  /**
   * Removes the element at the specified position in this list.
   * Shifts any subsequent elements to the left (subtracts one from their
   * indices).
   *
   * @param index the index of the element to removed.
   * @return the element that was removed from the list.
   * @throws    IndexOutOfBoundsException if index out of range <tt>(index
   *      &lt; 0 || index &gt;= size())</tt>.
   */
  public Object remove(int index) {
    Object oldValue = ea[index];

    List list = new ArrayList(Arrays.asList(ea));
    list.remove(index);
    ea = list.toArray(ea);
    numElems = ea.length;
    
    numElems--;
    if (--last <= 0) {
      last = numElems;
    }

    if (first == maxSize) {
      first = 0;
    }
    return oldValue;
  }

  public Object set(int index, Object element) {
    Object previous = ea[index];
    ea[index] = element;

    return previous;
  }

  /**
     Add an <code>event</code> as the last event in the buffer.

   */
  public boolean add(Object event) {
    ea[last] = event;

    if (++last == maxSize) {
      last = 0;
    }

    if (numElems < maxSize) {
      numElems++;
    } else if (++first == maxSize) {
      first = 0;
    }

    return true;
  }

  /**
     Get the <i>i</i>th oldest event currently in the buffer. If
     <em>i</em> is outside the range 0 to the number of elements
     currently in the buffer, then <code>null</code> is returned.


  */
  public Object get(int i) {
    if ((i < 0) || (i >= numElems)) {
      return null;
    }

    return ea[(first + i) % maxSize];
  }

  public int getMaxSize() {
    return maxSize;
  }
  
  public int getLast() {
      return last;
  }

  /**
     Get the oldest (first) element in the buffer. The oldest element
     is removed from the buffer.
  */
  public Object get() {
    Object r = null;

    if (numElems > 0) {
      numElems--;
      r = ea[first];
      ea[first] = null;

      if (++first == maxSize) {
        first = 0;
      }
    }

    return r;
  }

  /**
     Get the number of elements in the buffer. This number is
     guaranteed to be in the range 0 to <code>maxSize</code>
     (inclusive).
  */
  public int size() {
    return numElems;
  }

  /**
     Resize the cyclic buffer to <code>newSize</code>.

     @throws IllegalArgumentException if <code>newSize</code> is negative.
   */
  public void resize(int newSize) {
    if (newSize < 0) {
      throw new IllegalArgumentException(
        "Negative array size [" + newSize + "] not allowed.");
    }

    if (newSize == numElems) {
      return; // nothing to do
    }

    Object[] temp = new Object[newSize];

    int loopLen = (newSize < numElems) ? newSize : numElems;

    for (int i = 0; i < loopLen; i++) {
      temp[i] = ea[first];
      ea[first] = null;

      if (++first == numElems) {
        first = 0;
      }
    }

    ea = temp;
    first = 0;
    numElems = loopLen;
    maxSize = newSize;

    if (loopLen == newSize) {
      last = 0;
    } else {
      last = loopLen;
    }
  }
  /* (non-Javadoc)
   * @see java.util.Collection#clear()
   */
  public void clear() {
    ea = new Object[maxSize];
    first = 0;
    last = 0;
    numElems = 0;
    
  }

}
