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

package org.apache.log4j.chainsaw;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Represents the details of a logging event. It is intended to overcome the
 * problem that a LoggingEvent cannot be constructed with purely fake data.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 * @version 1.0
 */
class EventDetails {
  /** the time of the event **/
  private final long mTimeStamp;

  /** the priority of the event **/
  private final Priority mPriority;

  /** the category of the event **/
  private final String mCategoryName;

  /** the NDC for the event **/
  private final String mNDC;

  /** the thread for the event **/
  private final String mThreadName;

  /** the msg for the event **/
  private final String mMessage;

  /** the throwable details the event **/
  private final String[] mThrowableStrRep;

  /** the location details for the event **/
  private final String mLocationDetails;

  /**
   * Creates a new <code>EventDetails</code> instance.
   * @param aTimeStamp a <code>long</code> value
   * @param aPriority a <code>Priority</code> value
   * @param aCategoryName a <code>String</code> value
   * @param aNDC a <code>String</code> value
   * @param aThreadName a <code>String</code> value
   * @param aMessage a <code>String</code> value
   * @param aThrowableStrRep a <code>String[]</code> value
   * @param aLocationDetails a <code>String</code> value
   */
  EventDetails(
    long aTimeStamp, Priority aPriority, String aCategoryName, String aNDC,
    String aThreadName, String aMessage, String[] aThrowableStrRep,
    String aLocationDetails) {
    mTimeStamp = aTimeStamp;
    mPriority = aPriority;
    mCategoryName = aCategoryName;
    mNDC = aNDC;
    mThreadName = aThreadName;
    mMessage = aMessage;
    mThrowableStrRep = aThrowableStrRep;
    mLocationDetails = aLocationDetails;
  }

  /**
   * Creates a new <code>EventDetails</code> instance.
   *
   * @param aEvent a <code>LoggingEvent</code> value
   */
  EventDetails(LoggingEvent aEvent) {
    this(
      aEvent.timeStamp, aEvent.getLevel(), aEvent.getLoggerName(),
      aEvent.getNDC(), aEvent.getThreadName(), aEvent.getRenderedMessage(),
      aEvent.getThrowableStrRep(),
      (aEvent.getLocationInformation() == null) ? null
                                                : aEvent
      .getLocationInformation().fullInfo);
  }

  /** @see #mTimeStamp **/
  long getTimeStamp() {
    return mTimeStamp;
  }

  /** @see #mPriority **/
  Priority getPriority() {
    return mPriority;
  }

  /** @see #mCategoryName **/
  String getCategoryName() {
    return mCategoryName;
  }

  /** @see #mNDC **/
  String getNDC() {
    return mNDC;
  }

  /** @see #mThreadName **/
  String getThreadName() {
    return mThreadName;
  }

  /** @see #mMessage **/
  String getMessage() {
    return mMessage;
  }

  /** @see #mLocationDetails **/
  String getLocationDetails() {
    return mLocationDetails;
  }

  /** @see #mThrowableStrRep **/
  String[] getThrowableStrRep() {
    return mThrowableStrRep;
  }
}
