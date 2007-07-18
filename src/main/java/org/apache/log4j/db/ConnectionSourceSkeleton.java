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

import org.apache.log4j.db.dialect.Util;
import org.apache.log4j.spi.ComponentBase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;


/**
 * @author Ceki G&uuml;lc&uuml;
 */
public abstract class ConnectionSourceSkeleton extends ComponentBase implements ConnectionSource {
  private String user = null;
  private String password = null;

  // initially we have an unkonw dialect
  private int dialectCode = UNKNOWN_DIALECT;
  private boolean supportsGetGeneratedKeys = false;
  private boolean supportsBatchUpdates = false;


  /**
   * Learn relevant information about this connection source.
   *
   */
  public void discoverConnnectionProperties() {
    try {
      Connection connection = getConnection();
      if (connection == null) {
        getLogger().warn("Could not get a conneciton");
        return;
      }
      DatabaseMetaData meta = connection.getMetaData();
      Util util = new Util();
      util.setLoggerRepository(repository);
      supportsGetGeneratedKeys = util.supportsGetGeneratedKeys(meta);
      supportsBatchUpdates = util.supportsBatchUpdates(meta);
      dialectCode = Util.discoverSQLDialect(meta);
    } catch (SQLException se) {
      getLogger().warn("Could not discover the dialect to use.", se);
    }
  }

  /**
   * Does this connection support the JDBC Connection.getGeneratedKeys method?
   */
  public final boolean supportsGetGeneratedKeys() {
    return supportsGetGeneratedKeys;
  }

  public final int getSQLDialectCode() {
    return dialectCode;
  }

  /**
   * Get the password for this connection source.
   */
  public final String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   * @param password The password to set
   */
  public final void setPassword(final String password) {
    this.password = password;
  }

  /**
   * Get the user for this connection source.
   */
  public final String getUser() {
    return user;
  }

  /**
   * Sets the username.
   * @param username The username to set
   */
  public final void setUser(final String username) {
    this.user = username;
  }

  /**
   * Does this connection support batch updates?
   */
  public final boolean supportsBatchUpdates() {
    return supportsBatchUpdates;
  }
}
