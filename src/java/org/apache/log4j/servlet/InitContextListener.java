/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.log4j.selectors.ContextClassLoaderSelector;

/**
 * A servlet context listener for initializing and shutting down Log4j. See
 * <a href="http://jakarta.apache.org/log4j/docs/documentation.html">Log4j documentation</a>
 * for how to use Log4j.
 * <p>
 * This is a <code>ServletContextListener</code> as defined by the servlet 2.3
 * specification.  It gets called immediately before full application startup
 * and immediately before full application shutdown.  Unlike servlets, which
 * may be destroyed at the will of the container at any time during the
 * application lifecycle, a servlet context listener is guaranteed to be
 * called exactly twice within the application's lifecycle.  As such, we can
 * use it to initialize things once at application startup and clean things
 * up at application shutdown.</p>
 * <p>
 * Initialization is described below in the discussion of the various parameters
 * available for configuring this context listener.  In the case of shutdown we are
 * concerned with cleaning up loggers and appenders within the
 * <code>Hierarchy</code> that the current application is using for
 * logging.  If we didn't do this, there is a chance that, for instance, file
 * appenders won't have given up handles to files they are logging to which
 * would leave them in a locked state until the current JVM is shut down.  This
 * would entail a full shutdown of the application server in order to release
 * locks on log files.  Using this servlet context listener ensures that locks
 * will be released without requiring a full server shutdown.</p>
 * </p>
 * <p>
 * The following needs to be added to the webapp's web.xml file to configure this listener:
 * <blockquote>
 * <pre>
 * &lt;context-param&gt;
 *     &lt;!-- relative path to config file within current webapp --&gt;
 *     &lt;param-name&gt;log4j-config&lt;/param-name&gt;
 *     &lt;param-value&gt;WEB-INF/log4j.xml&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * &lt;context-param&gt;
 *     &lt;!-- config file re-reading specified in milliseconds...
 *              Note that if the webapp is served directly from the
 *              .war file, configureAndWatch() cannot be used because
 *              it requires a system file path. In that case, this
 *              param will be ignored.  Set to 0 or don't specify this
 *              param to do a normal configure(). --&gt;
 *     &lt;param-name&gt;log4j-cron&lt;/param-name&gt;
 *     &lt;param-value&gt;5000&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * &lt;!-- Below is an optional param for use with a File Appender.
 *          Specifies a path to be read from a log4j xml
 *          config file as a system property. The property name is
 *          dynamically generated and takes on the following pattern:
 *              [webapp name].log.home
 *          If the app has a path of &quot;/Barracuda&quot;, the system
 *          variable name would be &quot;Barracuda.log.home&quot;.  So,
 *          the FileAppender in log4j.xml would contain a param which looks like:
 *              &lt;param name=&quot;File&quot; value=&quot;${Barracuda.log.home}/arbitraryLogFileName.log&quot; /&gt;
 *          If the &quot;log4j-log-home&quot; context param is not specified, the
 *          path associated with the generated system variable defaults to
 *          the WEB-INF/logs directory of the current webapp which is created
 *          if it doesn't exist... unless the webapp is running directly
 *          from a .war file.  In the latter case, this context param
 *          *must* be specified if using a FileAppender.
 *          Note that, if specified, the value is treated as an absolute
 *          system path which is not relative to the webapp. --&gt;
 * &lt;!-- &lt;context-param&gt;
 *     &lt;param-name&gt;log4j-log-home&lt;/param-name&gt;
 *     &lt;param-value&gt;/usr/local/logs/tomcat&lt;/param-value&gt;
 * &lt;/context-param&gt; --&gt;
 *
 * &lt;listener&gt;
 *     &lt;listener-class&gt;
 *      org.apache.log4j.servlet.InitContextListener
 *     &lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * </blockquote>
 * </p>
 * <h4>Below is some more information on each of the configuration properties</h4>
 * <p>
 * <dl>
 * <dt><code>log4j-config</code></dt>
 * <dd>
 * The <code>log4j-config</code> init parameter specifies the location of the
 * Log4j configuration file relative to the current webapp.
 * If the <code>log4j-config</code> init parameter is omitted, this class
 * will just let Log4j configure itself since, upon first use of Log4j, if it
 * has not yet been configured, it will search for a config file named log4j.xml
 * or log4j.properties in the classpath. If it can't find one, it falls back to using the
 * <code>BasicConfigurator.configure()</code> to initialize Log4j.
 * </dd>
 * <dt><code>log4j-cron</code></dt>
 * <dd>
 * The <code>log4j-cron</code> init parameter specifies the number of milliseconds
 * to wait in between reads of the config file using <code>configureAndWatch()</code>.
 * If omitted, given a value of 0, or given a value that is other than something that
 * which can be converted to a Java long value a normal <code>configure()</code> is used.
 * </dd>
 * <dt><code>log4j-log-home</code></dt>
 * <dd>
 * The <code>log4j-log-home</code> init parameter is optional. It specifies a
 * custom path to a directory meant to contain log files for the current webapp
 * when using a <code>FileAppender</code>. If not specified, it will default to
 * using the location WEB-INF/logs to contain log files. If the directory doesn't
 * exist, it is created. A system parameter is then created in the following format:
 * <blockquote>
 *     <code>[webapp name].log.home</code>
 * </blockquote>
 * This can be referenced in an xml config file (not sure if it works for a properties
 * config file?) in the following fashion for a webapp with the context path &quot;/Barracuda&quot;:
 * <blockquote>
 *     <code>&lt;param name=&quot;File&quot; value=&quot;${Barracuda.log.home}/main.log&quot; /&gt;</code>
 * </blockquote>
 * In this case, we are running in the &quot;Barracuda&quot; context and the &quot;main.log&quot; file
 * will get created in whatever directory path is specified by the system property
 * &quot;Barracuda.log.home&quot;.
 * <p>
 * <strong>Note</strong> that if the webapp is being run directly from a .war file, the automatic creation
 * of the WEB-INF/logs directory and [webapp name].log.home system property will *not* be
 * performed. In this case, you would have to provide a custom directory path for the
 * this to work. Also note that <code>configureAndWatch()</code> will not be used in the case
 * that the webapp is running directly from a .war file. <code>configure()</code> will be used
 * instead.
 * </p>
 * </dd>
 * </dl>
 *
 * @author  Jacob Kjome <hoju@visi.com>
 * @since   1.3
 */
public class InitContextListener implements ServletContextListener {

    // store the time at which the current application became fully initialized
    public static long applicationInitialized = 0L;

    private final static String PARAM_LOG4J_CONFIG_PATH = "log4j-config";
    private final static String PARAM_LOG4J_WATCH_INTERVAL = "log4j-cron";
    private final static String PARAM_LOG4J_LOG_HOME = "log4j-log-home";
    private final static String DEFAULT_LOG_HOME = "WEB-INF" + File.separator + "logs";


    /**
     * Application Startup Event
     */
    public void contextInitialized(ServletContextEvent sce) {
        applicationInitialized = System.currentTimeMillis();

        ServletContext context = sce.getServletContext();

        initializeLog4j(context);
    }

    /**
     * Application Shutdown Event
     */
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        cleanupLog4j(context);
    }



    /**
     * Log4j specific cleanup.  Shuts down all loggers and appenders and
     * removes the hierarchy associated with the current classloader.
     */
    private void cleanupLog4j(ServletContext context) {
        //shutdown this webapp's logger repository
        context.log("Cleaning up Log4j resources for context: " + context.getServletContextName() + "...");
        context.log("Shutting down all loggers and appenders...");
        org.apache.log4j.LogManager.shutdown();
        context.log("Log4j cleaned up.");
    }

    /**
     * Log4j specific initialization.  Shuts down all loggers and appenders and
     * removes the hierarchy associated with the current classloader.
     */
    private void initializeLog4j(ServletContext context) {

        String configPath = context.getInitParameter(PARAM_LOG4J_CONFIG_PATH);
        // if the log4j-config parameter is not set, then no point in trying
        if (configPath!=null) {
            if (configPath.startsWith("/")) configPath = (configPath.length() > 1) ? configPath.substring(1) : "";
            // if the configPath is an empty string, then no point in trying
            if (configPath.length() >= 1) {
                // set up log path System property
                String logHome = context.getInitParameter(PARAM_LOG4J_LOG_HOME);
                if (logHome!=null) {
                    // set up custom log path system property
                    setFileAppenderSystemProperty(logHome, context);
                }
                boolean isXMLConfigFile = (configPath.endsWith(".xml")) ? true : false;
                String contextPath = context.getRealPath("/");
                if (contextPath!=null) {
                    // The webapp is deployed directly off the filesystem,
                    // not from a .war file so we *can* do File IO.
                    // This means we can use configureAndWatch() to re-read
                    // the the config file at defined intervals.
                    // Now let's check if the given configPath actually exists.
                    if (logHome==null) {
                        // no log path specified in web.xml. Setting to default
                        logHome = contextPath+DEFAULT_LOG_HOME;
                        setFileAppenderSystemProperty(logHome, context);
                    }
                    String systemConfigPath = configPath.replace('/', File.separatorChar);
                    File log4jFile = new File(contextPath+systemConfigPath);
                    if (log4jFile.canRead()) {
                        log4jFile = null;
                        String timerInterval = context.getInitParameter(PARAM_LOG4J_WATCH_INTERVAL);
                        long timerIntervalVal = 0L;
                        if (timerInterval!=null) {
                            try {
                                timerIntervalVal = Integer.valueOf(timerInterval).longValue();
                            }
                            catch (NumberFormatException nfe) {}
                        }
                        initLoggerRepository();
                        context.log("Configuring Log4j from File: "+contextPath+systemConfigPath);
                        if (timerIntervalVal > 0) {
                            context.log("Configuring Log4j with watch interval: "+timerIntervalVal+"ms");
                            if (isXMLConfigFile) {
                                DOMConfigurator.configureAndWatch(contextPath+systemConfigPath, timerIntervalVal);
                            }
                            else {
                                PropertyConfigurator.configureAndWatch(contextPath+systemConfigPath, timerIntervalVal);
                            }
                        }
                        else {
                            if (isXMLConfigFile) {
                                DOMConfigurator.configure(contextPath+systemConfigPath);
                            }
                            else {
                                PropertyConfigurator.configure(contextPath+systemConfigPath);
                            }
                        }
                    }
                    else {
                        // The given configPath does not exist.  So, let's just let Log4j look for the
                        // default files (log4j.properties or log4j.xml) on its own.
                        displayConfigNotFoundMessage();
                    } //end log4jFile.canRead() check
                }
                else {
                    // The webapp is deployed from a .war file, not directly
                    // off the file system so we *cannot* do File IO.
                    // Note that we *won't* be able to use configureAndWatch() here
                    // because that requires an absolute system file path.
                    // Now let's check if the given configPath actually exists.
                    URL log4jURL = null;
                    try {
                        log4jURL = context.getResource("/"+configPath);
                    }
                    catch (MalformedURLException murle) {}
                    if (log4jURL!=null) {
                        initLoggerRepository();
                        context.log("Configuring Log4j from URL at path: /"+configPath);
                        if (isXMLConfigFile) {
                            try {
                                DOMConfigurator.configure(log4jURL);
                            }
                            //catch (javax.xml.parsers.FactoryConfigurationError fce) {}
                            catch (Exception e) {
                                //report errors to server logs
                                LogLog.error(e.getMessage());
                            }
                        }
                        else {
                            Properties log4jProps = new Properties();
                            try {
                                log4jProps.load(log4jURL.openStream());
                                PropertyConfigurator.configure(log4jProps);
                            }
                            //catch (java.io.IOException ioe) {}
                            catch (Exception e) {
                                //report errors to server logs
                                LogLog.error(e.getMessage());
                            }
                        }
                    }
                    else {
                        // The given configPath does not exist.  So, let's just let Log4j look for the
                        // default files (log4j.properties or log4j.xml) on its own.
                        displayConfigNotFoundMessage();
                    } //end log4jURL null check
                } //end contextPath null check
            }
            else {
                LogLog.error("Zero length Log4j config file path given.");
                displayConfigNotFoundMessage();
            } //end configPath length check
        }
        else {
            LogLog.error("Missing log4j-config servlet parameter missing.");
            displayConfigNotFoundMessage();
        } //end configPath null check
    }

    private void displayConfigNotFoundMessage() {
        LogLog.warn("No Log4j configuration file found at given path. Falling back to Log4j auto-configuration.");
    }

    private void setFileAppenderSystemProperty(String logHome, ServletContext context) {
        File logHomeDir = new File(logHome);
        if (logHomeDir.exists() || logHomeDir.mkdirs()) {
            String tempdir = ""+context.getAttribute("javax.servlet.context.tempdir");
            int lastSlash = tempdir.lastIndexOf(File.separator);
            if ((tempdir.length()-1) > lastSlash) {
                String logHomePropertyName = tempdir.substring(lastSlash+1) + ".log.home";
                context.log("Setting system property [ " + logHomePropertyName + " ] to [ " + logHome + " ]");
                System.setProperty(logHomePropertyName, logHome);
            }
        }
    }

    private void initLoggerRepository() {
        ContextClassLoaderSelector.doIdempotentInitialization();
    }
}
