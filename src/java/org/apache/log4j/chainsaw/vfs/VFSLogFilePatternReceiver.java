/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.log4j.chainsaw.vfs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.log4j.varia.LogFilePatternReceiver;

/**
 * A VFS-enabled version of org.apache.log4j.varia.LogFilePatternReceiver.
 * 
 * VFSLogFilePatternReceiver can parse and tail log files, converting entries into
 * LoggingEvents.  If the file doesn't exist when the receiver is initialized, the
 * receiver will look for the file once every 10 seconds.
 * <p>
 * This receiver relies on ORO Perl5 features to perform the parsing of text in the 
 * log file, however the only regular expression field explicitly supported is 
 * a glob-style wildcard used to ignore fields in the log file if needed.  All other
 * fields are parsed by using the supplied keywords.
 * <p>
 * <b>Features:</b><br>
 * - specify the URL of the log file to be processed<br>
 * - specify the timestamp format in the file (if one exists)<br>
 * - specify the pattern (logFormat) used in the log file using keywords, a wildcard character (*) and fixed text<br>
 * - 'tail' the file (allows the contents of the file to be continually read and new events processed)<br>
 * - supports the parsing of multi-line messages and exceptions
 *<p>
 * <b>Keywords:</b><br>
 * TIMESTAMP<br>
 * LOGGER<br>
 * LEVEL<br>
 * THREAD<br>
 * CLASS<br>
 * FILE<br>
 * LINE<br>
 * METHOD<br>
 * RELATIVETIME<br>
 * MESSAGE<br>
 * NDC<br>
 * PROP(key)<br>
 * <p>
 * Use a * to ignore portions of the log format that should be ignored
 * <p>
 * Example:<br>
 * If your file's patternlayout is this:<br>
 * <b>%d %-5p [%t] %C{2} (%F:%L) - %m%n</b>
 *<p>
 * specify this as the log format:<br>
 * <b>TIMESTAMP LEVEL [THREAD] CLASS (FILE:LINE) - MESSAGE</b>
 *<p>
 * To define a PROPERTY field, use PROP(key)
 * <p>
 * Example:<br> 
 * If you used the RELATIVETIME pattern layout character in the file, 
 * you can use PROP(RELATIVETIME) in the logFormat definition to assign 
 * the RELATIVETIME field as a property on the event.
 * <p>
 * If your file's patternlayout is this:<br>
 * <b>%r [%t] %-5p %c %x - %m%n</b>
 *<p>
 * specify this as the log format:<br>
 * <b>PROP(RELATIVETIME) [THREAD] LEVEL LOGGER * - MESSAGE</b>
 * <p>
 * Note the * - it can be used to ignore a single word or sequence of words in the log file
 * (in order for the wildcard to ignore a sequence of words, the text being ignored must be
 *  followed by some delimiter, like '-' or '[') - ndc is being ignored in this example.
 * <p>
 * Assign a filterExpression in order to only process events which match a filter.
 * If a filterExpression is not assigned, all events are processed.
 *<p>
 * <b>Limitations:</b><br>
 * - no support for the single-line version of throwable supported by patternlayout<br>
 *   (this version of throwable will be included as the last line of the message)<br>
 * - the relativetime patternLayout character must be set as a property: PROP(RELATIVETIME)<br>
 * - messages should appear as the last field of the logFormat because the variability in message content<br>
 * - exceptions are converted if the exception stack trace (other than the first line of the exception)<br>
 *   is stored in the log file with a tab followed by the word 'at' as the first characters in the line<br>
 * - tailing may fail if the file rolls over. 
 *<p>
 * <b>Example receiver configuration settings</b> (add these as params, specifying a LogFilePatternReceiver 'plugin'):<br>
 * param: "timestampFormat" value="yyyy-MM-d HH:mm:ss,SSS"<br>
 * param: "logFormat" value="RELATIVETIME [THREAD] LEVEL LOGGER * - MESSAGE"<br>
 * param: "fileURL" value="file:///c:/events.log"<br>
 * param: "tailing" value="true"
 *<p>
 * This configuration will be able to process these sample events:<br>
 * 710    [       Thread-0] DEBUG                   first.logger first - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test><br>
 * 880    [       Thread-2] DEBUG                   first.logger third - <test>   <test2>something here</test2>   <test3 blah=something/>   <test4>       <test5>something else</test5>   </test4></test><br>
 * 880    [       Thread-0] INFO                    first.logger first - infomsg-0<br>
 * java.lang.Exception: someexception-first<br>
 *     at Generator2.run(Generator2.java:102)<br>
 *
 *@author Scott Deboy
 */
public class VFSLogFilePatternReceiver extends LogFilePatternReceiver {

  private Reader reader;
  public VFSLogFilePatternReceiver() {
    super();
  }

  public void shutdown() {
    if (reader != null) {
      try {
        reader.close();
        reader = null;
      } catch (IOException ioe) {
        getLogger().warn("Unable to close reader", ioe);
      }
    }
  }
  
  /**
   * Read and process the log file.
   */
  public void activateOptions() {
    new Thread(new Runnable() {
      public void run() {
    initialize();
      while (reader == null) {
        getLogger().info("attempting to load file: " + getFileURL());
        try {
          FileSystemManager fileSystemManager = (StandardFileSystemManager) VFS.getManager();
          FileObject fileObject = fileSystemManager.resolveFile(getFileURL());
          reader = new InputStreamReader(fileObject.getContent().getInputStream());
        } catch (FileSystemException fse) {
          getLogger().info("file not available - will try again in 10 seconds");
          synchronized(this) {
            try {
              wait(10000);
            } catch (InterruptedException ie){}
          }
      } 
      }
      try {
        process(reader);
      } catch (IOException ioe) {
        getLogger().info("stream closed");
      }
      }}).start();
   }
}