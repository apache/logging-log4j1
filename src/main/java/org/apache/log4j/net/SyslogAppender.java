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

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.SyslogWriter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;


// Contributors: Yves Bossel <ybossel@opengets.cl>
//               Christopher Taylor <cstaylor@pacbell.net>

/**
 * Implements an RFC 3164 compliant agent to send log messages to a remote
 * syslog daemon.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 * @author Hermod Opstvedt
 * @author Curt Arnold
 */
public class SyslogAppender extends WriterAppender {
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
     * Names of facilities.
     */
    private static final String[] FACILITIES =
            new String[] {
                    "kern", "user", "mail", "daemon",
                    "auth", "syslog", "lpr", "news",
                    "uucp", "cron", "authpriv","ftp",
                    null, null, null, null,
                    "local0", "local1", "local2", "local3",
                    "local4", "local5", "local6", "local7"

            };


  protected static final int SYSLOG_HOST_OI = 0;
  protected static final int FACILITY_OI = 1;
  static final String TAB = "    ";
  int syslogFacility = LOG_USER;
  String facilityStr = "user";
    /**
     * In log4j 1.2, controlled whether facility name was included in message,
     * but has no effect in current code.
     * @deprecated since 1.3
     */
  boolean facilityPrinting = false;

  String localHostname;
  String syslogHost;

  // We must use US locale to get the correct month abreviation  
  private SimpleDateFormat sdf =
    new SimpleDateFormat("MMM dd hh:mm:ss", new DateFormatSymbols(Locale.US));

  public SyslogAppender() {
  }

  public
  SyslogAppender(final Layout layout, final int syslogFacility) {
      this.layout = layout;
      this.syslogFacility = syslogFacility;
      String newFacilityStr = getFacilityString(syslogFacility);
      if (newFacilityStr != null) {
          facilityStr = newFacilityStr;
      }
  }

   public
   SyslogAppender(final Layout layout, final String syslogHost, final int syslogFacility) {
      this(layout, syslogFacility);
      setSyslogHost(syslogHost);
   }


  /**
   * This method gets the network name of the machine we are running on.
   * Returns "UNKNOWN_HOST" in the unlikely case where the host name cannot be
   * found.
   * @return String
   */
  public String getLocalHostname() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostName();
    } catch (UnknownHostException uhe) {
      getLogger().error(
        "Could not determine local host name for SyslogAppender" + "named ["
        + name + "].", uhe);
      return "UNKNOWN_HOST";
    }
  }

    /**
      * Returns the specified syslog facility as a lower-case String,
      * e.g. "kern", "user", etc.
      * @deprecated since 1.3
    */
    public
    static
    String getFacilityString(final int syslogFacility) {
        String facilityStr = null;
        if((syslogFacility & 0x7) == 0) {
           int index = syslogFacility >> 3;
           if(index >= 0 && index < FACILITIES.length) {
               facilityStr = FACILITIES[index];
           }
        }
        return facilityStr;
    }


  /**
   * Returns the integer value corresponding to the named syslog facility,
   * or -1 if it couldn't be recognized.
   *
   * */
  public static int getFacility(final String facilityStr) {
    int code = -1;
    if (facilityStr != null) {
        for(int i = 0; i < FACILITIES.length; i++) {
            if (facilityStr.equalsIgnoreCase(FACILITIES[i])) {
                code = i << 3;
                break;
            }
        }
    }
    return code;
  }

  /**
   * This method returns immediately as options are activated when they are set.
   * */
  public void activateOptions() {
    if (facilityStr == null) {
      String errMsg =
        "The Facility option must be set for SyslogAppender named [" + name
        + "].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }
    facilityStr = facilityStr.trim();
    getLogger().debug("Facility string set to be {}.", facilityStr);
    syslogFacility = getFacility(facilityStr);
    getLogger().debug("Facility set to be "+ syslogFacility);
    
    if (syslogFacility == -1) {
      String errMsg =
        "Unrecognized Facility option \"" + facilityStr
        + "\" SyslogAppender named [" + name + "].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (syslogHost == null) {
      String errMsg =
        "No syslog host is set for SyslogAppender named \"" + this.name
        + "\".";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (layout == null) {
      String errMsg =
        "No Layout is set for SyslogAppender named \"" + this.name
        + "\".";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }
    
    localHostname = getLocalHostname();

    SyslogWriter sw = new SyslogWriter(syslogHost);
    setWriter(sw);
    super.activateOptions();
  }

    /**
       The SyslogAppender requires a layout. Hence, this method returns
       <code>true</code>.

       @since 0.8.4 */
    public
    boolean requiresLayout() {
      return true;
    }


  /**
   * The <b>SyslogHost</b> option is the name of the the syslog host where log
   * output should go.  A non-default port can be specified by
   * appending a colon and port number to a host name,
   * an IPv4 address or an IPv6 address enclosed in square brackets.
   *
   * <b>WARNING</b> If the SyslogHost is not set, then this appender will fail.
   */
  public void setSyslogHost(String syslogHost) {
    this.syslogHost = syslogHost;
  }

  /**
   * Returns the value of the <b>SyslogHost</b> option.
   */
  public String getSyslogHost() {
    return syslogHost;
  }

  /**
   * Set the syslog facility. This is the <b>Facility</b> option.
   *
   * <p>The <code>facility</code> parameter must be one of the strings KERN,
   * USER, MAIL, DAEMON, AUTH, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP,
   * NTP, AUDIT, ALERT, CLOCK, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5,
   * LOCAL6, LOCAL7. Case is not important.
   *
   * See RFC 3164 for more information about the
   * <b>Facility</b> option.
   * */
  public void setFacility(final String facility) {
    if (facility != null) {
        syslogFacility = getFacility(facility);
        if (syslogFacility == -1) {
          System.err.println("["+facility +
                      "] is an unknown syslog facility. Defaulting to [USER].");
          syslogFacility = LOG_USER;
        }
        facilityStr = getFacilityString(syslogFacility);
    }
  }

  /**
   * Returns the value of the <b>Facility</b> option.
   *
   * See {@link #setFacility} for the set of allowed values.
   */
  public String getFacility() {
    return facilityStr;
  }

  private void writeTimestamp() throws IOException {
    StringBuffer timestamp = new StringBuffer();
    sdf.format(new Date(), timestamp, new FieldPosition(0));
    //According to the RFC the day of the month must be right justified with
    // no leading 0.
    if (timestamp.charAt(4) == '0') {
      timestamp.setCharAt(4, ' ');
    }
    qw.write(timestamp.toString());
  }

  private void writeInitialParts(LoggingEvent event) throws IOException {
    int pri = syslogFacility + event.getLevel().getSyslogEquivalent();
    qw.write("<");
    qw.write(String.valueOf(pri));
    qw.write(">");
    writeTimestamp();
    qw.write(' ');
    qw.write(localHostname);
    qw.write(' ');
  }
  
  protected void subAppend(LoggingEvent event) {
    try {
      writeInitialParts(event);
      String msg = layout.format(event);
      qw.write(msg);
      qw.flush();

/*
      if (layout.ignoresThrowable()) {
        String[] s = event.getThrowableStrRep();
        if (s != null) {
          int len = s.length;
          if(len > 0) {
            sqw.write(s[0]);
            for(int i = 1; i < len; i++) {
              sqw.write(TAB+s[i].substring(1));
            }
          }
        }
      }
*/
    } catch (IOException ioe) {
    }
  }

  /**
     * If the <b>FacilityPrinting</b> option is set to true, the printed
     * message will include the facility name of the application. It is
     * <em>false</em> by default.
     *
     * @deprecated No effect in log4j 1.3
     */
    public
    void setFacilityPrinting(boolean on) {
      facilityPrinting = on;
    }

    /**
     * Returns the value of the <b>FacilityPrinting</b> option.
     *
     * @deprecated No effect in log4j 1.3
     */
    public
    boolean getFacilityPrinting() {
      return facilityPrinting;
    }
    
    /**
     * Logs a footer/header as info.
     */
    private void info(String msg) {
      if (msg == null)
        return;
      LoggingEvent event = new LoggingEvent();
      event.setLevel(Level.INFO);
      try {
        writeInitialParts(event);
      } catch (IOException e) {
      }
      qw.write(msg);
      qw.flush();      
    }
    
    protected void writeFooter() {
      if (layout != null) {
        info(layout.getFooter());
      }
    }

    protected void writeHeader() {
      if (layout != null) {
        info(layout.getHeader());
      }
    }


}
