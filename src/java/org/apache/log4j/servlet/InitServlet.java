/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.log4j.selectors.ContextClassLoaderSelector;

/**
 * A servlet for initializing Log4j. See
 * <a href="http://jakarta.apache.org/log4j/docs/documentation.html">Log4j documentation</a>
 * for how to use Log4j.
 * <p>
 * <strong>Note:</strong> This log4j initialization servlet should be used *only* when
 * running under a container which doesn't support servlet-2.3. A far better choice for
 * servlet-2.3 configuration exists in {@link Log4jApplicationWatch}. Use it instead of
 * this class for initialization.  If you really need to use this class, read on...</p>
 * <p>
 * This servlet is never called by a client, but should be called during
 * web application initialization, i.e. when the servlet engine starts. The
 * following code should be inserted in the web.xml file for the web
 * application:
 * <p>
 * <pre>
 *  &lt;servlet&gt;
 *      &lt;servlet-name&gt;log4j-init&lt;/servlet-name&gt;
 *      &lt;servlet-class&gt;org.apache.log4j.servlet.InitServlet&lt;/servlet-class&gt;
 *      &lt;init-param&gt;
 *           &lt;param-name&gt;log4j-config&lt;/param-name&gt;
 *           &lt;param-value&gt;WEB-INF/log4j.xml&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      &lt;init-param&gt;
 *           &lt;param-name&gt;log4j-cron&lt;/param-name&gt;
 *           &lt;param-value&gt;5000&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      &lt;init-param&gt;
 *           &lt;param-name&gt;log4j-log-home&lt;/param-name&gt;
 *           &lt;param-value&gt;/usr/local/logs/tomcat&lt;/param-value&gt;
 *      &lt;/init-param&gt;
 *      &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 *  &lt;/servlet&gt;
 * </pre>
 * </p>
 * <p>
 * See {@link Log4jApplicationWatch} for detailed information about these parameters.
 * </p>
 *
 * @author  Jacob Kjome <hoju@visi.com>
 * @since   1.3
 */
public class InitServlet extends HttpServlet {

    private final static String PARAM_LOG4J_CONFIG_PATH = "log4j-config";
    private final static String PARAM_LOG4J_WATCH_INTERVAL = "log4j-cron";
    private final static String PARAM_LOG4J_LOG_HOME = "log4j-log-home";
    private final static String DEFAULT_LOG_HOME = "WEB-INF" + File.separator + "logs";

    private static Boolean CONFIGURED = Boolean.FALSE;

    public void init() throws ServletException {
        if (CONFIGURED.equals(Boolean.FALSE)) {
            String configPath = getInitParameter(PARAM_LOG4J_CONFIG_PATH);
            // if the log4j-config parameter is not set, then no point in trying
            if (configPath!=null) {
                if (configPath.startsWith("/")) configPath = (configPath.length() > 1) ? configPath.substring(1) : "";
                // if the configPath is an empty string, then no point in trying
                if (configPath.length() >= 1) {
                    // set up log path System property
                    String logHome = getInitParameter(PARAM_LOG4J_LOG_HOME);
                    if (logHome!=null) {
                        // set up custom log path system property
                        setFileAppenderSystemProperty(logHome, this);
                    }
                    boolean isXMLConfigFile = (configPath.endsWith(".xml")) ? true : false;
                    String contextPath = getServletContext().getRealPath("/");
                    if (contextPath!=null) {
                        // The webapp is deployed directly off the filesystem,
                        // not from a .war file so we *can* do File IO.
                        // This means we can use configureAndWatch() to re-read
                        // the the config file at defined intervals.
                        // Now let's check if the given configPath actually exists.
                        if (logHome==null) {
                            // no log path specified in web.xml. Setting to default
                            logHome = contextPath+DEFAULT_LOG_HOME;
                            setFileAppenderSystemProperty(logHome, this);
                        }
                        String systemConfigPath = configPath.replace('/', File.separatorChar);
                        File log4jFile = new File(contextPath+systemConfigPath);
                        if (log4jFile.canRead()) {
                            log4jFile = null;
                            String timerInterval = getInitParameter(PARAM_LOG4J_WATCH_INTERVAL);
                            long timerIntervalVal = 0L;
                            if (timerInterval!=null) {
                                try {
                                    timerIntervalVal = Integer.valueOf(timerInterval).longValue();
                                }
                                catch (NumberFormatException nfe) {}
                            }
                            synchronized (CONFIGURED) {
                                if (CONFIGURED.equals(Boolean.FALSE)) {
                                    initLoggerRepository();
                                    log("Configuring Log4j from File: "+contextPath+systemConfigPath);
                                    if (timerIntervalVal > 0) {
                                        log("Configuring Log4j with watch interval: "+timerIntervalVal+"ms");
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
                                    CONFIGURED = Boolean.TRUE;
                                } //end CONFIGURED check
                            } //end syncronized block
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
                            log4jURL = getServletContext().getResource("/"+configPath);
                        }
                        catch (MalformedURLException murle) {}
                        if (log4jURL!=null) {
                            synchronized (CONFIGURED) {
                                if (CONFIGURED.equals(Boolean.FALSE)) {
                                    initLoggerRepository();
                                    log("Configuring Log4j from URL at path: /"+configPath);
                                    if (isXMLConfigFile) {
                                        try {
                                            DOMConfigurator.configure(log4jURL);
                                            CONFIGURED = Boolean.TRUE;
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
                                            CONFIGURED = Boolean.TRUE;
                                        }
                                        //catch (IOException ioe) {}
                                        catch (Exception e) {
                                            //report errors to server logs
                                            LogLog.error(e.getMessage());
                                        }
                                    }
                                } //end CONFIGURED check
                            } //end syncronized block
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
        } //end CONFIGURED check
    } //end init() method

    private void displayConfigNotFoundMessage() {
        LogLog.warn("No Log4j configuration file found at given path. Falling back to Log4j auto-configuration.");
    }

    private void setFileAppenderSystemProperty(String logHome, ServletConfig config) {
        File logHomeDir = new File(logHome);
        if (logHomeDir.exists() || logHomeDir.mkdirs()) {
            ServletContext context = config.getServletContext();
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

    /**
     * Throws a ServletException.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
                                    throws ServletException, IOException {
        throw new ServletException("Servlet only used for Log4j initialization");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
                                    throws ServletException, IOException {
        doGet(req, res);
    }
}
