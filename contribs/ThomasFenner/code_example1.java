
// Here is a code example to configure the JDBCAppender with a configuration-file

import org.apache.log4j.*;
import java.sql.*;
import java.lang.*;
import java.util.*;

public class Log4JTest
{
   // Create a category instance for this class
   static Category cat = Category.getInstance(Log4JTest.class.getName());

   public static void main(String[] args)
   {
      // Ensure to have all necessary drivers installed !
      try
      {
         Driver d = (Driver)(Class.forName("oracle.jdbc.driver.OracleDriver").newInstance());
         DriverManager.registerDriver(d);
      }
      catch(Exception e){}

      // Set the priority which messages have to be logged
      cat.setPriority(Priority.INFO);

      // Configuration with configuration-file
      PropertyConfigurator.configure("log4jtestprops.txt");

      // These messages with Priority >= setted priority will be logged to the database.
      cat.debug("debug");  //this not, because Priority DEBUG is less than INFO
      cat.info("info");
      cat.error("error");
      cat.fatal("fatal");
   }
}

