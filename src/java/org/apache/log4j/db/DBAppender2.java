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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.db.dialect.SQLDialect;
import org.apache.log4j.db.dialect.Util;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


/**
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBAppender2 extends AppenderSkeleton {
  static final String insertPropertiesSQL =
    "INSERT INTO  logging_event_property (event_id, mapped_key, mapped_value) VALUES (?, ?, ?)";
  static final String insertExceptionSQL =
    "INSERT INTO  logging_event_exception (event_id, i, trace_line) VALUES (?, ?, ?)";
  static final String insertSQL;

  static {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO logging_event (");
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
    sql.append("caller_line) ");
    sql.append(" VALUES (?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?)");
    insertSQL = sql.toString();
  }

  int batchSize = 20;
  ConnectionSource connectionSource;
  SQLDialect sqlDialect;
  boolean locationInfo = false;
  Vector eventsBuffer = new Vector();
  int[] eventIDArray;

  public DBAppender2() {
  }

  public void activateOptions() {
    LogLog.debug("DBAppender.activateOptions called");

    if (connectionSource == null) {
      throw new IllegalStateException(
        "DBAppender cannot function without a connection source");
    }

    if (connectionSource.supportsGetGeneratedKeys()) {
      LogLog.info(
        "****JDBC getGeneratedKeys method is supported. Will use batch mode.");
    } else {
      // if getGeneratedKeys method is not supported, we fallback to batch size
      // of one.
      batchSize = 1;
      Util.getDialectFromCode(connectionSource.getSQLDialectCode());

      if (sqlDialect == null) {
        throw new IllegalStateException(
          "DBAppender cannot function without a determined SQL dialect");
      }
    }

    eventIDArray = new int[batchSize];
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
    LogLog.debug("setConnectionSource called for DBAppender");
    this.connectionSource = connectionSource;
  }

  protected void append(LoggingEvent event) {
    event.prepareForSerialization();
    eventsBuffer.add(event);

    LogLog.info("=========Buffer size is "+eventsBuffer.size());
    if (eventsBuffer.size() >= batchSize) {
      flush();
    }
  }

  void flush() {
    LogLog.info("=============flush called");

    Connection connection = null;
    LogLog.info("Buffer size is "+eventsBuffer.size());

    try {
      connection = connectionSource.getConnection();
      connection.setAutoCommit(false);

      PreparedStatement insertStatement =
        connection.prepareStatement(DBAppender2.insertSQL, Statement.RETURN_GENERATED_KEYS);

      for (int i = 0; i < eventsBuffer.size(); i++) {
        LoggingEvent event = (LoggingEvent) eventsBuffer.get(i);
                LogLog.info("*** event message is "+event.getRenderedMessage());
        insertStatement.setLong(1, event.getSequenceNumber());
        insertStatement.setLong(2, event.getTimeStamp());
        insertStatement.setString(3, event.getRenderedMessage());
        insertStatement.setString(4, event.getLoggerName());
        insertStatement.setString(5, event.getLevel().toString());
        insertStatement.setString(6, event.getNDC());
        insertStatement.setString(7, event.getThreadName());
        insertStatement.setShort(8, DBHelper.computeReferenceMask(event));

        LocationInfo li;

        if (event.locationInformationExists() || locationInfo) {
          li = event.getLocationInformation();
        } else {
          li = LocationInfo.NA_LOCATION_INFO;
        }
        insertStatement.setString(9, li.getFileName());
        insertStatement.setString(10, li.getClassName());
        insertStatement.setString(11, li.getMethodName());
        insertStatement.setString(12, li.getLineNumber());

        LogLog.info("*** performing insertion.");
        insertStatement.addBatch();
      }

      int[] r = insertStatement.executeBatch();
      
      if(r != null) {
        for(int x = 0; x < r.length; x++) {
          LogLog.info("inserted "+r[x]);
        }
      }
      connection.commit();
      fillEventIDArray(connection, insertStatement);
 
   
      

      //insertProperties(connection, eventsBuffer, eventIDArray);
      //insertAllExceptions(connection, eventsBuffer, eventIDArray);

      // remove the events
      connection.commit();
      DBHelper.closeStatement(insertStatement);
      //insertStatement = null;

      
      eventsBuffer.setSize(0);
    } catch (SQLException sqle) {
      LogLog.error("problem appending event", sqle);
    } finally {
      DBHelper.closeConnection(connection);
    }
  }

  void fillEventIDArray(Connection connection, Statement insertStatement) 
    throws SQLException {
    //if (connectionSource.supportsGetGeneratedKeys()) {
    if(true) {
      ResultSet keyRS = insertStatement.getGeneratedKeys();

     ResultSetMetaData rsmd = keyRS.getMetaData();
     LogLog.info(" numberOfColumns = "+rsmd.getColumnCount());
     int i = 0;
      while (keyRS.next()) {
        int id = keyRS.getInt(1);
        LogLog.info("**filling id ="+id);
        eventIDArray[i++] = id;;
      }

      if (i != eventsBuffer.size()) {
        LogLog.error("Number of inserted objects do not match.");
        // TODO CG better errorHandling code here
        // no point in continuing
        LogLog.error("number of found events: "+i+" bufferSize="+eventsBuffer.size());
        //throw new IllegalStateException("Programming error. Number of inserted objects do not match."); 

      }
    } else {
      Statement idStatement = connection.createStatement();
      idStatement.setMaxRows(1);

      ResultSet rs = idStatement.executeQuery(sqlDialect.getSelectInsertId());
      rs.first();
      eventIDArray[0] = rs.getInt(1);
    }
  }

  void insertProperties(
    Connection connection, Vector eventsVector, int[] eventIDArray)
    throws SQLException {
    PreparedStatement insertPropertiesStatement = null;

    try {
      insertPropertiesStatement =
        connection.prepareStatement(DBAppender2.insertPropertiesSQL);

      for (int i = 0; i < eventsBuffer.size(); i++) {
        LoggingEvent event = (LoggingEvent) eventsBuffer.get(i);

        Set propertiesKeys = event.getPropertyKeySet();

        if (propertiesKeys.size() > 0) {
          for (Iterator iterator = propertiesKeys.iterator();
              iterator.hasNext();) {
            String key = (String) iterator.next();
            String value = (String) event.getProperty(key);

            LogLog.info(
              "id " + eventIDArray[i] + ", key " + key + ", value " + value);
            insertPropertiesStatement.setInt(1, eventIDArray[i]);
            insertPropertiesStatement.setString(2, key);
            insertPropertiesStatement.setString(3, value);
            insertPropertiesStatement.addBatch();
          }
        }
      }

      // now that all properties for all events have been inserted, we can
      // execute insertPropertiesStatement
      insertPropertiesStatement.executeBatch();
    } finally {
      DBHelper.closeStatement(insertPropertiesStatement);
    }
  }

  void insertAllExceptions(
    Connection connection, Vector eventsVector, int[] eventIDArray)
    throws SQLException {
    PreparedStatement insertExceptionStatement = null;

    try {
      insertExceptionStatement =
        connection.prepareStatement(DBAppender2.insertExceptionSQL);

      for (int i = 0; i < eventsBuffer.size(); i++) {
        LoggingEvent event = (LoggingEvent) eventsBuffer.get(i);
        String[] strRep = event.getThrowableStrRep();

        if (strRep != null) {
          LogLog.info("Logging an exception");

          for (short k = 0; k < strRep.length; k++) {
            insertExceptionStatement.setInt(1, eventIDArray[i]);
            insertExceptionStatement.setShort(2, k);
            insertExceptionStatement.setString(3, strRep[k]);
            insertExceptionStatement.addBatch();
          }
        }
      }

      insertExceptionStatement.executeBatch();
    } finally {
      DBHelper.closeStatement(insertExceptionStatement);
    }
  }

  public void close() {
    if (closed) {
    } else {
      closed = true;
      flush();
    }
  }

  /*
   * The DBAppender does not require a layout.
   */
  public boolean requiresLayout() {
    return false;
  }

  /**
   * Returns value of the <b>LocationInfo</b> property which determines whether
   * caller's location info is written to the database.
   * */
  public boolean getLocationInfo() {
    return locationInfo;
  }

  /**
   * If true, the information written to the database will include
   * caller's location information. Due to performance concerns, by default no
   * location information is written to the database.
   * */
  public void setLocationInfo(boolean locationInfo) {
    this.locationInfo = locationInfo;
  }
}
