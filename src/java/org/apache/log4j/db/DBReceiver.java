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

package org.apache.log4j.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.scheduler.Job;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;


/**
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBReceiver
       extends Receiver
       implements Pauseable {
  ConnectionSource connectionSource;
  int refreshMillis;
  DBReceiverJob receiverJob;
  boolean paused = false;
  
  public void activateOptions() {
    if (connectionSource != null) {
      LogLog.info("activating connectionSource");
      connectionSource.activateOptions();
      receiverJob = new DBReceiverJob();
    } else {
      throw new IllegalStateException("DBAppender cannot function without a connection source");
    }
  }


  public void setRefreshMillis(int refreshMillis) {
    this.refreshMillis = refreshMillis;
  }


  public int getRefreshMillis() {
    return refreshMillis;
  }


  /**
   * @return Returns the connectionSource.
   */
  public ConnectionSource getConnectionSource() {
    return connectionSource;
  }


  /**
   * @param connectionSource The connectionSource to set.
   */
  public void setConnectionSource(ConnectionSource connectionSource) {
    this.connectionSource = connectionSource;
  }


  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#shutdown()
   */
  public void shutdown() {
    // TODO Auto-generated method stub
  }


  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Pauseable#setPaused(boolean)
   */
  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Pauseable#isPaused()
   */
  public boolean isPaused() {
    return paused;
  }

  class DBReceiverJob
         implements Job {
    public void execute() {
      LogLog.info("in DBReceiverJob.execute()");
      try {
        Logger logger;
        LoggerRepository loggerRepository = getLoggerRepository();
        Connection connection = connectionSource.getConnection();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT sequence_number, timestamp, rendered_message, ");
        sql.append("logger_name, level_string, ndc, thread_name, ");
        sql.append("reference_flag, id from logging_event");

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql.toString());
        rs.first();
        
        while (rs.next()) {
          LoggingEvent event = new LoggingEvent();
          long id;
          event.setSequenceNumber(rs.getLong(1));
          event.setTimeStamp(rs.getLong(2));
          event.setRenderedMessage(rs.getString(3));
          event.setLoggerName(rs.getString(4));

          String levelStr = rs.getString(5);

          // TODO CG The conversion of levelStr should be more general
          Level level = Level.toLevel(levelStr);
          event.setLevel(level);
          event.setNDC(rs.getString(6));
          event.setThreadName(rs.getString(7));

          short mask = rs.getShort(8);

          id = rs.getLong(9);

          //event.setProperty("id", Long.toString(id));
          if ((mask & DBHelper.PROPERTIES_EXIST) != 0) {
            getProperties(connection, id, event);
          }

          if ((mask & DBHelper.EXCEPTION_EXISTS) != 0) {
            getException(connection, id, event);
          }

          if (! DBReceiver.this.isPaused()) {
            DBReceiver.this.doPost(event);
          }
        }
      } catch (SQLException sqle) {
        LogLog.error("Problem receiving events", sqle);
      }
    }
  }

  /**
   * Retrieve the event properties from the logging_event_property table.
   * 
   * @param connection
   * @param id
   * @param event
   * @throws SQLException
   */
  void getProperties(Connection connection, long id, LoggingEvent event)
         throws SQLException {
    String sql = "SELECT mapped_key, mapped_value FROM logging_event_property WHERE event_id='" + id + "'";
    Statement statement = connection.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    rs.first();

    while (rs.next()) {
      String key = rs.getString(1);
      String value = rs.getString(2);
      event.setProperty(key, value);
    }
  }

  /**
   * Retrieve the exception string representation from the logging_event_exception
   * table.
   * 
   * @param connection
   * @param id
   * @param event
   * @throws SQLException
   */
  void getException(Connection connection, long id, LoggingEvent event)
         throws SQLException {
    String sql = "SELECT i, trace_line FROM logging_event_exception where event_id='" + id + "'";
    Statement statement = connection.createStatement();
    ResultSet rs = statement.executeQuery(sql);
    
    // if rs has results, then extract the exception
    if(rs.last()) {
      int len = rs.getRow();
      String[] strRep = new String[len];      
      rs.beforeFirst();
      while (rs.next()) {
        int i = rs.getShort(1);
        strRep[i] = rs.getString(2);
      }
      // we've filled strRep, we now attach it to the event
      event.setThrowableInformation(new ThrowableInformation(strRep));
    }
  }
}
