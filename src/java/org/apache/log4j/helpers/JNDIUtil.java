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

package org.apache.log4j.helpers;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 *
 * @author Ceki Gulcu
 */
public class JNDIUtil {
  public static Context getInitialContext() throws NamingException {
    return new InitialContext();
  }

  public static String lookup(Context ctx, String name) {
    if (ctx == null) {
      return null;
    }
    try {
      return (String) ctx.lookup(name);
    } catch (NamingException e) {
      //LogLog.warn("Failed to get "+name);
      return null;
    }
  }
}
