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
import org.apache.log4j.Hierarchy;
import org.apache.log4j.or.MessageRenderer;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.LogLog;

import javax.jms.*;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
   A simple application receiving the logging events sent by a JMSAppender.
   

   @author Ceki G&uuml;lc&uuml;
*/
public class JMSSink  {

  static public void main(String[] args) {
    if(args.length != 3) {
      usage("Wrong number of arguments.");     
    }

    String tcfBindingName = args[0];
    String topicBindingName = args[1];
    PropertyConfigurator.configure(args[2]);

    Category.getDefaultHierarchy().addRenderer(Message.class, 
					       new MessageRenderer());

    try {
      Context ctx = new InitialContext();      
      TopicConnectionFactory topicConnectionFactory;
      topicConnectionFactory = (TopicConnectionFactory) lookup(ctx, 
							       tcfBindingName);

      TopicConnection topicConnection = 
	                        topicConnectionFactory.createTopicConnection(); 
      topicConnection.start();
    
      TopicSession topicSession = topicConnection.createTopicSession(false,
							Session.AUTO_ACKNOWLEDGE);

      Topic topic = (Topic)ctx.lookup(topicBindingName);

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
	
	// dump the JMSMessage
	// remoteCategory.debug(msg);

      }
    } catch(Exception e) {
      LogLog.error("Could not read JMS message.", e);
    }
  }


  protected
  static
  Object lookup(Context ctx, String name) throws NamingException {
    try {
      return ctx.lookup(name);
    } catch(NameNotFoundException e) {
      LogLog.error("Could not find name ["+name+"].");
      throw e;
    }    
  }  


  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + JMSSink.class.getName()
            + " TopicConnectionFactoryBindingName TopicBindingName configFile");
    System.exit(1);
  }
}
