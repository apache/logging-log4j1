
package exercise.echo.client;

import java.rmi.Naming; 
import java.rmi.RemoteException; 
import exercise.echo.Echo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
    
public class EchoClient { 


  public static void main(String[] argv) {
    
    if(argv.length != 1) {
      usage();
      return;
    }
    String serverURL = argv[0];
    
    Echo echo  = null; 	
    try { 
      echo = (Echo)Naming.lookup(argv[0]);
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      while(true) {
	System.out.print("Type a string: ");
	String s = in.readLine();
	String reply = echo.reverse(s); 
	System.out.println("The reply from " + serverURL + " is [" + reply + "].");
      }
    }
    catch (Exception e) { 
      System.out.println("X exception: " + e.getMessage()); 
      e.printStackTrace(); 
    } 
  }

  private
  static
  void usage() {
     System.err.println("Usage: exercise.echo.client.EchoClient SERVER_URL");     
  }
}

