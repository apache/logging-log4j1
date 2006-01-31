
package wombat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Util {

 static public String foo(String name) {
   System.out.println("Util.foo() called");

   Context ctx = null;

    try {
      Initial ictx = new InitialContext();
      System.out.println("ctx type is "+ctx);
      return (String) lookup(ctx, name);      
    } catch (NamingException ne) {
      ne.printStackTrace();
      return null;
    }

 }


  static public String lookup(Context ctx, String name) {
    if(ctx == null) {
      return null;
    }
    try {
      return (String) ctx.lookup(name);
    } catch (NamingException ne) {
      System.out.println("Could not find ["+name+"]");
      ne.printStackTrace();
      return null;
    }
  }
}
