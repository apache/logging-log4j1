/*
 * Created on Feb 2, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.lbel;

import java.io.IOException;
import java.io.Reader;


/**
 * Decorate a reader by counting lines and colums.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
public class CountingReader extends Reader {
  private final Reader reader;
  private int lineNumber = 0;
  private int columnNumber = 0;
  private boolean lastCharacterWasR = false;
  
  CountingReader(Reader reader) {
    this.reader = reader;
  }

  public int read() throws IOException {
    int r = reader.read();
    if(r != -1) {
      switch(r) {
      case '\r':
        lastCharacterWasR = true;
        lineNumber++;
        columnNumber = 0;
        break;
      case '\n':
        if(!lastCharacterWasR) {
          lineNumber++;
          columnNumber = 0;
        }
        lastCharacterWasR = false;
        break;
      default: lastCharacterWasR = false;
               columnNumber++;
      }
    }
    return r;
  }
  
  public int read(char[] arg0, int arg1, int arg2) throws IOException {
    throw new  UnsupportedOperationException("char[] reading not supported.");
  }


  public void close() throws IOException {
     reader.close();  
  }

  public int getColumnNumber() {
    return columnNumber;
  }
  public int getLineNumber() {
    return lineNumber;
  }
}
