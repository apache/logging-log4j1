//      Copyright 1996-1999, International Business Machines 
//      Corporation. All Rights Reserved.

package org.apache.log4j.performance;

import java.io.Writer;
import java.io.IOException;

/**
  Extends {@link Writer} with methods that return immediately without
  doing anything. This class is used to measure the cost of
  constructing a log message but not actually writing to the
  OutputStream.
   
  @author  Ceki G&uuml;lc&uuml;
*/
public class NOPWriter extends Writer {

  //public
  //NOPWriter() {
  //}
  

  public
  void write(char[] cbuf) throws IOException {}

  public
  void write(char[] cbuf, int off, int len) throws IOException {}


  public
  void write(int b) throws IOException {}

  public 
  void write(String s) throws IOException {} 

  public 
  void write(String s, int off, int len) throws IOException {} 

  public 
  void flush() throws IOException {
  }

  public 
  void close() throws IOException {
    System.err.println("Close called.");
  }
}
