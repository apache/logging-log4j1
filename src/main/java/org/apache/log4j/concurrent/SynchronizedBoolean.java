
/*
  File: SynchronizedBoolean.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  19Jun1998  dl               Create public version
*/

package org.apache.log4j.concurrent;

/**
 * A class useful for offloading synch for boolean instance variables.
 * A cut down version of the original Doug Lea class.
 */
public final class SynchronizedBoolean {

  private boolean value;

  /** 
   * Make a new SynchronizedBoolean with the given initial value,
   * and using its own internal lock.
   **/
  public SynchronizedBoolean(boolean initialValue) { 
    value = initialValue; 
  }

  /** 
   * Return the current value 
   **/
  public synchronized boolean get() { return value; }

  /** 
   * Set to newValue.
   * @return the old value 
   **/
  public synchronized boolean set(boolean newValue) { 
    boolean old = value;
    value = newValue; 
    return old;
  }

  /**
   * Returns <code>String.valueOf(get()))</code>.
   */
  public String toString() { return String.valueOf(get()); }

}


