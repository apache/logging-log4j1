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

package org.apache.log4j.chainsaw.layout;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;


/**
 * Factory class to load and cache Layout information from resources.
 * 
 * @author Paul Smith <psmith@apache.org>
 */
public class DefaultLayoutFactory {
  private volatile static String defaultPatternLayout = null;

  private DefaultLayoutFactory() {
  }

  public static String getDefaultPatternLayout() {
    if (defaultPatternLayout == null) {
      StringBuffer content = new StringBuffer();
      URL defaultLayoutURL =
        DefaultLayoutFactory.class.getClassLoader().getResource(
          "org/apache/log4j/chainsaw/layout/DefaultDetailLayout.html");

      if (defaultLayoutURL == null) {
        LogLog.warn(
          "Could not locate the default Layout for Event Details and Tooltips");
      } else {
        try {
          BufferedReader reader = null;

          try {
            reader =
              new BufferedReader(
                new InputStreamReader(defaultLayoutURL.openStream()));

            String line = "";

            while ((line = reader.readLine()) != null) {
              content.append(line).append("\n");
            }
          } finally {
            if (reader != null) {
              reader.close();
            }
          }
        } catch (Exception e) {
          content = new StringBuffer(PatternLayout.TTCC_CONVERSION_PATTERN);
        }

        defaultPatternLayout = content.toString();
      }
    }

    return defaultPatternLayout;
  }
}
