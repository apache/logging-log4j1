package org.apache.joran;

import org.xml.sax.Locator;


/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ErrorItem {
  String message;
  int colNumber;
  int lineNulber;
  Throwable exception;

  public ErrorItem(String message, Locator locator, Exception e) {
    this.message = message;

    if (locator != null) {
      colNumber = locator.getColumnNumber();
      lineNulber = locator.getLineNumber();
    }

    exception = e;
  }

  public ErrorItem(String message, Locator locator) {
    this(message, locator, null);
  }

  public int getColNumber() {
    return colNumber;
  }

  public void setColNumber(int colNumber) {
    this.colNumber = colNumber;
  }

  public Throwable getException() {
    return exception;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public int getLineNulber() {
    return lineNulber;
  }

  public void setLineNulber(int lineNulber) {
    this.lineNulber = lineNulber;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
