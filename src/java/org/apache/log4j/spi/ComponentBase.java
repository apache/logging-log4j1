/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.spi;

import org.apache.log4j.LogManager;
import org.apache.ugli.ULogger;


/**
 * Most log4j components derive from this class.
 * 
 * @author Ceki Gulcu
 * @since 1.3
 */
public class ComponentBase implements Component {

  protected LoggerRepository repository;
  private ULogger logger;

  /**
   * Set the owning repository. The owning repository cannot be set more than
   * once.
   */
  public void setLoggerRepository(LoggerRepository repository) {
    if(this.repository == null) {
      this.repository = repository;
    } else {
      throw new IllegalStateException("Repository has been already set");
    }
  }
  
  /**
   * Return an instance specific logger to be used by the component itself.
   * This logger is not intended to be used by Mrs. Piggy, our proverbial user,
   * hence the protected keyword.
   * 
   * <p>In case the repository for this components is not correctly set,
   * this implementations returns a {@link SimpleLogger} instance.
   * 
   * @return A ULogger instance.
   */
  protected ULogger getLogger() {
    if(logger == null) {
      if(repository != null) {
        logger = repository.getLogger(this.getClass().getName());
      } else {
        logger = LogManager.SIMPLE_LOGGER_FA.getLogger(this.getClass().getName());
      }
    }
    return logger;
  }
  
  
  
}
