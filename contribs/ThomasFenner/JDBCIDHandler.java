
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
*/

package com.klopotek.utils.log;


/**
This interface has to be implemented to provide ID-columns with unique IDs and its used in class JDBCLogger.

<p><b>Author : </b><A HREF="mailto:t.fenner@klopotek.de">Thomas Fenner</A></p>

@since 1.0
*/
public interface JDBCIDHandler
{
	/**Get a unique ID*/
	Object getID();
}



