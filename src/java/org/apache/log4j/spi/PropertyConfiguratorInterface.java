package org.apache.log4j.spi;

import java.util.Properties;

/**
   Implemented by classes capable of configuring log4j using Java
   properties files.
   
   @since 0.9.2
   @author Anders Kristensen
 */
public interface PropertyConfiguratorInterface {
  /**
     Interprets the specified Properties map and configures
     log4j accordingly.
   */
  void doConfigure(Properties props);
}
