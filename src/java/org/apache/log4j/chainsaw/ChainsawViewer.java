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


/**
  Chainsaw compatible gui viewers must implement this interface
  in order to be opened and configured by the ChainsawAppender class.

  @author Mark Womack
*/
public interface ChainsawViewer {
  /**
    Called when the viewer should activate.

    @param model The ChainsawAppender model instance the viewer should use. */
  void activateViewer(ChainsawAppender model);
}
