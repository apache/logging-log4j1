/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.spi;


/**
 * A common interface shared by log4j components.
 * 
 * @author Ceki Gulcu
 * @since 1.3
 */
public interface Component {

  
  /** 
   * Set owning logger repository for this component. This operation can
   * only be performed once. Once set, the owning repository cannot be changed.
   *   
   * @param repository The repository where this appender is attached.
   * @throws IllegalStateException If you try to change the repository after it
   * has been set.
   **/
  public void setLoggerRepository(LoggerRepository repository);
 
}
