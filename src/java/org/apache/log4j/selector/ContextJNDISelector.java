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

package org.apache.log4j.selector;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.helpers.IntializationUtil;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootCategory;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Log4j JNDI based Repository selector
 *
 * <p>based primarily on Ceki G&uuml;lc&uuml;'s article <h3>Supporting the Log4j
 * <code>RepositorySelector</code> in Servlet Containers</h3> at:
 * http://qos.ch/logging/sc.html</p>
 *
 * <p>By default, the class static <code>RepositorySelector</code> variable
 * of the <code>LogManager</code> class is set to a trivial
 * <code>{@link org.apache.log4j.spi.RepositorySelector RepositorySelector}</code>
 * implementation which always returns the same logger repository. a.k.a.
 * hierarchy. In other words, by default log4j will use one hierarchy, the
 * default hierarchy. This behavior can be overridden via
 * the <code>LogManager</code>'s
 * <code>setRepositorySelector(RepositorySelector, Object)</code> method.</p>
 *
 * <p>That is where this class enters the picture.  It can be used to define a
 * custom logger repository.  It makes use of the fact that in J2EE
 * environments, each web-application is guaranteed to have its own JNDI
 * context relative to the <code>java:comp/env</code> context. In EJBs, each
 * enterprise bean (albeit not each application) has its own context relative
 * to the <code>java:comp/env</code> context.  An <code>env-entry</code> in a
 * deployment descriptor provides the information to the JNDI context.  Once the
 * <code>env-entry</code> is set, a repository selector can query the JNDI
 * application context to look up the value of the entry. The logging context of
 * the web-application will depend on the value the env-entry.  The JNDI context
 *  which is looked up by this class is
 * <code>java:comp/env/log4j/context-name</code>.
 *
 * <p>Here is an example of an <code>env-entry<code>:
 * <blockquote>
 * <pre>
 * &lt;env-entry&gt;
 *   &lt;description&gt;JNDI logging context name for this app&lt;/description&gt;
 *   &lt;env-entry-name&gt;log4j/context-name&lt;/env-entry-name&gt;
 *   &lt;env-entry-value&gt;aDistinctiveLoggingContextName&lt;/env-entry-value&gt;
 *   &lt;env-entry-type&gt;java.lang.String&lt;/env-entry-type&gt;
 * &lt;/env-entry&gt;
 * </pre>
 * </blockquote>
 * </p>
 *
 * <p> If multiple applications have the same logging context name, then they 
 * will share the same logging context. 
 * </p>
 * 
 *<p>You can also specify the URL for this context's configuration resource.
 * This repository selector (ContextJNDISelector) will use the specified resource
 * to automatically configure the log4j repository.
 *</p>
 ** <blockquote>
 * <pre>
 * &lt;env-entry&gt;
 *   &lt;description&gt;URL for configuring log4j context&lt;/description&gt;
 *   &lt;env-entry-name&gt;log4j/configuration-resource&lt;/env-entry-name&gt;
 *   &lt;env-entry-value&gt;urlOfConfigrationResource&lt;/env-entry-value&gt;
 *   &lt;env-entry-type&gt;java.lang.String&lt;/env-entry-type&gt;
 * &lt;/env-entry&gt;
 * </pre>
 * </blockquote>
 * 
 * <p>In case no configuration resource is specified, this repository selector
 * will attempt to find the files <em>log4j.xml</em> and 
 * <em>log4j.properties</em> from the resources available to the application.
 * </p>
 * 
 * <p>It follows that bundling a <em>log4j.xml</em> file in your web-application
 * and setting context name will be enough to ensure a separate logging 
 * environment for your applicaiton.
 *  
 * <p>Unlike the {@link ContextClassLoaderSelector} which will only work in
 * containers that provide for separate classloaders, JNDI is available in all
 * servers claiming to be servlet or J2EE compliant.  So, the JNDI selector
 * is the recommended context selector. However it is possible to spoof the 
 * value of the env-entry.  There are ways to avoid this, but this class makes 
 * no attempt to do so.  It would require a container specific implementation to,
 * for instance, append a non-random unique name to the user-defined value of
 * the env-entry.  Keep that in mind as you choose which custom repository
 * selector you would like to use in your own application.  Until this issue
 * is solved by container-controlled repository selectors, you will need to
 * be diligent in providing a distinctive env-entry-value for each application
 * running on the server.  This is not an issue when using the
 * {@link ContextClassLoaderSelector} in containers in which it is compatible
 * (such as Tomcat 4/5)</p>
 *
 * @author <a href="mailto:hoju@visi.com">Jacob Kjome</a>
 * @author Ceki G&uuml;lc&uuml;
 * @since  1.3
 */
public class ContextJNDISelector implements RepositorySelector {
  static String JNDI_CONTEXT_NAME = "java:comp/env/log4j/context-name";
  static String JNDI_CONFIGURATION_RESOURCE =
    "java:comp/env/log4j/configuration-resource";
  static String JNDI_CONFIGURATOR_CLASS =
    "java:comp/env/log4j/configurator-class";

  /**
   * key: name of logging context,
   * value: Hierarchy instance
   */
  private final Map hierMap;

  /**
   * default hierarchy used in case the JNDI lookup
   * fails to return a non-null value
   */
  private LoggerRepository defaultRepository;

  /**
   * public no-args constructor
   */
  public ContextJNDISelector() {
    hierMap = Collections.synchronizedMap(new HashMap());
  }

  public void setDefaultRepository(LoggerRepository dh) {
    if (defaultRepository == null) {
      defaultRepository = dh;
    } else {
      throw new IllegalStateException(
        "default hierarchy has been already set.");
    }
  }

  /**
   * implemented RepositorySelector interface method. The returned
   * value is guaranteed to be non-null.
   *
   * @return the appropriate JNDI-keyed Hierarchy/LoggerRepository
   */
  public LoggerRepository getLoggerRepository() {
    String loggingContextName = null;
    Context ctx = null;

    try {
      ctx = new InitialContext();
      loggingContextName = (String) ctx.lookup(JNDI_CONTEXT_NAME);
    } catch (NamingException ne) {
      // we can't log here
      //debug minor issue in Tomcat5 where, after the first webapp install,
      //the second webapp first fails the JNDI lookup and Log4j reports that
      //"no appenders could be found".  Subsequent webapp installs report the
      //same except with no "no appenders could be found" message.  However,
      //the appender do indeed work so I'm not sure why it is reported that
      //they don't?  No issues like this in Tomcat4.
      //System.out.println("failed to look up logging context!");
      ;
    }

    if (loggingContextName == null) {
      return defaultRepository;
    } else {
      Hierarchy hierarchy = (Hierarchy) hierMap.get(loggingContextName);

      if (hierarchy == null) {
        // create new hierarchy
        hierarchy = new Hierarchy(new RootCategory(Level.DEBUG));
        hierMap.put(loggingContextName, hierarchy);

        // Use automatic configration to configure the default hierarchy
        IntializationUtil.log4jInternalConfiguration(hierarchy);

        String configResourceStr = lookup(ctx, JNDI_CONFIGURATION_RESOURCE);
        String configuratorClassName = lookup(ctx, JNDI_CONFIGURATOR_CLASS);

        if (configResourceStr == null) {
          if (
            Loader.getResource(Constants.DEFAULT_XML_CONFIGURATION_FILE) != null) {
            configResourceStr = Constants.DEFAULT_XML_CONFIGURATION_FILE;
          } else if (
            Loader.getResource(Constants.DEFAULT_CONFIGURATION_FILE) != null) {
            configResourceStr = Constants.DEFAULT_CONFIGURATION_FILE;
          }
        }

        IntializationUtil.initialConfiguration(
          hierarchy, configResourceStr, configuratorClassName);
      }

      return hierarchy;
    }
  }

  String lookup(Context ctx, String name) {
    try {
      return (String) ctx.lookup(name);
    } catch (NamingException e) {
      LogLog.warn("Failed to get "+name);
      return null;
    }
  }
  
  /** Remove the repository with the given context name from the list of
   * known repositories.
   */
  public void remove(String contextName) {
    hierMap.remove(contextName);  
  }
}
