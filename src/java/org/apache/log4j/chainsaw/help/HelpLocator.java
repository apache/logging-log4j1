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

package org.apache.log4j.chainsaw.help;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.messages.MessageCenter;

/**
 * A helper class that assists the HelpManager by serving as a collection of
 * Class loaders based on URL roots.
 * 
 * @author Paul Smith
 *         <psmith@apache.org>
 */
class HelpLocator {
  private List classLoaders = new ArrayList();
  private static Logger logger = LogManager.getLogger(HelpLocator.class);
  HelpLocator() {
  }

  /**
   * Adds a ClassLoader to be used as a help resource locator
   */
  void installClassloaderLocator(ClassLoader cl) {
    classLoaders.add(cl);
  }
  /**
   * Adds a new locator to the current set of locators by using the passed in
   * URL as a base to locate help resources. The URL SHOULD end in a '/'
   * character.
   */
  void installLocator(URL url) {
    try {
      classLoaders.add(new HelpResourceLoader(url));
    } catch (Exception e) {
      MessageCenter.getInstance().getLogger().error(
        "Failed to setup the resoure loaders for the Help Subsystem");
    }
  }

  /**
   * Locates a help resource by using the internal resource locator collection.
   * 
   * @return URL of the located resource, or null if it cannot be located.
   */
  URL findResource(String name) {
    URL url = null;

    for (Iterator iter = classLoaders.iterator(); iter.hasNext();) {
      ClassLoader loader = (ClassLoader) iter.next();
      url = loader.getResource(name);

      if (url != null) {
        break;
      }
    }

    return url;
  }

  private static class HelpResourceLoader extends ClassLoader {
    private URL root;

    private HelpResourceLoader(URL root) {
      this.root = root;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    protected URL findResource(String name) {
      URL url = super.findResource(name);

      if (url != null) {
        return url;
      }

      try {
        URL resourceURL = new URL(root, name);
        URL[] urlArray = new URL[] { root, resourceURL };
        logger.debug("Looking for Help resource at:" + resourceURL.toExternalForm());
        logger.debug("urlArray=" + Arrays.asList(urlArray));
        return new URLClassLoader(
          urlArray).findResource(
          name);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }

      return null;
    }
  }

  //  public static void main(String[] args) throws Exception {
  //    HelpLocator locator = new HelpLocator();
  //    locator.installLocator(new File(".").toURL());
  //    locator.installLocator(new
  // URL("http://java.sun.com/j2se/1.4.2/docs/api/"));
  //    String[] resources =
  //      new String[] { "build.properties", "java/lang/ClassLoader.html", };
  //
  //    for (int i = 0; i < resources.length; i++) {
  //      String resource = resources[i];
  //      URL url = locator.findResource(resource);
  //      System.out.println("resource=" + resource + ", url=" + url);
  //    }
  //  }
}
