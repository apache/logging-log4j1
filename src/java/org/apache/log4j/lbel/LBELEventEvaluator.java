/*
 * Created on Jan 27, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.lbel;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.lbel.comparator.Comparator;
import org.apache.log4j.spi.LoggingEvent;


/**
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 *
 */
public class LBELEventEvaluator implements EventEvaluator {

  private Node top;
  
  LBELEventEvaluator(String expression) throws ScanError {
    StringReader sr = new StringReader(expression);
    TokenStream ts = new TokenStream(sr);
    Parser parser = new Parser(ts);
    try {
      top = parser.parse(); 
    } catch(IOException ioe) {
      throw new ScanError("Unexpeted IOException thrown", ioe);
    }
  }

  public boolean evaluate(LoggingEvent event) {
    return evaluate(top, event);
  }

  void dumpSyntaxTree(String prefix) {
    top.leftFirstDump(prefix);
  }
  
  private boolean evaluate(Node node, LoggingEvent event) {
    int type = node.getType();
    boolean left;
    switch(type) {
    case Node.TRUE:
      return true;
    case Node.FALSE:
      return false;
    case Node.COMPARATOR:
      return ((Comparator) node.getValue()).compare(event);
    case Node.OR:
      left = evaluate(node.getLeft(), event);
      if(left == true) {
        return true;
      } else {
        return evaluate(node.getRight(), event);
      }
    case Node.AND:
      left = evaluate(node.getLeft(), event);
      if(left == false) {
        return false;
      } else {
        return evaluate(node.getRight(), event);
      }
    case Node.NOT:
      return !evaluate(node.getLeft(), event);
    }
    return false;
  }
  
}
