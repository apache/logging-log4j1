/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TopicConnection;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSubscriber;
import javax.jms.Session;
import javax.jms.TopicSession;
import javax.jms.ObjectMessage;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.plugins.Receiver;

/**
  JMSReceiver receives a remote logging event on a configured
  JSM topic and "posts" it to a LoggerRepository as if the event was 
  generated locally. This class is designed to receive events from 
  the JMSAppender class (or classes that send compatible events).
  
  <p>Once the event has been "posted", it will be handled by the 
  appenders currently configured in the LoggerRespository.
  
  <p>This implementation borrows heavily from the JMSSink
  implementation.
  
  @author Mark Womack
  @since 1.3
*/
public class JMSReceiver extends Receiver implements MessageListener {

  private static Logger logger = Logger.getLogger(JMSReceiver.class);

  private boolean active = false;

  protected String topicFactoryName;
  protected String topicName;
  protected String userId;
  protected String password;
  protected TopicConnection topicConnection;
  
  private String remoteInfo;

  public JMSReceiver() { }

  public JMSReceiver(String _topicFactoryName, String _topicName,
    String _userId, String _password) {
      
      topicFactoryName = _topicFactoryName;
      topicName = _topicName;
      userId = _userId;
      password = _password;
  }

  /**
    Sets the JMS topic factory name to use when creating the 
    JMS connection. */
  public void setTopicFactoryName(String _topicFactoryName) {
    topicFactoryName = _topicFactoryName;
  }
  
  /**
    Gets the curernt JMS topic factory name property. */
  public String getTopicFactoryName() {
    return topicFactoryName;
  }
  
  /**
    Sets the JMS topic name to use when creating the 
    JMS connection. */
  public void setTopicName(String _topicName) {
    topicName = _topicName;
  }
  
  /**
    Gets the curernt JMS topic name property. */
  public String getTopicName() {
    return topicName;
  }

  /**
    Sets the user id to use when creating the 
    JMS connection. */
  public void setUserId(String _userId) {
    userId = _userId;
  }
  
  /**
    Gets the curernt user id property. */
  public String getUserId() {
    return userId;
  }

  /**
    Sets the password to use when creating the 
    JMS connection. */
  public void setPassword(String _password) {
    password = _password;
  }
  
  /**
    Gets the curernt password property. */
  public String getPassword() {
    return password;
  }
 
  /**
    Returns true if the receiver is the same class and they are
    configured for the same topic info, logger repository, and name.
    This is used when determining if the same receiver is being
    configured.  */
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof JMSReceiver) {
      JMSReceiver receiver = (JMSReceiver)obj;
      String rName = receiver.getName();
      return (repository == receiver.getLoggerRepository() &&
        topicFactoryName.equals(receiver.getTopicFactoryName()) &&
        ((rName != null && rName.equals(this.getName()) || 
         (rName == null && this.getName() == null))));
    }
    
    return false;
  }
  
  /**
    Returns true if this receiver is active. */
  public synchronized boolean isActive() {
    return active;
  }
  
  /**
    Sets the flag to indicate if receiver is active or not. */
  protected synchronized void setActive(boolean _active) {
    active = _active;
  }
  
  /**
    Starts the JMSReceiver with the current options. */
  public void activateOptions() {
    if (!isActive()) {
      try {
        remoteInfo = topicFactoryName + ":" + topicName;
        
        Context ctx = new InitialContext();
        TopicConnectionFactory topicConnectionFactory;
        topicConnectionFactory = 
          (TopicConnectionFactory) lookup(ctx, topicFactoryName);
        
        if (userId != null && password != null) {
          topicConnection =
    	       topicConnectionFactory.createTopicConnection(userId, password);
        } else {
          topicConnection =
    	       topicConnectionFactory.createTopicConnection();
        }
  	       
        TopicSession topicSession =
          topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
  
        Topic topic = (Topic)ctx.lookup(topicName);
  
        TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
      
        topicSubscriber.setMessageListener(this);
 
        topicConnection.start();
 
        setActive(true);
      } catch(Exception e) {
        setActive(false);
        if (topicConnection != null) {
          try {
            topicConnection.close();
          } catch (Exception e2) {
            // do nothing
          }
          topicConnection = null;
        }
        logger.error("Could not start JMSReceiver.", e);
      }
    }
  }
  
  /**
    Called when the receiver should be stopped. */
  public synchronized void shutdown() {
    if (isActive()) {
      // mark this as no longer running
      setActive(false);
    
      if (topicConnection != null) {
        try {
          topicConnection.close();
        } catch (Exception e) {
          // do nothing
        }
        topicConnection = null;
      }
    }
  }

  public void onMessage(Message message) {
    try {
      if(message instanceof  ObjectMessage) {
        // get the logging event and post it to the repository
      	ObjectMessage objectMessage = (ObjectMessage) message;
      	LoggingEvent event = (LoggingEvent) objectMessage.getObject();
      	
      	// store the known remote info in an event property
      	event.setProperty("log4j.remoteSourceInfo", remoteInfo);
      	
      	doPost(event);
      } else {
      	logger.warn("Received message is of type "+message.getJMSType()
		    +", was expecting ObjectMessage.");
      }      
    } catch(Exception e) {
      logger.error("Exception thrown while processing incoming message.", e);
    }
  }

  protected static Object lookup(Context ctx, String name) throws NamingException {
    try {
      return ctx.lookup(name);
    } catch(NameNotFoundException e) {
      logger.error("Could not find name ["+name+"].");
      throw e;
    }
  }

}
