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
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;


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
public class RollingFileAppender extends FileAppender {
  File activeFile;
  TriggeringPolicy triggeringPolicy;
  RollingPolicy rollingPolicy;

  /**
   * The default constructor simply calls its {@link
   * FileAppender#FileAppender parents constructor}.
   * */
  public RollingFileAppender() {
    super();
  }

  public void activate() {
    if (triggeringPolicy == null) {
      getLogger().warn("Please set a TriggeringPolicy for the RollingFileAppender named '{}'", getName());
      return;
    }

    if (rollingPolicy != null) {
      String afn = rollingPolicy.getActiveFileName();
      activeFile = new File(afn);
      getLogger().debug("Active log file name: "+afn);
      setFile(afn);
      
      // the activeFile variable is used by the triggeringPolicy.isTriggeringEvent method
      activeFile = new File(afn);
      super.activate();
    } else {
      getLogger().warn("Please set a rolling policy");
    }
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
    // Note: synchronization at this point is unnecessary as the doAppend 
    // is already synched
    
    //
    // make sure to close the hereto active log file! Renaming under windows
    // does not work for open files.
    this.closeWriter();    
    
    // By default, the newly created file will be created in truncate mode.
    // (See the setFile(fileName,...) call a few lines below.)
    boolean append = false;
    try { 
      rollingPolicy.rollover();
    } catch(RolloverFailure rf) {
      getLogger().warn("RolloverFailure occurred. Deferring rollover.");
      // we failed to rollover, let us not truncate and risk data loss
      append = true;
    }
    
    // Although not certain, the active file name may change after roll over.
    fileName = rollingPolicy.getActiveFileName();
    getLogger().debug("Active file name is now [{}].", fileName);

    // the activeFile variable is used by the triggeringPolicy.isTriggeringEvent method
    activeFile = new File(fileName);

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, append, bufferedIO, bufferSize);
    } catch (IOException e) {
      getLogger().error(
        "setFile(" + fileName + ", false) call failed.", e);
    }
  }

  /**
     This method differentiates RollingFileAppender from its super
     class.
  */
  protected void subAppend(LoggingEvent event) {
    // The rollover check must precede actual writing. This is the 
    // only correct behavior for time driven triggers. 
    if (triggeringPolicy.isTriggeringEvent(activeFile, event)) {
      getLogger().debug("About to rollover");
      rollover();
    }
      
    super.subAppend(event);
  }

  public RollingPolicy getRollingPolicy() {
    return rollingPolicy;
  }

  public TriggeringPolicy getTriggeringPolicy() {
    return triggeringPolicy;
  }

  /**
   * Sets the rolling policy. In case the 'policy' argument also implements
   * {@link TriggeringPolicy}, then the triggering policy for this appender
   * is automatically set to be the policy argument.
   * @param policy
   */
  public void setRollingPolicy(RollingPolicy policy) {
    rollingPolicy = policy;
    if(rollingPolicy instanceof TriggeringPolicy) {
      triggeringPolicy = (TriggeringPolicy) policy;
    }
    
  }

  public void setTriggeringPolicy(TriggeringPolicy policy) {
    triggeringPolicy = policy;
    if(policy instanceof RollingPolicy) {
      rollingPolicy = (RollingPolicy) policy;
    }
  }
}
