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

package org.apache.log4j.chainsaw;

import java.util.EventListener;


/**
 * Interested parties are notified when a MDC/Property key has arrived
 * that has not been seen before by the source Model
 * 
 * @author Paul Smith 
 */
public interface NewKeyListener extends EventListener {
	
	/**
	 * @param e the new key event being added
	 */
  public void newKeyAdded(NewKeyEvent e);
}
