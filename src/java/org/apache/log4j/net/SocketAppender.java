//      Copyright 1996-2000, International Business Machines
//      Corporation. All Rights Reserved.
//
//      See the LICENCE file for the terms of distribution.



// Contributors: Dan MacDonald <dan@redknee.com>

package org.apache.log4j.net;

import java.net.InetAddress;
import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.AppenderSkeleton;

/**
    Sends {@link LoggingEvent} objects to a remote a log server,
    usually a {@link SocketNode}.

    <p>The SocketAppender has the following properties:

    <ul>

      <p><li>If sent to a {@link SocketNode}, remote logging is
      non-intrusive as far as the log event is concerned. In other
      words, the event will be logged with the same time stamp, {@link
      org.apache.log4j.NDC}, location info as if it were logged locally by
      the client.

      <p><li>SocketAppenders do not use a layout. They ship a
      serialized {@link LoggingEvent} object to the server side.

      <p><li>Remote logging uses the TCP protocol. Consequently, if
      the server is reachable, then log events will eventually arrive
      at the server.
    
      <p><li>If the remote server is down, the logging requests are
      simply dropped. However, if and when the server comes back up,
      then event transmission is resumed transparently. This
      transparent reconneciton is performed by a <em>connector</em>
      thread which periodically attempts to connect to the server.
    
      <p><li>Logging events are automatically <em>buffered</em> by the
      native TCP implementation. This means that if the link to server
      is slow but still faster than the rate of (log) event production
      by the client, the client will not be affected by the slow
      network connection. However, if the network connection is slower
      then the rate of event production, then the client can only
      progress at the network rate. In particular, if the network link
      to the the server is down, the client will be blocked.

      <p>On the other hand, if the network link is up, but the server
      is down, the client will not be blocked when making log requests
      but the log events will be lost due to server unavailability.

      <p><li>Even if a <code>SocketAppender</code> is no longer
      attached to any category, it will not be garbage collected in
      the presence of a connector thread. A connector thread exists
      only if the connection to the server is down. To avoid this
      garbage collection problem, you should {@link #close} the the
      <code>SocketAppender</code> explicitly. See also next item.

      <p>Long lived applications which create/destroy many
      <code>SocketAppender</code> instances should be aware of this
      garbage collection problem. Most other applications can safely
      ignore it.

      <p><li>If the JVM hosting the <code>SocketAppender</code> exits
      before the <code>SocketAppender</code> is closed either
      explicitly or subsequent to garbage collection, then there might
      be untransmitted data in the pipe which might be lost. This is a
      common problem on Windows based systems.

      <p>To avoid lost data, it is usually sufficient to {@link #close}
      the <code>SocketAppender</code> either explicitly or by calling
      the {@link Category#shutdown} method before exiting the
      application.  


     </ul>
    
    @author  Ceki G&uuml;lc&uuml;
    @since 0.8.4 */

public class SocketAppender extends AppenderSkeleton {

  InetAddress address;
  int port = 4560;
  String hostName;
  ObjectOutputStream oos;
  int reconnectionDelay = 30000;
  boolean locationInfo = false;

  private Connector connector;

  int counter = 0; 
  

  // reset the ObjectOutputStream every 70 calls
  //private static final int RESET_FREQUENCY = 70;
  private static final int RESET_FREQUENCY = 1;

  /**
     A string constant used in naming the option for setting the the
     host name of the remote server.  Current value of this string
     constant is <b>RemoteHost</b>. See the {@link #setOption} method
     for the meaning of this option.

  */
  public static final String REMOTE_HOST_OPTION = "RemoteHost";

 /**
     A string constant used in naming the option for setting the the
     port to contect on the remote server.  Current value of this string
     constant is <b>Port</b>.  See the {@link #setOption} method
     for the meaning of this option.

  */
  public static final String PORT_OPTION = "Port";

  /**
     A string constant used in naming the option for setting the the
     location information flag.  Current value of this string
     constant is <b>LocationInfo</b>.  See the {@link #setOption} method
     for the meaning of this option.

  */
  public static final String LOCATION_INFO_OPTION = "LocationInfo";

  /**
     A string constant used in naming the option for setting the delay
     between each reconneciton attempt to remote server.  Current
     value a of this string constant is <b>ReconnectionDelay</b>.  See
     the {@link #setOption} method for the meaning of this option.

  */
  public static final String RECONNECTION_DELAY_OPTION = "ReconnectionDelay";

  public SocketAppender() {
  }

  /**
     Connects to remote server at <code>address</code> and <code>port</code>.
  */
  public
  SocketAppender(InetAddress address, int port) {
    this.address = address;
    this.port = port;
    this.hostName = address.getHostName();
    connect(address, port);
  }

  /**
     Connects to remote server at <code>host</code> and <code>port</code>.
  */
  public
  SocketAppender(String host, int port) { 
    this.port = port;
    this.hostName = host;
    this.address = getAddressByName(host);
    connect(address, port);
  }

  /**
     Connect to the specified <b>RemoteHost</b> and <b>Port</b>. 
  */
  public
  void activateOptions() {
    connect(address, port);
  }

  /**
     Close this appender. 
     <p>This will mark the appender as closed and
     call then {@link #cleanUp} method.
  */
  public
  void close() {
    this.closed = true;
    cleanUp();
  }

  /**
     Drop the connection to the remote host and release the underlying
     connector thread if it has been created
   */
  public 
  void cleanUp() {
    if(oos != null) {
      try {
	oos.close();
      }
      catch(IOException e) {
	LogLog.error("Could not close oos.", e);
      }
      oos = null;      
    }
    if(connector != null) {
      //LogLog.debug("Interrupting the connector.");      
      connector.interrupt();
      connector = null;  // allow gc
    }
  }

  void connect(InetAddress address, int port) {
    if(this.address == null)
      return;
    try {
      // First, close the previous connection if any.
      cleanUp();          
      oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
    }
    catch(IOException e) {
      LogLog.error("Could not connect to remote log4j server at ["
		   +address.getHostName()+"]. We will try again later.", e);
      fireConnector();
    }
  }


  public
  void append(LoggingEvent event) {

    if(address==null) {
      errorHandler.error("No remote host is set for SocketAppedender named \""+
			this.name+"\".");
      return;
    }

    if(oos != null) {
      try {
	if(locationInfo) {
	   event.setLocationInformation();	
	} 
	oos.writeObject(event);
	//LogLog.debug("=========Flushing.");
	oos.flush();
	if(++counter >= RESET_FREQUENCY) {
	  counter = 0;
	  // Failing to reset the object output stream every now and
	  // then creates a serious memory leak.
	  //System.err.println("Doing oos.reset()");
	  oos.reset();
	}
      }
      catch(IOException e) {
	oos = null;
	LogLog.debug("Detected problem with connection: "+e);
	fireConnector();
      }
    }
  }

  void fireConnector() {
    if(connector == null) {
      LogLog.debug("Starting a new connector thread.");
      connector = new Connector();
      connector.setDaemon(true);
      connector.setPriority(Thread.MIN_PRIORITY);
      connector.start();      
    }
  }

  InetAddress getAddressByName(String host) {
    try {
      return InetAddress.getByName(host);
    }	
    catch(Exception e) {
      LogLog.error("Could not find address of ["+host+"].", e);
      return null;
    }
  }

  /**
     Retuns the option names for this component, namely the string
     array consisting of {{@link #REMOTE_HOST_OPTION}, {@link
     #PORT_OPTION}, {@link #LOCATION_INFO_OPTION}, {@link
     #RECONNECTION_DELAY_OPTION}} in addition to the options of its
     super class {@link AppenderSkeleton}.

    */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
                          new String[] {REMOTE_HOST_OPTION, PORT_OPTION, 
					LOCATION_INFO_OPTION,
					RECONNECTION_DELAY_OPTION});
  }


  /**
     The SocketAppender does not use a layout. Hence, this method returns
     <code>false</code>.
  */
  public
  boolean requiresLayout() {
    return false;
  }

  
  /**
     Set SocketAppender specific options.
     
     <p>On top of the options of the super class {@link
     AppenderSkeleton}, the recognized options are <b>RemoteHost</b>,
     <b>Port</b> and <b>ReconnectionDelay</b>, i.e. the values of the
     string constants {@link #REMOTE_HOST_OPTION}, {@link
     #PORT_OPTION},{@link #LOCATION_INFO_OPTION} and respectively {@link
     #RECONNECTION_DELAY_OPTION}.
     
     <p>The <b>RemoteHost</b> option takes a string value which should be
     the host name of the server where a {@link SocketNode} is running.

     <p>The <b>Port</b> option takes a positive integer representing
     the port where the server is waiting for connections.

     <p>The <b>LocationInfo</b> option takes a boolean value. If true,
     the information sent to the remote host will include location
     information. By default no location information is sent to the server.
     
     <p>The <b>ReconnectionDelay</b> option takes a positive integer
     representing the number of milliseconds to wait between each
     failed connection attempt to the server. The default value of
     this option is 30000 which corresponds to 30 seconds.
         
   */
  public
  void setOption(String option, String value) {
    if(value == null) return;
    super.setOption(option, value);    

    if(option.equals(REMOTE_HOST_OPTION)) 
      address = getAddressByName(value);
    else if (option.equals(PORT_OPTION))
      port = OptionConverter.toInt(value, port);
    else if (option.equals(LOCATION_INFO_OPTION))
      locationInfo = OptionConverter.toBoolean(value, locationInfo);    
    else if (option.equals(RECONNECTION_DELAY_OPTION))
      reconnectionDelay = OptionConverter.toInt(value, reconnectionDelay);
  }


  /**
     The Connector will reconnect when the server becomes available
     again.  It does this by attempting to open a new connection every
     <code>reconnectionDelay</code> milliseconds.

     <p>It stops trying whenever a connection is established. It will
     restart to try reconnect to the server when previpously open
     connection is droppped.

     @author  Ceki G&uuml;lc&uuml; 
     @since 0.8.4
  */
  class Connector extends Thread {

    public
    void run() {
      Socket socket;      
      while(!isInterrupted()) {
	try {
	  sleep(reconnectionDelay);
	  LogLog.debug("Attempting connection to "+address.getHostName());
	  socket = new Socket(address, port);
	  synchronized(this) {
	    oos = new ObjectOutputStream(socket.getOutputStream()); 
	    connector = null;
	    break;
	  }
	}
	catch(InterruptedException e) {
	  LogLog.debug("Connector interrupted. Leaving loop.");
	  return;
	}
	catch(java.net.ConnectException e) {
	  LogLog.debug("Remote host "+address.getHostName()
		       +" refused connection.");
	}
	catch(IOException e) {	  
	  LogLog.debug("Could not connect to " + address.getHostName()+
		       ". Exception is " + e);
	}
      }
      //LogLog.debug("Exiting Connector.run() method.");
    }
    
    /**
       public
       void finalize() {
       LogLog.debug("Connector finalize() has been called.");
       }
    */
  }

}
