import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

public class SampleServlet extends HttpServlet {

  public void getLogger1() throws Exception {

    Logger logger = Logger.getLogger(SampleServlet.class);
    
    LoggerRepository lr = logger.getLoggerRepository();
    if(lr == null) {
      throw new Exception("The LR should not be null");
    }
    
    if(!"test".equals(lr.getName())) {
      throw new Exception("The name of the returned LR should be 'test'"); 
    }

  }


}

