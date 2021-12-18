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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
 * A simple appender that used to publish to a JMS Topic in Log4j up to 1.2.17.
 *
 * Changed in 1.2.18+ to complain about its use and do nothing else.
 * See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
 * for more information on why JMS is disabled since 1.2.18.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @deprecated
 * @noinspection unused
 */
public class JMSAppender extends AppenderSkeleton {

  static final String JMS_UNSUPPORTED =
      "ERROR-LOG4J-NETWORKING-UNSUPPORTED: JMS unsupported!" +
      " This is a breaking change in Log4J 1 >=1.2.18. Change your config to stop using JMS!";

  public
  JMSAppender() {
    LogLog.error(JMS_UNSUPPORTED);
  }

  public
  void setTopicConnectionFactoryBindingName(String tcfBindingName) {
  }

  public
  String getTopicConnectionFactoryBindingName() {
    return null;
  }

  public
  void setTopicBindingName(String topicBindingName) {
  }

  public
  String getTopicBindingName() {
    return null;
  }

  public
  boolean getLocationInfo() {
    return false;
  }

  public void activateOptions() {
  }

  protected Object lookup(Context ctx, String name) throws NamingException {
    LogLog.error(JMS_UNSUPPORTED);
    throw new NameNotFoundException(JMS_UNSUPPORTED);
  }

  /** @noinspection UnusedReturnValue*/
  protected boolean checkEntryConditions() {
    errorHandler.error(JMS_UNSUPPORTED);
    return false;
  }

  public synchronized void close() {
    if(this.closed) {
        return;
    }

    LogLog.debug("Closing appender ["+name+"].");
    this.closed = true;
  }

  public void append(LoggingEvent event) {
    checkEntryConditions();
  }

  public String getInitialContextFactoryName() {
    return null;
  }
  
  public void setInitialContextFactoryName(String initialContextFactoryName) {
  }

  public String getProviderURL() {
    return null;
  }

  public void setProviderURL(String providerURL) {
  }

  String getURLPkgPrefixes( ) {
    return null;
  }

  public void setURLPkgPrefixes(String urlPkgPrefixes ) {
  }
  
  public String getSecurityCredentials() {
    return null;
  }

  public void setSecurityCredentials(String securityCredentials) {
  }

  public String getSecurityPrincipalName() {
    return null;
  }

  public void setSecurityPrincipalName(String securityPrincipalName) {
  }

  public String getUserName() {
    return null;
  }

  public void setUserName(String userName) {
  }

  public String getPassword() {
    return null;
  }

  public void setPassword(String password) {
  }

  public void setLocationInfo(boolean locationInfo) {
  }

  protected TopicConnection  getTopicConnection() {
    throw new IllegalStateException(JMS_UNSUPPORTED);
  }

  protected TopicSession  getTopicSession() {
    throw new IllegalStateException(JMS_UNSUPPORTED);
  }

  protected TopicPublisher  getTopicPublisher() {
    throw new IllegalStateException(JMS_UNSUPPORTED);
  }
  
  public boolean requiresLayout() {
    return false;
  }
}
