package org.log4j.spi;

import java.net.URL;

/**
   Implemented by classes capable of configuring log4j using a URL.
   
   @since 0.9.2
   @author Anders Kristensen
   @author Ceki Gulcu
   
 */
public interface Configurator {
  /**
     Interprets the specified Properties map and configures
     log4j accordingly.
   */
  void doConfigure(URL url);
}
