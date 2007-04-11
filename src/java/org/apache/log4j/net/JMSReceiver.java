/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import java.io.FileInputStream;
import java.util.Properties;

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

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.plugins.Plugin;
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
  @author Paul Smith
  @author Stephen Pain
  @since 1.3
*/
public class JMSReceiver extends Receiver implements MessageListener {

  private boolean active = false;

  protected String topicFactoryName;
  protected String topicName;
  protected String userId;
  protected String password;
  protected TopicConnection topicConnection;
  protected String jndiPath;
  
  private String remoteInfo;
  private String providerUrl;

  public JMSReceiver() { }

  public JMSReceiver(String _topicFactoryName, String _topicName,
          String _userId, String _password, String _jndiPath) {      
      topicFactoryName = _topicFactoryName;
      topicName = _topicName;
      userId = _userId;
      password = _password;
      jndiPath = _jndiPath;
  }

  /**
         * Sets the path to a properties file containing
         * the initial context and jndi provider url
         */
    public void setJndiPath(String _jndiPath) {
          jndiPath = _jndiPath;
    }
  
     /**
         * Gets the path to a properties file containing
         * the initial context and jndi provider url
         */
     public String getJndiPath() {
          return jndiPath;
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
   * Sets the JMS topic name to use when creating the
   * JMS connection.
   */
  public void setTopicName(String _topicName) {
    topicName = _topicName;
  }
  
  /**
   * Gets the curernt JMS topic name property.
   */
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
   * Gets the current user id property.
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Sets the password to use when creating the
   * JMS connection.
   */
  public void setPassword(String _password) {
    password = _password;
  }
  
  /**
   * Gets the curernt password property.
   */
  public String getPassword() {
    return password;
  }
 
  /**
   * Returns true if the receiver is the same class and they are
   * configured for the same properties, and super class also considers
   * them to be equivalent. This is used by PluginRegistry when determining
   * if the a similarly configured receiver is being started.
   * 
   * @param testPlugin The plugin to test equivalency against.
   * @return boolean True if the testPlugin is equivalent to this plugin.
   */
  public boolean isEquivalent(Plugin testPlugin) {
    // only do full check if an instance of this class
    if (testPlugin instanceof JMSReceiver) {
 
      JMSReceiver receiver = (JMSReceiver)testPlugin;
      
      // check for same topic name and super class equivalency
      return (
            topicFactoryName.equals(receiver.getTopicFactoryName()) && 
            (jndiPath == null || jndiPath.equals(receiver.getJndiPath())) && 
            super.isEquivalent(testPlugin)
            );
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

        Context ctx = null;
        if (jndiPath == null || jndiPath.equals("")) {
                ctx = new InitialContext();
        } else {
                FileInputStream is = new FileInputStream(jndiPath);
                Properties p = new Properties();
                p.load(is);
                is.close();
                ctx = new InitialContext(p);
        }

        // give some more flexibility about the choice of a tab name
        providerUrl = (String)ctx.getEnvironment().get(Context.PROVIDER_URL);
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
        getLogger().error("Could not start JMSReceiver.", e);
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
        event.setProperty("log4j.jmsProviderUrl", providerUrl);
        
      	doPost(event);
      } else {
      	getLogger().warn("Received message is of type "+message.getJMSType()
		    +", was expecting ObjectMessage.");
      }      
    } catch(Exception e) {
      getLogger().error("Exception thrown while processing incoming message.", e);
    }
  }

  protected Object lookup(Context ctx, String name) throws NamingException {
    try {
      return ctx.lookup(name);
    } catch(NameNotFoundException e) {
      getLogger().error("Could not find name ["+name+"].");
      throw e;
    }
  }

}
