/*
 * Created on Aug 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.joran;

/**
 * @author ceki
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Rule {

  Pattern pattern;
  Action action;
  
  Rule(Pattern p, Action a) {
  	pattern = p;
  	action = a;
  }
}
