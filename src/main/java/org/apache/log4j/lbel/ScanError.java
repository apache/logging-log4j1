package org.apache.log4j.lbel;

public class ScanError extends Exception {
  
  Throwable cause;
  
  public ScanError(String msg) {
    super(msg);
  }

  public ScanError(String msg, Throwable rootCause) {
    super(msg);
    this.cause = rootCause;
  }

  public Throwable getCause() {
   return cause;
  }
}
