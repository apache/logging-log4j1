/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.spi;

import org.apache.log4j.Hierarchy;
import java.net.URL;

/**
   Implemented by classes capable of configuring log4j using a URL.
   
   @since 1.0
   @author Anders Kristensen
 */
public interface Configurator {
  /**
     Interpret a resource pointed by a URL and set up log4j accordingly.

     The configuration is done relative to the <code>hierarchy</code>
     parameter.

     @param url The URL to parse
     @param hierarchy The hierarchy to operation upon.
   */
  void doConfigure(URL url, Hierarchy hierarchy);
}
