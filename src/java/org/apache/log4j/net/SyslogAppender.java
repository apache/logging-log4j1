/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.net.SocketException;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.SyslogWriter;
import org.apache.log4j.helpers.SyslogQuietWriter;
import org.apache.log4j.net.SyslogTracerPrintWriter;

// Contributors: Yves Bossel <ybossel@opengets.cl>
//               Christopher Taylor <cstaylor@pacbell.net>

/**
    Use SyslogAppender to send log messages to a remote syslog daemon.
 
    @author Ceki G&uuml;lc&uuml; 
 */
public class SyslogAppender extends AppenderSkeleton {
  // The following constants are extracted from a syslog.h file
  // copyrighted by the Regents of the University of California
  // I hope nobody at Berkley gets offended.
  
  /** Kernel messages */
  final static public int LOG_KERN     = 0; 
  /** Random user-level messages */
  final static public int LOG_USER     = 1<<3; 
  /** Mail system */
  final static public int LOG_MAIL     = 2<<3; 
  /** System daemons */
  final static public int LOG_DAEMON   = 3<<3; 
  /** security/authorization messages */
  final static public int LOG_AUTH     = 4<<3; 
  /** messages generated internally by syslogd */
  final static public int LOG_SYSLOG   = 5<<3; 
                                               
  /** line printer subsystem */  
  final static public int LOG_LPR      = 6<<3; 
  /** network news subsystem */
  final static public int LOG_NEWS     = 7<<3; 
  /** UUCP subsystem */
  final static public int LOG_UUCP     = 8<<3; 
  /** clock daemon */
  final static public int LOG_CRON     = 9<<3; 
  /** security/authorization  messages (private) */
  final static public int LOG_AUTHPRIV = 10<<3; 
  /** ftp daemon */
  final static public int LOG_FTP      = 11<<3; 

  // other codes through 15 reserved for system use
  /** reserved for local use */
  final static public int LOG_LOCAL0 = 16<<3; 
  /** reserved for local use */
  final static public int LOG_LOCAL1 = 17<<3; 
  /** reserved for local use */
  final static public int LOG_LOCAL2 = 18<<3; 
  /** reserved for local use */
  final static public int LOG_LOCAL3 = 19<<3; 
  /** reserved for local use */
  final static public int LOG_LOCAL4 = 20<<3; 
  /** reserved for local use */
  final static public int LOG_LOCAL5 = 21<<3; 
  /** reserved for local use */
  final static public int LOG_LOCAL6 = 22<<3; 
  /** reserved for local use*/
  final static public int LOG_LOCAL7 = 23<<3; 

   /**
     A string constant used in naming the option for setting the the
     syslog server.  Current value of this string constant is
     <b>SyslogHost</b>.
     @since 0.8.1 */
  public static final String SYSLOG_HOST_OPTION = "SyslogHost";

   /**
     A string constant used in naming the option for setting facility
     type.  Current value of this string constant is <b>Facility</b>.

     @since 0.8.1 */
  public static final String FACILITY_OPTION = "Facility";  

   /**
     A string constant used in naming the option for setting whether
     the facility name is printed or not.  Current value of this
     string constant is <b>FacilityPrinting</b>.

     @since 0.8.1 */
  public static final String FACILITY_PRINTING_OPTION = "FacilityPrinting";  

  protected static final int SYSLOG_HOST_OI = 0;
  protected static final int FACILITY_OI = 1;
  
  // Have LOG_USER as default
  int syslogFacility = LOG_USER;
  String facilityStr;  
  boolean facilityPrinting = false;
  
  SyslogTracerPrintWriter stp;
  SyslogQuietWriter sqw;  
  String syslogHost;
  
  public
  SyslogAppender() {
    this.initSyslogFacilityStr(this.syslogFacility);
  }
  
  public
  SyslogAppender(Layout layout, int syslogFacility) {
    this.layout = layout;
    this.syslogFacility = syslogFacility;    
    this.initSyslogFacilityStr(syslogFacility);
  }
		 
  public
  SyslogAppender(Layout layout, String syslogHost, int syslogFacility) {
    this(layout, syslogFacility);
    setSyslogHost(syslogHost);
  }


  /**
     Release any resources held by this SyslogAppender.

     @since 0.8.4
   */
  public
  void close() {
    closed = true;
    // A SyslogWriter is UDP based and needs no opening. Hence, it
    // can't be closed. We just unset the variables here.    
    sqw = null;
    stp = null;
  }
  
  private
  void initSyslogFacilityStr(int syslogFacility) {
    switch(syslogFacility) {
    case LOG_KERN: facilityStr = "kern:"; break;
    case LOG_USER: facilityStr = "user:"; break;
    case LOG_MAIL: facilityStr = "mail:"; break;
    case LOG_DAEMON: facilityStr = "daemon:"; break;
    case LOG_AUTH: facilityStr = "auth:";; break;
    case LOG_SYSLOG: facilityStr = "syslog:"; break;
    case LOG_LPR: facilityStr = "lpr:"; break;
    case LOG_NEWS: facilityStr = "news:"; break;
    case LOG_UUCP: facilityStr = "uucp:"; break;
    case LOG_CRON: facilityStr = "cron:"; break;
    case LOG_AUTHPRIV: facilityStr = "authpriv:"; break;
    case LOG_FTP: facilityStr = "ftp:"; break;
    case LOG_LOCAL0: facilityStr = "local0:"; break;
    case LOG_LOCAL1: facilityStr = "local1:"; break;
    case LOG_LOCAL2: facilityStr = "local2:"; break;
    case LOG_LOCAL3: facilityStr = "local3:"; break;
    case LOG_LOCAL4: facilityStr = "local4:"; break;
    case LOG_LOCAL5: facilityStr = "local5:"; break;
    case LOG_LOCAL6: facilityStr = "local6:"; break;
    case LOG_LOCAL7: facilityStr = "local7:"; break;
    default: 
      System.err.println("\"" + syslogFacility +
                  "\" is an unknown syslog facility. Defaulting to \"USER\".");
      this.syslogFacility = LOG_USER;
      facilityStr = "user:";
    }	   
  }	   

  public
  void append(LoggingEvent event) {

    if(!isAsSevereAsThreshold(event.priority))
      return;
    
    // We must not attempt to append if sqw is null.
    if(sqw == null) {
      errorHandler.error("No syslog host is set for SyslogAppedender named \""+
			this.name+"\".");
      return;
    }

    String buffer = (facilityPrinting? facilityStr : "") +
                          layout.format(event);

    sqw.setPriority(event.priority.getSyslogEquivalent());    
    sqw.write(buffer);

 
    if(event.throwable != null) 
      event.throwable.printStackTrace(stp);
    else if (event.throwableInformation != null) {
      sqw.write(event.throwableInformation);
    }
  }

  /**
     This method returns immediately as options are activated when they
     are set.

     @see #setOption
  */
  public
  void activateOptions() {
  }

  
  /**
     Retuns the option names for this component, namely the string
     array consisting of {{@link #SYSLOG_HOST_OPTION}, {@link
     #FACILITY_OPTION}, {@link #FACILITY_PRINTING_OPTION}}.

     @since 0.8.1 */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
		      new String[] {SYSLOG_HOST_OPTION, FACILITY_OPTION,
			            FACILITY_PRINTING_OPTION});
  }
  
  /**
     The SyslogAppender requires a layout. Hence, this method return
     <code>true</code>.

     @since 0.8.4 */
  public
  boolean requiresLayout() {
    return true;
  }
  
  /**
     Set the syslog facility.

     <p>The <code>facilityName</code> parameter must be one of the
     strings KERN, USER, MAIL, DAEMON, AUTH, SYSLOG, LPR, NEWS, UUCP,
     CRON, AUTHPRIV, FTP, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4,
     LOCAL5, LOCAL6, LOCAL7. Case is unimportant.
     
     @since 0.8.1 */
  public
  void setFacility(String facilityName) {
    if(facilityName == null)
      return;
    
    if("KERN".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_KERN;
    else if("USER".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_USER;
    else if("MAIL".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_MAIL;
    else if("DAEMON".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_DAEMON;
    else if("AUTH".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_AUTH;
    else if("SYSLOG".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_SYSLOG;
    else if("LPR".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LPR;
    else if("NEWS".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_NEWS;
    else if("UUCP".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_UUCP;
    else if("CRON".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_CRON;
    else if("AUTHPRIV".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_AUTHPRIV;
    else if("FTP".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_FTP;
    else if("LOCAL0".equalsIgnoreCase(facilityName)) 
      this.syslogFacility = LOG_LOCAL0;
    else if("LOCAL1".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL1;
    else if("LOCAL2".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL2;
    else if("LOCAL3".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL3;
    else if("LOCAL4".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL4;
    else if("LOCAL5".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL5;
    else if("LOCAL6".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL6;
    else if("LOCAL7".equalsIgnoreCase(facilityName))
      this.syslogFacility = LOG_LOCAL7;
    else {
      System.err.println(facilityName +
                  " is an unknown syslog facility. Defaulting to \"USER\".");
      this.syslogFacility = LOG_USER;
    }
    this.initSyslogFacilityStr(this.syslogFacility);

    // If there is already a sqw, make it use the new facility.
    if(sqw != null) {
      sqw.setSyslogFacility(this.syslogFacility);
    }

  }
  
  /**
    Set SyslogAppender specific parameters. 

    <p>The recognized options are <b>SyslogHost</b>, <b>Facility</b> and
    <b>FacilityPrinting</b>, i.e. the values of the string constants
    {@link #SYSLOG_HOST_OPTION}, {@link #FACILITY_OPTION} and {@link
    #FACILITY_PRINTING_OPTION} respectively.
     
    <dl>

    <p><dt><b>SyslogHost</b>

    <dd>The host name of the syslog host where log output should
    go.

    <b>WARNING</b> If the SyslogHost is not set, then this appender
    will fail. 


     <p><dt><b>Facility</b>

     A string representing the syslog facility.

     <p>Acceptable values are in the set {KERN, USER, MAIL, DAEMON,
     AUTH, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP LOCAL0,
     LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6, LOCAL7}.
    
    <p><dt><b>FacilityPrinting</b>

    <dd>If set to true, the printed message will include the facility
    name of the application. Is set to <em>false</em> by default.
    
    </dl>

    <p>
    @since 0.8.1 */
  public
  void setOption(String option, String value) {
    if(value == null) return;
    
    super.setOption(option, value);    
    
    if(option.equals(SYSLOG_HOST_OPTION)) 
      this.setSyslogHost(value);
    else if(option.equals(FACILITY_PRINTING_OPTION))
      facilityPrinting = OptionConverter.toBoolean(value, facilityPrinting);
    else if(option.equals(FACILITY_OPTION)) {
      this.setFacility(value);
    }
  }

  public
  void setSyslogHost(String syslogHost) {
    this.sqw = new SyslogQuietWriter(new SyslogWriter(syslogHost), 
				     syslogFacility, errorHandler);
    this.stp = new SyslogTracerPrintWriter(sqw);    
    this.syslogHost = syslogHost;
  }
}
