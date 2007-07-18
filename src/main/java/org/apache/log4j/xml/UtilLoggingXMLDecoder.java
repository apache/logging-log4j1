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
import java.util.ArrayList;
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
import org.apache.log4j.helpers.UtilLoggingLevel;
import org.apache.log4j.spi.Decoder;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Decodes JDK 1.4's java.util.logging package events
 * delivered via XML (using the logger.dtd).
 *
 * @author Scott Deboy (sdeboy@apache.org)
 * @author Paul Smith (psmith@apache.org)
 * @since 1.3
 *
 */
public class UtilLoggingXMLDecoder implements Decoder {
  //NOTE: xml section is only handed on first delivery of events
  //on this first delivery of events, there is no end tag for the log element
  /**
   * Document prolog.
   */
  private static final String BEGIN_PART =
    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
    + "<!DOCTYPE log SYSTEM \"logger.dtd\"><log>";
    /**
     * Document close.
     */
  private static final String END_PART = "</log>";
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
     * Record end.
     */
  private static final String RECORD_END = "</record>";
    /**
     * Owner.
     */
  private Component owner = null;

    /**
     * Create new instance.
     * @param o owner
     */
  public UtilLoggingXMLDecoder(final Component o) {
      this();
      this.owner = o;
  }

    /**
     * Create new instance.
     */
  public UtilLoggingXMLDecoder() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);

    try {
      docBuilder = dbf.newDocumentBuilder();
      docBuilder.setErrorHandler(new SAXErrorHandler());
      docBuilder.setEntityResolver(new UtilLoggingEntityResolver());
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
    this.additionalProperties.putAll(properties);
  }

  /**
   * Converts the LoggingEvent data in XML string format into an actual
   * XML Document class instance.
   * @param data XML fragment
   * @return  XML document
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

      /**
       * resetting the length of the StringBuffer is dangerous, particularly
       * on some JDK 1.4 impls, there's a known Bug that causes a memory leak
       */
      StringBuffer buf = new StringBuffer(1024);

      if (!data.startsWith("<?xml")) {
        buf.append(BEGIN_PART);
      }

      buf.append(data);

      if (!data.endsWith(END_PART)) {
        buf.append(END_PART);
      }

      InputSource inputSource =
        new InputSource(new StringReader(buf.toString()));
      inputSource.setSystemId("dummy://logger.dtd");
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
    LineNumberReader reader = null;
    if (owner != null) {
        reader = new LineNumberReader(
                new InputStreamReader(
                        new ProgressMonitorInputStream(owner,
                                "Loading " + url , url.openStream())));
    } else {
        reader = new LineNumberReader(new InputStreamReader(url.openStream()));
    }
    Vector v = new Vector();

    String line = null;
    try {
        while ((line = reader.readLine()) != null) {
            StringBuffer buffer = new StringBuffer(line);
            for (int i = 0; i < 100; i++) {
                buffer.append(reader.readLine());
            }
            v.addAll(decodeEvents(buffer.toString()));
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

          if (document.equals("")) {
              return null;
          }

          String newDoc;
          String newPartialEvent = null;
          //separate the string into the last portion ending with </record>
          // (which willbe processed) and the partial event which
          // will be combined and processed in the next section

          //if the document does not contain a record end,
          // append it to the partial event string
          if (document.lastIndexOf(RECORD_END) == -1) {
              partialEvent = partialEvent + document;
              return null;
          }

          if (document.lastIndexOf(RECORD_END) + RECORD_END.length()
                  < document.length()) {
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
      * @return a single LoggingEvent or null
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
   * @return vector of logging events
   */
  private Vector decodeEvents(final Document document) {
    Vector events = new Vector();

    NodeList eventList = document.getElementsByTagName("record");

    for (int eventIndex = 0; eventIndex < eventList.getLength();
        eventIndex++) {
      Node eventNode = eventList.item(eventIndex);

      Logger logger = null;
      long timeStamp = 0L;
      Level level = null;
      String threadName = null;
      Object message = null;
      String ndc = null;
      String[] exception = null;
      String className = null;
      String methodName = null;
      String fileName = null;
      String lineNumber = null;
      Hashtable properties = new Hashtable();

      //format of date: 2003-05-04T11:04:52
      //ignore date or set as a property? using millis in constructor instead
      NodeList list = eventNode.getChildNodes();
      int listLength = list.getLength();

      if (listLength == 0) {
        continue;
      }

      for (int y = 0; y < listLength; y++) {
        String tagName = list.item(y).getNodeName();

        if (tagName.equalsIgnoreCase("logger")) {
          logger = Logger.getLogger(getCData(list.item(y)));
        }

        if (tagName.equalsIgnoreCase("millis")) {
          timeStamp = Long.parseLong(getCData(list.item(y)));
        }

        if (tagName.equalsIgnoreCase("level")) {
          level = UtilLoggingLevel.toLevel(getCData(list.item(y)));
        }

        if (tagName.equalsIgnoreCase("thread")) {
          threadName = getCData(list.item(y));
        }

        if (tagName.equalsIgnoreCase("sequence")) {
          properties.put("log4jid", getCData(list.item(y)));
        }

        if (tagName.equalsIgnoreCase("message")) {
          message = getCData(list.item(y));
        }

        if (tagName.equalsIgnoreCase("class")) {
          className = getCData(list.item(y));
        }

        if (tagName.equalsIgnoreCase("method")) {
          methodName = getCData(list.item(y));
        }

        if (tagName.equalsIgnoreCase("exception")) {
          ArrayList exceptionList = new ArrayList();
          NodeList exList = list.item(y).getChildNodes();
          int exlistLength = exList.getLength();

          for (int i2 = 0; i2 < exlistLength; i2++) {
            Node exNode = exList.item(i2);
            String exName = exList.item(i2).getNodeName();

            if (exName.equalsIgnoreCase("message")) {
              exceptionList.add(getCData(exList.item(i2)));
            }

            if (exName.equalsIgnoreCase("frame")) {
              NodeList exList2 = exNode.getChildNodes();
              int exlist2Length = exList2.getLength();

              for (int i3 = 0; i3 < exlist2Length; i3++) {
                exceptionList.add(getCData(exList2.item(i3)) + "\n");
              }
            }
          }

          exception =
            (String[]) exceptionList.toArray(new String[exceptionList.size()]);
        }
      }

      /**
       * We add all the additional properties to the properties
       * hashtable
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

      LocationInfo info = null;
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
      loggingEvent.setLevel(level);
      loggingEvent.setThreadName(threadName);
      loggingEvent.setMessage(message);
      loggingEvent.setNDC(ndc);
      loggingEvent.setThrowableInformation(new ThrowableInformation(exception));
      loggingEvent.setLocationInformation(info);
      loggingEvent.setProperties(properties);

      events.add(loggingEvent);

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
