package org.apache.log4j.varia;



import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.OptionConverter;



import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;



import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;



/**
 * This JDBCAppender is intended to provide a convinent base clas or
 * default class for sending log messages to a database.
 *
 * Each append call adds to an ArrayList buffer.  When the buffer is filled
 * (set by the BUFFER_OPTION or setBufferSize(int buffer), default is 1) each
 * log event is placed in a sql statement (configurable) and executed.
 *
 * BufferSize, db URL, User, & Password are configurable options in
 * the standard Log4J methods.
 *
 * DB driver is also configurable -- setting the DRIVER_OPTION
 * automaticaly loads the driver The SQL_OPTION sets the SQL statement
 * to be used for logging -- by default all the conversion patterns in
 * PatternLayout can be used inside of the statement.  (see the test
 * cases for examples) if a layout is explicitely attached it's output
 * will replace the first instance of "%m" in the SQL statement.
 * Overriding the getSQL method allows more explicit control of the
 * statement used for logging.
 *
 *
 * For use as a base class:
 *
 *    Override executeSQL(String sql) to modify connection/statement
 *    behavior The default implementation creates a new connection
 *    from the Driver for every statement.
 *
 *    Override getSQL(LoggingEvent event) to produce specialized or
 *    dynamic statements The default uses the sql option value
 *
 *
 * @author: Kevin Steppe (ksteppe@pacbell.net) 
*/


public class JDBCAppender extends org.apache.log4j.AppenderSkeleton
    implements org.apache.log4j.Appender {


  public static final String URL_OPTION = "URL";
  public static final String USER_OPTION = "User";
  public static final String PASSWORD_OPTION = "Password";
  public static final String BUFFER_OPTION = "Buffer";
  public static final String DRIVER_OPTION = "Driver";
  public static final String SQL_OPTION = "Sql";


  protected String databaseURL = "jdbc:odbc:myDB";
  protected String databaseUser = "me";
  protected String databasePassword = "mypassword";
  protected String databaseDriver = "sun.jdbc.odbc.JdbcOdbcDriver";


  protected PatternLayout sqlLayout = null;
  protected String sqlStatement = "";


  protected int bufferSize = 1;
  protected List buffer;


  public JDBCAppender() {
    super();
    buffer = new ArrayList();
  }


  /**
   * Adds the event to the buffer.  When full the buffer is flushed.
   */
  public void append(LoggingEvent event) {
    buffer.add(event);


    if (buffer.size() >= bufferSize)
      flushBuffer();
  }


  /**
   * Sets options as per the 1.0 version of Log4J option setting.
   * all cases are forwarded to the appropriate JavaBeans Introspection set
method.
   * @deprecated
   */
  public void setOption(String key, String value) {
    super.setOption(key, value);


    key = key.trim();
    value = value.trim();


 if(key == null || value == null)
     return;


    if (key.equalsIgnoreCase(URL_OPTION))
      setURL(value);
    else if (key.equalsIgnoreCase(USER_OPTION))
      setUser(value);
    else if (key.equalsIgnoreCase(PASSWORD_OPTION))
      setPassword(value);
    else if (key.equalsIgnoreCase(BUFFER_OPTION))
      setBufferSize(Integer.parseInt(value));
    else if (key.equalsIgnoreCase(DRIVER_OPTION))
      setDriver(value);
    else if (key.equalsIgnoreCase(SQL_OPTION))
      setSql(value);
  }


  /** @deprecated */
  public String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
   new String[] {URL_OPTION, USER_OPTION, PASSWORD_OPTION, BUFFER_OPTION,
DRIVER_OPTION});
  }


  /**
   * By default getSQL formats the event into the provided statement using
   * PatternLayout style conversion characters.  In fact, it uses an internal
   * PatternLayout object to do this.
   *
   * If a separate layout has been attached to the appender then the first
   * instance of "%m" in the sql statement will be replaced with the output
   * from that layout's format(LoggingEvent) method.
   *
   *
   * Overriding this provides direct access to the LoggingEvent
   * when constructing the logging statement.
   *
   * In hind-sight I should have given this a more different name from the
setSql, getSql
   * property methods.
   */
  protected String getSQL(LoggingEvent event) {
    if (getLayout() != null) {
        String msg = this.layout.format(event);
        String _sql = getSql();
        return _sql.substring(0, _sql.indexOf("%m")) + msg +
_sql.substring(_sql.indexOf("%m") + 2, _sql.length());
    } else
        return sqlLayout.format(event);


  }



  /**
   * By default this method creates a new connection for each
   * statement executed.
   *
   * Override this to provide an alertnate method of getting
   * connections (such as caching).  One method to fix this is to open
   * connections at the start of flushBuffer() and close them at the
   * end.  I use a connection pool outside of JDBCAppender which is
   * accessed in an override of this method.
   * */
  protected void executeSQL(String sql) throws SQLException {
    Connection con = null;
    Statement stmt = null;
    
    
    try {
      if (!DriverManager.getDrivers().hasMoreElements())
	setDriver(databaseDriver);
      
      
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


  /**
   * loops through the buffer of LoggingEvents, gets a
   * sql string from getSQL() and sends it to
   * executeSQL()
   */
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
  
  
  /** closes the appender, flushing the buffer first */
  public void close() {
    flushBuffer();
    this.closed = true;
  }
  
  
  /** closes the appender before disposal */
  public void finalize() {
    close();
  }
  
  
  /**
   * JDBCAppender has a built in layout.
   * a separate layout can be attached if needed.
   */
  public boolean requiresLayout() {
    return false;
  }


  public void setSql(String s) {
    sqlStatement = s;
    if (getLayout() == null) {
      if (sqlLayout == null)
	sqlLayout = new PatternLayout(s);
      else
	sqlLayout.setConversionPattern(s);
    }
  }
  
  
  public String getSql() {
    return sqlStatement;
  }
  
  
  public void setUser(String user) {
    databaseUser = user;
  }
  

  public void setURL(String url) {
    databaseURL = url;
  }


  public void setPassword(String password) {
    databasePassword = password;
  }


  public void setBufferSize(int buffer) {
    bufferSize = buffer;
  }


  public String getUser() {
    return databaseUser;
  }


  public String getURL() {
    return databaseURL;
  }


  public String getPassword() {
    return databasePassword;
  }


  public int getBufferSize() {
    return bufferSize;
  }


  public void setDriver(String driverClass) {
    databaseDriver = driverClass;
    try {
      Class.forName(databaseDriver);
    } catch (Exception e) {
      errorHandler.error("Failed to load driver", e,
			 ErrorCode.GENERIC_FAILURE);
    }
  }
  
  
  /**
   * Returns whatever driver JDBCAppender was last told to load.
   * If a driver was loaded elsewhere in the program this may not
   * be meaningful.
   */
  public String getDriver() {
    return databaseDriver;
  }
}

