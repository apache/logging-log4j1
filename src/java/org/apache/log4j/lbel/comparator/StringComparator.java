/*
 * Created on Jan 27, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.lbel.comparator;

import org.apache.log4j.lbel.Operator;
import org.apache.log4j.lbel.ScanError;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;


/**
 * Base class for string-based comparators. 
 * 
 * <p>Allowed comparison operators are 'CHILDOF', '=', '!=', '>', '>=', '<', 
 * '<=', '~' and '!~' where '~' stands for regular expression match.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * @author Scott Deboy
 */
abstract public class StringComparator implements Comparator {

  Operator operator;
  String rightSide;
  String rightSideWithDotSuffix;
  Pattern rightSidePattern;
  Perl5Matcher matcher;

  /**
   * Derived classes supply the left side of the comparison based on the event.
   * 
   * @param event
   * @return the left side of the expression
   */
  abstract protected String getLeftSide(LoggingEvent event);
  
  public StringComparator(Operator operator, String rightSide) throws ScanError  {
    this.operator = operator;
    this.rightSide = rightSide;
    
    
    if(operator.isRegex()) {
      Perl5Compiler compiler = new Perl5Compiler();
      matcher = new Perl5Matcher();
      try {
        rightSidePattern = compiler.compile(rightSide);
      } catch(MalformedPatternException mfpe) {
        throw new ScanError("Malformed pattern ["+rightSide+"]", mfpe);
      }
    }
    
    // if CHILDOF operator and rightSide does not end with a dot add one
    if(operator.getCode() == Operator.CHILDOF && !rightSide.endsWith(".")) {
      this.rightSideWithDotSuffix = rightSide + ".";
    }
  }
  

  public boolean compare(LoggingEvent event) {
    
    String leftSide = getLeftSide(event);
    
    if(operator.isRegex()) {
      boolean match = matcher.contains(leftSide, rightSidePattern);
      if(operator.getCode() == Operator.REGEX_MATCH) {
        return match;
      } else {
        return !match;
      }
    }

    if(operator.getCode() == Operator.CHILDOF) {
      if(leftSide.equals(rightSide)) {
        return true;
      } else {
        return leftSide.startsWith(rightSideWithDotSuffix);
      }
    }
  
    int compResult;

    if(leftSide == null) {
      // Assuming rightside can never be null, if leftSide == null, then it is
      // defined to be lexicographically smaller
      compResult = -1;
    } else {
      compResult = leftSide.compareTo(rightSide);
    }
    
    switch(operator.getCode()) {
    case Operator.EQUAL: return compResult == 0;   
    case Operator.NOT_EQUAL: return compResult != 0;      
    case Operator.GREATER: return compResult > 0;   
    case Operator.GREATER_OR_EQUAL: return compResult >= 0;   
    case Operator.LESS: return compResult < 0;   
    case Operator.LESS_OR_EQUAL: return compResult <= 0;    
    }
    
    throw new IllegalStateException("Unreachable state reached, operator "+operator);
  }

  
  public String toString() {
    String full = this.getClass().getName();
    int i = full.lastIndexOf(".");
    return full.substring(i)+"("+operator+", "+rightSide+")";
  }

}
