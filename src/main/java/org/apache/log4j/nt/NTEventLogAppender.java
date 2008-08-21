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

package org.apache.log4j.nt;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
   Append to the NT event log system.

   <p><b>WARNING</b> This appender can only be installed and used on a
   Windows system.

   <p>Do not forget to place an NTEventLogAppender.dll
   or "NTEventLogAppender" + System.getProperty("os.arch") + ".dll"
   on the path to provide the native functions used by this appender
   or the appender will not function.

   @author <a href="mailto:cstaylor@pacbell.net">Chris Taylor</a>
   @author <a href="mailto:jim_cakalic@na.biomerieux.com">Jim Cakalic</a> */
public class NTEventLogAppender extends AppenderSkeleton {
  private int _handle = 0;
  private long handle64 = 0;

  private String source = null;
  private String server = null;


  public NTEventLogAppender() {
    this(null, null, null);
  }

  public NTEventLogAppender(String source) {
    this(null, source, null);
  }

  public NTEventLogAppender(String server, String source) {
    this(server, source, null);
  }

  public NTEventLogAppender(Layout layout) {
    this(null, null, layout);
  }

  public NTEventLogAppender(String source, Layout layout) {
    this(null, source, layout);
  }

  public NTEventLogAppender(String server, String source, Layout layout) {
    if (source == null) {
      source = "Log4j";
    }
    if (layout == null) {
      this.layout = new TTCCLayout();
    } else {
      this.layout = layout;
    }

    try {
      handle64 = registerEventSource64(server, source);
    } catch(UnsatisfiedLinkError le) {
        try {
			_handle = registerEventSource(server, source);
		} catch (Throwable e) {
			e.printStackTrace();
		}
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public
  void close() {
    // unregister ...
  }

  public
  void activateOptions() {
    if (source != null) {
      try {
          if (handle64 != 0) {
             deregisterEventSource64(handle64);
          } else if (_handle != 0) {
             deregisterEventSource(_handle);
        }
      } catch(Throwable t) {          
      }
      try {
         handle64 = registerEventSource64(server, source);
      } catch(UnsatisfiedLinkError le) {
          le.printStackTrace();
          try {
             _handle = registerEventSource(server, source);
          } catch (Throwable e) {
            LogLog.error("Could not register event source.", e);
          }
      } catch(Throwable e) {
          LogLog.error("Could not register event source.", e);
      }
    }
  }


  public void append(LoggingEvent event) {

    if (handle64 != 0 || _handle != 0) {
        StringBuffer sbuf = new StringBuffer();

        sbuf.append(layout.format(event));
        if(layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for(int i = 0; i < len; i++) {
                    sbuf.append(s[i]);
                }
            }
        }
        // Normalize the log message level into the supported categories
        int nt_category = event.getLevel().toInt();

        // Anything above FATAL or below DEBUG is labeled as INFO.
        //if (nt_category > FATAL || nt_category < DEBUG) {
        //  nt_category = INFO;
        //}
        try {
            if (handle64 != 0) {
                reportEvent64(handle64, sbuf.toString(), nt_category);
            } else {
                reportEvent(_handle, sbuf.toString(), nt_category);
            }
        } catch(Throwable ex) {
        }
    }
  }


  public
  void finalize() {
    try {
        if (handle64 != 0) {
            deregisterEventSource64(handle64);
            handle64 = 0;
        }
        if (_handle != 0) {
            deregisterEventSource(_handle);
            _handle = 0;
        }
    } catch(Throwable t) {
    }
  }

  /**
     The <b>Source</b> option which names the source of the event. The
     current value of this constant is <b>Source</b>.
   */
  public
  void setSource(String source) {
    this.source = source.trim();
  }

  public
  String getSource() {
    return source;
  }

/**
     The <code>NTEventLogAppender</code> requires a layout. Hence,
     this method always returns <code>true</code>. */
  public
  boolean requiresLayout() {
    return true;
  }

  native private int registerEventSource(String server, String source);
  native private void reportEvent(int handle, String message, int level);
  native private void deregisterEventSource(int handle);
  native private long registerEventSource64(String server, String source);
  native private void reportEvent64(long handle, String message, int level);
  native private void deregisterEventSource64(long handle);

  static {
    try {
        System.loadLibrary("NTEventLogAppender");
    } catch(UnsatisfiedLinkError e) {
        try {
            String archLib = "NTEventLogAppender" + System.getProperty("os.arch");
            try {
                System.loadLibrary(archLib);
            } catch(Throwable t) {
                LogLog.error("Unable to load NTEventLogAppender.dll or " + archLib + ".dll.", t);
            }
        } catch(Throwable t) {
            LogLog.error("Unable to load NTEventLogAppender.dll", e);
        }
    }

  }
}
