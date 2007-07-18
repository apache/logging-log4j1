package org.apache.log4j.spi;

import java.io.InputStream;

/**
 * Defines extended methods for Configurators to implement.
 *
 * @author Mark Womack
 * @since 1.3
 */
public interface ConfiguratorEx {
  /**
   * Configures using an InputStream for input.
   * 
   * @param stream
   * @param repository
   */
  public void doConfigure(InputStream stream, LoggerRepository repository);
}
