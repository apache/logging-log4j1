/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.log4j;

/**
 * <font color="#AA2222"><b>This class has been deprecated and replaced by the
 * {@link Logger}<em>subclass</em></b></font>. It will be kept around to
 * preserve backward compatibility until such time as the Log4j team sees fit
 * to remove it.
 *
 * <p>
 * <b>There is absolutely no need for new client code to use or refer to the
 * <code>Category</code> class.</b> Whenever possible, please avoid referring
 * to it or using it.
 * </p>
 * 
 * @deprecated Please use the {@link Logger} class instead.
 */
public class Category extends Logger {

  /**
   * Constructor.
   *
   * @param name The logger instance name
   */
  protected Category(String name) {
    super(name);
  }

}

// End of class: Category.java
