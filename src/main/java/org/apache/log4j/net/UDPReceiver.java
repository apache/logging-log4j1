/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.Decoder;
import org.apache.log4j.spi.LoggingEvent;


/**
 *  Receive LoggingEvents encoded with an XMLLayout, convert the XML data to a
 *  LoggingEvent and post the LoggingEvent.
 *
 *  @author Scott Deboy <sdeboy@apache.org>
 *
 */
public class UDPReceiver extends Receiver implements PortBased, Pauseable {
  private static final int PACKET_LENGTH = 16384;
  private UDPReceiverThread receiverThread;
  private String encoding;

  //default to log4j xml decoder
  private String decoder = "org.apache.log4j.xml.XMLDecoder";
  private Decoder decoderImpl;
  protected boolean paused;
  private transient boolean closed = false;
  private int port;
  private DatagramSocket socket;
  UDPHandlerThread handlerThread;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  /**
   * The <b>Encoding</b> option specifies how the bytes are encoded.  If this 
   * option is not specified, the system encoding will be used.
   * */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Returns value of the <b>Encoding</b> option.
   */
  public String getEncoding() {
    return encoding;
  }

  public String getDecoder() {
    return decoder;
  }

  public void setDecoder(String decoder) {
    this.decoder = decoder;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean b) {
    paused = b;
  }

  public synchronized void shutdown() {
    if(closed == true) {
      return;
    }
    closed = true;
    // Closing the datagram socket will unblock the UDPReceiverThread if it is
    // was waiting to receive data from the socket.
    socket.close();

    try {
      if(handlerThread != null) {
      	handlerThread.close();
        handlerThread.join();
      }
      if(receiverThread != null) {
        receiverThread.join();
      }
    } catch(InterruptedException ie) {
    }
  }

  /**
    Returns true if this receiver is active. */
//  public synchronized boolean isActive() {
//    return isActive;
//}

  public void activateOptions() {
    try {
      Class c = Class.forName(decoder);
      Object o = c.newInstance();

      if (o instanceof Decoder) {
        this.decoderImpl = (Decoder) o;
      }
    } catch (ClassNotFoundException cnfe) {
      getLogger().warn("Unable to find decoder", cnfe);
    } catch (IllegalAccessException iae) {
      getLogger().warn("Could not construct decoder", iae);
    } catch (InstantiationException ie) {
      getLogger().warn("Could not construct decoder", ie);
    }

    try {
      socket = new DatagramSocket(port);
      receiverThread = new UDPReceiverThread();
      receiverThread.start();
      handlerThread = new UDPHandlerThread();
      handlerThread.start();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  class UDPHandlerThread extends Thread {
    private List list = new ArrayList();

    public UDPHandlerThread() {
      setDaemon(true);
    }

    public void append(String data) {
      synchronized (list) {
        list.add(data);
        list.notify();
      }
    }
  
    /**
     * Allow the UDPHandlerThread to wakeup and exit gracefully.
     */
    void close() {
      synchronized(list) {
      	list.notify();
      }
    }

    public void run() {
      ArrayList list2 = new ArrayList();

      while (!UDPReceiver.this.closed) {
        synchronized (list) {
          try {
            while (!UDPReceiver.this.closed && list.size() == 0) {
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
      } // while
      getLogger().debug(UDPReceiver.this.getName()+ "'s handler thread is exiting");
    } // run
  } // UDPHandlerThread

  class UDPReceiverThread extends Thread {
    public UDPReceiverThread() {
      setDaemon(true);
    }
    
    public void run() {
      byte[] b = new byte[PACKET_LENGTH];
      DatagramPacket p = new DatagramPacket(b, b.length);

      while (!UDPReceiver.this.closed) {
        try {
          socket.receive(p);
          
          //this string constructor which accepts a charset throws an exception if it is 
          //null
          if (encoding == null) {
            handlerThread.append(
              new String(p.getData(), 0, p.getLength()));
          } else {
            handlerThread.append(
              new String(p.getData(), 0, p.getLength(), encoding));
          }
        } catch (SocketException se) {
          //disconnected
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }

      //LogLog.debug(UDPReceiver.this.getName() + "'s thread is ending.");
    }
  }
}
