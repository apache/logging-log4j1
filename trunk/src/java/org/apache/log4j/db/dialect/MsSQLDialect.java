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

package org.apache.log4j.db.dialect; 

/** 
* The MS SQL Server dialect is untested. 
* 
* Note that the dialect is not needed if your JDBC driver supports 
* the getGeneratedKeys method introduced in JDBC 3.0 specification.
* 
* @author James Stauffer 
*/ 
public class MsSQLDialect implements SQLDialect { 
 public static final String SELECT_CURRVAL = "SELECT @@identity id"; 

 public String getSelectInsertId() { 
   return SELECT_CURRVAL; 
 } 
}
