/*
 /*
 * Copyright 2006 The Apache Software Foundation.
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

package org.apache.log4j.concurrent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * Tests of FileAppender.
 */
public class FileAppenderTest extends TestCase {

  private Logger log = Logger.getLogger(FileAppender.class);

  private FileAppender appender = new FileAppender();

  private SimpleLayout layout = new SimpleLayout();

  private File f1;

  protected void setUp() throws Exception {
    f1 = File.createTempFile("FileAppenderTest", ".tmp");
    f1.deleteOnExit();
    appender.setLayout(layout);
    appender.activateOptions(); // won't work
    appender.setFile(f1.toString());
    log.addAppender(appender);
  }

  /**
   * Tests FileAppender methods and writing.
   */
  public void testBasic() throws Exception {
    assertEquals(false, appender.isActive());
    assertEquals(true, appender.getAppend());
    assertEquals(f1.toString(), appender.getFile());

    // Check and change default options
    assertEquals(true, appender.getBufferedIO());
    appender.setBufferedIO(false);
    assertEquals(false, appender.getBufferedIO());
    assertEquals(true, appender.getAppend());
    appender.setAppend(false);
    assertEquals(false, appender.getAppend());
    appender.setBufferSize(400);
    assertEquals(400, appender.getBufferSize());
    appender.activateOptions(); // works

    log.debug("HI");
    appender.close();
    readHI();
  }
  
  private void readHI() throws Exception {
	try{
		BufferedReader r = new BufferedReader(new FileReader(f1));
	    assertEquals("DEBUG - HI", r.readLine());
	}finally{
		r.close();
	}     
  }

  /**
   * Tests that FileAppender works with default options.
   */
  public void testDefaultOptions() throws Exception {
    appender.activateOptions();
    log.debug("HI");
    appender.close();
    readHI();
  }

}
