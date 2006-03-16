/*
 * Copyright 1999-2006 The Apache Software Foundation.
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

// Contributors:     Mathias Bogaert
//                   joelr@viair.com

package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;

/**
   <code>BoundedFIFO</code> serves as the bounded first-in-first-out
   buffer previously used by the {@link org.apache.log4j.AsyncAppender}.
   
   @author Ceki G&uuml;lc&uuml; 
   @since version 0.9.1

   @deprecated Since 1.3.

 */
public class BoundedFIFO {
  
  LoggingEvent[] buf;
  int numElements = 0;
  int first = 0;
  int next = 0;
  int maxSize;

  /**
     Instantiate a new BoundedFIFO with a maximum size passed as argument.
   */
  public
  BoundedFIFO(int maxSize) {
   if(maxSize < 1) {
      throw new IllegalArgumentException("The maxSize argument ("+maxSize+
			    ") is not a positive integer.");
    }
    this.maxSize = maxSize;
    buf = new LoggingEvent[maxSize];
  }
  
  /**
     Get the first element in the buffer. Returns <code>null</code> if
     there are no elements in the buffer.  */
  public
  LoggingEvent get() {
    if(numElements == 0) 
      return null;
    
    LoggingEvent r = buf[first];
    buf[first] = null; // help garbage collection

    if(++first == maxSize) {
	first = 0;
    }
    numElements--;    
    return r;    
  }

  /**
     Place a {@link LoggingEvent} in the buffer. If the buffer is full
     then the event is <b>silently dropped</b>. It is the caller's
     responsability to make sure that the buffer has free space.  */
  public 
  void put(LoggingEvent o) {
    if(numElements != maxSize) {      
      buf[next] = o;    
      if(++next == maxSize) {
	next = 0;
      }
      numElements++;
    }
  }

  /**
     Get the maximum size of the buffer.
   */
  public 
  int getMaxSize() {
    return maxSize;
  }

  /**
     Return <code>true</code> if the buffer is full, that is the
     number of elements in the buffer equals the buffer size. */
  public 
  boolean isFull() {
    return numElements == maxSize;
  }

  /**
     Get the number of elements in the buffer. This number is
     guaranteed to be in the range 0 to <code>maxSize</code>
     (inclusive).
  */
  public
  int length() {
    return numElements;
  } 


  int min(int a, int b) {
    return a < b ? a : b;
  }


  /**
     Resize the buffer to a new size. If the new size is smaller than
     the old size events might be lost.
     
     @since 1.1
   */
  synchronized
  public 
  void resize(int newSize) {
    if(newSize == maxSize) 
      return;


   LoggingEvent[] tmp = new LoggingEvent[newSize];

   // we should not copy beyond the buf array
   int len1 = maxSize - first;

   // we should not copy beyond the tmp array
   len1 = min(len1, newSize);

   // er.. how much do we actually need to copy?
   // We should not copy more than the actual number of elements.
   len1 = min(len1, numElements);

   // Copy from buf starting a first, to tmp, starting at position 0, len1 elements.
   System.arraycopy(buf, first, tmp, 0, len1);
   
   // Are there any uncopied elements and is there still space in the new array?
   int len2 = 0;
   if((len1 < numElements) && (len1 < newSize)) {
     len2 = numElements - len1;
     len2 = min(len2, newSize - len1);
     System.arraycopy(buf, 0, tmp, len1, len2);
   }
   
   this.buf = tmp;
   this.maxSize = newSize;    
   this.first=0;   
   this.numElements = len1+len2;
   this.next = this.numElements;
   if(this.next == this.maxSize) // this should never happen, but again, it just might.
     this.next = 0;
  }

  
  /**
     Returns <code>true</code> if there is just one element in the
     buffer. In other words, if there were no elements before the last
     {@link #put} operation completed.  */
  public
  boolean wasEmpty() {
    return numElements == 1;
  }

  /**
      Returns <code>true</code> if the number of elements in the
      buffer plus 1 equals the maximum buffer size, returns
      <code>false</code> otherwise. */
  public
  boolean wasFull() {
    return (numElements+1 == maxSize);
  }

}
