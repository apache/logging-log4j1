/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.spi;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.NDC;

import org.apache.log4j.helpers.LogLog;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Hashtable;

// Contributors:   Nelson Minar <nelson@monkey.org>
//                 Wolf Siberski
//                 Anders Kristensen <akristensen@dynamicsoft.com>

/**
   The internal representation of logging events. When an affirmative
   decision is made to log then a <code>LoggingEvent</code> instance
   is created. This instance is passed around to the different log4j
   components.

   <p>This class is of concern to those wishing to extend log4j. 

   @author Ceki G&uuml;lc&uuml;
   @author James P. Cakalic
   
   @since 0.8.2 */
public class LoggingEvent {

  private static long startTime = System.currentTimeMillis();

  /** The category of the logging event. The category field is not
  serialized for performance reasons. 

  <p>It is set by the LoggingEvent constructor or set by a remote
  entity after deserialization. */
  transient public Category category;

  /** The category name. */
  public final String categoryName;
  
  /** Priority of logging event. Priority cannot be serializable
      because it is a flyweight.  Due to its special seralization it
      cannot be declared final either. */
  transient public Priority priority;

  /** The nested diagnostic context (NDC) of logging event. */
  //private String ndc;

  /** Have we tried to do an NDC lookup? If we did, there is no need
      to do it again.  Note that its value is always false when
      serialized. Thus, a receiving SocketNode will never use it's own
      (incorrect) NDC. See also writeObject method. */
  private boolean ndcLookupRequired = true;


  /** The application supplied message of logging event. */
  transient public String message;
  
  /** The name of thread in which this logging event was generated. */
  private String threadName;

  /** The throwable associated with this logging event.

      This is field is transient because not all exception are
      serializable. More importantly, the stack information does not
      survive serialization.
  */
  transient public Throwable throwable;

  /** This variable collects the info on a throwable. This variable
      will be shipped to       
   */
  public String throwableStr;

  /** The number of milliseconds elapsed from 1/1/1970 until logging event
      was created. */
  public final long timeStamp;

  /**
     Instantiate a LoggingEvent from the supplied parameters.
     
     <p>Except {@link #timeStamp} all the other fields of
     <code>LoggingEvent</code> are filled when actually needed.
     <p>
     @param category The category of this event.
     @param priority The priority of this event.
     @param message  The message of this event.
     @param throwable The throwable of this event.  */
  public LoggingEvent(Category category, Priority priority, Object message, 
		      Throwable throwable) {
    this.category = category;
    this.categoryName = category.getName();
    this.priority = priority;
    this.message = message.toString();
    this.throwable = throwable;
    timeStamp = System.currentTimeMillis();
  }  

  /**
     Return the message for this logging event. 

     <p>Before serialization, the returned object is the message
     passed by the user to generate the logging event. After
     serialization, the returned value equals the String form of the
     message possibly after object rendering. 

     @since 1.1 */
  public
  Object getMessage() {
    return message;
  }

  //public
  //String getMessageStr() {
  //return message.toString();
  //}


  //public
  //String getNDC() {
  //  if(ndcLookupRequired) {
  //	ndcLookupRequired = false;
  //	ndc = NDC.get();
  //  }
  //  return ndc; 
  //}

  /**
     Returns the time when the application started, in milliseconds
     elapsed since 01.01.1970.  */
  public
  static 
  long getStartTime() {
    return startTime;
  }

  public
  String getThreadName() {
    if(threadName == null)
      threadName = (Thread.currentThread()).getName();
    return threadName;
  }

  /**
     Returns the throwable information contained within this
     event. May be <code>null</code> if there is no such information.
  */
  public
  String getThrowableStr() {
    if(throwable == null) {
      return null;
    }
    
    if(throwableStr == null ) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      
      throwable.printStackTrace(pw);
      throwableStr = sw.toString();
    }    
    return throwableStr;
  }
}
