/*
 * ============================================================================
 * The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "log4j" and
 * "Apache Software Foundation" must not be used to endorse or promote products
 * derived from this software without prior written permission. For written
 * permission, please contact apache@apache.org. 5. Products derived from this
 * software may not be called "Apache", nor may "Apache" appear in their name,
 * without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 *  
 */

package org.apache.log4j.chainsaw.help;

import org.apache.log4j.chainsaw.messages.MessageCenter;
import org.apache.log4j.helpers.LogLog;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A helper class that assists the HelpManager by serving as a collection of
 * Class loaders based on URL roots.
 * 
 * @author Paul Smith
 *         <psmith@apache.org>
 */
class HelpLocator {
  private List classLoaders = new ArrayList();

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
        LogLog.debug("Looking for Help resource at:" + resourceURL.toExternalForm());
        LogLog.debug("urlArray=" + Arrays.asList(urlArray));
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
