/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;

import javax.jms.*;

/**
   A simple appender based on JMS.
   
   @author Ceki G&uuml;lc&uuml;
*/
public class JMSAppender extends AppenderSkeleton {

  TopicConnectionFactory  topicConnectionFactory;
  TopicConnection  topicConnection;
  TopicSession topicSession;
  TopicPublisher  topicPublisher;

  int port = 22000;
  static String TOPIC = "MyTopic";

  public 
  JMSAppender() {
    try {
      topicConnectionFactory = new com.sun.messaging.TopicConnectionFactory(port);
      topicConnection = topicConnectionFactory.createTopicConnection();
      LogLog.debug("Starting topic connection");
      topicConnection.start();
    
      topicSession = topicConnection.createTopicSession(false,
						      Session.AUTO_ACKNOWLEDGE);
      Topic topic = topicSession.createTopic(TOPIC);
      topicPublisher = topicSession.createPublisher(topic);
    } catch(Exception e) {
      LogLog.error("-------------", e);
    }

  }

  public 
  void close() {
  }

  public
  void append(LoggingEvent event) {
    try {
      ObjectMessage msg = topicSession.createObjectMessage();
      msg.setObject(event);
      topicPublisher.publish(msg);
    } catch(Exception e) {
      LogLog.error("-------------", e);
    }
  }

  public
  boolean requiresLayout() {
    return false;
  }
  
}
