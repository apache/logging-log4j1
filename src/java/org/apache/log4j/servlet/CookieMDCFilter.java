/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.servlet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
  A useful Servlet 2.3 compatible filter which will search for a predefined 
  set of cookies in the request and place their values into the log4j MDC. 
  The key used in the MDC is the name of the cookie.  The value placed in
  the MDC is the value of the cookie in the request.
  
  @author Mark Womack <mwomack@apache.org>
  @since 1.3
  */
  
//  An example of the web.xml configuration:
//
//    <!-- looks for cookies named "JSESSIONID" and "USERID" -->
//    <filter>
//      <filter-name>trace-cookie-context-filter</filter-name>
//      <filter-class>
//        org.apache.log4j.servlet.CookieMDCFilter
//      </filter-class>
//      <init-param>
//        <param-name>cookie-list</param-name>
//        <param-value>
//          JSESSIONID
//          USERID
//        </param-value>
//      </init-param>
//    </filter>
//    
//    <!-- any jsp will have this filter execute first -->
//    <filter-mapping>
//      <filter-name>trace-cookie-context-filter</filter-name>
//      <url-pattern>*/*.jsp</url-pattern>
//    </filter-mapping>
//  
//  An example of the log4j xml configuration using PatternLayout:
//  
//    <appender name="stdout" class="org.apache.log4j.ConsoleAppender">
//      <layout class="org.apache.log4j.PatternLayout">
//        <param name="ConversionPattern"
//           value="%d{ABSOLUTE} %-5p %X{JSESSIONID} %X{USERID} %c{1} : %m%n"/>
//      </layout>
//    </appender>
public class CookieMDCFilter implements Filter {
    private static final Logger _TRACE =
        Logger.getLogger(CookieMDCFilter.class);
    
    /**
      The set of cookies names we want to look for. */
    private HashMap cookieMap;
    
    /**
      Uses filter init parameter to initialize a list of cookies that will be
      retrieved from the request and placed into the log4j MDC context. */
    public void init(FilterConfig filterConfig) throws ServletException {
        
        // check for existense of init param
        String cookieParam = filterConfig.getInitParameter("cookie-list");
        if (cookieParam == null || cookieParam.length() == 0)
            return;
        
        // parse the list of cookies
        StringTokenizer tokenizer = new StringTokenizer(cookieParam);
        while (tokenizer.hasMoreTokens()) {
            if (cookieMap == null) {
                cookieMap = new HashMap();
            }
            cookieMap.put(tokenizer.nextToken(), null);
        }
        
        // report the configuration
        if (cookieMap != null && _TRACE.isDebugEnabled()) {
            Iterator iter = cookieMap.keySet().iterator();
            while (iter.hasNext()) {
                _TRACE.debug("configured to search for cookie with name " + 
                    iter.next());
            }
        }
    }
    
    /**
      Search the request cookies for the cookies whose value we want to
      stuff into the log4j MDC. */
    public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException, ServletException {
        
        // if this filter configured and request has cookies, search
        if (cookieMap != null) {
            Cookie[] cookies = ((HttpServletRequest)request).getCookies();
            if (cookies != null) {
                for (int x = 0; x < cookies.length; x++) {
                    
                    // if request has a cookie we are lookig for, put its
                    // value into the MDC
                    String name = cookies[x].getName();
                    if (cookieMap.containsKey(cookies[x].getName())) {
                        MDC.put(name, cookies[x].getValue());
                        if (_TRACE.isDebugEnabled())
                            _TRACE.debug("put into MDC cookie with name " + 
                                name + " and value " + cookies[x].getValue());
                    }
                    else {
                        if (_TRACE.isDebugEnabled())
                            _TRACE.debug("ignoring cookie with name " + name);
                        
                    }
                }
            }
        }
        
        // pass control to the next filter
        chain.doFilter(request, response);
    }
    
    /**
      Not used. */
    public void destroy() {
        // do nothing
    }
}
