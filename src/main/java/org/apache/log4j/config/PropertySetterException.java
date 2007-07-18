/*
 * Copyright 1999-2006 The Apache Software Foundation.
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

package org.apache.log4j.config;

/**
 * Thrown when an error is encountered whilst attempting to set a property
 * using the {@link PropertySetter} utility class.
 * 
 * @author Anders Kristensen
 * @since 1.1
 */
public class PropertySetterException extends Exception {
  protected Throwable rootCause;
  
  public
  PropertySetterException(String msg) {
    super(msg);
  }
  
  public
  PropertySetterException(Throwable rootCause)
  {
    super();
    this.rootCause = rootCause;
  }
  
  /**
     Returns descriptive text on the cause of this exception.
   */
  public
  String getMessage() {
    String msg = super.getMessage();
    if (msg == null && rootCause != null) {
      msg = rootCause.getMessage();
    }
    return msg;
  }
}
