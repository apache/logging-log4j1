package wombat;

import javax.servlet.http.*;
import javax.servlet.*;

public class InitServlet extends HttpServlet {
  
  public void init() { 
    //System.out.println("TATA wombat.InitServlet.init() called.");
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) {
    // nothing to do
  }
}

