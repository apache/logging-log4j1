/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j;

import org.apache.log4j.spi.CategoryFactory;

class DefaultCategoryFactory implements CategoryFactory {
    
  private static final String DEFAULT_FQN = "org.apache.log4j.Category";

  DefaultCategoryFactory() {
  }    
    
  public
  Category makeNewCategoryInstance(String name) {
    return new Category(name, DEFAULT_FQN);
  }    
}
