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

package org.apache.log4j.helpers;


/**
   This class used to output log statements from within the log4j package.

   <p>Log4j components cannot make log4j logging calls. However, it is
   sometimes useful for the user to learn about what log4j is
   doing. You can enable log4j internal logging by defining the
   <b>log4j.configDebug</b> variable.

   <p>All log4j internal debug calls go to <code>System.out</code>
   where as internal error messages are sent to
   <code>System.err</code>. All internal messages are prepended with
   the string "log4j: ".

   @since 0.8.2
   @author Ceki G&uuml;lc&uuml;
*/
public class LogLog {
  /**
     Defining this value makes log4j print log4j-internal debug
     statements to <code>System.out</code>.

    <p> The value of this string is <b>log4j.debug</b>.

    <p>Note that the search for all option names is case sensitive.  */
  public static final String DEBUG_KEY = "log4j.debug";

  /**
     Defining this value makes log4j components print log4j-internal
     debug statements to <code>System.out</code>.

    <p> The value of this string is <b>log4j.configDebug</b>.

    <p>Note that the search for all option names is case sensitive.

    @deprecated Use {@link #DEBUG_KEY} instead.
  */
  public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
  protected static boolean debugEnabled = false;

  /**
     In quietMode not even errors generate any output.
   */
  private static boolean quietMode = false;
  private static final String PREFIX = "log4j: ";
  private static final String ERR_PREFIX = "log4j:ERROR ";
  private static final String INFO_PREFIX = "log4j:INFO ";
  private static final String WARN_PREFIX = "log4j:WARN ";

  static {
    String key = OptionConverter.getSystemProperty(DEBUG_KEY, null);

    if (key == null) {
      key = OptionConverter.getSystemProperty(CONFIG_DEBUG_KEY, null);
    }

    if (key != null) {
      debugEnabled = OptionConverter.toBoolean(key, true);
    }
  }

  /**
     Allows to enable/disable log4j internal logging.
   */
  public static void setInternalDebugging(boolean enabled) {
    debugEnabled = enabled;
  }

  /**
     This method is used to output log4j internal debug
     statements. Output goes to <code>System.out</code>.
  */
  public static void debug(String msg) {
    if (debugEnabled && !quietMode) {
      System.out.println(PREFIX + msg);
    }
  }

  public static void info(String msg) {
    if ( !quietMode) {
      System.out.println(INFO_PREFIX + msg);
    }
  }
  
  /**
     This method is used to output log4j internal debug
     statements. Output goes to <code>System.out</code>.
  */
  public static void debug(String msg, Throwable t) {
    if (debugEnabled && !quietMode) {
      System.out.println(PREFIX + msg);

      if (t != null) {
        t.printStackTrace(System.out);
      }
    }
  }

  /**
     This method is used to output log4j internal error
     statements. There is no way to disable error statements.
     Output goes to <code>System.err</code>.
  */
  public static void error(String msg) {
    if (quietMode) {
      return;
    }

    System.err.println(ERR_PREFIX + msg);
  }

  /**
     This method is used to output log4j internal error
     statements. There is no way to disable error statements.
     Output goes to <code>System.err</code>.
  */
  public static void error(String msg, Throwable t) {
    if (quietMode) {
      return;
    }

    System.err.println(ERR_PREFIX + msg);

    if (t != null) {
      t.printStackTrace();
    }
  }

  /**
     In quite mode no LogLog generates strictly no output, not even
     for errors.

     @param quietMode A true for not
  */
  public static void setQuietMode(boolean quietMode) {
    LogLog.quietMode = quietMode;
  }

  /**
     This method is used to output log4j internal warning
     statements. There is no way to disable warning statements.
     Output goes to <code>System.err</code>.  */
  public static void warn(String msg) {
    if (quietMode) {
      return;
    }

    System.err.println(WARN_PREFIX + msg);
  }

  /**
     This method is used to output log4j internal warnings. There is
     no way to disable warning statements.  Output goes to
     <code>System.err</code>.  */
  public static void warn(String msg, Throwable t) {
    if (quietMode) {
      return;
    }

    System.err.println(WARN_PREFIX + msg);

    if (t != null) {
      t.printStackTrace();
    }
  }
}
