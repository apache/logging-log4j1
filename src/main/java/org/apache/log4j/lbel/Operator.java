/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.log4j.lbel;


/**
 * <code>Operator</code> is an enumeration of known comparison operators.
 * 
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 */
final public class Operator {
  public static final int EQUAL = 1;
  public static final int NOT_EQUAL = 2;

  public static final int GREATER = 10;
  public static final int GREATER_OR_EQUAL = 11;
  
  public static final int LESS = 20;
  public static final int LESS_OR_EQUAL = 21;
  
  public static final int REGEX_MATCH = 30;
  public static final int NOT_REGEX_MATCH = 31;
  
  public static final int CHILDOF = 40;
  
  int code;

  Operator(int code) {
    this.code = code;
    
    switch(code) {
    case EQUAL: 
    case NOT_EQUAL: 
    case GREATER:    
    case GREATER_OR_EQUAL:    
    case LESS:    
    case LESS_OR_EQUAL: 
    case REGEX_MATCH: 
    case NOT_REGEX_MATCH:
    case CHILDOF:
      break;
    default: 
      new IllegalArgumentException("Unknown operator code ["+code+"]");
    }
  }

  public int getCode() {
    return code;
  }
  
  public boolean isRegex() {
    return (code == REGEX_MATCH) || (code == NOT_REGEX_MATCH);
  }
  
  public String toString() {
    switch(code) {
    case EQUAL: return "=";
    case NOT_EQUAL: return "!=";
    case GREATER:    return ">";
    case GREATER_OR_EQUAL: return ">=";   
    case LESS:    return "<";
    case LESS_OR_EQUAL: return "<=";
    case REGEX_MATCH: return "~";
    case NOT_REGEX_MATCH: return "!~";
    case CHILDOF: return "CHILDOF";
    }
    return "UNKNOWN_OPERATOR";
  }
  
}
