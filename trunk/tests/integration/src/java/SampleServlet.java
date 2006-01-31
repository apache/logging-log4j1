import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggerRepository;

public class SampleServlet extends HttpServlet {

  public void getLogger1() throws Exception {

    Logger logger = Logger.getLogger(this.getClass().getName());
    
    LoggerRepository lr = logger.getLoggerRepository();
    if(lr == null) {
      throw new Exception("The LR should not be null");
    }
    
    if(!"test".equals(lr.getName())) {
      throw new Exception("The name of the returned LR should be 'test'"); 
    }
  }

  /**
   * This tests checks that an instance TestAppender named TEST could be
   * instanciated.
   *
   * <p>TestAppender is shipped part of the web-application.
   * */
  public void webappShippedAppender() throws Exception {

    Logger logger = Logger.getLogger(this.getClass().getName());
    
    LoggerRepository lr = logger.getLoggerRepository();

    Logger root = lr.getRootLogger();
    Appender appender = root.getAppender("TEST");
    if(appender == null) {
      throw new Exception("An appender named TEST should exist."); 
    }
  }


  public void exerciseSMPTPAppender() throws Exception {
    Logger logger = Logger.getLogger(this.getClass().getName());
    logger.error("testing");
  }

}

