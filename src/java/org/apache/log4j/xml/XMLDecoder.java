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

package org.apache.log4j.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Decoder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.UtilLoggingLevel;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Decodes Logging Events in XML formated into elements that are used by
 * Chainsaw.
 *
 * NOTE:  Only a single LoggingEvent is returned from the decode method
 * even though the DTD supports multiple events nested in an eventSet.
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class XMLDecoder implements Decoder {
  private static final String BEGINPART =
    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><!DOCTYPE log4j:eventSet SYSTEM \"log4j.dtd\"><log4j:eventSet version=\"1.2\" xmlns:log4j=\"http://jakarta.apache.org/log4j/\">";
  private static final String ENDPART = "</log4j:eventSet>";
  private static final String RECORD_END = "</log4j:event>";
  private StringBuffer buf = new StringBuffer();
  private DocumentBuilderFactory dbf;
  private DocumentBuilder docBuilder;
  private Map additionalProperties = Collections.EMPTY_MAP;
  private String partialEvent;
  
  public XMLDecoder() {
    dbf = DocumentBuilderFactory.newInstance();
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
   * @param additionalProperties
   */
  public void setAdditionalProperties(Map additionalProperties) {
    this.additionalProperties = additionalProperties;
  }

  /**
   * Converts the LoggingEvent data in XML string format into an actual
   * XML Document class instance.
   * @param data
   * @return
   */
  private Document parse(String data) {
    if (docBuilder == null) {
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
      buf = new StringBuffer(1024);

      buf.append(BEGINPART);
      buf.append(data);
      buf.append(ENDPART);

      InputSource inputSource =
        new InputSource(new StringReader(buf.toString()));
      inputSource.setSystemId("dummy://log4j.dtd");
      document = docBuilder.parse(inputSource);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return document;
  }

  /**
   * Reads the contents of the file into a String
   * @param file the file to load
   * @return The contents of the file as a String
   * @throws IOException if an error occurred during the loading process
   */
  private String loadFileSource(File file) throws IOException {
    LineNumberReader reader = null;
    StringBuffer buf = new StringBuffer(1024);

    try {
      reader = new LineNumberReader(new FileReader(file));

      String line = null;

      while ((line = reader.readLine()) != null) {
        buf.append(line);
      }
    } catch (IOException e) {
      throw e;
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception e) {
      }
    }

    return buf.toString();
  }

  /**
   * Decodes a File into a Vector of LoggingEvents
   * @param file the file to decode events from
   * @return Vector of LoggingEvents
   * @throws IOException
   */
  public Vector decode(File file) throws IOException {
    String fileContents = loadFileSource(file);
    Document doc = parse(fileContents);

    if (doc == null) {
      return null;
    }
    return decodeEvents(fileContents);
  }

  public Vector decodeEvents(String document) {
    if (document != null) {
      document = document.trim();
      if (document.equals("")) {
        return null;
      } else {
      	String newDoc=null;
      	String newPartialEvent=null;
      	//separate the string into the last portion ending with </log4j:event> (which will
      	//be processed) and the partial event which will be combined and processed in the next section

		//if the document does not contain a record end, append it to the partial event string
      	if (document.lastIndexOf(RECORD_END) == -1) {
			partialEvent = partialEvent + document;
      		return null;
      	}

      	if (document.lastIndexOf(RECORD_END) + RECORD_END.length() < document.length()) {
	      	newDoc = document.substring(0, document.lastIndexOf(RECORD_END) + RECORD_END.length());
			newPartialEvent = document.substring(document.lastIndexOf(RECORD_END) + RECORD_END.length());
     	} else {
      		newDoc = document;
      	}
		if (partialEvent != null) {
			newDoc=partialEvent + newDoc;
		}	      		
      	partialEvent=newPartialEvent;
      	
        Document doc = parse(newDoc);
        if (doc == null) {
          return null;
        }
        return decodeEvents(doc);
      }
    }
    return null;
  }

  /**
   * Converts the string data into an XML Document, and then soaks out the
   * relevant bits to form a new LoggingEvent instance which can be used
   * by any Log4j element locally.
   * @param data
   * @return a single LoggingEvent
   */
  public LoggingEvent decode(String data) {
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
   * Given a Document, converts the XML into a Vector of LoggingEvents
   * @param document
   * @return
   */
  private Vector decodeEvents(Document document) {
    Vector events = new Vector();

    Logger logger = null;
    long timeStamp = 0L;
    String level = null;
    String threadName = null;
    Object message = null;
    String ndc = null;
    Hashtable mdc = null;
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

      logger =
        Logger.getLogger(
          eventNode.getAttributes().getNamedItem("logger").getNodeValue());
      timeStamp =
        Long.parseLong(
          eventNode.getAttributes().getNamedItem("timestamp").getNodeValue());
      level =eventNode.getAttributes().getNamedItem("level").getNodeValue();
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

        if (tagName.equalsIgnoreCase("log4j:MDC")) {
          mdc = new Hashtable();

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
              mdc.put(name, value);
            }
          }
        }

        if (tagName.equalsIgnoreCase("log4j:throwable")) {
          exception = new String[] { getCData(list.item(y)) };
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

        /**
         * We add all the additional properties to the properties
         * hashtable
         */
        if (additionalProperties.size() > 0) {
          if (properties == null) {
            properties = new Hashtable(additionalProperties);
          } else {
            properties.putAll(additionalProperties);
          }
        }
      }
      Level levelImpl = null;
      if ((properties != null)  && (properties.get("log4j.eventtype") != null)) {
      	String s = (String)properties.get("log4j.eventtype");
		if (s.equalsIgnoreCase("util-logging")) {
			levelImpl=UtilLoggingLevel.toLevel(level);
		}
      }
      
      if (levelImpl==null) {
      	levelImpl=Level.toLevel(level);
      }
      		
      events.add(
        new LoggingEvent(
          logger.getName(), logger, timeStamp, levelImpl, threadName, message, ndc,
          mdc, exception,
          new LocationInfo(fileName, className, methodName, lineNumber),
          properties));
    }

    return events;
  }

  private String getCData(Node n) {
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
