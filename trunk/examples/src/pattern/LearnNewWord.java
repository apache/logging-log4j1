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

package pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.joran.JoranConfigurator;

/**
 * 
 * Example showing how to extend PatternLayout to recognize additional 
 * conversion words without through a configuration file.
 * 
 * <p>In this case have PatternLayout recognize %counter conversion word. 
 * It outputs the value of an internal counter which is also incremented at 
 * each call.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */

public class LearnNewWord {

  public static void main(String[] args) {
    if (args.length != 1) {
     System.err.println("Usage: java " + LearnNewWord.class.getName() +
        " configFile");
      
    }
    
    JoranConfigurator joran = new JoranConfigurator();
    
    joran.doConfigure(args[0], LogManager.getLoggerRepository());
    joran.dumpErrors();
    
    Logger logger = Logger.getLogger("some.cat");
    logger.debug("Hello, log");
    logger.info("Hello again...");
  }
}
