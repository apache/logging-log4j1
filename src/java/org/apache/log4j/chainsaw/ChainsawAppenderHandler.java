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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.plugins.PluginRegistry;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;


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
  private static final String DEFAULT_IDENTIFIER = "Unknown";
  private WorkQueue worker;
  private final Object mutex = new Object();
  private int sleepInterval = 1000;
  private EventListenerList listenerList = new EventListenerList();
  private double dataRate = 0.0;
  private String identifierExpression;
  private final LoggingEventFieldResolver resolver =
    LoggingEventFieldResolver.getInstance();
  private PropertyChangeSupport propertySupport =
    new PropertyChangeSupport(this);

  public ChainsawAppenderHandler(ChainsawAppender appender) {
    appender.setAppender(this);
    activateOptions();
  }

  public ChainsawAppenderHandler() {
    activateOptions();
  }

  public void setIdentifierExpression(String identifierExpression) {
    this.identifierExpression = identifierExpression;
  }

  public String getIdentifierExpression() {
    return identifierExpression;
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

  public int getQueueInterval() {
    return sleepInterval;
  }

  public void setQueueInterval(int interval) {
    sleepInterval = interval;
  }

  /**
   * Determines an appropriate title for the Tab for the Tab Pane
   * by locating a the hostname property
   * @param event
   * @return identifier
   */
  String getTabIdentifier(LoggingEvent e) {
    String ident = resolver.applyFields(identifierExpression, e);

    return ((ident != null) ? ident : DEFAULT_IDENTIFIER);
  }

  /**
   * A little test bed
   * @param args
   */
  public static void main(String[] args) throws InterruptedException {
    ChainsawAppenderHandler handler = new ChainsawAppenderHandler();
    handler.addEventBatchListener(
      new EventBatchListener() {
        public String getInterestedIdentifier() {
          return null;
        }

        public void receiveEventBatch(
          String identifier, List eventBatchEntrys) {
          LogLog.debug(
            "received batch for '" + identifier + "', list.size()="
            + eventBatchEntrys.size());
          LogLog.debug(eventBatchEntrys.toString());
        }
      });
    LogManager.getRootLogger().addAppender(handler);

    SocketReceiver receiver = new SocketReceiver(4445);
    PluginRegistry.startPlugin(receiver);

    Thread.sleep(60000);
  }

  /**
   * Exposes the current Data rate calculated.  This is periodically updated
   * by an internal Thread as is the number of events that have
   * been processed, and dispatched to all listeners since the last sample period
   * divided by the number of seconds since the last sample period.
   *
   * This method fires a PropertyChange event so listeners can monitor the rate
   * @return double # of events processed per second
   */
  public double getDataRate() {
    return dataRate;
  }

  /**
   * @param dataRate
   */
  void setDataRate(double dataRate) {
    double oldValue = this.dataRate;
    this.dataRate = dataRate;
    propertySupport.firePropertyChange(
      "dataRate", new Double(oldValue), new Double(this.dataRate));
  }

  /**
   * @param listener
   */
  public synchronized void addPropertyChangeListener(
    PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(listener);
  }

  /**
   * @param propertyName
   * @param listener
   */
  public synchronized void addPropertyChangeListener(
    String propertyName, PropertyChangeListener listener) {
    propertySupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * @param listener
   */
  public synchronized void removePropertyChangeListener(
    PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(listener);
  }

  /**
   * @param propertyName
   * @param listener
   */
  public synchronized void removePropertyChangeListener(
    String propertyName, PropertyChangeListener listener) {
    propertySupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Queue of Events are placed in here, which are picked up by an
   * asychronous thread.  The WorkerThread looks for events once a second and
   * processes all events accumulated during that time..
   */
  class WorkQueue {
    final ArrayList queue = new ArrayList();
    Thread workerThread;

    protected WorkQueue() {
      workerThread = new WorkerThread();
      workerThread.start();
    }

    public final void enqueue(LoggingEvent event) {
      synchronized (mutex) {
        queue.add(event);
        mutex.notify();
      }
    }

    public final void stop() {
      synchronized (mutex) {
        workerThread.interrupt();
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

        while (true) {
          long timeStart = System.currentTimeMillis();

          synchronized (mutex) {
            try {
              while (queue.size() == 0) {
                setDataRate(0);
                mutex.wait();
              }

              if (queue.size() > 0) {
                innerList.addAll(queue);
                queue.clear();
              }
            } catch (InterruptedException ie) {
            }
          }

          int size = innerList.size();

          if (size > 0) {
            Iterator iter = innerList.iterator();
            ChainsawEventBatch eventBatch = new ChainsawEventBatch();

            while (iter.hasNext()) {
              LoggingEvent e = (LoggingEvent) iter.next();
              Vector properties = new Vector();
              Iterator iterx = e.getPropertyKeySet().iterator();

              while (iterx.hasNext()) {
                String thisProp = iterx.next().toString();
                properties.add(thisProp + " " + e.getProperty(thisProp));
              }

              eventBatch.addEvent(
                getTabIdentifier(e),
                (e.getProperty(ChainsawConstants.EVENT_TYPE_KEY) == null)
                ? ChainsawConstants.LOG4J_EVENT_TYPE
                : e.getProperty(ChainsawConstants.EVENT_TYPE_KEY), e);
            }

            dispatchEventBatch(eventBatch);

            innerList.clear();
          }

          try {
            synchronized (this) {
              wait(getQueueInterval());
            }
          } catch (InterruptedException ie) {
          }

          if (size == 0) {
            setDataRate(0.0);
          } else {
            long timeEnd = System.currentTimeMillis();
            long diffInSeconds = (timeEnd - timeStart) / 1000;
            double rate = (((double) size) / diffInSeconds);
            setDataRate(rate);
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
}
