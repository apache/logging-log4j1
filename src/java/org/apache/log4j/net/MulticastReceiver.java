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

package org.apache.log4j.net;

import org.apache.log4j.Decoder;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *  Multicast-based receiver.  Accepts LoggingEvents encoded using
 *  MulticastAppender and XMLLayout. The the XML data is converted
 *  back to a LoggingEvent and is posted.
 *
 *  @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class MulticastReceiver extends Receiver implements PortBased,
  AddressBased, Pauseable {
  private static final int PACKET_LENGTH = 16384;
  private boolean isActive = false;
  private int port;
  private String address;
  private String encoding;
  private MulticastSocket socket = null;

  //default to log4j xml decoder
  private String decoder = "org.apache.log4j.xml.XMLDecoder";
  private Decoder decoderImpl;
  private MulticastHandlerThread handlerThread;
  private MulticastReceiverThread receiverThread;
  private boolean paused;

  public String getDecoder() {
    return decoder;
  }

  public void setDecoder(String decoder) {
    this.decoder = decoder;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getAddress() {
    return address;
  }

  /**
      The <b>Encoding</b> option specifies how the bytes are encoded.  If this option is not specified,
      the system encoding will be used.
    */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
     Returns value of the <b>Encoding</b> option.
   */
  public String getEncoding() {
    return encoding;
  }

  public synchronized void shutdown() {
    isActive = false;
    handlerThread.interrupt();
    receiverThread.interrupt();
    socket.close();
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean b) {
    paused = b;
  }

  /**
    Returns true if this receiver is active. */
  public synchronized boolean isActive() {
    return isActive;
  }

  public void activateOptions() {
    InetAddress addr = null;

    try {
      Class c = Class.forName(decoder);
      Object o = c.newInstance();

      if (o instanceof Decoder) {
        this.decoderImpl = (Decoder) o;
      }
    } catch (ClassNotFoundException cnfe) {
      LogLog.warn("Unable to find decoder", cnfe);
    } catch (IllegalAccessException iae) {
      LogLog.warn("Could not construct decoder", iae);
    } catch (InstantiationException ie) {
      LogLog.warn("Could not construct decoder", ie);
    }

    try {
      addr = InetAddress.getByName(address);
    } catch (UnknownHostException uhe) {
      uhe.printStackTrace();
    }

    try {
      isActive = true;
      socket = new MulticastSocket(port);
      socket.joinGroup(addr);
      receiverThread = new MulticastReceiverThread();
      receiverThread.start();
      handlerThread = new MulticastHandlerThread();
      handlerThread.start();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  class MulticastHandlerThread extends Thread {
    private List list = new ArrayList();

    public MulticastHandlerThread() {
      setDaemon(true);
    }

    public void append(String data) {
      synchronized (list) {
        list.add(data);
        list.notify();
      }
    }

    public void run() {
      ArrayList list2 = new ArrayList();

      while (isAlive()) {
        synchronized (list) {
          try {
            while (list.size() == 0) {
              list.wait();
            }

            if (list.size() > 0) {
              list2.addAll(list);
              list.clear();
            }
          } catch (InterruptedException ie) {
          }
        }

        if (list2.size() > 0) {
          Iterator iter = list2.iterator();

          while (iter.hasNext()) {
            String data = (String) iter.next();
            List v = decoderImpl.decodeEvents(data);

            if (v != null) {
              Iterator eventIter = v.iterator();

              while (eventIter.hasNext()) {
                if (!isPaused()) {
                  doPost((LoggingEvent) eventIter.next());
                }
              }
            }
          }

          list2.clear();
        } else {
          try {
            synchronized (this) {
              wait(1000);
            }
          } catch (InterruptedException ie) {
          }
        }
      }
    }
  }

  class MulticastReceiverThread extends Thread {
    public MulticastReceiverThread() {
      setDaemon(true);
    }

    public void run() {
      isActive = true;

      byte[] b = new byte[PACKET_LENGTH];
      DatagramPacket p = new DatagramPacket(b, b.length);

      while (isActive) {
        try {
          socket.receive(p);

          //this string constructor which accepts a charset throws an exception if it is 
          //null
            if (encoding == null) {
            handlerThread.append(
              new String(p.getData(), 0, p.getLength()).trim());
          } else {
            handlerThread.append(
              new String(p.getData(), 0, p.getLength(), encoding).trim());
          }
        } catch (SocketException se) {
          //disconnected
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }

      LogLog.debug(MulticastReceiver.this.getName() + "'s thread is ending.");
    }
  }
}
