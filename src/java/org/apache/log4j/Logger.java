/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j;

import org.apache.log4j.spi.RootCategory;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.or.ObjectRenderer;

import java.util.Enumeration;
import java.util.Vector;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.net.URL;
import java.net.MalformedURLException;


/**
  This is the central class in the log4j package. One of the
  distintive features of log4j are hierarchical categories and their
  evaluation.

  <p>See the <a href="../../../../manual.html">user manual</a> for an
  introduction on this class. 

  @author Ceki G&uuml;lc&uuml;
  @author Anders Kristensen */
public class Logger extends Category {

  protected 
  Logger(String name) {
    super(name);
  }
}
