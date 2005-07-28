/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
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

import org.apache.log4j.LogManager;
import org.apache.log4j.joran.JoranConfigurator;
import org.apache.log4j.spi.LoggerRepository;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;

import javax.xml.parsers.SAXParser;


// Contributors:   Mark Womack
//                 Arun Katkere
//                 Curt Arnold

/**
   Use this class to initialize the log4j environment using a DOM tree.

   <p>The DTD is specified in <a
   href="log4j.dtd"><b>log4j.dtd</b></a>.

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

  /**
   *  Configure log4j using a <code>configuration</code> element.
   * @param element element, may not be null.
  */
  public static void configure(final Element element) {
    DOMConfigurator configurator = new DOMConfigurator();
    configurator.doConfigure(element, LogManager.getLoggerRepository());
  }

  /**
   *  Configure by taking in an DOM element.
   * @param element configuration element, may not be null.
   * @param repository logger repository.
  */
  public void doConfigure(
    final Element element, final LoggerRepository repository) {
    ParseAction action = new DOMElementParseAction(element);
    doConfigure(action, repository);
  }

  /**
   *  Class that "parses" a DOM element by replaying the
   * corresponding SAX events.
   */
  private static class DOMElementParseAction implements ParseAction {
    private final Element element;
    private final AttributesImpl attributes = new AttributesImpl();

    /**
     * Creates an DOMElementParser.
     * @param element configuration element.
     */
    public DOMElementParseAction(final Element element) {
      this.element = element;
    }

    /**
     * Generates the SAX events corresponding to the document element.
     * @param parser SAX parser, ignored.
     * @param handler content receiver, may not be null.
     * @throws SAXException thrown on content or handling exception.
     */
    public void parse(final SAXParser parser, final DefaultHandler handler)
      throws SAXException {
      handler.startDocument();
      replay(element, handler);
      handler.endDocument();
    }

    /**
     * Generates the SAX events corresponding to the element.
     *
     * @param element element, may not be null.
     * @param handler content handler, may not be null.
     * @throws SAXException if content error.
     */
    private void replay(final Element element, final DefaultHandler handler)
      throws SAXException {
      String localName = element.getLocalName();
      String nsURI = element.getNamespaceURI();
      String qName = element.getNodeName();

      if (localName == null) {
        localName = qName;
      }

      attributes.clear();

      NamedNodeMap attrNodes = element.getAttributes();
      int attrCount = attrNodes.getLength();
      Node attr;

      for (int i = 0; i < attrCount; i++) {
        attr = attrNodes.item(i);

        String attrQName = attr.getNodeName();
        String attrName = attr.getLocalName();

        if (attrName == null) {
          attrName = attrQName;
        }

        String attrNsURI = attr.getNamespaceURI();
        String attrValue = attr.getNodeValue();
        attributes.addAttribute(
          attrNsURI, attrName, attrQName, "#PCDATA", attrValue);
      }

      handler.startElement(nsURI, localName, qName, attributes);

      for (
        Node child = element.getFirstChild(); child != null;
          child = child.getNextSibling()) {
        //
        //   Joran only inteprets element content,
        //      so unnecessary to playback comments, character data, etc.
        //
        if (child.getNodeType() == Node.ELEMENT_NODE) {
          replay((Element) child, handler);
        }
      }

      handler.endElement(nsURI, localName, qName);
    }
  }
}
