package wombat;

import org.apache.log4j.*;
import org.apache.log4j.spi.RootCategory;
import javax.servlet.http.*;
import javax.servlet.*;

public class Log4jInit extends HttpServlet {
  
 static Logger logger = Logger.getLogger(Log4jInit.class);
  public void init() { 
    logger.info("Logging initialized for Tata.");
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) {
    // nothing to do
  }
}

