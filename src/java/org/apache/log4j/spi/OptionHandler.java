/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */



package org.apache.log4j.spi;

/**
   A string based interface to configure package components.

   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen
   @since 0.8.1
 */
public interface OptionHandler {

  /**
     Activate the options that were previously set with calls to option
     setters.

     <p>This allows to defer activiation of the options until all
     options have been set. This is required for components which have
     related options that remain ambigous until all are set.

     <p>For example, the FileAppender has the
     {@link FileAppender#setFile File} and
     {@link FileAppender#setAppend Append} options both of which are
     ambigous until the other is also set.
  */
  void activateOptions();
}
