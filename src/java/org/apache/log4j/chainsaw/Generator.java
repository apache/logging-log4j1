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

package org.apache.log4j.chainsaw;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Class designed to stress, and/or test the Chainsaw GUI by sending it
 * lots of Logging Events.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class Generator extends Receiver implements Runnable {
  private static final Logger logger1 =
    Logger.getLogger("com.mycompany.mycomponentA");
  private static final Logger logger2 =
    Logger.getLogger("com.mycompany.mycomponentB");
  private static final Logger logger3 =
    Logger.getLogger("com.someothercompany.corecomponent");
  private final String baseString_;
  private Thread thread;
  private boolean shutdown;

  public Generator(String name) {
    setName(name);
    baseString_ = name;
  }

  private LoggingEvent createEvent(
    Level level, Logger logger, String msg, Throwable t) {
    LoggingEvent e = new LoggingEvent(
        logger.getClass().getName(), logger, level, msg, t);
    e.setProperty(Constants.APPLICATION_KEY, getName());
    e.setProperty(Constants.HOSTNAME_KEY, "localhost");

    return e;
  }

  public void run() {
    NDC.push(baseString_);
    MDC.put("some string", "some value" + baseString_);

    int i = 0;

    while (!shutdown) {
      doPost(
        createEvent(
          Level.DEBUG, logger1,
          "debugmsg " + i++
          + " g dg sdfa sadf sdf safd fsda asfd sdfa sdaf asfd asdf fasd fasd adfs fasd adfs fads afds afds afsd afsd afsd afsd afsd fasd asfd asfd afsd fasd afsd",
          new Exception("someexception-" + baseString_)));

     doPost(createEvent(Level.INFO, logger2, "infomsg " + i++, new Exception("someexception-" + baseString_)));

     doPost(createEvent(Level.WARN,logger3,  "warnmsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.ERROR, logger1, "errormsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.DEBUG, logger2,"debugmsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.INFO, logger3,"infomsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.WARN, logger1, "warnmsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.ERROR, logger2, "errormsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.DEBUG, logger3, "debugmsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.INFO, logger1, "infomsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.WARN, logger2, "warnmsg " + i++, new Exception("someexception-" + baseString_)));
     doPost(createEvent(Level.ERROR, logger3, "errormsg " + i++, new Exception("someexception-" + baseString_)));
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ie) {
      }
    }
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.plugins.Plugin#shutdown()
   */
  public void shutdown() {
    shutdown = true;
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.spi.OptionHandler#activateOptions()
   */
  public void activateOptions() {
    thread = new Thread(this);
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }
}
