
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
*/

package com.klopotek.utils.log;

import java.sql.*;


/**
This interface has to be implemented for your own database-connection-handling and its used in class JDBCLogger.

<p><b>Author : </b><A HREF="mailto:t.fenner@klopotek.de">Thomas Fenner</A></p>

@since 1.0
*/
public interface JDBCConnectionHandler
{
	/**Get a connection*/
	Connection getConnection();
	/**Get a defined connection*/
   Connection getConnection(String _url, String _username, String _password);
}


