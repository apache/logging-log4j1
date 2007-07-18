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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

// PortableRemoteObject was introduced in JDK 1.3. We won't use it.
// import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;


/**
 *  The <id>JNDIConnectionSource</id> is an implementation of
 *  {@link ConnectionSource} that obtains a {@link javax.sql.DataSource} from a
 *  JNDI provider and uses it to obtain a {@link java.sql.Connection}.  It is
 *  primarily designed to be used inside of J2EE application servers or
 *  application server clients, assuming the application server supports remote
 *  access of {@link javax.sql.DataSource}s.  In this way one can take
 *  advantage of  connection pooling and whatever other goodies the application
 *  server provides.
 *  <p>
 *  Sample configuration:<br>
 *  <pre>
 *    &lt;connectionSource class="org.apache.log4j.jdbc.JNDIConnectionSource"&gt;
 *        &lt;param name="jndiLocation" value="jdbc/MySQLDS" /&gt;
 *    &lt;/connectionSource&gt;
 *  </pre>
 *  <p>
 *  Sample configuration (with username and password):<br>
 *  <pre>
 *    &lt;connectionSource class="org.apache.log4j.jdbc.JNDIConnectionSource"&gt;
 *        &lt;param name="jndiLocation" value="jdbc/MySQLDS" /&gt;
 *        &lt;param name="username" value="myUser" /&gt;
 *        &lt;param name="password" value="myPassword" /&gt;
 *    &lt;/connectionSource&gt;
 *  </pre>
 *  <p>
 *  Note that this class will obtain an {@link javax.naming.InitialContext}
 *  using the no-argument constructor.  This will usually work when executing
 *  within a J2EE environment.  When outside the J2EE environment, make sure
 *  that you provide a jndi.properties file as described by your JNDI
 *  provider's documentation.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
public class JNDIConnectionSource
       extends ConnectionSourceSkeleton {
  private String jndiLocation = null;
  private DataSource dataSource = null;

  /**
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  public void activateOptions() {
    if (jndiLocation == null) {
      getLogger().error("No JNDI location specified for JNDIConnectionSource.");
    }
    
    discoverConnnectionProperties();

  }
  
  /**
   * @see org.apache.log4j.db.ConnectionSource#getConnection()
   */
  public Connection getConnection()
         throws SQLException {
    Connection conn = null;
    try {

      if(dataSource == null) {
        dataSource = lookupDataSource();
      }
      if (getUser() == null) {
        conn = dataSource.getConnection();
      } else {
        conn = dataSource.getConnection(getUser(), getPassword());
      }
    } catch (final NamingException ne) {
         getLogger().error("Error while getting data source", ne);
      throw new SQLException("NamingException while looking up DataSource: " + ne.getMessage());
    } catch (final ClassCastException cce) {
      getLogger().error("ClassCastException while looking up DataSource.", cce);
      throw new SQLException("ClassCastException while looking up DataSource: " + cce.getMessage());
    }

    return conn;
  }

  /**
   * Returns the jndiLocation.
   * @return String
   */
  public String getJndiLocation() {
    return jndiLocation;
  }


  /**
   * Sets the jndiLocation.
   * @param jndiLocation The jndiLocation to set
   */
  public void setJndiLocation(String jndiLocation) {
    this.jndiLocation = jndiLocation;
  }


  private DataSource lookupDataSource()
         throws NamingException, SQLException {
    DataSource ds;
    Context ctx = new InitialContext();
    Object obj = ctx.lookup(jndiLocation);

    // PortableRemoteObject was introduced in JDK 1.3. We won't use it.
    //ds = (DataSource)PortableRemoteObject.narrow(obj, DataSource.class);
    ds = (DataSource) obj;

    if (ds == null) {
      throw new SQLException("Failed to obtain data source from JNDI location " + jndiLocation);
    } else {
      return ds;
    }
  }
}
