
package org.apache.joran.helper;

/**
 * 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class Option {

  static final String EMPTY_STR = "";
  
   static public boolean isEmpty(String val) {
     return (val == null || EMPTY_STR.equals(val));
   }

}
