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

package org.apache.log4j.rolling;

import org.apache.log4j.FileAppender;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.rolling.helper.CompositeAction;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <code>RollingFileAppender</code> extends {@link FileAppender} to backup the log files
 * depending on {@link RollingPolicy} and {@link TriggeringPolicy}.
 * <p>
 * To be of any use, a <code>RollingFileAppender</code> instance must have both
 * a <code>RollingPolicy</code> and a <code>TriggeringPolicy</code> set up.
 * However, if its <code>RollingPolicy</code> also implements the
 * <code>TriggeringPolicy</code> interface, then only the former needs to be
 * set up. For example, {@link TimeBasedRollingPolicy} acts both as a
 * <code>RollingPolicy</code> and a <code>TriggeringPolicy</code>.
 *
 * <p><code>RollingFileAppender</code> can be configured programattically or
 * using {@link org.apache.log4j.joran.JoranConfigurator}. Here is a sample
 * configration file:

<pre>&lt;?xml version="1.0" encoding="UTF-8" ?>
&lt;!DOCTYPE log4j:configuration>

&lt;log4j:configuration debug="true">

  &lt;appender name="ROLL" class="org.apache.log4j.rolling.RollingFileAppender">
    <b>&lt;rollingPolicy class="org.apache.log4j.rolling.TimeBasedRollingPolicy">
      &lt;param name="FileNamePattern" value="/wombat/foo.%d{yyyy-MM}.gz"/>
    &lt;/rollingPolicy></b>

    &lt;layout class="org.apache.log4j.PatternLayout">
      &lt;param name="ConversionPattern" value="%c{1} - %m%n"/>
    &lt;/layout>
  &lt;/appender>

  &lt;root">
    &lt;appender-ref ref="ROLL"/>
  &lt;/root>

&lt;/log4j:configuration>
</pre>

 *<p>This configuration file specifies a monthly rollover schedule including
 * automatic compression of the archived files. See
 * {@link TimeBasedRollingPolicy} for more details.
 *
 * @author Heinz Richter
 * @author Ceki G&uuml;lc&uuml;
 * @since  1.3
 * */
public final class RollingFileAppender extends FileAppender {
  /**
   * Triggering policy.
   */
  private TriggeringPolicy triggeringPolicy;

  /**
   * Rolling policy.
   */
  private RollingPolicy rollingPolicy;

  /**
   * Length of current active log file.
   */
  private long fileLength = 0;

  /**
   * Thread for any asynchronous actions from last rollover.
   */
  private Thread rollingThread = null;

  /**
   * Construct a new instance.
   */
  public RollingFileAppender() {
  }

  /**
   * Prepare instance of use.
   */
  public void activateOptions() {
    if (rollingPolicy == null) {
      getLogger().warn(
        "Please set a rolling policy for the RollingFileAppender named '{}'",
        getName());
    }

    //
    //  if no explicit triggering policy and rolling policy is both.
    //
    if (
      (triggeringPolicy == null) && rollingPolicy instanceof TriggeringPolicy) {
      triggeringPolicy = (TriggeringPolicy) rollingPolicy;
    }

    if (triggeringPolicy == null) {
      getLogger().warn(
        "Please set a TriggeringPolicy for the RollingFileAppender named '{}'",
        getName());

      return;
    }

    IOException ioException = null;

    synchronized (this) {
      triggeringPolicy.activateOptions();
      rollingPolicy.activateOptions();

      StringBuffer activeFileName = new StringBuffer();
      List synchronousActions = new ArrayList();
      List asynchronousActions = new ArrayList();

      try {
        if (
          rollingPolicy.rollover(
              activeFileName, synchronousActions, asynchronousActions)) {
          performActions(synchronousActions, asynchronousActions);
        }

        String afn = activeFileName.toString();
        setFile(afn);

        File activeFile = new File(afn);

        if (getAppend()) {
          fileLength = activeFile.length();
        } else {
          fileLength = 0;
        }

        super.activateOptions();
      } catch (IOException ex) {
        ioException = ex;
      }
    }

    if (ioException != null) {
      getLogger().warn(
        "IOException while preparing while initializing RollingFileAppender named '"
        + getName() + "'", ioException);
    }
  }

  /**
   * Perform any actions specified by triggering policy.
   * @param synchronousActions list of Action instances to be performed after active file close.
   * @param asynchronousActions list of Action instances to be performed asynchronously after file close
   * and synchronous actions.
   * @return true if all synchronous actions were successful.
   * @throws IOException if IO error during synchronous actions.
   */
  private boolean performActions(
    final List synchronousActions, final List asynchronousActions)
    throws IOException {
    Iterator syncIterator = synchronousActions.iterator();

    while (syncIterator.hasNext()) {
      if (!((Action) syncIterator.next()).execute()) {
        return false;
      }
    }

    if (asynchronousActions.size() > 0) {
      Runnable action = null;

      if (asynchronousActions.size() > 1) {
        action = new CompositeAction(asynchronousActions, false, getLogger());
      } else {
        action = (Runnable) asynchronousActions.get(0);
      }

      rollingThread = new Thread(action);
      rollingThread.start();
    }

    return true;
  }

  /**
     Implements the usual roll over behaviour.

     <p>If <code>MaxBackupIndex</code> is positive, then files
     {<code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code>}
     are renamed to {<code>File.2</code>, ...,
     <code>File.MaxBackupIndex</code>}. Moreover, <code>File</code> is
     renamed <code>File.1</code> and closed. A new <code>File</code> is
     created to receive further log output.

     <p>If <code>MaxBackupIndex</code> is equal to zero, then the
     <code>File</code> is truncated with no backup files created.

   */
  public void rollover() {
    if (rollingPolicy != null) {
      Exception exception = null;

      synchronized (this) {
        try {
          //
          //  if we have some in-process compression, etc
          //     from the previous rollover, wait till they are finished.
          //
          if (rollingThread != null) {
            rollingThread.join();
            rollingThread = null;
          }

          StringBuffer activeFileName = new StringBuffer(super.getFile());
          List synchronousActions = new ArrayList();
          List asynchronousActions = new ArrayList();

          try {
            boolean doRollover =
              rollingPolicy.rollover(
                activeFileName, synchronousActions, asynchronousActions);

            if (doRollover) {
              String oldFileName = getFile();
              String newFileName = activeFileName.toString();

              //
              //  if the file names are the same then we
              //     have to close, do actions, then reopen
              if (newFileName.equals(oldFileName)) {
                closeWriter();

                try {
                  if (!performActions(synchronousActions, asynchronousActions)) {
                    throw new IOException(
                      "Unable to complete action after active file close.");
                  }
                } catch (IOException ex) {
                  setFile(oldFileName, true, bufferedIO, bufferSize);
                  throw ex;
                }

                fileLength = 0;
                setFile(newFileName, false, bufferedIO, bufferSize);
              } else {
                //
                //  if not the same, we can try opening new file before
                //     closing old file
                if (bufferedIO) {
                  setImmediateFlush(false);
                }

                Writer newWriter =
                  createWriter(new FileOutputStream(newFileName, false));
                closeWriter();

                try {
                  performActions(synchronousActions, asynchronousActions);
                  fileLength = 0;

                  if (bufferedIO) {
                    this.writer = new BufferedWriter(newWriter, bufferSize);
                  } else {
                    this.writer = newWriter;
                  }
                } catch (IOException ex) {
                  setFile(oldFileName, true, bufferedIO, bufferSize);
                  throw ex;
                }

                writeHeader();
              }
            }
          } catch (IOException ex) {
            exception = ex;
          }
        } catch (InterruptedException ex) {
          exception = ex;
        }
      }

      if (exception != null) {
        getLogger().warn(
          "Exception during rollover, rollover deferred.", exception);
      }
    }
  }

  /**
   * {@inheritDoc}
  */
  protected void subAppend(final LoggingEvent event) {
    // The rollover check must precede actual writing. This is the 
    // only correct behavior for time driven triggers. 
    if (
      triggeringPolicy.isTriggeringEvent(
          this, event, getFile(), getFileLength())) {
      rollover();
    }

    super.subAppend(event);
  }

  /**
   * Get rolling policy.
   * @return rolling policy.
   */
  public RollingPolicy getRollingPolicy() {
    return rollingPolicy;
  }

  /**
   * Get triggering policy.
   * @return triggering policy.
   */
  public TriggeringPolicy getTriggeringPolicy() {
    return triggeringPolicy;
  }

  /**
   * Sets the rolling policy.
   * @param policy rolling policy.
   */
  public void setRollingPolicy(final RollingPolicy policy) {
    rollingPolicy = policy;
  }

  /**
   * Set triggering policy.
   * @param policy triggering policy.
   */
  public void setTriggeringPolicy(final TriggeringPolicy policy) {
    triggeringPolicy = policy;
  }

  /**
   * Close appender.  Waits for any asynchronous file compression actions to be completed.
   */
  public void close() {
    if (rollingThread != null) {
      try {
        rollingThread.join();
      } catch (InterruptedException ex) {
        getLogger().info(
          "Interrupted while waiting for completion of rollover actions.", ex);
      }

      rollingThread = null;
    }

    super.close();
  }

  /**
     Returns an OutputStreamWriter when passed an OutputStream.  The
     encoding used will depend on the value of the
     <code>encoding</code> property.  If the encoding value is
     specified incorrectly the writer will be opened using the default
     system encoding (an error message will be printed to the loglog.
   @param os output stream, may not be null.
   @return new writer.
   */
  protected OutputStreamWriter createWriter(final OutputStream os) {
    return super.createWriter(new CountingOutputStream(os, this));
  }

  /**
   * Get byte length of current active log file.
   * @return byte length of current active log file.
   */
  public long getFileLength() {
    return fileLength;
  }

  /**
   * Increments estimated byte length of current active log file.
   * @param increment additional bytes written to log file.
   */
  public void incrementFileLength(int increment) {
    fileLength += increment;
  }

  /**
   * Wrapper for OutputStream that will report all write
   * operations back to this class for file length calculations.
   */
  private static class CountingOutputStream extends OutputStream {
    /**
     * Wrapped output stream.
     */
    private final OutputStream os;

    /**
     * Rolling file appender to inform of stream writes.
     */
    private final RollingFileAppender rfa;

    /**
     * Constructor.
     * @param os output stream to wrap.
     * @param rfa rolling file appender to inform.
     */
    public CountingOutputStream(
      final OutputStream os, final RollingFileAppender rfa) {
      this.os = os;
      this.rfa = rfa;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
      os.close();
    }

    /**
     * {@inheritDoc}
     */
    public void flush() throws IOException {
      os.flush();
    }

    /**
     * {@inheritDoc}
     */
    public void write(final byte[] b) throws IOException {
      os.write(b);
      rfa.incrementFileLength(b.length);
    }

    /**
     * {@inheritDoc}
     */
    public void write(final byte[] b, final int off, final int len)
      throws IOException {
      os.write(b, off, len);
      rfa.incrementFileLength(len);
    }

    /**
     * {@inheritDoc}
     */
    public void write(final int b) throws IOException {
      os.write(b);
      rfa.incrementFileLength(1);
    }
  }
}
