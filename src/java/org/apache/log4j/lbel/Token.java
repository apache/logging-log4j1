package org.apache.log4j.lbel;


public class Token {

	private int type;
	private Object value;
	
	public Token(int type) {
	  this(type, null);
	}
	
	public Token(int type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public String toString() {
		return "Token("+type+", "+value+")";
	}
	
	public int getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
}
