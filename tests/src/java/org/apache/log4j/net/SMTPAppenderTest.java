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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.AbstractAppenderTest;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Test if SMTPAppender honors the Appender contract.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * 
 */
public class SMTPAppenderTest extends AbstractAppenderTest {

  SMTPAppender ca = new SMTPAppender();

  protected void setUp() {
    ca.setLayout(new PatternLayout("%m%n"));
    ca.setFrom("from@example.net");
    ca.setTo("to@example.net");
    ca.setSMTPHost("localhost");
    ca.setSubject("subject");
  }
  
  protected void tearDown() {
    ca.close();
  }
  
  protected AppenderSkeleton getAppender() {
    return ca;
  }

  public AppenderSkeleton getConfiguredAppender() {
    return ca;
  }

  public class MailServer extends Thread {
    ServerSocket ss;
    String enc = "ASCII";
    StringBuffer sb = new StringBuffer();

    MailServer() throws Exception {
      ss = new ServerSocket(0);
    }

    public void run() {
      while (!Thread.interrupted()) {
        try {
          run0();
        } catch (IOException e) {
          System.out.println(e);
        }
      }
    }
    
    private void println(Writer w, String s) throws IOException {
      w.write(s);
      w.write("\r\n");
      w.flush();
    }
    
    private void run0() throws IOException {
      Socket s;
      s = ss.accept();
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), enc));
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), enc));
      println(bw, "220 SMTP");
      String helo = br.readLine();
      println(bw, "250 OK");
      String from = br.readLine();
      println(bw, "250 OK");
      String to = br.readLine();
      println(bw, "250 OK");
      String data = br.readLine();
      println(bw, "354 send, end with .");
      String line;
      while (true) {
        line = br.readLine();
        sb.append(line).append("\r\n");
        if (line.equals(".")) break;
      }
      System.out.println(sb);
      println(bw, "250 OK");
      String quit = br.readLine();
      s.close();
    }

  }

  public void testSend() throws Exception {
    MailServer server = new MailServer();
    server.start();
    ca.setSMTPPort(server.ss.getLocalPort());
    ca.activateOptions();
    
    String msg = "XYZZY";
    Logger l = Logger.getLogger(getClass());
    l.addAppender(ca);
    l.error(msg);
    Thread.sleep(500);
    server.interrupt();
    server.ss.close();
    
    String s = server.sb.toString();
    assertTrue("got the message", s.indexOf(msg) != -1);
    assertTrue(s.indexOf(ca.getFrom()) != -1);
    assertTrue(s.indexOf(ca.getTo()) != -1);
    assertTrue(s.indexOf(ca.getSubject()) != -1);
  }

  public void testBadSessionJNDI() {
    ca.setSessionJNDI("/not/here");
    try {
      ca.activateOptions();
      fail("cannot start");
    } catch (IllegalStateException e) {
    }
    ca.setSessionJNDI(null);
    ca.setLayout(new DummyLayout());
    ca.activateOptions();
  }

}
