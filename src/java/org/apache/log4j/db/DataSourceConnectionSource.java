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

import org.apache.log4j.db.dialect.Util;
import org.apache.log4j.helpers.LogLog;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 *  The DataSourceConnectionSource is an implementation of {@link ConnectionSource}
 *  that obtains the Connection in the recommended JDBC manner based on
 *  a {@link javax.sql.DataSource DataSource}.
 *  <p>
 *
 *  @author Ray DeCampo
 *  @author Ceki G&uuml;lc&uuml;
 */
public class DataSourceConnectionSource extends ConnectionSourceSkeleton {

  private DataSource dataSource;
  int dialectCode;
  
  public void activateOptions() {
    //LogLog.debug("**********DataSourceConnectionSource.activateOptions called");
    if (dataSource == null) {
      LogLog.warn("WARNING: No data source specified");
      
      if (errorHandler != null) {
        errorHandler.error("WARNING: No data source specified");
      }
    } else {
      try {
        Connection connection = getConnection();
        dialectCode = Util.discoverSQLDialect(connection);
      } catch(SQLException se) {
        LogLog.warn("Could not discover the dialect to use.", se);
      }
    }
  }

  /**
   * @see org.apache.log4j.db.ConnectionSource#getConnection()
   */
  public Connection getConnection() throws SQLException {
    if (dataSource == null) {
      if (errorHandler != null) {
        errorHandler.error("WARNING: No data source specified");
      }

      return null;
    }

    if (user == null) {
      return dataSource.getConnection();
    } else {
      return dataSource.getConnection(user, password);
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public int getSQLDialect() {
    return dialectCode;
  }
}
