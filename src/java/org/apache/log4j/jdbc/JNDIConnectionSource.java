/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.jdbc;

import org.apache.log4j.spi.ErrorHandler;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.rmi.PortableRemoteObject;

import javax.sql.DataSource;


/**
 *  The <code>JNDIConnectionSource</code> is an implementation of
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
public class JNDIConnectionSource implements ConnectionSource {
  private String jndiLocation = null;
  private DataSource dataSource = null;
  private ErrorHandler errorHandler = null;
  private String username = null;
  private String password = null;

  /**
   * @see org.apache.log4j.jdbc.ConnectionSource#getConnection()
   */
  public Connection getConnection() throws SQLException {
    Connection conn = null;

    try {
      ensureDataSource();

      if (username == null) {
        conn = dataSource.getConnection();
      } else {
        conn = dataSource.getConnection(username, password);
      }
    } catch (final NamingException ne) {
      if (errorHandler != null) {
        errorHandler.error(ne.getMessage(), ne, 0);
      }

      throw new SQLException(
        "NamingException while looking up " + "DataSource: " + ne.getMessage());
    } catch (final ClassCastException cce) {
      if (errorHandler != null) {
        errorHandler.error(cce.getMessage(), cce, 0);
      }

      throw new SQLException(
        "ClassCastException while looking up " + "DataSource: "
        + cce.getMessage());
    }

    return conn;
  }

  /**
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  public void activateOptions() {
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

  /**
   * Sets the error handler.
   * @param errorHandler  the error handler to set
   */
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * Returns the password.
   * @return String
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the username.
   * @return String
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the password.
   * @param password The password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Sets the username.
   * @param username The username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  private void ensureDataSource() throws NamingException, SQLException {
    if (dataSource == null) {
      Context ctx = new InitialContext();
      Object obj = ctx.lookup(jndiLocation);
      dataSource =
        (DataSource) PortableRemoteObject.narrow(obj, DataSource.class);
    }

    if (dataSource == null) {
      throw new SQLException(
        "Failed to obtain data source from JNDI " + "location " + jndiLocation);
    }
  }
}
