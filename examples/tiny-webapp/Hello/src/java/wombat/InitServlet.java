package wombat;

import org.apache.log4j.*;
import org.apache.log4j.spi.RootCategory;
import javax.servlet.http.*;
import javax.servlet.*;

public class InitServlet extends HttpServlet {
  
 static Logger logger = Logger.getLogger(InitServlet.class);
  public void init() { 
    logger.info("Logging initialized for Hello.");
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) {
    // nothing to do
  }
}

