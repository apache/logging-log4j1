/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.selector.servlet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.JNDIUtil;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;

import javax.naming.Context;
import javax.naming.NamingException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * This is a very simple ServletContextListener which detaches a
 * {@link LoggerRepository} from {@link ContextJNDISlector} when the
 * web-application is destroyed.
 *
 * This class is highly coupled with JNDI but not necessarily
 * ContextJNDISlector.
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 * @since 1.3
 */
public class ContextDetachingSCL implements ServletContextListener {
  static Logger logger = Logger.getLogger(ContextDetachingSCL.class);

  /**
   * When the context is destroy, detach the logging repository given by
   * the value of "log4j/context-name" environment variable.
   * 
   * If found, the logging repository is also shutdown.
   */
  public void contextDestroyed(ServletContextEvent sce) {
    String loggingContextName = null;

    try {
      Context ctx = JNDIUtil.getInitialContext();
      loggingContextName =
        (String) JNDIUtil.lookup(ctx, Constants.JNDI_CONTEXT_NAME);
    } catch (NamingException ne) {
    }

    if (loggingContextName != null) {
      logger.debug(
        "About to detach logger context named [" + loggingContextName + "].");

      RepositorySelector repositorySelector =
        LogManager.getRepositorySelector();
      LoggerRepository lr = repositorySelector.detachRepository(loggingContextName);
      if(lr != null) {
        logger.debug("About to shutdown logger repository named ["+lr.getName()+
            "]");
        lr.shutdown();
      }
    }
  }

  /**
   * Does nothing.
   */
  public void contextInitialized(ServletContextEvent sce) {
    ServletContext sc = sce.getServletContext();
    logger.debug("Context named ["+sc.getServletContextName()+"] initialized.");     
  }
}
