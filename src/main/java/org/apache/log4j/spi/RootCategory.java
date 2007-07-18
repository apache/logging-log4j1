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

package org.apache.log4j.spi;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

// Contibutors: Mathias Bogaert

/**
 * This class is deprecated and will be removed in future releases of log4j.
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @deprecated Replaced by {@link RootLogger}.  
 */
public final class RootCategory extends Logger {
  /**
     The root category names itself as "root". However, the root
     category cannot be retrieved by name.
  */
  public RootCategory(Level level) {
    super("root");
    setLevel(level);
  }

  /**
     Return the assigned level value without walking the category
     hierarchy.
  */
  public final Level getChainedLevel() {
    return level;
  }

  /**
     Setting a null value to the level of the root logger may have catastrophic
     results. We prevent this here.

     @since 0.8.3 */
  public final void setLevel(Level level) {
    if (level == null) {
      if (repository instanceof LoggerRepositoryEx) {
        ((LoggerRepositoryEx) repository).addErrorItem(
          new ErrorItem(
            "You have tried to set a null level to root.", new Exception()));
      }
    } else {
      this.level = level;
    }
  }

    final
    public
    void setPriority(Level level) {
      setLevel(level);
    }
    

}
