/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.plugins;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;


/**
  Defines the required interface for all Plugin objects.

  <p>A plugin implements some specific functionality to extend
  the log4j framework.  Each plugin is associated with a specific
  LoggerRepository, which it then uses/acts upon.  The functionality
  of the plugin is up to the developer.

  <p>Examples of plugins are Receiver and Watchdog. Receiver plugins
  allow for remote logging events to be received and processed by
  a repository as if the event was sent locally. Watchdog plugins
  allow for a repository to be reconfigured when some "watched"
  configuration data changes.

  @author Mark Womack
  @author Nicko Cadell
  @since 1.3
*/
public interface Plugin extends OptionHandler {
  /**
    Gets the name of the plugin.

    @return String the name of the plugin. */
  public String getName();

  /**
    Sets the name of the plugin.

    @param name the name of the plugin. */
  public void setName(String name);

  /**
    Gets the logger repository for this plugin.

    @return LoggerRepository the logger repository this plugin is
      attached to. */
  public LoggerRepository getLoggerRepository();

  /**
    Sets the logger repository used by this plugin. This
    repository will be used by the plugin functionality.

    @param repository the logger repository to attach this plugin to. */
  public void setLoggerRepository(LoggerRepository repository);

  /**
    True if the plugin is active and running.

    @return boolean true if the plugin is currently active. */
  public boolean isActive();

  /**
    Call when the plugin should be stopped. */
  public void shutdown();
}
