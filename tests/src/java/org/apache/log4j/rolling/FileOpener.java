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

import java.io.FileInputStream;
import java.io.InputStream;


/**
 * Keep the file "output/test.log open for 10 seconds so that we can test
 * RollingFileAppender's ability to roll file open by another process.
 * @author Ceki G&uumllc&uuml;
 */
public class FileOpener {
  public static void main(String[] args) throws Exception {
    InputStream is = new FileInputStream("output/test.log");
    is.read();
    Thread.sleep(10000);
    is.close();
    System.out.println("Exiting FileOpener");
  }
}
