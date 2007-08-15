/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.awt.Component;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.ProgressMonitorInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Decoder;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Decodes Logging Events in XML formated into elements that are used by
 * Chainsaw.
 *
 * This decoder can process a collection of log4j:event nodes ONLY
 * (no XML declaration nor eventSet node)
 *
 * NOTE:  Only a single LoggingEvent is returned from the decode method
 * even though the DTD supports multiple events nested in an eventSet.
 *
 * @since 1.3
 *
 * @author Scott Deboy (sdeboy@apache.org)
 * @author Paul Smith (psmith@apache.org)
 *
 */
public class XMLDecoder implements Decoder {
    /**
     * Document prolog.
     */
  private static final String BEGINPART =
    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
    + "<!DOCTYPE log4j:eventSet SYSTEM \"http://localhost/log4j.dtd\">"
    + "<log4j:eventSet version=\"1.2\" "
    + "xmlns:log4j=\"http://jakarta.apache.org/log4j/\">";
    /**
     * Document close.
     */
  private static final String ENDPART = "</log4j:eventSet>";
    /**
     * Record end.
     */
  private static final String RECORD_END = "</log4j:event>";

    /**
     * Document builder.
     */
  private DocumentBuilder docBuilder;
    /**
     * Additional properties.
     */
  private Map additionalProperties = new HashMap();
    /**
     * Partial event.
     */
  private String partialEvent;
    /**
     * Owner.
     */
  private Component owner = null;

    /**
     * Create new instance.
     * @param o owner
     */
  public XMLDecoder(final Component o) {
      this();
      this.owner = o;
  }

    /**
     * Create new instance.
     */
   public XMLDecoder() {
    super();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);

    try {
      docBuilder = dbf.newDocumentBuilder();
      docBuilder.setErrorHandler(new SAXErrorHandler());
      docBuilder.setEntityResolver(new Log4jEntityResolver());
    } catch (ParserConfigurationException pce) {
      System.err.println("Unable to get document builder");
    }
  }

  /**
   * Sets an additionalProperty map, where each Key/Value pair is
   * automatically added to each LoggingEvent as it is decoded.
   *
   * This is useful, say, to include the source file name of the Logging events
   * @param properties additional properties
   */
  public void setAdditionalProperties(final Map properties) {
    this.additionalProperties = properties;
  }

  /**
   * Converts the LoggingEvent data in XML string format into an actual
   * XML Document class instance.
   * @param data XML fragment
   * @return dom document
   */
  private Document parse(final String data) {
    if (docBuilder == null || data == null) {
      return null;
    }
    Document document = null;

    try {
      // we change the system ID to a valid URI so that Crimson won't
      // complain. Indeed, "log4j.dtd" alone is not a valid URI which
      // causes Crimson to barf. The Log4jEntityResolver only cares
      // about the "log4j.dtd" ending.
      //      buf.setLength(0);

      /**
       * resetting the length of the StringBuffer is dangerous, particularly
       * on some JDK 1.4 impls, there's a known Bug that causes a memory leak
       */
      StringBuffer buf = new StringBuffer(1024);

      buf.append(BEGINPART);
      buf.append(data);
      buf.append(ENDPART);

      InputSource inputSource =
        new InputSource(new StringReader(buf.toString()));
      document = docBuilder.parse(inputSource);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document;
  }

  /**
   * Decodes a File into a Vector of LoggingEvents.
   * @param url the url of a file containing events to decode
   * @return Vector of LoggingEvents
   * @throws IOException if IO error during processing.
   */
  public Vector decode(final URL url) throws IOException {
    LineNumberReader reader;
    if (owner != null) {
      reader = new LineNumberReader(new InputStreamReader(
              new ProgressMonitorInputStream(owner,
                      "Loading " + url , url.openStream())));
    } else {
      reader = new LineNumberReader(new InputStreamReader(url.openStream()));
    }

    Vector v = new Vector();

    String line;
    Vector events;
    try {
        while ((line = reader.readLine()) != null) {
            StringBuffer buffer = new StringBuffer(line);
            for (int i = 0; i < 1000; i++) {
                buffer.append(reader.readLine()).append("\n");
            }
            events = decodeEvents(buffer.toString());
            if (events != null) {
                v.addAll(events);
            }
        }
    } finally {
      partialEvent = null;
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return v;
  }

    /**
     * Decodes a String representing a number of events into a
     * Vector of LoggingEvents.
     * @param document to decode events from
     * @return Vector of LoggingEvents
     */
  public Vector decodeEvents(final String document) {
    if (document != null) {
      if (document.trim().equals("")) {
        return null;
      }
        String newDoc = null;
        String newPartialEvent = null;
        //separate the string into the last portion ending with
        // </log4j:event> (which will be processed) and the
        // partial event which will be combined and
        // processed in the next section

        //if the document does not contain a record end,
        // append it to the partial event string
        if (document.lastIndexOf(RECORD_END) == -1) {
            partialEvent = partialEvent + document;
            return null;
        }

        if (document.lastIndexOf(RECORD_END)
                + RECORD_END.length() < document.length()) {
            newDoc = document.substring(0,
                    document.lastIndexOf(RECORD_END) + RECORD_END.length());
            newPartialEvent = document.substring(
                    document.lastIndexOf(RECORD_END) + RECORD_END.length());
        } else {
            newDoc = document;
        }
        if (partialEvent != null) {
            newDoc = partialEvent + newDoc;
        }
        partialEvent = newPartialEvent;
        Document doc = parse(newDoc);
        if (doc == null) {
            return null;
        }
        return decodeEvents(doc);
    }
    return null;
  }

  /**
   * Converts the string data into an XML Document, and then soaks out the
   * relevant bits to form a new LoggingEvent instance which can be used
   * by any Log4j element locally.
   * @param data XML fragment
   * @return a single LoggingEvent
   */
  public LoggingEvent decode(final String data) {
    Document document = parse(data);

    if (document == null) {
      return null;
    }

    Vector events = decodeEvents(document);

    if (events.size() > 0) {
      return (LoggingEvent) events.firstElement();
    }

    return null;
  }

  /**
   * Given a Document, converts the XML into a Vector of LoggingEvents.
   * @param document XML document
   * @return Vector of LoggingEvents
   */
  private Vector decodeEvents(final Document document) {
    Vector events = new Vector();

    Logger logger;
    long timeStamp;
    String level;
    String threadName;
    Object message = null;
    String ndc = null;
    String[] exception = null;
    String className = null;
    String methodName = null;
    String fileName = null;
    String lineNumber = null;
    Hashtable properties = null;

    NodeList nl = document.getElementsByTagName("log4j:eventSet");
    Node eventSet = nl.item(0);

    NodeList eventList = eventSet.getChildNodes();

    for (int eventIndex = 0; eventIndex < eventList.getLength();
        eventIndex++) {
        Node eventNode = eventList.item(eventIndex);
      //ignore carriage returns in xml
        if (eventNode.getNodeType() != Node.ELEMENT_NODE) {
            continue;
        }
        logger =
            Logger.getLogger(
               eventNode.getAttributes().getNamedItem("logger").getNodeValue());
      timeStamp =
        Long.parseLong(
          eventNode.getAttributes().getNamedItem("timestamp").getNodeValue());
      level = eventNode.getAttributes().getNamedItem("level").getNodeValue();
      threadName =
        eventNode.getAttributes().getNamedItem("thread").getNodeValue();

      NodeList list = eventNode.getChildNodes();
      int listLength = list.getLength();

      for (int y = 0; y < listLength; y++) {
        String tagName = list.item(y).getNodeName();

        if (tagName.equalsIgnoreCase("log4j:message")) {
          message = getCData(list.item(y));
        }

        if (tagName.equalsIgnoreCase("log4j:NDC")) {
          ndc = getCData(list.item(y));
        }
        //still support receiving of MDC and convert to properties
        if (tagName.equalsIgnoreCase("log4j:MDC")) {
          properties = new Hashtable();
          NodeList propertyList = list.item(y).getChildNodes();
          int propertyLength = propertyList.getLength();

          for (int i = 0; i < propertyLength; i++) {
            String propertyTag = propertyList.item(i).getNodeName();

            if (propertyTag.equalsIgnoreCase("log4j:data")) {
              Node property = propertyList.item(i);
              String name =
                property.getAttributes().getNamedItem("name").getNodeValue();
              String value =
                property.getAttributes().getNamedItem("value").getNodeValue();
              properties.put(name, value);
            }
          }
        }

        if (tagName.equalsIgnoreCase("log4j:throwable")) {
          exception = new String[] {
                  getCData(list.item(y))
          };
        }

        if (tagName.equalsIgnoreCase("log4j:locationinfo")) {
          className =
            list.item(y).getAttributes().getNamedItem("class").getNodeValue();
          methodName =
            list.item(y).getAttributes().getNamedItem("method").getNodeValue();
          fileName =
            list.item(y).getAttributes().getNamedItem("file").getNodeValue();
          lineNumber =
            list.item(y).getAttributes().getNamedItem("line").getNodeValue();
        }

        if (tagName.equalsIgnoreCase("log4j:properties")) {
          if (properties == null) {
              properties = new Hashtable();
          }
          NodeList propertyList = list.item(y).getChildNodes();
          int propertyLength = propertyList.getLength();

          for (int i = 0; i < propertyLength; i++) {
            String propertyTag = propertyList.item(i).getNodeName();

            if (propertyTag.equalsIgnoreCase("log4j:data")) {
              Node property = propertyList.item(i);
              String name =
                property.getAttributes().getNamedItem("name").getNodeValue();
              String value =
                property.getAttributes().getNamedItem("value").getNodeValue();
              properties.put(name, value);
            }
          }
        }

        /**
         * We add all the additional properties to the properties
         * hashtable.  Don't override properties that already exist
         */
        if (additionalProperties.size() > 0) {
          if (properties == null) {
            properties = new Hashtable(additionalProperties);
          } else {
            Iterator i = additionalProperties.entrySet().iterator();
            while (i.hasNext()) {
              Map.Entry e = (Map.Entry) i.next();
              if (!(properties.containsKey(e.getKey()))) {
                properties.put(e.getKey(), e.getValue());
              }
            }
          }
        }
      }
      Level levelImpl = Level.toLevel(level);

      LocationInfo info;
      if ((fileName != null)
              || (className != null)
              || (methodName != null)
              || (lineNumber != null)) {
          info = new LocationInfo(fileName, className, methodName, lineNumber);
      } else {
        info = LocationInfo.NA_LOCATION_INFO;
      }
      if (exception == null) {
          exception = new String[]{""};
      }

      LoggingEvent loggingEvent = new LoggingEvent();
      loggingEvent.setLogger(logger);
      loggingEvent.setTimeStamp(timeStamp);
      loggingEvent.setLevel(levelImpl);
      loggingEvent.setThreadName(threadName);
      loggingEvent.setMessage(message);
      loggingEvent.setNDC(ndc);
      loggingEvent.setThrowableInformation(new ThrowableInformation(exception));
      loggingEvent.setLocationInformation(info);
      loggingEvent.setProperties(properties);

      events.add(loggingEvent);

      message = null;
      ndc = null;
      exception = null;
      className = null;
      methodName = null;
      fileName = null;
      lineNumber = null;
      properties = null;
    }

    return events;
  }

    /**
     * Get contents of CDATASection.
     * @param n CDATASection
     * @return text content of all text or CDATA children of node.
     */
  private String getCData(final Node n) {
    StringBuffer buf = new StringBuffer();
    NodeList nl = n.getChildNodes();

    for (int x = 0; x < nl.getLength(); x++) {
      Node innerNode = nl.item(x);

      if (
        (innerNode.getNodeType() == Node.TEXT_NODE)
          || (innerNode.getNodeType() == Node.CDATA_SECTION_NODE)) {
        buf.append(innerNode.getNodeValue());
      }
    }

    return buf.toString();
  }
}
