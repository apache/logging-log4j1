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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.scheduler.Job;
import org.apache.log4j.scheduler.Scheduler;
import org.apache.log4j.spi.LocationInfo;
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
  
  /**
   * By default we refresh data every 1000 milliseconds.
   * @see #setRefreshMillis
   */
  static int DEFAULT_REFRESH_MILLIS = 1000;
  
  ConnectionSource connectionSource;
  int refreshMillis = DEFAULT_REFRESH_MILLIS;
  DBReceiverJob receiverJob;
  boolean paused = false;
  
  public void activateOptions() {
    if (connectionSource != null) {
      LogLog.info("activating connectionSource");
      connectionSource.activateOptions();
      receiverJob = new DBReceiverJob();
      Scheduler scheduler = LogManager.getSchedulerInstance();
      scheduler.schedule(receiverJob, System.currentTimeMillis()+500, refreshMillis);
      
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
    LogLog.info("removing receiverJob from the Scheduler.");
    Scheduler scheduler = LogManager.getSchedulerInstance();
    scheduler.delete(receiverJob);
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

  /**
   * Actual retrieval of data is made by the instance of DBReceiverJob associated
   * with DBReceiver.
   * 
   * @author Ceki G&uuml;lc&uuml;
   */
  class DBReceiverJob implements Job {
    
    long lastId = 0;
    
   
    public void execute() {
      LogLog.info("DBReceiverJob.execute() called");
      try {
        Logger logger;
        LoggerRepository loggerRepository = getLoggerRepository();
        Connection connection = connectionSource.getConnection();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append("sequence_number, ");
        sql.append("timestamp, ");
        sql.append("rendered_message, ");
        sql.append("logger_name, ");
        sql.append("level_string, ");
        sql.append("ndc, ");
        sql.append("thread_name, ");
        sql.append("reference_flag, ");
        sql.append("caller_filename, ");
        sql.append("caller_class, ");
        sql.append("caller_method, ");
        sql.append("caller_line, ");
        sql.append("event_id ");
        sql.append("FROM logging_event ");
        // have subsequent SELECTs start from we left off last time
        sql.append(" WHERE event_id > "+lastId);
        sql.append(" ORDER BY event_id ASC");
        
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql.toString());
        rs.beforeFirst();
        
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

          String fileName = rs.getString(9);
          String className = rs.getString(10);
          String methodName = rs.getString(11);
          String lineNumber = rs.getString(12).trim();
          
          if(fileName.equals(LocationInfo.NA)) {
            event.setLocationInformation(LocationInfo.NA_LOCATION_INFO);
          } else {
            event.setLocationInformation(new LocationInfo(fileName, className,
                methodName, lineNumber));
          }
          
          id = rs.getLong(13);
          lastId = id;
          
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
    rs.beforeFirst();

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
