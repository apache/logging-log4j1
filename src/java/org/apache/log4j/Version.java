/*
 * Copyright 1999,2006 The Apache Software Foundation.
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

package org.apache.log4j;

import java.util.Properties;
import java.io.IOException;

/**
 * Provides methods for determining version information for the version of the
 * log4j library being used.  Callers can find the full version, milestone,
 * milestone version, and api version.  This information can be used in a
 * variety of ways.  For example, one could write code to ensure that only
 * "final" versions of the log4j library are used on production systems, or that
 * a certain api version is used.
 *
 * @author Mark Womack
 * @since 1.3
 */
public class Version {

  private static final String CONST_UNKNOWN = "UNKNOWN";

  private static String fullVersion       = CONST_UNKNOWN;
  private static String version           = CONST_UNKNOWN;
  private static String apiVersion        = CONST_UNKNOWN;
  private static String milestone         = CONST_UNKNOWN;
  private static String milestoneVersion  = CONST_UNKNOWN;

  static {
    loadVersionInfo();
  }

  /**
   * Returns the full version information for the version of
   * log4j being used.  The full version contains the major version,
   * milestone, and milestone version inf the following format:
   *
   * &lt;majorVersion&gt;-&lt;milestone&gt;[-&lt;milestoneVersion&gt;]
   *
   * The milestoneVersion information will not be included if
   * the version is a final release version.
   *
   * Examples are:
   *
   * "1.3-final"     - version string for the final 1.3 version
   * "1.3.3-final"   - version string for the final 1.3.3 version
   * "1.3.1-alpha-3" - version string for the alpha 3, 1.3.1 version
   * "1.3.2-beta-2"  - version string for the beta 2, 1.3.2 version
   * "1.3-rc-1"      - version string for the release candidate 1, 1.3 version

   * @return the full version string for the log4j library being used
   */
  public static String getFullVersion() {
    return fullVersion;
  }

  /**
   * Returns the version for the log4j library being used.
   *
   * @return the version for the log4j library being used
   */
  public static String getVersion() {
    return version;
  }

  /**
   * Returns the milestone for the log4j library being used.
   * Valid values are "alpha", "beta", "rc", and "final".
   *
   * @return the milestone for the log4j library being used
   */
  public static String getMilestone() {
    return milestone;
  }

  /**
   * Returns the milestone version for the log4j library being used.
   * If this value will be empty if the milestone is "final".
   *
   * @return the milestone version for the log4j library being used
   */
  public static String getMilestoneVersion() {
    return milestoneVersion;
  }

  /**
   * Returns the api version for the log4j library being used.  This
   * value can be different than the value returned by getVersion().  For
   * example, the version might be "1.3.1" but the api version will be "1.3".
   *
   * @return the api version for the log4j library being used
   */
  public static String getApiVersion() {
    return apiVersion;
  }

  /**
   * Loads the version information from a file in the resources.
   */
  private static void loadVersionInfo() {
    ClassLoader loader = Version.class.getClassLoader();
    Properties versionProps = new Properties();
    try {
      versionProps.load(loader.getResourceAsStream("org/apache/log4j/versionInfo"));
    } catch (IOException e) {
      // do nothing, version will remain UNKNOWN
    }

    version = versionProps.getProperty("version", CONST_UNKNOWN);
    apiVersion = versionProps.getProperty("apiVersion", CONST_UNKNOWN);
    milestone = versionProps.getProperty("milestone", CONST_UNKNOWN);
    milestoneVersion =
      versionProps.getProperty("milestoneVersion", CONST_UNKNOWN);
    fullVersion = version + "-" + milestone +
      ((milestoneVersion.equals(CONST_UNKNOWN) || milestoneVersion.length() == 0) ? "" :
                                         "-"+milestoneVersion);
  }
}
