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

import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *  The UrlConnectionSource is an implementation of {@link ConnectionSource}
 *  that obtains the Connection in the traditional JDBC manner based on the
 *  connection URL.
 *  <p>
 *  Note that this class will estanblish a new Connection for each call to
 *  {@link #getConnection()}.  It is recommended that you either use a JDBC
 *  driver that natively supported Connection pooling or that you create
 *  your own implementation of {@link ConnectionSource} that taps into whatever
 *  pooling mechanism you are already using.  (If you have access to a JNDI
 *  implementation that supports {@link javax.sql.DataSource}s, e.g. within
 *  a J2EE application server, see {@link JNDIConnectionSource}).
 *  <p>
 *  Sample configuration:<br>
 *  <pre>
 *     &lt;connectionSource class="org.apache.log4j.jdbc.UrlConnectionSource"&gt;
 *        &lt;param name="driver" value="com.mysql.jdbc.Driver" /&gt;
 *        &lt;param name="url" value="jdbc:mysql://localhost:3306/mydb" /&gt;
 *        &lt;param name="username" value="myUser" /&gt;
 *        &lt;param name="password" value="myPassword" /&gt;
 *     &lt;/connectionSource&gt;
 *  </pre>
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
public class UrlConnectionSource implements ConnectionSource {
  private String driver = null;
  private String url = null;
  private String username = null;
  private String password = null;
  private ErrorHandler errorHandler = null;

  public void activateOptions() {
    try {
      Class.forName(driver);
    } catch (final ClassNotFoundException cnfe) {
      if (errorHandler != null) {
        errorHandler.error(
          "Could not load JDBC driver class: " + driver, cnfe,
          ErrorCode.GENERIC_FAILURE);
      } else {
        cnfe.printStackTrace();
      }
    }
  }

  /**
       * @see org.apache.log4j.jdbc.ConnectionSource#getConnection()
       */
  public Connection getConnection() throws SQLException {
    if (username == null) {
      return DriverManager.getConnection(url);
    } else {
      return DriverManager.getConnection(url, username, password);
    }
  }

  /**
   * Returns the password.
   * @return String
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the url.
   * @return String
   */
  public String getUrl() {
    return url;
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
   * Sets the url.
   * @param url The url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Sets the username.
   * @param username The username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the driver.
   * @return String
   */
  public String getDriver() {
    return driver;
  }

  /**
   * Sets the driver.
   * @param driver The driver to set
   */
  public void setDriver(String driver) {
    this.driver = driver;
  }

  /**
   * Sets the error handler.
   * @param errorHandler  the error handler to set
   */
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
}
