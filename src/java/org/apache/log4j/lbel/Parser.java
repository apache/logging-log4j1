package org.apache.log4j.lbel;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.lbel.comparator.ClassComparator;
import org.apache.log4j.lbel.comparator.LevelComparator;
import org.apache.log4j.lbel.comparator.LoggerComparator;
import org.apache.log4j.lbel.comparator.MessageComparator;
import org.apache.log4j.lbel.comparator.MethodComparator;
import org.apache.log4j.lbel.comparator.PropertyComparator;
import org.apache.log4j.lbel.comparator.TimestampComparator;

/**
 * 


 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
class Parser {

// The core of LBEL can be summarized by the following grammar.
  
//  <bexp> ::= <bexp> 'OR' <bterm>
//  <bexp> ::= <bterm>
//  <bterm> ::= <bterm> 'AND' <bfactor>
//  <bterm> ::= <bfactor>
//  <bfactor> ::= NOT <bfactor>
//  <bfactor> ::= '(' <bexp> ')'
//  <bfactor> ::= true
//  <bfactor> ::= false

// In reality <bfactor> takes more varied forms then just true|false but
// from a conceptual point of view, the variations are quite easy to deal with.

// By eliminating left-recursion, th above grammar can be transformed into the
// following LL(1) form. '#' stands for lambda, that is an empty sequence.
  
// <bexp> ::= <bterm> <bexpTail>
// <bexpTail> ::= 'OR' <bterm>
// <bexpTail> ::= #      
// <bterm> ::= <bfactor> <btermTail>
// <btermTail> ::= 'AND' <bfactor> <btermTail>
// <btermTail> ::= #
// <bfactor> ::= NOT <bfactor>
// <bfactor> ::= '(' <bexp> ')'
// <bfactor> ::= true
// <bfactor> ::= false

// Which is implemented almost directly by the following top-down parser.
  
	TokenStream ts;
	
	Parser(TokenStream bexpTS) {
	  ts = bexpTS;
	}
	
	Node parse()  throws IOException, ScanError {
		ts.next();
		return bexp();
	}
	
	Node bexp() throws IOException, ScanError {
		Node result;		
		Node bterm = bterm(); 
		Node bexpTail = bexpTail();
		if(bexpTail == null) {
			result = bterm;
		} else {
			result = bexpTail;
			result.setLeft(bterm);
		}
		return result;
	}
	
	Node bexpTail() throws IOException, ScanError {
    Token token = ts.getCurrent();
    switch(token.getType()) {
    case Token.OR:
    	ts.next();
    	Node or = new Node(Node.OR, "OR");
      
    	Node bterm = bterm();
      Node bexpTail = bexpTail();
      if(bexpTail == null) {
      	or.setRight(bterm);	
  		} else {
  			or.setRight(bexpTail);
  			bexpTail.setLeft(bterm);
  		}
      return or;
    default: 
    	return null;
    }		
	}
	
	Node bterm() throws IOException, ScanError {
		Node result;
		Node bfactor = bfactor(); 
		Node btermTail = btermTail();
		if(btermTail == null) {
			result = bfactor;
		} else {
			result = btermTail;
			btermTail.setLeft(bfactor);
		}
		return result;
	}

	Node btermTail() throws IOException, ScanError {
    Token token = ts.getCurrent();
    switch(token.getType()) {
    case Token.AND:
    	ts.next();
  	  Node and = new Node(Node.AND, "AND");
      Node bfactor = bfactor();
      Node btermTail = btermTail();
      if(btermTail == null) {
      	and.setRight(bfactor);
      } else {
      	and.setRight(btermTail);
      	btermTail.setLeft(bfactor);
      }
      return and;
    default: 
    	return null;
    }
	}
	
	Node bfactor() throws IOException, ScanError {
    Token token = ts.getCurrent();
    switch(token.getType()) {
    case Token.NOT:
    	ts.next();
      Node result = new Node(Node.NOT, "NOT");
      Node bsubfactor = bsubfactor();
      result.setLeft(bsubfactor);
      return result;
    default: 
      return bsubfactor();
    }
	}
	
	Node bsubfactor() throws IOException, ScanError {
    Token token = ts.getCurrent();
    Operator operator;
    String literal;
    
    switch(token.getType()) {
    case Token.TRUE:
    	ts.next();
      return new Node(Node.TRUE, "TRUE");
    case Token.FALSE:
    	ts.next();
      return new Node(Node.FALSE, "FALSE");
    case   Token.LP:
    	ts.next();
      Node result = bexp();
      Token token2 = ts.getCurrent();
      if(token2.getType() == Token.RP) {
      	ts.next();
      } else {
      	throw new IllegalStateException("Expected right parantheses but got" +token);
      }
      return result;
    case Token.LOGGER:
      ts.next();
      operator = getOperator();
      ts.next();
      literal = getLiteral();
      return new Node(Node.COMPARATOR, new LoggerComparator(operator, literal));
    case Token.LEVEL:
      ts.next();
      operator = getOperator();
      ts.next();
      int levelInt = getLevelInt();
      return new Node(Node.COMPARATOR, new LevelComparator(operator, levelInt));
    case Token.MESSAGE:
      ts.next();
      operator = getOperator();
      ts.next();
      literal = getLiteral();
      return new Node(Node.COMPARATOR, new MessageComparator(operator, literal));
    case Token.METHOD:
      ts.next();
      operator = getOperator();
      ts.next();
      literal = getLiteral();
      return new Node(Node.COMPARATOR, new MethodComparator(operator, literal));
    case Token.CLASS:
      ts.next();
      operator = getOperator();
      ts.next();
      literal = getLiteral();
      return new Node(Node.COMPARATOR, new ClassComparator(operator, literal));
    case Token.TIMESTAMP:
      ts.next();
      operator = getOperator();
      ts.next();
      return new Node(Node.COMPARATOR, new TimestampComparator(operator, getLong()));
    case Token.PROPERTY:
      ts.next();
      String key = (String) getPropertyKey();
      ts.next();
      operator = getOperator();
      ts.next();
      literal = getLiteral();
      return new Node(Node.COMPARATOR, new PropertyComparator(operator, key, literal));
    default: throw new IllegalStateException("Unexpected token " +token);
    
    }
 	}
  
  Operator getOperator() throws ScanError {
    Token token = ts.getCurrent();
    if(token.getType() == Token.OPERATOR) {
      String value = (String) token.getValue();
      if("=".equals(value)) {
        return new Operator(Operator.EQUAL);
      } else if("!=".equals(value)) {
        return new Operator(Operator.NOT_EQUAL);
      } else if(">".equals(value)) {
        return new Operator(Operator.GREATER);
      } else if(">=".equals(value)) {
        return new Operator(Operator.GREATER_OR_EQUAL);
      } else if("<".equals(value)) {
        return new Operator(Operator.LESS);
      } else if("<=".equals(value)) {
        return new Operator(Operator.LESS_OR_EQUAL);
      } else if("~".equals(value)) {
        return new Operator(Operator.REGEX_MATCH);
      } else if("!~".equals(value)) {
        return new Operator(Operator.NOT_REGEX_MATCH);
      } else if("childof".equals(value)) {
        return new Operator(Operator.CHILDOF);
      } else {
        throw new ScanError("Unknown operator type ["+value+"]");
      }
    } else {
      throw new ScanError("Expected operator token");
    }
  }
	
  String getLiteral() throws ScanError {
    Token token = ts.getCurrent();
    if(token.getType() == Token.LITERAL) {
      return (String) token.getValue();
    } else if(token.getType() == Token.NULL) {
      return null;
    } else {
      throw new ScanError("Expected LITERAL or NULL but got "+token);
    }
  }

  long getLong() throws ScanError {
    Token token = ts.getCurrent();
    if(token.getType() == Token.NUMBER) {
      Long l = (Long) token.getValue();
      return l.longValue();
    } else {
      throw new ScanError("Expected LITERAL but got "+token);
    }
  }
  
  int getLevelInt() throws ScanError {
    String levelStr = getLiteral();

    if("DEBUG".equalsIgnoreCase(levelStr)) {
      return Level.DEBUG_INT;
    } else if("INFO".equalsIgnoreCase(levelStr)) {
      return Level.INFO_INT;
    } else if("WARN".equalsIgnoreCase(levelStr)) {
      return Level.WARN_INT;
    } else if("ERROR".equalsIgnoreCase(levelStr)) {
      return Level.ERROR_INT;
    } else {
      throw new ScanError("Expected a level stirng got "+levelStr);
    }
  }
  
  String getPropertyKey() throws IOException, ScanError {
    Token token = ts.getCurrent();
    if(token.getType() == Token.DOT) {
      ts.next();
      Token token2 = ts.getCurrent();
      if(token2.getType() == Token.LITERAL) {
        return (String) token2.getValue();
      } else {
        throw new ScanError("Expected LITERAL but got "+token2);
      }
    } else {
      throw new ScanError("Expected '.' but got "+token);
    }
  }
}
