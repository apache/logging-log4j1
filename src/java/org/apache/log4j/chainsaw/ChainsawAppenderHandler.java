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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.EventListenerList;


/**
 * A handler class that either extends a particular appender hierarchy or can be bound
 * into the Log4j appender framework, and queues events, to be later
 * dispatched to registered/interested parties.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ChainsawAppenderHandler extends AppenderSkeleton {
  private ChainsawAppender appender;
  private WorkQueue worker;
  private final Object mutex = new Object();
  private int sleepInterval = 1000;
  private EventListenerList listenerList = new EventListenerList();

  public ChainsawAppenderHandler(ChainsawAppender appender) {
    this.appender = appender;
    appender.addAppender(this);
    activateOptions();
  }

  public ChainsawAppenderHandler() {
    activateOptions();
  }

  public void addEventBatchListener(EventBatchListener l) {
    listenerList.add(EventBatchListener.class, l);
  }

  public void removeEventBatchListener(EventBatchListener l) {
    listenerList.remove(EventBatchListener.class, l);
  }

  public void append(LoggingEvent event) {
    worker.enqueue(event);
  }

  /**
   * Allows a Collection of events to be posted into this handler
   */
  public void appendBatch(Collection events) {
    for (Iterator iter = events.iterator(); iter.hasNext();) {
      LoggingEvent element = (LoggingEvent) iter.next();
      append(element);
    }
  }

  public void close() {
  }

  public void activateOptions() {
    worker = new WorkQueue();
  }

  public boolean requiresLayout() {
    return false;
  }

  /**
   * Converts a LoggingEvent into a Vector of element (columns really).
   * @param event
   * @return
   * 
   * @deprecated
   */
  static Vector convert(LoggingEvent event) {
    Vector v = new Vector();
    LocationInfo info = event.getLocationInformation();
    String className = "";
    String methodName = "";
    String fileName = "";
    String lineNum = "";

    if (info != null) {
      try {
        className = info.getClassName();
        methodName = info.getMethodName();
        fileName = info.getFileName();
        lineNum = info.getLineNumber();
      } catch (NullPointerException npe) {
      }

      //ignore..malformed info
    }

    StringBuffer MDC = new StringBuffer();
    Set mdc = event.getMDCKeySet();
    Iterator iter = mdc.iterator();

    while (iter.hasNext()) {
      if (MDC.length() != 0) {
        MDC.append(",");
      }

      String propName = (String) iter.next();
      MDC.append(propName);
      MDC.append("=");

      String propValue = (String) event.getMDC(propName);
      MDC.append(propValue);
    }

    StringBuffer prop = new StringBuffer();
    Set properties = event.getPropertyKeySet();

    if (properties != null) {
      Iterator iter2 = properties.iterator();

      while (iter2.hasNext()) {
        if (prop.length() != 0) {
          prop.append(",");
        }

        String propName = (String) iter2.next();
        prop.append(propName);
        prop.append("=");

        String propValue = (String) event.getProperty(propName);
        prop.append(propValue);
      }
    }

    v.add(event.getLoggerName());
    v.add(new Date(event.timeStamp));
    v.add(event.getLevel().toString());
    v.add(event.getThreadName());
    v.add(event.getRenderedMessage());
    v.add(event.getNDC());
    v.add(MDC.toString());

    StringBuffer exc = new StringBuffer();
    String[] excarray = event.getThrowableStrRep();

    if (excarray != null) {
      for (int i = 0; i < excarray.length; i++) {
        exc.append(excarray[i]);
      }
    }

    v.add(exc.toString());
    v.add(className);
    v.add(methodName);
    v.add(fileName);
    v.add(lineNum);
    v.add(prop.toString());

    return v;
  }

  public int getQueueInterval() {
    return sleepInterval;
  }

  public void setQueueInterval(int interval) {
    sleepInterval = interval;
  }

  /**
   * Determines an appropriate title for the Tab for the Tab Pane
   * by locating a the log4jmachinename property
   * @param v
   * @return
   */
    private static String getTabIdentifier(LoggingEvent e) {
    StringBuffer ident = new StringBuffer();
    String machinename = e.getProperty(ChainsawConstants.LOG4J_MACHINE_KEY);

    if (machinename != null) {
      ident.append(machinename);
    }

    String appname = e.getProperty(ChainsawConstants.LOG4J_APP_KEY);
    
    if (appname != null) {
      ident.append("-");
      ident.append(appname);
    }

    if (ident.length() == 0) {
      /**
           * Maybe there's a Remote Host entry?
           */
      String remoteHost = e.getProperty(ChainsawConstants.LOG4J_REMOTEHOST_KEY);
//        int rhlength = remoteHost.indexOf(":");
//
//        if (rhlength == -1) {
//          rhlength = properties.length();
//        }
//
//        remoteHost = properties.substring(rhposition, rhlength);
//      }

      if (remoteHost != null) {
        ident.append(remoteHost);
      }
    }

    if (ident.length() == 0) {
      ident.append(ChainsawConstants.UNKNOWN_TAB_NAME);
    }

    return ident.toString();
  }

  /**
   * Queue of Events are placed in here, which are picked up by an
   * asychronous thread.  The WorkerThread looks for events once a second and
   * processes all events accumulated during that time..
   */
  class WorkQueue {
    private final ArrayList queue = new ArrayList();
    private boolean stopped = false;

    protected WorkQueue() {
      new WorkerThread().start();
    }

    public final void enqueue(LoggingEvent event) {
      synchronized (mutex) {
        queue.add(event);
      }
    }

    public final void stop() {
      synchronized (mutex) {
        stopped = true;
      }
    }

    /**
     * The worker thread converts each queued event
     * to a vector and forwards the vector on to the UI.
     */
    private class WorkerThread extends Thread {
      public WorkerThread() {
        setDaemon(true);
        setPriority(Thread.NORM_PRIORITY - 1);
      }

      public void run() {
        List innerList = new ArrayList();

        while (isAlive()) {
          synchronized (mutex) {
            if (stopped) {
              return;
            } else {
              if (queue.size() > 0) {
                innerList.addAll(queue);
                queue.clear();
              }
            }
          }

          if (innerList.size() > 0) {
            Iterator iter = innerList.iterator();
            Map identifiersEventsMap = new HashMap();
            ChainsawEventBatch eventBatch = new ChainsawEventBatch();

            while (iter.hasNext()) {
              LoggingEvent e = (LoggingEvent) iter.next();
              String eventType =
                e.getProperty(ChainsawConstants.EVENT_TYPE_KEY);

              if (eventType == null) {
                eventType = ChainsawConstants.LOG4J_EVENT_TYPE;
              }

              String ident = getTabIdentifier(e);
              eventBatch.addEvent(ident, eventType, e);
            }

            dispatchEventBatch(eventBatch);

            innerList.clear();
          }

          try {
            Thread.sleep(getQueueInterval());
          } catch (InterruptedException ie) {
          }
        }
      }

      /**
      * Dispatches the event batches contents to all the interested parties
      * by iterating over each identifier and dispatching the
      * ChainsawEventBatchEntry object to each listener that is interested.
       * @param eventBatch
       */
      private void dispatchEventBatch(ChainsawEventBatch eventBatch) {
        EventBatchListener[] listeners =
          (EventBatchListener[]) listenerList.getListeners(
            EventBatchListener.class);

        for (Iterator iter = eventBatch.identifierIterator(); iter.hasNext();) {
          String identifier = (String) iter.next();
          List eventList = null;

          for (int i = 0; i < listeners.length; i++) {
            EventBatchListener listener = listeners[i];

            if (
              (listener.getInterestedIdentifier() == null)
                || listener.getInterestedIdentifier().equals(identifier)) {
              if (eventList == null) {
                eventList = eventBatch.entrySet(identifier);
              }

              listener.receiveEventBatch(identifier, eventList);
            }
          }

          eventList = null;
        }
      }
    }
  }
  
  /**
   * A little test bed 
   * @param args
   */
  public static void main(String[] args) throws InterruptedException {
	
      ChainsawAppenderHandler handler = new ChainsawAppenderHandler();
      handler.addEventBatchListener(new EventBatchListener() {

		public String getInterestedIdentifier() {
			return null;
		}

		public void receiveEventBatch(String identifier, List eventBatchEntrys) {
            LogLog.debug("received batch for '" + identifier + "', list.size()=" + eventBatchEntrys.size());
            LogLog.debug(eventBatchEntrys.toString());
			
		}});
      LogManager.getRootLogger().addAppender(handler);
      
      SocketReceiver receiver = new SocketReceiver(4445);
      PluginRegistry.startPlugin(receiver);
      
      Thread.sleep(60000);
}
}
