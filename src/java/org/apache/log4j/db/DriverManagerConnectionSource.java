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
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.spi.ErrorCode;


/**
 *  The DriverManagerConnectionSource is an implementation of {@link ConnectionSource}
 *  that obtains the Connection in the traditional JDBC manner based on the
 *  connection URL.
 *  <p>
 *  Note that this class will establish a new Connection for each call to
 *  {@link #getConnection()}.  It is recommended that you either use a JDBC
 *  driver that natively supported Connection pooling or that you create
 *  your own implementation of {@link ConnectionSource} that taps into whatever
 *  pooling mechanism you are already using.  (If you have access to a JNDI
 *  implementation that supports {@link javax.sql.DataSource}s, e.g. within
 *  a J2EE application server, see {@link JNDIConnectionSource}).  See
 *  <a href="#dbcp">below</a> for a configuration example that uses the
 *  <a href="http://jakarta.apache.org/commons/dbcp/index.html">commons-dbcp</a>
 *  package from Apache.
 *  <p>
 *  Sample configuration:<br>
 *  <pre>
 *     &lt;connectionSource class="org.apache.log4j.jdbc.DriverManagerConnectionSource"&gt;
 *        &lt;param name="driver" value="com.mysql.jdbc.Driver" /&gt;
 *        &lt;param name="url" value="jdbc:mysql://localhost:3306/mydb" /&gt;
 *        &lt;param name="username" value="myUser" /&gt;
 *        &lt;param name="password" value="myPassword" /&gt;
 *     &lt;/connectionSource&gt;
 *  </pre>
 *  <p>
 *  <a name="dbcp">If</a> you do not have another connection pooling mechanism
 *  built into your application, you can use  the
 *  <a href="http://jakarta.apache.org/commons/dbcp/index.html">commons-dbcp</a>
 *  package from Apache:<br>
 *  <pre>
 *     &lt;connectionSource class="org.apache.log4j.jdbc.DriverManagerConnectionSource"&gt;
 *        &lt;param name="driver" value="org.apache.commons.dbcp.PoolingDriver" /&gt;
 *        &lt;param name="url" value="jdbc:apache:commons:dbcp:/myPoolingDriver" /&gt;
 *     &lt;/connectionSource&gt;
 *  </pre>
 *  Then the configuration information for the commons-dbcp package goes into
 *  the file myPoolingDriver.jocl and is placed in the classpath.  See the
 *  <a href="http://jakarta.apache.org/commons/dbcp/index.html">commons-dbcp</a>
 *  documentation for details.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
public class DriverManagerConnectionSource
       extends ConnectionSourceSkeleton {
  static private final String POSTGRES_PART = "postgresql";
  static private final String MYSQL_PART = "mysql";
  static private final String ORACLE_PART = "oracle";
  
  private String driverClass = null;
  protected String url = null;

  public void activateOptions() {
    try {
      if (driverClass != null) {
        Class.forName(driverClass);
      } else if (errorHandler != null) {
        errorHandler.error("WARNING: No JDBC driver specified for log4j " + "DriverManagerConnectionSource.");
      }
    } catch (final ClassNotFoundException cnfe) {
      if (errorHandler != null) {
        errorHandler.error("Could not load JDBC driver class: " + driverClass, cnfe, ErrorCode.GENERIC_FAILURE);
      } else {
        cnfe.printStackTrace();
      }
    }
  }


  /**
   * @see org.apache.log4j.db.ConnectionSource#getConnection()
   */
  public Connection getConnection()
         throws SQLException {
    if (user == null) {
      return DriverManager.getConnection(url);
    } else {
      return DriverManager.getConnection(url, user, password);
    }
  }


  /**
   * Returns the url.
   * @return String
   */
  public String getUrl() {
    return url;
  }


  /**
   * Sets the url.
   * @param url The url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }


  /**
   * Returns the name of the driver class.
   * @return String
   */
  public String getDriverClass() {
    return driverClass;
  }


  /**
   * Sets the driver class.
   * @param driver The driver to set
   */
  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }


  public int getSQLDialect() {
    int dialectCode = 0;

    if (url == null) {
      return ConnectionSource.UNKNOWN_DIALECT;
    }

    if (url.indexOf(POSTGRES_PART) != -1) {
      return ConnectionSource.POSTGRES_DIALECT;
    } else if (url.indexOf(MYSQL_PART) != -1) {
      return ConnectionSource.MYSQL_DIALECT;
    } else if (url.indexOf(ORACLE_PART) != -1) {
      return ConnectionSource.ORACLE_DIALECT;
    } else {
      return ConnectionSource.UNKNOWN_DIALECT;
    }
  }
}
