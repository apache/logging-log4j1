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
package org.apache.log4j.db;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;

/**
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public class DBReeceiverTest
       extends TestCase {
  /*
   * @see TestCase#setUp()
   */
  protected void setUp()
         throws Exception {
    super.setUp();
  }


  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown()
         throws Exception {
    super.tearDown();
  }

  /**
   * Constructor for DBReeceiverTest.
   * @param arg0
   */
  public DBReeceiverTest(String arg0) {
    super(arg0);
  }

  public void testBasic() {
    BasicConfigurator.configure();
    LogLog.info("asdasd");

    UrlConnectionSource connectionSource = new UrlConnectionSource();
    connectionSource.setDriverClass("com.mysql.jdbc.Driver");
    connectionSource.setUrl("jdbc:mysql:///test");
    connectionSource.setUser("root");
    LogLog.info("xxxxxxx");

    DBReceiver dbReceiver = new DBReceiver();
    dbReceiver.setLoggerRepository(LogManager.getLoggerRepository());
    dbReceiver.setConnectionSource(connectionSource);
    dbReceiver.activateOptions();
    LogLog.info("after  dbReceiver.activateOptions()");

    
 
    try { Thread.sleep(3000); } catch(Exception e) {}
    dbReceiver.shutdown();
    LogLog.info("after  dbReceiver.shutdown()");
    try { Thread.sleep(3000); } catch(Exception e) {}
  }


  public void xtestJNDI()
         throws Exception {
    Hashtable env = new Hashtable();

    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
    env.put(Context.PROVIDER_URL, "file:///home/jndi");

    Context ctx = new InitialContext(env);

    //ctx.addToEnvironment("toto", new Integer(1));
    ctx.bind("toto", new Integer(1));
  }
}
