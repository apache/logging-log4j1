
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


public class TestAppender extends AppenderSkeleton {

  public void activateOptions() {
    getLogger().debug("Activate options called for appender named {}.", 
                      getName());
  }

  public void append(LoggingEvent event) {
  }

  public void close() {    
  }
}
