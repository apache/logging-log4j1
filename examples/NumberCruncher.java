
package org.apache.log4j.examples;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
   NumberCruncher's factor positive integers. See <a
   href=doc-files/NumberCruncher.java>source</a> code for more details.

   @author Ceki G&uuml;lc&uuml;
   
*/
public interface NumberCruncher extends Remote {

  /**
     Factor a positive integer <code>number</code> and return its
     <em>distinct</em> factor's as an integer array.
  */
  int[] factor(int number) throws RemoteException;
}
