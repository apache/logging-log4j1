/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  
*/

package org.apache.log4j.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;


/**
 * A servlet used to dynamically adjust package logging levels
 * while an application is running.  NOTE: This servlet is 
 * only aware of pre-configured packages and packages that contain objects 
 * that have logged at least one message since application startup.
 *
 * @author <a href="mailto:lebirdzell@yahoo.com">Luther E. Birdzell</a>
 * @since 1.3
 */
public class ConfigurationServlet extends HttpServlet implements SingleThreadModel 
{
    /**
     * The response content type: text/html
     */
    public static final String CONTENT_TYPE = "text/html";

    /**
     * The root appender.
     */
    public static final String ROOT         = "Root";

    /**
     * The name of the class / package.
     */
    public static final String CLASS        = "CLASS";
    
    /**
     * The logging level.
     */
    public static final String PRIORITY     = "PRIORITY";

    /**
     * Print the status of all current <code>Logger</code>s and 
     * an option to change their respective logging levels.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException  
    {
        response.setContentType(CONTENT_TYPE);

        PrintWriter out        = response.getWriter();
        List        loggers    = getSortedLoggers();
        Logger      logger     = null;
        String      loggerName = null;
        int         loggerNum  = 0;
        
        // print title and header
        out.println("<html><head><title>Log4J Control Console</title></head>" +
                    "<body><H3>Log4J Control Console</H3>");
        out.println("<A href=\"" + request.getRequestURI() + "\">Refresh</A><HR>");
        out.println("<table width=\"50%\" border=\"1\">");
        out.println("<tr BGCOLOR=\"#5991A6\">");
        out.println("<td><FONT  COLOR=\"BLACK\" FACE=\"Helvetica\"><B>Class</B></FONT></td>");
        out.println("<td><FONT  COLOR=\"BLACK\" FACE=\"Helvetica\"><B>Priority</B></FONT></td>");
        out.println("</tr>");

        // print the root Logger
        displayLogger(out, Logger.getRootLogger(), loggerNum++, request);

        // print the rest of the loggers
        Iterator ii = loggers.iterator();
        while( ii.hasNext() ) 
        {
            displayLogger(out, (Logger) ii.next(), loggerNum++, request);
        }

        out.println("</table>");
        out.println("<FONT SIZE=\"-3\" COLOR=\"BLACK\" FACE=\"Helvetica\">* "+
                    "Inherits Priority From Parent.</FONT><BR>");
        out.println("<A href=\"" + request.getRequestURI() + "\">Refresh</A><HR>");

        // print set options
        out.println("<FORM action=\"" +  request.getRequestURI() + "\" method=\"post\">");
        out.println("<FONT  SIZE=\"+2\" COLOR=\"BLACK\" FACE=\"Helvetica\"><U>"+
                    "Set Log4J Option</U><BR><BR></FONT>");
        out.println("<FONT COLOR=\"BLACK\" FACE=\"Helvetica\">");
        out.println("<table width=\"50%\" border=\"1\">");
        out.println("<tr BGCOLOR=\"#5991A6\">");
        out.println("<td><FONT COLOR=\"BLACK\" "+
                    "FACE=\"Helvetica\"><B>Class Name:</B></FONT></td>");
        out.println("<td><SELECT name=\"CLASS\">");
        out.println("<OPTION VALUE=\"" + ROOT + "\">" + ROOT + "</OPTION>");
        
        ii = loggers.iterator();
        while( ii.hasNext() ) 
        {
            logger     = (Logger) ii.next();
            loggerName = (logger.getName().equals("") ? "Root" : logger.getName());
            out.println("<OPTION VALUE=\"" + loggerName + "\">" + loggerName + "</OPTION>");
        }
        out.println("</SELECT><BR></td></tr>");

        // print logging levels
        out.println("<tr BGCOLOR=\"#5991A6\"><td><FONT COLOR=\"BLACK\" "+
                    "FACE=\"Helvetica\"><B>Priority:</B></FONT></td>");
        out.println("<td><SELECT name=\"PRIORITY\">");
        out.println("<OPTION VALUE=\"" + Level.OFF   + "\">" + Level.OFF   + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.FATAL + "\">" + Level.FATAL + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.ERROR + "\">" + Level.ERROR + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.WARN  + "\">" + Level.WARN  + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.INFO  + "\">" + Level.INFO  + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.DEBUG + "\">" + Level.DEBUG + "</OPTION>");
        out.println("<OPTION VALUE=\"" + Level.ALL   + "\">" + Level.ALL   + "</OPTION>");
        out.println("</SELECT><BR></td></tr>");
        out.println("</table></FONT>");
        out.println("<input type=\"submit\" name=\"Submit\" value=\"Set Option\"></FONT>");
        out.println("</FORM>");
        out.println("</body></html>");

        out.flush();
        out.close();
    }

    /**
     * Change a <code>Logger</code>'s level, then call <code>doGet</code>
     * to refresh the page.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception java.io.IOException if an error occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException  
    {
        String className = (String) request.getParameter(CLASS);
        String priority  = (String) request.getParameter(PRIORITY);

        if (className != null) 
        {
            setClass(className, priority);
        }
    
        doGet(request, response);
    }

    // print a Logger and its current level
    private void displayLogger(PrintWriter out, Logger logger, 
                               int row, HttpServletRequest request) 
    {
        String color      = null;
        String loggerName = (logger.getName().equals("") ? ROOT : logger.getName());
    
        color = (row%2 == 1) ? "#E1E1E1" : "#FBFBFB";
    
        out.println("<tr BGCOLOR=\"" + color + "\">");
        out.println("<td><FONT SIZE=\"-2\" COLOR=\"BLACK\" FACE=\"Helvetica\">" + 
                    loggerName + "</FONT></td>");
        out.println("<td><FONT SIZE=\"-2\" COLOR=\"BLACK\" FACE=\"Helvetica\">" + 
                    ( (logger.getLevel() == null) ? logger.getEffectiveLevel().toString() +
                      "*" : logger.getLevel().toString()) + "</FONT></td>");
        out.println("</tr>");
    }
    
    // set a logger's level
    private synchronized String setClass(String className, String level) 
    {
        Logger logger  = null;
        String message = null;
        
        try 
        {
            logger = (className.equals(ROOT)) ? logger.getRootLogger() : 
                logger.getLogger(className);

            logger.setLevel(Level.toLevel(level));
        } 
    
        catch (Exception e) 
        {
            System.out.println("ERROR Setting LOG4J Logger:" + e);
        }
    
        return "Message Set For " + (logger.getName().equals("") ? ROOT : logger.getName());
    }

    // get a sorted list of all current loggers
    private List getSortedLoggers()
    {
        Logger      logger = null;
        Enumeration enum   = LogManager.getCurrentLoggers();
        Comparator  comp   = new LoggerComparator();
        ArrayList   list   = new ArrayList();

        // Add all current loggers to the list
        while(enum.hasMoreElements()) 
        {
            list.add(enum.nextElement());
        }
        
        // sort the loggers
        Collections.sort(list, comp);
    
        return list;
    }

    /**
     * Compare the names of two <code>Logger</code>s.  Used
     * for sorting.
     */
    private class LoggerComparator implements Comparator
    {
    
        /**
         * Compare the names of two <code>Logger</code>s.
         *
         * @param o1 an <code>Object</code> value
         * @param o2 an <code>Object</code> value
         * @return an <code>int</code> value
         */
        public int compare(Object o1, Object o2)
        {
            Logger logger1 = (Logger) o1;
            Logger logger2 = (Logger) o2;;
        
            String logger1Name = null;
            String logger2Name = null;
        
            if ( logger1 != null )
                logger1Name = (logger1.getName().equals("") ? ROOT : logger1.getName());
        
            if ( logger2 != null )
                logger2Name = (logger2.getName().equals("") ? ROOT : logger2.getName());
                
            return logger1Name.compareTo(logger2Name); 
        }
    
        /**
         * Return <code>true</code> if the <code>Object</code> is a
         * <code>LoggerComparator</code> instance.
         * 
         *
         * @param o an <code>Object</code> value
         * @return a <code>boolean</code> value
         */
        public boolean equals(Object o)
        {
        
            if (o instanceof LoggerComparator) 
                return true;
        
            else
                return false;
        }
    }

}//EOF

