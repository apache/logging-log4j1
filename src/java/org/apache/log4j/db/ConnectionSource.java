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

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.OptionHandler;

import java.sql.Connection;
import java.sql.SQLException;


/**
 *  The <id>ConnectionSource</id> interface provides a pluggable means of
 *  transparently obtaining JDBC {@link java.sql.Connection}s for log4j classes
 *  that require the use of a {@link java.sql.Connection}.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
public interface ConnectionSource extends OptionHandler {

  final int UNKNOWN_DIALECT = 0;
  final int POSTGRES_DIALECT = 1;
  final int MYSQL_DIALECT = 2;
  final int ORACLE_DIALECT = 3;
  
  /**
   *  Obtain a {@link java.sql.Connection} for use.  The client is
   *  responsible for closing the {@link java.sql.Connection} when it is no
   *  longer required.
   *
   *  @throws SQLException  if a {@link java.sql.Connection} could not be
   *                        obtained
   */
  Connection getConnection() throws SQLException;

  /**
   *  Set the error handler.
   *
   *  @param errorHandler  the new error handler
   */
  void setErrorHandler(ErrorHandler errorHandler);
  
  /**
   * 
   * Get the SQL dialect that should be used for this connection.
   *
   */
  int getSQLDialect();
}
