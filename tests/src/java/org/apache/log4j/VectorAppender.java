/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Vector;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.helpers.LogLog;

/**
   An appender that appends logging events to a vector.
   @author Ceki  G&uuml;lc&uuml;
*/
public class VectorAppender extends AppenderSkeleton {

  public Vector vector;
  
  public VectorAppender() {
    vector = new Vector();
  }

  /**
     Does nothing.
  */
  public void activateOptions() {
  }


  /**
     This method is called by the {@link AppenderSkeleton#doAppend}
     method.

  */
  public void append(LoggingEvent event) {
    //System.out.println("---Vector appender called with message ["+event.getRenderedMessage()+"].");
    //System.out.flush();
    try {
      Thread.currentThread().sleep(100);
    } catch(Exception e) {
    }
    vector.addElement(event);
   }

  public Vector getVector() {
    return vector;
  }

  public synchronized void close() {
    if(this.closed)
      return;
    this.closed = true;
  }


  public boolean isClosed() {
    return closed;
  }

  public boolean requiresLayout() {
    return false;
  }
}
