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


// Contibutors:  Aaron Greenhouse <aarong@cs.cmu.edu>
//               Thomas Tuft Muller <ttm@online.no>
package org.apache.log4j;

import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.BoundedFIFO;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Enumeration;


/**
 * The AsyncAppender lets users log events asynchronously. It uses a bounded
 * buffer to store logging events.
 * 
 * <p>
 * The AsyncAppender will collect the events sent to it and then dispatch them
 * to all the appenders that are attached to it. You can attach multiple
 * appenders to an AsyncAppender.
 * </p>
 * 
 * <p>
 * The AsyncAppender uses a separate thread to serve the events in its bounded
 * buffer.
 * </p>
 * 
 * <p>
 * Refer to the results in {@link org.apache.log4j.performance.Logging} for
 * the impact of using this appender.
 * </p>
 * 
 * <p>
 * <b>Important note:</b> The <code>AsyncAppender</code> can only be script
 * configured using the {@link org.apache.log4j.xml.DOMConfigurator}.
 * </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 *
 * @since 0.9.1
 */
public class AsyncAppender extends AppenderSkeleton
  implements AppenderAttachable {
  /**
   * The default buffer size is set to 128 events.
   */
  public static final int DEFAULT_BUFFER_SIZE = 128;

  //static Category cat = Category.getInstance(AsyncAppender.class.getName());
  BoundedFIFO bf = new BoundedFIFO(DEFAULT_BUFFER_SIZE);
  AppenderAttachableImpl aai;
  Dispatcher dispatcher;
  boolean locationInfo = false;
  boolean interruptedWarningMessage = false;

  public AsyncAppender() {
    // Note: The dispatcher code assumes that the aai is set once and
    // for all.
    aai = new AppenderAttachableImpl();
    dispatcher = new Dispatcher(bf, this);
    dispatcher.start();
  }

  public void addAppender(Appender newAppender) {
    synchronized (aai) {
      aai.addAppender(newAppender);
    }
  }

  public void append(LoggingEvent event) {
    // Set the NDC and thread name for the calling thread as these
    // LoggingEvent fields were not set at event creation time.
    event.getNDC();
    event.getThreadName();

    // Get a copy of this thread's MDC.
    event.getMDCCopy();

    if (locationInfo) {
      event.getLocationInformation();
    }

    synchronized (bf) {
      while (bf.isFull()) {
        try {
          //LogLog.debug("Waiting for free space in buffer, "+bf.length());
          bf.wait();
        } catch (InterruptedException e) {
          if (!interruptedWarningMessage) {
            interruptedWarningMessage = true;
            LogLog.warn("AsyncAppender interrupted.", e);
          } else {
            LogLog.warn("AsyncAppender interrupted again.");
          }
        }
      }

      //cat.debug("About to put new event in buffer.");
      bf.put(event);

      if (bf.wasEmpty()) {
        //cat.debug("Notifying dispatcher to process events.");
        bf.notify();
      }
    }
  }

  /**
   * Close this <code>AsyncAppender</code> by interrupting the dispatcher
   * thread which will process all pending events before exiting.
   */
  public void close() {
    synchronized (this) {
      // avoid multiple close, otherwise one gets NullPointerException
      if (closed) {
        return;
      }

      closed = true;
    }

    // The following cannot be synchronized on "this" because the
    // dispatcher synchronizes with "this" in its while loop. If we
    // did synchronize we would systematically get deadlocks when
    // close was called.
    dispatcher.close();

    try {
      dispatcher.join();
    } catch (InterruptedException e) {
      LogLog.error(
        "Got an InterruptedException while waiting for the "
        + "dispatcher to finish.", e);
    }

    dispatcher = null;
    bf = null;
  }

  public Enumeration getAllAppenders() {
    synchronized (aai) {
      return aai.getAllAppenders();
    }
  }

  public Appender getAppender(String name) {
    synchronized (aai) {
      return aai.getAppender(name);
    }
  }

  /**
   * Returns the current value of the <b>LocationInfo</b> option.
   */
  public boolean getLocationInfo() {
    return locationInfo;
  }

  /**
   * Is the appender passed as parameter attached to this category?
   */
  public boolean isAttached(Appender appender) {
    return aai.isAttached(appender);
  }

  /**
   * The <code>AsyncAppender</code> does not require a layout. Hence, this
   * method always returns <code>false</code>.
   */
  public boolean requiresLayout() {
    return false;
  }

  public void removeAllAppenders() {
    synchronized (aai) {
      aai.removeAllAppenders();
    }
  }

  public void removeAppender(Appender appender) {
    synchronized (aai) {
      aai.removeAppender(appender);
    }
  }

  public void removeAppender(String name) {
    synchronized (aai) {
      aai.removeAppender(name);
    }
  }

  /**
   * The <b>LocationInfo</b> option takes a boolean value. By default, it is
   * set to false which means there will be no effort to extract the location
   * information related to the event. As a result, the event that will be
   * ultimately logged will likely to contain the wrong location information
   * (if present in the log format).
   * 
   * <p>
   * Location information extraction is comparatively very slow and should be
   * avoided unless performance is not a concern.
   * </p>
   */
  public void setLocationInfo(boolean flag) {
    locationInfo = flag;
  }

  /**
   * The <b>BufferSize</b> option takes a non-negative integer value. This
   * integer value determines the maximum size of the bounded buffer.
   * Increasing the size of the buffer is always safe. However, if an
   * existing buffer holds unwritten elements, then <em>decreasing the buffer
   * size will result in event loss.</em> Nevertheless, while script
   * configuring the AsyncAppender, it is safe to set a buffer size smaller
   * than the {@link #DEFAULT_BUFFER_SIZE default buffer size} because
   * configurators guarantee that an appender cannot be used before being
   * completely configured.
   */
  public void setBufferSize(int size) {
    bf.resize(size);
  }

  /**
   * Returns the current value of the <b>BufferSize</b> option.
   */
  public int getBufferSize() {
    return bf.getMaxSize();
  }
}


// ------------------------------------------------------------------------------
// ------------------------------------------------------------------------------
// ----------------------------------------------------------------------------
class Dispatcher extends Thread {
  BoundedFIFO bf;
  AppenderAttachableImpl aai;
  boolean interrupted = false;
  AsyncAppender container;

  Dispatcher(BoundedFIFO bf, AsyncAppender container) {
    this.bf = bf;
    this.container = container;
    this.aai = container.aai;

    // It is the user's responsibility to close appenders before
    // exiting. 
    this.setDaemon(true);

    // set the dispatcher priority to lowest possible value
    this.setPriority(Thread.MIN_PRIORITY);
    this.setName("Dispatcher-" + getName());

    // set the dispatcher priority to MIN_PRIORITY plus or minus 2
    // depending on the direction of MIN to MAX_PRIORITY.
    //+ (Thread.MAX_PRIORITY > Thread.MIN_PRIORITY ? 1 : -1)*2);
  }

  void close() {
    synchronized (bf) {
      interrupted = true;

      // We have a waiting dispacther if and only if bf.length is
      // zero.  In that case, we need to give it a death kiss.
      if (bf.length() == 0) {
        bf.notify();
      }
    }
  }

  /**
   * The dispatching strategy is to wait until there are events in the buffer
   * to process. After having processed an event, we release the monitor
   * (variable bf) so that new events can be placed in the buffer, instead of
   * keeping the monitor and processing the remaining events in the buffer.
   * 
   * <p>
   * Other approaches might yield better results.
   * </p>
   */
  public void run() {
    //Category cat = Category.getInstance(Dispatcher.class.getName());
    LoggingEvent event;

    while (true) {
      synchronized (bf) {
        if (bf.length() == 0) {
          // Exit loop if interrupted but only if the the buffer is empty.
          if (interrupted) {
            //cat.info("Exiting.");
            break;
          }

          try {
            //LogLog.debug("Waiting for new event to dispatch.");
            bf.wait();
          } catch (InterruptedException e) {
            LogLog.error("The dispathcer should not be interrupted.");

            break;
          }
        }

        event = bf.get();

        if (bf.wasFull()) {
          //LogLog.debug("Notifying AsyncAppender about freed space.");
          bf.notify();
        }
      }
       // synchronized

      synchronized (container.aai) {
        if ((aai != null) && (event != null)) {
          aai.appendLoopOnAppenders(event);
        }
      }
    }
     // while

    // close and remove all appenders
    aai.removeAllAppenders();
  }
}
