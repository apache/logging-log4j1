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

package org.apache.log4j;

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;


/**
 * Implement this interface for your own strategies for outputting log
 * statements.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public interface Appender {
  /**
   * Add a filter to the end of the filter list.
   *
   * @since 0.9.0
   */
  void addFilter(Filter newFilter);

  /**
   * Returns the head Filter. The Filters are organized in a linked list and
   * so all Filters on this Appender are available through the result.
   *
   * @return the head Filter or null, if no Filters are present
   *
   * @since 1.1
   */
  public Filter getFilter();

  /**
   * Clear the list of filters by removing all the filters in it.
   *
   * @since 0.9.0
   */
  public void clearFilters();

  /**
   * Release any resources allocated within the appender such as file handles,
   * network connections, etc.
   * 
   * <p>
   * It is a programming error to append to a closed appender.
   * </p>
   *
   * @since 0.8.4
   */
  public void close();

  /**
   * Log in <code>Appender</code> specific way. When appropriate, Loggers will
   * call the <code>doAppend</code> method of appender implementations in
   * order to log.
   */
  public void doAppend(LoggingEvent event);

  /**
   * Get the name of this appender. The name uniquely identifies the appender.
   */
  public String getName();

  /**
   * Set the {@link ErrorHandler} for this appender.
   *
   * @since 0.9.0
   */
  public void setErrorHandler(ErrorHandler errorHandler);

  /**
   * Returns the {@link ErrorHandler} for this appender.
   *
   * @since 1.1
   */
  public ErrorHandler getErrorHandler();

  /**
   * Set the {@link Layout} for this appender.
   *
   * @since 0.8.1
   */
  public void setLayout(Layout layout);

  /**
   * Returns this appenders layout.
   *
   * @since 1.1
   */
  public Layout getLayout();

  /**
   * Set the name of this appender. The name is used by other components to
   * identify this appender.
   *
   * @since 0.8.1
   */
  public void setName(String name);

  /**
   * Configurators call this method to determine if the appender requires a
   * layout. If this method returns <code>true</code>, meaning that layout is
   * required, then the configurator will configure an layout using the
   * configuration information at its disposal.  If this method returns
   * <code>false</code>, meaning that a layout is not required, then layout
   * configuration will be skipped even if there is available layout
   * configuration information at the disposal of the configurator..
   * 
   * <p>
   * In the rather exceptional case, where the appender implementation admits
   * a layout but can also work without it, then the appender should return
   * <code>true</code>.
   * </p>
   *
   * @since 0.8.4
   */
  public boolean requiresLayout();
}
