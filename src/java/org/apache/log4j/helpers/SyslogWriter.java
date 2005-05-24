/*
 * Copyright 1999-2005 The Apache Software Foundation.
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


import java.io.Writer;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.io.IOException;

/**
   SyslogWriter is a wrapper around the java.net.DatagramSocket class
   so that it behaves like a java.io.Writer.

   @since 0.7.3
*/
public class SyslogWriter extends Writer {

  final int SYSLOG_PORT = 514;
  static String syslogHost;
  
  private InetAddress address;
  private DatagramSocket ds;

  public
  SyslogWriter(String syslogHost) {
    this.syslogHost = syslogHost;

    try {      
      this.address = InetAddress.getByName(syslogHost);
    }
    catch (UnknownHostException e) {
      LogLog.error("Could not find " + syslogHost +
			 ". All logging will FAIL.", e);
    }

    try {
      this.ds = new DatagramSocket();
    }
    catch (SocketException e) {
      e.printStackTrace(); 
      LogLog.error("Could not instantiate DatagramSocket to " + syslogHost +
			 ". All logging will FAIL.", e);
    }
  }


  public
  void write(char[] buf, int off, int len) throws IOException {
    this.write(new String(buf, off, len));
  }
  
  public
  void write(String string) throws IOException {
    byte[] bytes = string.getBytes();
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
					       address, SYSLOG_PORT);

    if(this.ds != null)
      ds.send(packet);
    
  }

  public
  void flush() {}

  public
  void close() {}
}
