/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

import javax.jms.*;

/**
   A simple application receiving the logging events sent by a JMSAppender.
   
   @author Ceki G&uuml;lc&uuml;
*/
public class JMSSink  {

  static int PORT = 22000;
  static String TOPIC = "MyTopic";

  static public void main(String[] args) {
    PropertyConfigurator.configure(args[0]);
    //PropertyConfigurator.configure();

    

    try {
      TopicConnectionFactory topicConnectionFactory = new 
                                  com.sun.messaging.TopicConnectionFactory(PORT);
      TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
    
      topicConnection.start();
    
      TopicSession topicSession = topicConnection.createTopicSession(false,
							Session.AUTO_ACKNOWLEDGE);
      Topic topic = topicSession.createTopic(TOPIC);
      //TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
      TopicSubscriber topicSubscriber = 
           topicSession.createDurableSubscriber(topic, "x");

      
      LoggingEvent event;
      Category remoteCategory;    

      while(true) {
	ObjectMessage msg = (ObjectMessage)topicSubscriber.receive();      
	event = (LoggingEvent) msg.getObject();
	remoteCategory = Category.getInstance(event.categoryName);
	remoteCategory.callAppenders(event);	
      }
    } catch(Exception e) {
      LogLog.error("---------------------", e);
    }
  }

}
