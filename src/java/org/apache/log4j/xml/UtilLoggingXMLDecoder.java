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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Decodes JDK 1.4's java.util.logging package events delivered via XML (using the logger.dtd).
 *
 * @author Scott Deboy <sdeboy@apache.org>
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class UtilLoggingXMLDecoder implements Decoder {
  //NOTE: xml section is only handed on first delivery of events
  //on this first delivery of events, there is no end tag for the log element
  private static final String BEGIN_PART =
    "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><!DOCTYPE log SYSTEM \"logger.dtd\"><log>";
  private static final String END_PART= "</log>";
  private DocumentBuilderFactory dbf;
  private DocumentBuilder docBuilder;
  private Map additionalProperties = new HashMap();
  private String partialEvent;
  private static final String RECORD_END="</record>";

  public UtilLoggingXMLDecoder() {
    dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);

    try {
      docBuilder = dbf.newDocumentBuilder();
      docBuilder.setErrorHandler(new SAXErrorHandler());
      docBuilder.setEntityResolver(new UtilLoggingEntityResolver());
    } catch (ParserConfigurationException pce) {
      System.err.println("Unable to get document builder");
    }

    additionalProperties.put("log4j.eventtype", "util-logging");
  }

  /**
   * Sets an additionalProperty map, where each Key/Value pair is
   * automatically added to each LoggingEvent as it is decoded.
   *
   * This is useful, say, to include the source file name of the Logging events
   * @param additionalProperties
   */
  public void setAdditionalProperties(Map additionalProperties) {
    this.additionalProperties.putAll(additionalProperties);
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

      /**
       * resetting the length of the StringBuffer is dangerous, particularly
       * on some JDK 1.4 impls, there's a known Bug that causes a memory leak
       */
      if (data != null) {
        data = data.trim();
      }

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

    return decodeEvents(doc);
  }

  /**
   * Decodes a String with possibly multiple events into a Vector of LoggingEvents
   * @param String to decode events from
   * @return Vector of LoggingEvents
   */
  public Vector decodeEvents(String document) {
  	
    if (document != null) {
      document = document.trim();

      if (document.equals("")) {
        return null;
      } else {
      	String newDoc=null;
      	String newPartialEvent=null;
      	//separate the string into the last portion ending with </record> (which will
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
      Hashtable mdc = null;
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
          properties.putAll(additionalProperties);
        }
      }

      events.add(
        new LoggingEvent(
          logger.getName(), logger, timeStamp, level, threadName, message, ndc,
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
