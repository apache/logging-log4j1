/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.jdbc;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Hashtable;
import java.util.StringTokenizer;


/**
 * Convert Log data stored in a database into LoggingEvents.
 *
 * This receiver executes the SQL statement defined in the plugin configuration once,
 * converting the rows it finds into LoggingEvents, and then ends.
 *
 * The configuration of this plugin is very similar to the JDBCAppender, however a
 * SELECT statement must be provided instead of an INSERT statement.
 *
 * The select statement must provide all fields which define a LoggingEvent, with
 * the column names matching this list: LOGGER, TIMESTAMP, LEVEL, THREAD, MESSAGE,
 * NDC, MDC, CLASS, METHOD, FILE, LINE, PROPERTIES, EXCEPTION
 *
 * If the source table doesn't provide a column for any of the fields, the field must
 * still be provided in the SELECT statement.  For example, if a JDBCAppender was used
 * to write only a timestamp, level and patternlayout combination of message and other
 * fields, here is a sample SQL statement you would provide as the plugin 'sql' param:
 *
 * EXAMPLE MYSQL SELECT STATEMENT WHICH CAN BE USED TO PROVIDE EVENTS TO CHAINSAW - 
 * (counter is an autoincrement int column and timestamp is a datetime column):
 * 
 * param name="sql" value='select logger as LOGGER, timestamp as TIMESTAMP, 
 * level as LEVEL, thread as THREAD, message as MESSAGE, ndc as NDC, mdc as MDC, 
 * class as CLASS, method as METHOD, file as FILE, line as LINE, 
 * concat("{{log4japp,databaselogs,log4jmachinename,mymachine,log4jid,", COUNTER, "}}") 
 * as PROPERTIES, "" as EXCEPTION from logtable' 
 *
 * In other words, if a number of LoggingEvent properties were combined into one field
 * in the database, the combined field should be set as the MESSAGE column in the
 * SELECT statement.  Missing columns should be set to "".
 *
 * Make sure to alias the column names if needed to match the list provided above.
 *
 * NOTE: Patternlayout doesn't support Properties and JDBCAppender doesn't support
 * exceptions, but the fields can be defined in the SQL statement and included in
 * the event.
 *
 * This means that log4japp and/or log4jmachinename properties can be provided and
 * the properties may be used to create a unique tab for the events.
 *
 * Both {{name, value, name2, value2}} formats and formats without the double braces
 * are supported for MDC and properties fields.
 * 
 * NOTE: If refreshMillis is not set, the receiver will run the SQL ONCE.  If it is set,
 * the SQL will be ran every refreshMillis milliseconds.
 * 
 * WARNING: Using refreshMillis with an event processing tool that doesn't know how 
 * to ignore duplicate events will result in duplicate events being processed.
 * 
 * CREATING EVENTS USABLE BY CHAINSAW: 
 * Chainsaw's event reception ignores duplicate event delivery, so refreshMillis can be 
 * set and JDBCReceiver can be used as a primary receiver with Chainsaw - allowing 
 * a timed re-retrieve of events from a database into the UI for analysis of events.
 * 
 * Include the properties as provided in the example SQL above to successfully get 
 * events to be delivered into Chainsaw.  The log4jid property must be provided by the 
 * database and the timestamp field must be a datetime.  The log4jmachinename and log4japp 
 * properties are specific to your application and define which unique tab the events are 
 * delivered to.
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class JDBCReceiver extends Receiver {
  private boolean isActive = false;

  /**
   * URL of the DB for default connection handling
   */
  protected String databaseURL = "jdbc:odbc:myDB";

  /**
   * User to connect as for default connection handling
   */
  protected String databaseUser = "me";

  /**
   * User to use for default connection handling
   */
  protected String databasePassword = "mypassword";
  protected Connection connection = null;
  protected String sqlStatement = "";
  protected String refreshMillis = null;

  public JDBCReceiver() {
  }

  /**
   * Start a thread which will handle the retrieval.
   */
  public void activateOptions() {
    new JDBCReceiverThread().start();
  }

  protected Connection getConnection() throws SQLException {
    if (!DriverManager.getDrivers().hasMoreElements()) {
      setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
    }

    if (connection == null) {
      connection =
        DriverManager.getConnection(
          databaseURL, databaseUser, databasePassword);
    }

    return connection;
  }

  public void close() {
    try {
      if ((connection != null) && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void finalize() {
    close();
  }

  public synchronized void shutdown() {
  }

  public void setRefreshMillis(String s) {
    refreshMillis = s;
  }

  public String getRefreshMillis() {
    return refreshMillis;
  }

  public void setSql(String s) {
    sqlStatement = s;
  }

  public String getSql() {
    return sqlStatement;
  }

  public void setUser(String user) {
    databaseUser = user;
  }

  public String getUser() {
    return databaseUser;
  }

  public void setURL(String url) {
    databaseURL = url;
  }

  public String getURL() {
    return databaseURL;
  }

  public void setPassword(String password) {
    databasePassword = password;
  }

  public String getPassword() {
    return databasePassword;
  }

  /**
   * Ensures that the given driver class has been loaded for sql connection
   * creation.
   */
  public void setDriver(String driverClass) {
    try {
      Class.forName(driverClass);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  class JDBCReceiverThread extends Thread {
    public JDBCReceiverThread() {
      setDaemon(true);
    }

    public void run() {
      active = true; 

      do {
        try {
          Logger logger = null;
          long timeStamp = 0L;
          String level = null;
          String threadName = null;
          Object message = null;
          String ndc = null;
          Hashtable mdc = null;
          String[] exception = null;
          String className = null;
          String methodName = null;
          String fileName = null;
          String lineNumber = null;
          Hashtable properties = null;

          Statement statement = getConnection().createStatement();
          ResultSet rs = statement.executeQuery(sqlStatement);

          while (rs.next()) {
            logger = Logger.getLogger(rs.getString("LOGGER"));
            timeStamp = rs.getTimestamp("TIMESTAMP").getTime();

            level = rs.getString("LEVEL");
            threadName = rs.getString("THREAD");
            message = rs.getString("MESSAGE");
            ndc = rs.getString("NDC");

            String mdcString = rs.getString("MDC");
            mdc = new Hashtable();

            if (mdcString != null) {
              //support MDC being wrapped in {{name, value}} or just name, value
              if (
                (mdcString.indexOf("{{") > -1)
                  && (mdcString.indexOf("}}") > -1)) {
                mdcString =
                  mdcString.substring(
                    mdcString.indexOf("{{") + 2, mdcString.indexOf("}}"));
              }

              StringTokenizer tok = new StringTokenizer(mdcString, ",");

              while (tok.countTokens() > 1) {
                mdc.put(tok.nextToken(), tok.nextToken());
              }
            }

            //although exception is not supported by jdbcappender, it needs to be provided in the SQL string
            exception = new String[] { rs.getString("EXCEPTION") };
            className = rs.getString("CLASS");
            methodName = rs.getString("METHOD");
            fileName = rs.getString("FILE");
            lineNumber = rs.getString("LINE");

            //although properties are not supported by JDBCAppender, if they are provided in the 
            //SQL they can be used here (for example, to route events to a unique tab if 
            //the machinename and/or appname property are set)
            String propertiesString = rs.getString("PROPERTIES");
            properties = new Hashtable();

            if (propertiesString != null) {
              //support properties being wrapped in {{name, value}} or just name, value
              if (
                (propertiesString.indexOf("{{") > -1)
                  && (propertiesString.indexOf("}}") > -1)) {
                propertiesString =
                  propertiesString.substring(
                    propertiesString.indexOf("{{") + 2,
                    propertiesString.indexOf("}}"));
              }

              StringTokenizer tok2 =
                new StringTokenizer(propertiesString, ",");

              while (tok2.countTokens() > 1) {
                properties.put(tok2.nextToken(), tok2.nextToken());
              }
            }

            Level levelImpl = Level.toLevel(level);

            LoggingEvent event =
              new LoggingEvent(
                logger.getName(), logger, timeStamp, levelImpl, threadName,
                message, ndc, mdc, exception,
                new LocationInfo(fileName, className, methodName, lineNumber),
                properties);

            doPost(event);
          }
        } catch (SQLException se) {
          se.printStackTrace();
        }

        if (refreshMillis != null) {
          try {
            Thread.sleep(Integer.parseInt(refreshMillis));
          } catch (InterruptedException ie) {
          }
        }
      } while (refreshMillis != null);
    }
  }
}
