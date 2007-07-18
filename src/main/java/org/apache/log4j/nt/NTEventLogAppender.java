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

package org.apache.log4j.nt;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
   Append to the NT event log system.

   <p><b>WARNING</b> This appender can only be installed and used on a
   Windows system.

   <p>Do not forget to place the file NTEventLogAppender.dll in a
   directory that is on the PATH of the Windows system. Otherwise, you
   will get a java.lang.UnsatisfiedLinkError.

   @author <a href="mailto:cstaylor@pacbell.net">Chris Taylor</a>
   @author <a href="mailto:jim_cakalic@na.biomerieux.com">Jim Cakalic</a> */
public class NTEventLogAppender extends AppenderSkeleton {

  static {
    System.loadLibrary("NTEventLogAppender");
  }

  private int _handle = 0;
  private boolean dirty = true;
  private String source = null;
  private String server = null;

  public NTEventLogAppender() {
    this(null, null, null);
  }

  public NTEventLogAppender(final String source) {
    this(null, source, null);
  }

  public NTEventLogAppender(final String server, final String source) {
    this(server, source, null);
  }

  public NTEventLogAppender(final Layout layout) {
    this(null, null, layout);
  }

  public NTEventLogAppender(final String source, final Layout layout) {
    this(null, source, layout);
  }

  public NTEventLogAppender(
    final String server, final String source, final Layout layout) {
    super(false);
    this.server = server;

    if (source == null) {
      this.source = "Log4j";
    }

    if (layout == null) {
      this.layout = new PatternLayout("%d [%t] %p %c %x %m%n");
    } else {
      this.layout = layout;
    }

    activateOptions();
  }

  public void close() {
    // unregister ...
  }

  public void activateOptions() {
    if (dirty && (source != null)) {
      dirty = false;

      if (_handle != 0) {
        try {
          deregisterEventSource(_handle);
        } catch (Exception e) {
          getLogger().error("Could not deregister event source.", e);
        }
      }

      try {
        _handle = registerEventSource(server, source);
        super.activateOptions();
      } catch (Exception e) {
        getLogger().error("Could not register event source.", e);
        _handle = 0;
      }
    }
  }

  public void append(final LoggingEvent event) {
    StringBuffer sbuf = new StringBuffer();

    sbuf.append(layout.format(event));

    if (layout.ignoresThrowable()) {
      String[] s = event.getThrowableStrRep();

      if (s != null) {
        int len = s.length;

        for (int i = 0; i < len; i++) {
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
    reportEvent(_handle, sbuf.toString(), nt_category);
  }

  public void finalize() {
    super.finalize();
    deregisterEventSource(_handle);
    _handle = 0;
  }

  /**
     The <b>Source</b> option which names the source of the event. The
     current value of this constant is <b>Source</b>.
   */
  public void setSource(final String source) {
    this.source = source.trim();
    dirty = true;
  }

  public String getSource() {
    return source;
  }

  /**
       The <code>NTEventLogAppender</code> requires a layout. Hence,
       this method always returns <code>true</code>. */
  public boolean requiresLayout() {
    return true;
  }

  private native int registerEventSource(String server, String source);

  private native void reportEvent(int handle, String message, int level);

  private native void deregisterEventSource(int handle);
}
