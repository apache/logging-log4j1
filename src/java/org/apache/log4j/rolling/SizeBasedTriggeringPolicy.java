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

package org.apache.log4j.rolling;

import java.io.File;


/**
 * @author Ceki G&uuml;lc&uuml;
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SizeBasedTriggeringPolicy implements TriggeringPolicy {
  long maxFileSize = 10 * 1024 * 1024; // let 10 MB the default max size

  public boolean isTriggeringEvent(File file) {
    //System.out.println("Size"+file.length());
    return (file.length() >= maxFileSize);
  }

  public long getMaxFileSize() {
    return maxFileSize;
  }

  public void setMaxFileSize(long l) {
    maxFileSize = l;
  }
  
  public void activateOptions() {
   
  }
}
