package org.apache.log4j.db.dialect;

/**
 * 
 * 
 * @author Ceki
 *
 */
public class MySQLDialect implements SQLDialect {
  public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
  
  public String getSelectInsertId() {
    return SELECT_LAST_INSERT_ID;
  }
}
