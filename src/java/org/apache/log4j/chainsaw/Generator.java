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

package org.apache.log4j.chainsaw;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
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
    LoggingEvent e =
      new LoggingEvent(
        logger.getClass().getName(), logger, System.currentTimeMillis(), level,
        msg, t);
    e.setProperty(ChainsawConstants.LOG4J_APP_KEY, getName());

    return e;
  }

  public synchronized void run() {
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
    thread.start();
  }
}
