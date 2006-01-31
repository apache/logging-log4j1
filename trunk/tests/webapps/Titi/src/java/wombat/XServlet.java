
package wombat;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import wombat.Util;

public class XServlet extends HttpServlet {

  static final String MY_ENTRY = "java:comp/env/my-entry";

  public void init() throws ServletException {
    ServletContext context = getServletConfig().getServletContext();
  }


  public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    
    String result = Util.foo(MY_ENTRY);

    out.println(result);
    out.close();    
  }
}
