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

import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.Decoder;
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
      getLogger().warn("Unable to find decoder", cnfe);
    } catch (IllegalAccessException iae) {
      getLogger().warn("Could not construct decoder", iae);
    } catch (InstantiationException ie) {
      getLogger().warn("Could not construct decoder", ie);
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
            List v = decoderImpl.decodeEvents(data.trim());

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

      getLogger().debug("{}'s thread is ending.", MulticastReceiver.this.getName());
    }
  }
}
