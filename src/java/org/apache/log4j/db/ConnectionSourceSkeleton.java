package org.apache.log4j.db;

import org.apache.log4j.spi.ErrorHandler;


/**
 * @author Ceki G&uuml;lc&uuml;
 */
abstract public class ConnectionSourceSkeleton
       implements ConnectionSource {
  protected String user = null;
  protected String password = null;
  protected ErrorHandler errorHandler = null;

  /**
   * Get teh errorHandler for this connection source
   */
  public ErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * Sets the error handler.
   * @param errorHandler  the error handler to set
   */
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }



  /**
   * Get the password for this connection source.
   */
  public String getPassword() {
    return password;
  }


  /**
   * Sets the password.
   * @param password The password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Get the user for this connection source.
   */
  public String getUser() {
    return user;
  }

  /**
   * Sets the username.
   * @param username The username to set
   */
  public void setUser(String username) {
    this.user = username;
  }

}
