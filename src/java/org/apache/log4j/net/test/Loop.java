
package org.log4j.net.test;

import org.log4j.*;
import org.log4j.net.SocketAppender;

public class Loop {

  public static void main(String[] args) {
    
    
    Category root = Category.getRoot();
    Category cat = Category.getInstance(Loop.class.getName());

    if(args.length != 2) 
      usage("Wrong number of arguments.");     

    String host = args[0];
    int port = 0;

    try {
      port = Integer.valueOf(args[1]).intValue();
    }
    catch (NumberFormatException e) {
        usage("Argument [" + args[1]  + "] is not in proper int form.");
    }

    SocketAppender sa = new SocketAppender(host, port);
    Layout layout = new PatternLayout("%5p [%t] %x %c - %m\n");
    FileAppender so = new FileAppender(layout, System.out);
    root.addAppender(sa);
    root.addAppender(so);

    int i = 0;

    while(true) {
      NDC.push(""+ (i++));
      cat.debug("Debug message.");
      root.info("Info message.");
      NDC.pop();
    }

  }

  static
  void  usage(String msg) {
    System.err.println(msg);
    System.err.println(
      "Usage: java " +Loop.class.getName() + " host port");
    System.exit(1);
  }
    

}
