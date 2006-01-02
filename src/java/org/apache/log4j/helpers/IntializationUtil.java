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

package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggerRepositoryEx;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class groups certain internally used methods.
 *
 * @author Ceki Gulcu
 * @since 1.3
 */
public class IntializationUtil {


  public static void log4jInternalConfiguration(LoggerRepository repository) {
    // This method does not do anoything currently. It might become useful
    // when sub-domains are added to log4j.
    
//    Logger logger = repository.getLogger("LOG4J");
//    logger.setAdditivity(false);
//    logger.addAppender(
//      new ConsoleAppender(
//        new PatternLayout("log4j-internal: %r %-22c{2} - %m%n")));
  }

  /**
   * Configure <code>repository</code> using <code>configuratonResourceStr</code> 
   * and <code>configuratorClassNameStr</code>.  
   * 
   * If <code>configuratonResourceStr</code>  is not a URL it will be searched
   * as a resource from the classpath. 
   * 
   * @param repository The repository to configre
   * @param configuratonResourceStr URL to the configuration resourc
   * @param configuratorClassNameStr The name of the class to use as
   * the configrator. This parameter can be null.
   * */
  public static void initialConfiguration(LoggerRepository repository, 
                                          String configuratonResourceStr,
                                          String configuratorClassNameStr) {
                             
    if(configuratonResourceStr == null) {
      return;
    }
    URL url = null;

    try {
      url = new URL(configuratonResourceStr);
    } catch (MalformedURLException ex) {
      // so, resource is not a URL:
      // attempt to get the resource from the class loader path
      // Please refer to Loader.getResource documentation.
      url = Loader.getResource(configuratonResourceStr);
    }

    // If we have a non-null url, then delegate the rest of the
    // configuration to the OptionConverter.selectAndConfigure
    // method.
    if (url != null) {
      if (repository instanceof LoggerRepositoryEx) {
        LogLog.info(
            "Using URL [" + url
            + "] for automatic log4j configuration of repository named ["+
            ((LoggerRepositoryEx) repository).getName()+"].");
      } else {
          LogLog.info(
              "Using URL [" + url
              + "] for automatic log4j configuration of unnamed repository.");
      }

      OptionConverter.selectAndConfigure(url, configuratorClassNameStr, repository);
    }
  }

  /*
  public static void initialConfiguration(LoggerRepository repository) {
    String configurationOptionStr =
      OptionConverter.getSystemProperty(DEFAULT_CONFIGURATION_KEY, null);

    String configuratorClassName =
      OptionConverter.getSystemProperty(CONFIGURATOR_CLASS_KEY, null);

    URL url = null;

    // if the user has not specified the log4j.configuration
    // property, we search first for the file "log4j.xml" and then
    // "log4j.properties"
    if (configurationOptionStr == null) {
      url = Loader.getResource(DEFAULT_XML_CONFIGURATION_FILE);

      if (url == null) {
        url = Loader.getResource(DEFAULT_CONFIGURATION_FILE);
      }
    } else {
      try {
        url = new URL(configurationOptionStr);
      } catch (MalformedURLException ex) {
        // so, resource is not a URL:
        // attempt to get the resource from the class path
        url = Loader.getResource(configurationOptionStr);
      }
    }

    // If we have a non-null url, then delegate the rest of the
    // configuration to the OptionConverter.selectAndConfigure
    // method.
    if (url != null) {
      LogLog.debug(
        "Using URL [" + url + "] for automatic log4j configuration.");
      OptionConverter.selectAndConfigure(
        url, configuratorClassName, repository);
    } else {
      LogLog.debug(
        "Could not find resources to perform automatic configuration.");
    }
  }
  */
}
