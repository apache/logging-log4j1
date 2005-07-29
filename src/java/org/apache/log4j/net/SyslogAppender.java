/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.SyslogWriter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
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
 */
public class SyslogAppender extends AppenderSkeleton {
  protected static final int SYSLOG_HOST_OI = 0;
  protected static final int FACILITY_OI = 1;
  static final String TAB = "    ";
  int facility;
  String facilityStr;
  String localHostname;
  String syslogHost;

  //SyslogTracerPrintWriter stp;
  SyslogWriter sw;
  Calendar calendar = Calendar.getInstance();
  long now = -1;
  Date date = new Date();
  StringBuffer timestamp = new StringBuffer();
  protected FieldPosition pos = new FieldPosition(0);

  // We must use US locale to get the correct month abreviation  
  private SimpleDateFormat sdf =
    new SimpleDateFormat("MMM dd hh:mm:ss", new DateFormatSymbols(Locale.US));

  Layout layout;
  
  public SyslogAppender() {
      super(false);
  }

  /**
   * Release any resources held by this SyslogAppender.
   * @since 0.8.4
   */
  public synchronized void close() {
    closed = true;

    // A SyslogWriter is UDP based and needs no opening. Hence, it
    // can't be closed. We just unset the variables here.
    sw = null;
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
   * Returns the integer value corresponding to the named syslog facility,
   * or -1 if it couldn't be recognized.
   *
   * */
  static int facilityStringToint(String facilityStr) {
    if ("KERN".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_KERN;
    } else if ("USER".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_USER;
    } else if ("MAIL".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_MAIL;
    } else if ("DAEMON".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_DAEMON;
    } else if ("AUTH".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_AUTH;
    } else if ("SYSLOG".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_SYSLOG;
    } else if ("LPR".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LPR;
    } else if ("NEWS".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_NEWS;
    } else if ("UUCP".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_UUCP;
    } else if ("CRON".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_CRON;
    } else if ("AUTHPRIV".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_AUTHPRIV;
    } else if ("FTP".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_FTP;
    } else if ("LOCAL0".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL0;
    } else if ("LOCAL1".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL1;
    } else if ("LOCAL2".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL2;
    } else if ("LOCAL3".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL3;
    } else if ("LOCAL4".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL4;
    } else if ("LOCAL5".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL5;
    } else if ("LOCAL6".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL6;
    } else if ("LOCAL7".equalsIgnoreCase(facilityStr)) {
      return SyslogConstants.LOG_LOCAL7;
    } else {
      return -1;
    }
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
    facility = facilityStringToint(facilityStr);
    getLogger().debug("Facility set to be "+ facility);
    
    if (facility == -1) {
      String errMsg =
        "Unrecognized Facility option \"" + facilityStr
        + "\" SyslogAppender named [" + name + "].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (syslogHost == null) {
      String errMsg =
        "No syslog host is set for SyslogAppedender named \"" + this.name
        + "\".";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (layout == null) {
      String errMsg =
        "No Layout is set for SyslogAppedender named \"" + this.name
        + "\".";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }
    
    localHostname = getLocalHostname();

    sw = new SyslogWriter(syslogHost);
  }

  /**
   * The <b>SyslogHost</b> option is the name of the the syslog host where log
   * output should go.
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
   * See {@link SyslogConstants} and RFC 3164 for more information about the
   * <b>Facility</b> option.
   * */
  public void setFacility(String facility) {
    this.facilityStr = facility;
  }

  /**
   * Returns the value of the <b>Facility</b> option.
   *
   * See {@link #setFacility} for the set of allowed values.
   */
  public String getFacility() {
    return facilityStr;
  }

  void fillInTimestamp() throws IOException {
    long n = System.currentTimeMillis();
    n -= (n & 1000);

    if ((n != now) || (timestamp.length() == 0)) {
      now = n;
      date.setTime(n);
      // erase any previous value of the timestamp
      timestamp.setLength(0);
      sdf.format(date, timestamp, pos);
      //According to the RFC the day of the month must be right justified with
      // no leading 0.
      if (timestamp.charAt(4) == '0') {
        timestamp.setCharAt(4, ' ');
      }
    }
    sw.write(timestamp.toString());
  }

  void writeInitialParts(LoggingEvent event) throws IOException {
    int pri = facility+event.getLevel().getSyslogEquivalent();
    System.out.println(""+pri);
    sw.write("<");
    sw.write(String.valueOf(pri));
    sw.write(">");
    fillInTimestamp();
    sw.write(' ');
    sw.write(localHostname);
    sw.write(' ');
  }
  
  public void append(LoggingEvent event) {
    // We must not attempt to append if sw is null.
    if (sw == null) {
      return;
    }

    try {
      writeInitialParts(event);
      layout.format(sw, event);
      sw.flush();

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
  public Layout getLayout() {
    return layout;
  }
  public void setLayout(Layout layout) {
    this.layout = layout;
  }
}
