/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import java.util.Properties;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
   A simple appender based on JMS.
   
   @author Ceki G&uuml;lc&uuml;
*/
public class JMSAppender extends AppenderSkeleton {
 
  TopicConnection  topicConnection;
  TopicSession topicSession;
  TopicPublisher  topicPublisher;
  String topicBindingName;
  String tcfBindingName;
  boolean locationInfo;

  public 
  JMSAppender() {
  }

  /**
     The <b>TopicConnectionFactoryBindingName</b> option takes a
     string value. Its value will be used to lookup the appropriate
     <code>TopicConnectionFactory</code> from the JNDI context.
   */
  public
  void setTopicConnectionFactoryBindingName(String tcfBindingName) {
    this.tcfBindingName = tcfBindingName;
  }
  
  /**
     Returns the value of the <b>TopicConnectionFactoryBindingName</b> option.
   */
  public
  String getTopicConnectionFactoryBindingName() {
    return tcfBindingName;
  }
  
  /**
     The <b>TopicBindingName</b> option takes a
     string value. Its value will be used to lookup the appropriate
     <code>Topic</code> from the JNDI context.
   */
  public
  void setTopicBindingName(String topicBindingName) {
    this.topicBindingName = topicBindingName;
  }
  
  /**
     Returns the value of the <b>TopicBindingName</b> option.
   */
  public
  String getTopicBindingName() {
    return topicBindingName;
  }


  /**
     Returns value of the <b>LocationInfo</b> property which
     determines whether location (stack) info is sent to the remote
     subscriber. */
  public
  boolean getLocationInfo() {
    return locationInfo;
  }
  
  public
  void activateOptions() {
    TopicConnectionFactory  topicConnectionFactory;

    try {
      Context ctx = new InitialContext();      
      topicConnectionFactory = (TopicConnectionFactory) lookup(ctx, tcfBindingName);
      topicConnection = topicConnectionFactory.createTopicConnection();
      topicConnection.start();
    
      topicSession = topicConnection.createTopicSession(false,
							Session.AUTO_ACKNOWLEDGE);
      
      Topic topic = (Topic) lookup(ctx, topicBindingName);
      topicPublisher = topicSession.createPublisher(topic);

      ctx.close();      
    } catch(Exception e) {
      errorHandler.error("Error while activating options for appender named ["+name+
			 "].", e, ErrorCode.GENERIC_FAILURE);
    }
  }
 
  protected
  Object lookup(Context ctx, String name) throws NamingException {
    try {
      return ctx.lookup(name);
    } catch(NameNotFoundException e) {
      LogLog.error("Could not find name ["+name+"].");
      throw e;
    }    
  }
  
  protected
  boolean checkEntryConditions() {
    String fail = null;

    if(this.topicConnection == null) {
      fail = "No TopicConnection";
    } else if(this.topicSession == null) {
      fail = "No TopicSession";
    } else if(this.topicPublisher == null) {
      fail = "No TopicPublisher";
    } 

    if(fail != null) {
      errorHandler.error(fail +" for JMSAppender named ["+name+"].");      
      return false;
    } else {
      return true;
    }
  }

  /**
     Close this JMSAppender. Closing releases all resources used by the
     appender. A closed appender cannot be re-opened. */
  public 
  synchronized // avoid concurrent append and close operations
  void close() {
    if(this.closed) 
      return;

    LogLog.debug("Closing appender ["+name+"].");
    this.closed = true;    

    try {
      if(topicSession != null) 
	topicSession.close();	
      if(topicConnection != null) 
	topicConnection.close();
    } catch(Exception e) {
      LogLog.error("Error while closing JMSAppender ["+name+"].", e);	
    }   
    // Help garbage collection
    topicPublisher = null;
    topicSession = null;
    topicConnection = null;
  }

  /**
     This method called by {@link AppenderSkeleton#doAppend} method to
     do most of the real appending work.  */
  public
  void append(LoggingEvent event) {
    if(!checkEntryConditions()) {
      return;
    }

    try {
      ObjectMessage msg = topicSession.createObjectMessage();
      if(locationInfo) {
	event.getLocationInformation();	
      } 
      msg.setObject(event);
      topicPublisher.publish(msg);
    } catch(Exception e) {
      errorHandler.error("Could not publish message in JMSAppender ["+name+"].", e, 
			 ErrorCode.GENERIC_FAILURE);
    }
  }

  /** 
      If true, the information sent to the remote subscriber will include
      location information. By default no location information is sent
      to the subscriber.  */
  public
  void setLocationInfo(boolean locationInfo) {
    this.locationInfo = locationInfo;
  }
  

  public
  boolean requiresLayout() {
    return false;
  }  
}
