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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggerRepository;

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
    Logger logger = repository.getLogger("LOG4J");
    logger.setAdditivity(false);
    logger.addAppender(
      new ConsoleAppender(
        new PatternLayout("log4j-internal: %r %-22c{2} - %m%n")));
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
      // attempt to get the resource from the class path
      url = Loader.getResource(configuratonResourceStr);
    }

    // If we have a non-null url, then delegate the rest of the
    // configuration to the OptionConverter.selectAndConfigure
    // method.
    if (url != null) {
      LogLog.info(
        "Using URL [" + url 
          + "] for automatic log4j configuration of repository named ["+
          repository.getName()+"].");
      OptionConverter.selectAndConfigure(
        url, configuratorClassNameStr, repository);
    } else {
      LogLog.debug(
        "Could not find resources to perform automatic configuration.");
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
