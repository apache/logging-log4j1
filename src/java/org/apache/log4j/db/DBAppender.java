package org.apache.log4j.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.db.dialect.MySQLDialect;
import org.apache.log4j.db.dialect.PostgreSQLDialect;
import org.apache.log4j.db.dialect.SQLDialect;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBAppender
       extends AppenderSkeleton {
  ConnectionSource connectionSource;
  SQLDialect sqlDialect;

  public void activateOptions() {
    if (connectionSource != null) {
      connectionSource.activateOptions();
    } else {
      throw new IllegalStateException("DBAppender cannot function without a connection source");
    }

    switch (connectionSource.getSQLDialect()) {
    case ConnectionSource.POSTGRES_DIALECT :
      sqlDialect = new PostgreSQLDialect();
      break;
    case ConnectionSource.MYSQL_DIALECT :
      sqlDialect = new MySQLDialect();
      break;
    }
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


  protected void append(LoggingEvent event) {
    try {
      Connection connection = connectionSource.getConnection();
      connection.setAutoCommit(false);

//      sequence_number BIGINT NOT NULL,
//      timestamp         BIGINT NOT NULL,
//      rendered_message  TEXT NOT NULL,
//      logger_name       VARCHAR(254) NOT NULL,
//      ndc               TEXT,
//      thread_name       VARCHAR(254),
//      id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY
      StringBuffer sql = new StringBuffer();
      sql.append("INSERT INTO logging_event (");
      sql.append("sequence_number, timestamp, rendered_message, ");
      sql.append("logger_name, ndc, thread_name) ");
      sql.append(" VALUES (?, ?, ? ,?, ?, ?)");

      PreparedStatement insertStatement = connection.prepareStatement(sql.toString());
      insertStatement.setLong(1, event.getSequenceNumber());
      insertStatement.setLong(2, event.getTimeStamp());
      insertStatement.setString(3, event.getRenderedMessage());
      insertStatement.setString(4, event.getLoggerName());
      insertStatement.setString(5, event.getNDC());
      insertStatement.setString(6, event.getThreadName());

      int updateCount = insertStatement.executeUpdate();

      if (updateCount != 1) {
        LogLog.warn("Failed to insert loggingEvent");
      }

      Statement idStatement = connection.createStatement();
      idStatement.setMaxRows(1);

      ResultSet rs = idStatement.executeQuery(sqlDialect.getSelectInsertId());
      rs.first();

      int eventId = rs.getInt(1);
      LogLog.info("inserted id is " + eventId);

//      event_id        INT NOT NULL,
//      mapped_key        VARCHAR(254) NOT NULL,
//      mapped_value      VARCHAR(254),
      Set mdcKeys = event.getMDCKeySet();

      if (mdcKeys.size() > 0) {
        String insertMDCSQL = "INSERT INTO mdc (event_id, mapped_key, mapped_value) VALUES (?, ?, ?)";
        PreparedStatement insertMDCStatement = connection.prepareStatement(insertMDCSQL);

        for (Iterator i = mdcKeys.iterator(); i.hasNext();) {
          String key = (String)i.next();
          String value = (String)event.getMDC(key);
          LogLog.debug("id " + eventId + ", key " + key + ", value " + value);
          insertMDCStatement.setInt(1, eventId);
          insertMDCStatement.setString(2, key);
          insertMDCStatement.setString(3, value);
          insertMDCStatement.addBatch();
        }
        insertMDCStatement.executeBatch();
      }

      connection.commit();
    } catch (SQLException sqle) {
      LogLog.error("problem appending event", sqle);
    }
  }


  public void close() {
    // TODO Auto-generated method st  
  }


  /*
   * The DBAppender does not require a layout.
   */
  public boolean requiresLayout() {
    return false;
  }
}
