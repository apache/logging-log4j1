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
 * Compare the logger of an event passed as parameter to the logger name and 
 * comparison operator set in the constructor. 
 * 
 * <p>Allowed comparison operators are '=', '!=', '>', '>=', '<', '<=', '~' and
 * '!~' where '~' stands for regular expression match.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * @author Scott Deboy
 */
public class LoggerComparator implements Comparator {

  Operator operator;
  String rightSide;
  Pattern rightSidePattern;
  Perl5Matcher matcher;
  
  public LoggerComparator(Operator operator, String rightSide) throws ScanError  {
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
    
  }
  

  public boolean compare(LoggingEvent event) {
    if(operator.isRegex()) {
      boolean match = matcher.matches(event.getLoggerName(), rightSidePattern);
      if(operator.getCode() == Operator.REGEX_MATCH) {
        return match;
      } else {
        return !match;
      }
    }
    
    int compResult = rightSide.compareTo(event.getLoggerName());
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

}
