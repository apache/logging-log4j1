package org.apache.log4j.selector;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.naming.Context;
import javax.naming.InitialContext;

import org.mortbay.http.SocketListener;
import org.mortbay.jetty.plus.Server;


import junit.framework.TestCase;

/**
 * PassByJNDI studies the effects of passing parameters through JNDI
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class PassByJNDI extends TestCase {

  Server server;

  public PassByJNDI(String arg0) {
    super(arg0);
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();

    Context ctx = new InitialContext();
    server = new Server();
    SocketListener listener = new SocketListener();
    listener.setPort(8080);
    server.addListener(listener);
    
    server.addWebApplication("localhost", "tata", "./webapps/Tata/tata.war");
    server.addWebApplication("localhost", "titi", "./webapps/Titi/titi.war");
    
    server.start();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    server.stop();
  }

  public void test1() throws Exception {
    HttpURLConnection connection;
    String contents;
    
    URL urlToto = new URL("http://localhost:8080/tata/XServlet");
    URL urlTiti = new URL("http://localhost:8080/titi/XServlet");

    connection = (HttpURLConnection) urlToto.openConnection();
    contents = getContents(connection).toString();
    assertEquals("tata", contents.trim());

    connection = (HttpURLConnection) urlTiti.openConnection();
    contents = getContents(connection).toString();
    assertEquals("titi", contents.trim());

  }

  StringBuffer getContents(HttpURLConnection connection) throws IOException {
    StringBuffer content = new StringBuffer();
    InputStream is = connection.getInputStream();

    byte[] buffer = new byte[2048];
    int count;
    while (-1 != (count = is.read(buffer))) {
      content.append(new String(buffer, 0, count));
    }
    return content;
  }
  
}