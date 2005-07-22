/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

package org.apache.log4j;
import java.io.File;


/**
 *
 * Test if WriterAppender honors the Appender contract.
 *
 * @author <a href="http://www.qos.ch/log4j/">Ceki G&uuml;lc&uuml;</a>
 * @author Curt Arnold
 */
public class FileAppenderTest extends AbstractAppenderTest {
  protected AppenderSkeleton getAppender() {
    return new FileAppender();
  }

  protected AppenderSkeleton getConfiguredAppender() {
    FileAppender wa = new FileAppender();
    wa.setFile("output/temp");
    wa.setLayout(new DummyLayout());
    return wa;
  }

  public void testPartiallyConfiguredAppender() {
    FileAppender wa1 = new FileAppender();
    wa1.setFile("output/temp");
    assertFalse(wa1.isActive());

    FileAppender wa2 = new FileAppender();
    wa2.setLayout(new DummyLayout());
    assertFalse(wa2.isActive());
  }

  /**
   * Tests that any necessary directories are attempted to
   * be created if they don't exist.  See bug 9150.
   *
   */
  public void testDirectoryCreation() {
      File newFile = new File("output/newdir/temp.log");
      newFile.delete();
      File newDir = new File("output/newdir");
      newDir.delete();

      FileAppender wa = new FileAppender();
      wa.setFile("output/newdir/temp.log");
      wa.setLayout(new DummyLayout());
      wa.activateOptions();

      assertTrue(new File("output/newdir/temp.log").exists());
  }
}
