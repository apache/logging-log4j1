/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.examples;

import org.apache.log4j.Category;
import org.apache.log4j.spi.CategoryFactory;

/**
   A factory that makes new {@link MyCategory} objects.

   See <b><a href="doc-files/MyCategoryFactory.java">source
   code</a></b> for more details.

   @author Ceki G&uuml;lc&uuml; */
public class MyCategoryFactory implements CategoryFactory {
  /**
     The constructor should be public as it will be called by
     configurators in different packages.  */
  public
  MyCategoryFactory() {
  }

  public
  Category makeNewCategoryInstance(String name) {
    return new MyCategory(name);
  }
}
