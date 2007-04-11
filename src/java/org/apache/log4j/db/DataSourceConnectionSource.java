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

package org.apache.log4j.db;


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

  
  public void activateOptions() {
    //LogLog.debug("**********DataSourceConnectionSource.activateOptions called");
    if (dataSource == null) {
      getLogger().warn("WARNING: No data source specified");
    } else {
      Connection connection = null;
      try {
        connection = getConnection();
      } catch(SQLException se) {
        getLogger().warn("Could not get a connection to discover the dialect to use.", se);
      }
      if(connection != null) {
        discoverConnnectionProperties();
      } 
      if(!supportsGetGeneratedKeys() && getSQLDialectCode() == ConnectionSource.UNKNOWN_DIALECT) {
        getLogger().warn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
      }
    }
  }

  /**
   * @see org.apache.log4j.db.ConnectionSource#getConnection()
   */
  public Connection getConnection() throws SQLException {
    if (dataSource == null) {
      getLogger().error("WARNING: No data source specified");
      return null;
    }

    if (getUser() == null) {
      return dataSource.getConnection();
    } else {
      return dataSource.getConnection(getUser(), getPassword());
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }


}
