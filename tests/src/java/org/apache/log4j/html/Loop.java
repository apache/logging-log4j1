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


package org.apache.log4j.html;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.util.Random;


/**
 * 
 * @author Ceki
 */
public class Loop {
  public static Logger logger = Logger.getLogger(Loop.class);

  public static void main(String[] args) {
    HTMLLayout htmlLayout = new HTMLLayout("%relative%thread%level%logger%m");
    //EnhancedHTMLLayout htmlLayout = new EnhancedHTMLLayout("%relative%thread%level%logger%m");
    FileAppender appender = new FileAppender();
    appender.setFile("toto.html");
    appender.setAppend(false);
    appender.setLayout(htmlLayout);
    appender.activateOptions();
    Logger root = Logger.getRootLogger();
    root.addAppender(appender);

    loop(200);
  }

  static void loop(int len) {
    Random random = new Random();
    for (int i = 0; i < len; i++) {
      int r = random.nextInt(200);
      if (r < 150) {
        logger.debug("message "+i);
      } else if (r < 180) {
        logger.info("some veryyyyyyyyyy loooooooooooooong informational message. Blah blah bla. Blah blah blah. " + i);
      } else if (r < 190) {
        logger.warn("some warning message " + i);
      } else {
        logger.error("some error  message " + i, new Exception("testing"));
      }
    }
  }
}
