package org.apache.log4j.lbel;


class Token {

	private int type;
	private Object value;
  public static final int TRUE = 1;
  public static final int FALSE = 2;
  public static final int OR    = 10;
  public static final int AND   = 11;
  public static final int NOT   = 12;
  public static final int LITERAL = 20;
  public static final int NUMBER  = 21;
  public static final int OPERATOR = 30;
  public static final int LP = 40;
  public static final int RP = 41;
  
  public static final int LOGGER  = 100;
  public static final int MESSAGE = 110;
  public static final int LEVEL   = 120;
  public static final int TIMESTAMP = 130;
  public static final int THREAD  = 140;
  public static final int PROPERTY = 150;
  public static final int DATE    = 160;

  public static final int CLASS  = 170;
  public static final int METHOD = 180;
  public static final int NULL   = 190;
  public static final int DOT   = 200;
  
  public static final int EOF    = 1000;
	
	public Token(int type) {
	  this(type, null);
	}
	
	public Token(int type, Object value) {
		this.type = type;
		this.value = value;
	}
	

	
	public int getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
  
  public String toString() {
    String typeStr = null;
    switch(type) {

    case TRUE: typeStr = "TRUE"; break;
    case FALSE: typeStr = "FALSE"; break;
    case OR: typeStr = "OR"; break;
    case AND: typeStr = "AND"; break;
    case NOT: typeStr = "NOT"; break;
    case LITERAL: typeStr = "IDENTIFIER"; break;
    case NUMBER: typeStr = "NUMBER"; break;
    case OPERATOR: typeStr = "OPERATOR"; break;
    case LP: typeStr = "LP"; break;
    case RP: typeStr = "RP"; break;
    case LOGGER: typeStr = "LOGGER"; break;
    case MESSAGE: typeStr = "MESSAGE"; break;
    case LEVEL: typeStr = "LEVEL"; break;
    case TIMESTAMP: typeStr = "TIMESTAMP"; break;
    case THREAD: typeStr = "THREAD"; break;
    case PROPERTY: typeStr = "PROPERTY"; break;
    case DATE: typeStr = "DATE"; break;
    case EOF: typeStr = "EOF"; break;
    default:  typeStr = "UNKNOWN";
    }
    return "Token("+typeStr +", " + value+")";
  }
}
