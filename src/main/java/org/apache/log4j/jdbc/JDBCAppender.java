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
package org.apache.log4j.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
  The JDBCAppender provides for sending log events to a database
  in Log4j up to 1.2.17.

  Changed in 1.2.18+ to complain about its use and do nothing else.
  See <a href="https://logging.apache.org/log4j/1.2/">the log4j 1.2 homepage</a>
  for more information on why JDBC is disabled since 1.2.18.

  @author Kevin Steppe (<A HREF="mailto:ksteppe@pacbell.net">ksteppe@pacbell.net</A>)
  @deprecated
*/
public class JDBCAppender extends org.apache.log4j.AppenderSkeleton {

  static final String JDBC_UNSUPPORTED =
      "ERROR-LOG4J-NETWORKING-UNSUPPORTED: JDBC unsupported!" +
      " This is a breaking change in Log4J 1 >=1.2.18. Change your config to stop using JDBC!";

  protected String databaseURL = "jdbc:odbc:myDB";

  protected String databaseUser = "me";

  protected String databasePassword = "mypassword";

  protected Connection connection = null;

  protected String sqlStatement = "";

  protected int bufferSize = 1;

  protected ArrayList buffer;

  protected ArrayList removes;
  
  public JDBCAppender() {
    LogLog.error(JDBC_UNSUPPORTED);
  }

  public boolean getLocationInfo() {
    return false;
  }
  
  public void setLocationInfo(final boolean flag) {
  }
  
  public void append(LoggingEvent event) {
    errorHandler.error(JDBC_UNSUPPORTED);
  }

  protected String getLogStatement(LoggingEvent event) {
    throw new IllegalStateException(JDBC_UNSUPPORTED);
  }

  protected void execute(String sql) throws SQLException {
    throw new IllegalStateException(JDBC_UNSUPPORTED);
  }

  protected void closeConnection(Connection con) {
  }

  protected Connection getConnection() throws SQLException {
    throw new SQLException(JDBC_UNSUPPORTED);
  }

  public void close()
  {
  }

  public void flushBuffer() {
  }

  public void finalize() {
  }

  public boolean requiresLayout() {
    return true;
  }

  public void setSql(String sql) {
  }

  public String getSql() {
    return null;
  }

  public void setUser(String user) {
  }


  public void setURL(String url) {
  }


  public void setPassword(String password) {
  }

  public void setBufferSize(int newBufferSize) {
  }

  public String getUser() {
    return null;
  }

  public String getURL() {
    return null;
  }

  public String getPassword() {
    return null;
  }

  public int getBufferSize() {
    return 0;
  }

  public void setDriver(String driverClass) {
  }
}
