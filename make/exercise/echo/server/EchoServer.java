

package exercise.echo.server;

import java.rmi.Naming; 
import java.rmi.RemoteException; 
import exercise.echo.Echo;
import java.rmi.RMISecurityManager; 
import java.rmi.server.UnicastRemoteObject; 


public class EchoServer extends UnicastRemoteObject implements Echo {

  public static void main(String[] argv) {
    if(argv.length != 1) {
      usage();
      return;
    }
    String serverURL = argv[0];
    
    try { 
      EchoServer server = new EchoServer(); 
      // Bind this object instance to the name "HelloServer"
      System.out.println("Attempting to bind to [" + serverURL + "]."); 
      Naming.rebind(serverURL, server); 
      System.out.println("EchoServer now bound in registry as [" + serverURL + "].");
      
    } catch (Exception e) { 
      System.out.println("EchoServer error: " + e.getMessage()); 
      e.printStackTrace(); 
    } 
  }

  public EchoServer() throws RemoteException {   
    super();
  } 
  
  private
  static
  void usage() {
     System.err.println("Usage: exercise.echo.client.EchoServer URL");     
  }

  public
  String reverse(String s) {
    StringBuffer buffer = new StringBuffer(s);
    return new String(buffer.reverse());
  }

}
