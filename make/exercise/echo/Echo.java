
package exercise.echo;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

public interface Echo extends Remote {
  String reverse(String s) throws RemoteException;
}
