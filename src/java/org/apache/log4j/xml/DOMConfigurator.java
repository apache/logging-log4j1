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

package org.apache.log4j.xml;

import java.net.URL;

import org.apache.log4j.*;
import org.apache.log4j.joran.JoranConfigurator;


// Contributors:   Mark Womack
//                 Arun Katkere 

/**
   Use this class to initialize the log4j environment using a DOM tree.

   <p>The DTD is specified in <a
   href="doc-files/log4j.dtd"><b>log4j.dtd</b></a>.

   <p>Sometimes it is useful to see how log4j is reading configuration
   files. You can enable log4j internal logging by defining the
   <b>log4j.debug</b> variable on the java command
   line. Alternatively, set the <code>debug</code> attribute in the
   <code>log4j:configuration</code> element. As in
<pre>
   &lt;log4j:configuration <b>debug="true"</b> xmlns:log4j="http://jakarta.apache.org/log4j/">
   ...
   &lt;/log4j:configuration>
</pre>

   <p>There are sample XML files included in the package.

   @author Christopher Taylor
   @author Ceki G&uuml;lc&uuml;
   @author Anders Kristensen
   @deprecated Replaced by the much more flexible {@link org.apache.log4j.joran.JoranConfigurator}.
   @since 0.8.3 */
public class DOMConfigurator extends JoranConfigurator {
  
  public static void configure(String file) {
    JoranConfigurator joran = new JoranConfigurator();
    joran.doConfigure(file, LogManager.getLoggerRepository());
  }

  public static void configure(URL url) {
    JoranConfigurator joran = new JoranConfigurator();
    joran.doConfigure(url, LogManager.getLoggerRepository());
  }
}
