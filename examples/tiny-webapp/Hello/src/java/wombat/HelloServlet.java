
package wombat;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.*;

public class HelloServlet extends HttpServlet {

  private Logger logger = Logger.getLogger(HelloServlet.class);

  public void init() throws ServletException {
    ServletContext context = getServletConfig().getServletContext();
    logger.info("Servlet loaded");
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    
    String name = request.getParameter("name");
    
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    
    logger.info("About to say hello to "+name);
 
    out.println("<HTML><BODY>");
    out.println("<H2> Hello there " + name + ". How are you today?</H2>");     
    out.println("</BODY></HTML>");
    out.close();    
  }
}
