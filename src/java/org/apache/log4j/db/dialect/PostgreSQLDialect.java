/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.db.dialect;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PostgreSQLDialect
       implements SQLDialect {
  public static final String SELECT_CURRVAL = "SELECT currval('logging_event_id_seq')";

  public String getSelectInsertId() {
    return SELECT_CURRVAL;
  }
}
