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

package org.apache.log4j.chainsaw.help;

import org.apache.log4j.LogManager;
import org.apache.log4j.chainsaw.Generator;
import org.apache.log4j.plugins.Plugin;
import org.apache.log4j.plugins.PluginRegistry;


/**
 * A runnable element that installs into the Log4j environment some fake Receivers
 * which generates events for use as a tutorial.
 *
 * @author Paul Smith
 */
public class Tutorial implements Runnable {
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
      Plugin p1 = new Generator("Generator 1");
      Plugin p2 = new Generator("Generator 2");
      Plugin p3 = new Generator("Generator 3");
      
      PluginRegistry pluginRegistry = LogManager.getLoggerRepository().getPluginRegistry();
      pluginRegistry.startPlugin(p1);
      pluginRegistry.startPlugin(p2);
      pluginRegistry.startPlugin(p3);
  }
}
