/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.spi;

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
