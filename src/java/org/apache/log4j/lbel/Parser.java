package org.apache.log4j.lbel;

import java.io.IOException;
import java.util.Stack;

/**
 * 


 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
public class Parser {

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
	Stack stack = new Stack();
	
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
    case TokenStream.OR:
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
    case TokenStream.AND:
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
    case TokenStream.NOT:
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

		Node result;
    Token token = ts.getCurrent();
    switch(token.getType()) {
    case TokenStream.TRUE:
    	ts.next();
      result = new Node(Node.TRUE, "TRUE");
      break;
    case TokenStream.FALSE:
    	ts.next();
      result = new Node(Node.FALSE, "FALSE");
      break;
    case   TokenStream.LP:
    	ts.next();
      result = bexp();
      Token token2 = ts.getCurrent();
      if(token2.getType() == TokenStream.RP) {
      	ts.next();
      } else {
      	throw new IllegalStateException("Expected right parantheses but got" +token);
      }
      break;
    default: throw new IllegalStateException("Excpected DIGIT but got" +token);
    }
    return result;
	}
	
	public boolean evaluate(Node node) {
		int type = node.getType();
		boolean left;
		switch(type) {
		case Node.TRUE:
			return true;
		case Node.FALSE:
			return false;
		case Node.OR:
			left = evaluate(node.getLeft());
		  if(left == true) {
		  	return true;
		  } else {
		  	return evaluate(node.getRight());
		  }
		case Node.AND:
		  left = evaluate(node.getLeft());
	    if(left == false) {
	  	  return false;
	    } else {
	  	  return evaluate(node.getRight());
	    }
    case Node.NOT:
    	return !evaluate(node.getLeft());
		}
		return false;
	}

}
