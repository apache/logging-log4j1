/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

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


  static String TOPIC_CONNECTION_FACTORY_BINDING_NAME_OPTION 
                                                 = "TopicConnectionFactoryBindingName";

  static String TOPIC_BINDING_NAME_OPTION = "TopicBindingName";

  String topicBindingName;
  String tcfBindingName;

  public 
  JMSAppender() {
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

  public
  void append(LoggingEvent event) {
    if(!checkEntryConditions()) {
      return;
    }

    try {
      ObjectMessage msg = topicSession.createObjectMessage();
      msg.setObject(event);
      topicPublisher.publish(msg);
    } catch(Exception e) {
      errorHandler.error("Could not publish message in JMSAppender ["+name+"].", e, 
			 ErrorCode.GENERIC_FAILURE);
    }
  }


  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {TOPIC_BINDING_NAME_OPTION, 
			  TOPIC_CONNECTION_FACTORY_BINDING_NAME_OPTION});
  }
  
  public
  void setOption(String key, String value) {
    if(value == null) return;
    super.setOption(key, value);    
    
    if(key.equals(TOPIC_BINDING_NAME_OPTION)) 
      topicBindingName = value;
    else if(key.equals(TOPIC_CONNECTION_FACTORY_BINDING_NAME_OPTION)) {
      tcfBindingName = value;
    }
  }

  public
  boolean requiresLayout() {
    return false;
  }  
}
