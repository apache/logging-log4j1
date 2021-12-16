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

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * A simple application that consumes logging events sent by a {@link
 * JMSAppender} in Log4j up to 1.2.17.
 *
 * Changed in 1.2.18+ to complain about its use and do nothing else.
 * See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
 * for more information on why JMS is disabled since 1.2.18.
 *
 * @author Ceki G&uuml;lc&uuml; 
 */
public class JMSSink implements javax.jms.MessageListener {

  static Logger logger = Logger.getLogger(JMSSink.class);

  static public void main(String[] args) throws Exception {
    usage();
  }

  /** @noinspection unused*/
  public JMSSink(String tcfBindingName, String topicBindingName, String username,
                 String password) {
    logger.error(JMSAppender.JMS_UNSUPPORTED);
  }

  public void onMessage(javax.jms.Message message) {
    logger.error(JMSAppender.JMS_UNSUPPORTED);
  }

  /** @noinspection unused*/
  protected static Object lookup(Context ctx, String name) throws NamingException {
    logger.error(JMSAppender.JMS_UNSUPPORTED);
    throw new NamingException(JMSAppender.JMS_UNSUPPORTED);
  }

  static void usage() {
    System.err.println(JMSAppender.JMS_UNSUPPORTED);
    System.exit(1);
  }
}
