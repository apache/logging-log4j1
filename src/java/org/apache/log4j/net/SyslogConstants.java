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

package org.apache.log4j.net;


/**
 * Constants used by the {@link SyslogAppender} class.
 * @author Ceki G&uuml;lc&uuml;
 **/
public class SyslogConstants {
  // Following constants extracted from RFC 3164, we multipy them by 8
  // in order to precompute the facility part of PRI.
  // See RFC 3164, Section 4.1.1 for exact details.

  /** kernel messages, numerical code 0. */
  public static final int LOG_KERN = 0;
  /** user-level messages, numerical code 1. */
  public static final int LOG_USER = 1 << 3;
  /** mail system, numerical code 2. */
  public static final int LOG_MAIL = 2 << 3;
  /** system daemons, numerical code 3. */
  public static final int LOG_DAEMON = 3 << 3;
  /** security/authorization messages, numerical code 4. */
  public static final int LOG_AUTH = 4 << 3;
  /** messages generated internally by syslogd, numerical code 5. */
  public static final int LOG_SYSLOG = 5 << 3;
  /** line printer subsystem, numerical code 6. */
  public static final int LOG_LPR = 6 << 3;
  /** network news subsystem, numerical code 7. */
  public static final int LOG_NEWS = 7 << 3;
  /** UUCP subsystem, numerical code 8 */
  public static final int LOG_UUCP = 8 << 3;
  /** clock daemon, numerical code 9. */
  public static final int LOG_CRON = 9 << 3;
  /** security/authorization  messages, numerical code 10. */
  public static final int LOG_AUTHPRIV = 10 << 3;
  /** ftp daemon, numerical code 11. */
  public static final int LOG_FTP = 11 << 3;
  /** NTP subsystem, numerical code 12. */
  public static final int LOG_NTP = 12 << 3;
  /** log audit, numerical code 13. */
  public static final int LOG_AUDIT = 13 << 3;
  /** log alert, numerical code 14. */
  public static final int LOG_ALERT = 14 << 3;
  /** clock daemon, numerical code 15. */
  public static final int LOG_CLOCK = 15 << 3;
  /** reserved for local use, numerical code 16. */
  public static final int LOG_LOCAL0 = 16 << 3;
  /** reserved for local use, numerical code 17. */
  public static final int LOG_LOCAL1 = 17 << 3;
  /** reserved for local use, numerical code 18. */
  public static final int LOG_LOCAL2 = 18 << 3;
  /** reserved for local use, numerical code 19. */
  public static final int LOG_LOCAL3 = 19 << 3;
  /** reserved for local use, numerical code 20. */
  public static final int LOG_LOCAL4 = 20 << 3;
  /** reserved for local use, numerical code 21. */
  public static final int LOG_LOCAL5 = 21 << 3;
  /** reserved for local use, numerical code 22. */
  public static final int LOG_LOCAL6 = 22 << 3;
  /** reserved for local use, numerical code 23.*/
  public static final int LOG_LOCAL7 = 23 << 3;
}
