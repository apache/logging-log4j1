package org.apache.log4j.varia;


import org.apache.log4j.*;
import org.apache.log4j.spi.*;


import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;


/**
 * Contribution from MD Data Direct.
 *
 * Implements an ArrayList buffer before storing messages to the DB.
 * Override getSQL to fit your database schema (or implement spLog msg
 * on your DB) Override executeSQL to modify how DB connection and SQL
 * execution is made.
 *
 * @author: Kevin Steppe */

public class JDBCAppender extends org.apache.log4j.AppenderSkeleton
    implements org.apache.log4j.Appender {
  
  protected String databaseURL = "jdbc:odbc:myDB";
  protected String databaseUser = "me";
  protected String databasePassword = "mypassword";


  public static final String URL_OPTION = "URL";
  public static final String USER_OPTION = "User";
  public static final String PASSWORD_OPTION = "Password";
  public static final String BUFFER_OPTION = "Buffer";
  protected int bufferSize = 1;
  protected List buffer;


  public JDBCAppender() {
    super();
    buffer = new ArrayList();
  }


  public void append(LoggingEvent event) {
    buffer.add(event);


    if (buffer.size() >= bufferSize)
      flushBuffer();
  }


  public void close() {
    flushBuffer();
    this.closed = true;
  }

  public void setOption(String key, String value) {
    super.setOption(key, value);


    if (key.equalsIgnoreCase(URL_OPTION))
      databaseURL = value;
    else if (key.equalsIgnoreCase(USER_OPTION))
      databaseUser = value;
    else if (key.equalsIgnoreCase(PASSWORD_OPTION))
      databasePassword = value;
    else if (key.equalsIgnoreCase(BUFFER_OPTION))
      bufferSize = Integer.parseInt(value);
  }


  /**
   * Override this to create the SQL needed for your DB schema
   */
  protected String getSQL(LoggingEvent event) {
    String msg = this.layout.format(event);
    String sql = "spLog '" + msg + "'";
    return sql;
  }


    /**
     * Override this to provide an alertnate method of getting
     * connections (such as caching) This implementation creates a new
     * connection and statement for every execution which is very
     * wastefull.  One method to fix this is to open connections at
     * the start of flushBuffer() and close them at the end.  MD Data
     * uses a connection pool outside of JDBCAppender which is
     * accessed in the override of this method.


     */
    protected void executeSQL(String sql) throws SQLException {
      Connection con = null;
      Statement stmt = null;


      try {
	con = DriverManager.getConnection(databaseURL, databaseUser, 
					  databasePassword);
	stmt = con.createStatement();
	stmt.executeUpdate(sql);
      } catch (SQLException e) {
	if (con != null)
	  con.close();
	if (stmt != null)
	  stmt.close();	
	throw e;
      }
      stmt.close();
      con.close();
    }


  public void flushBuffer()     {
    //Do the actual logging
    for (Iterator i = buffer.iterator(); i.hasNext();) {
      try {
	String sql = getSQL((LoggingEvent)i.next());
	executeSQL(sql);
      }
      catch (SQLException e) {
	errorHandler.error("Failed to excute sql", e,
			   ErrorCode.FLUSH_FAILURE);
      }
    }
    buffer.clear();
  }


  public void finalize() {
    close();
  }


  public boolean requiresLayout() {
    return true;
  }


}

