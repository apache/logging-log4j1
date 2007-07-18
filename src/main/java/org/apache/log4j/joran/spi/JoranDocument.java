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

package org.apache.log4j.joran.spi;

import org.apache.log4j.spi.SimpleULogger;
import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.LoggerRepository;

import org.apache.log4j.ULogger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Collects all configuration significant elements from
 * an XML parse.
 *
 * @author Curt Arnold
 */
public final class JoranDocument extends DefaultHandler {
  public static final String LOG4J_NS = "http://jakarta.apache.org/log4j/";
  public static final String LS_NS = "http://logging.apache.org/";
  private final List errorList;
  private final List events = new ArrayList(20);
  private SAXParseException fatalError;
  private Locator location;
  private final LoggerRepository repository;

  public JoranDocument(final List errorList, LoggerRepository repository) {
    this.errorList = errorList;
    this.repository = repository;
  }

  public void error(final SAXParseException spe) {
    errorReport(spe);
  }

  public void fatalError(final SAXParseException spe) {
    if (fatalError == null) {
      fatalError = spe;
    }
    errorReport(spe);
  }

  public void warning(final SAXParseException spe) {
    errorReport(spe);
  }

  private void errorReport(final SAXParseException spe) {
    int line = spe.getLineNumber();
    ErrorItem errorItem = new ErrorItem("Parsing warning", spe);
    errorItem.setLineNumber(line);
    errorItem.setColNumber(spe.getColumnNumber());
    errorList.add(errorItem);
  }

  public void startElement(
    final String namespaceURI, final String localName, final String qName,
    final Attributes attributes) {
    if (
      (namespaceURI == null) || (namespaceURI.length() == 0)
        || namespaceURI.equals(LOG4J_NS) || namespaceURI.equals(LS_NS)) {
      events.add(new StartElementEvent(localName, location, attributes));
    }
  }

  public void endElement(
    final String namespaceURI, final String localName, final String qName) {
    if (
      (namespaceURI == null) || (namespaceURI.length() == 0)
        || namespaceURI.equals(LOG4J_NS) || namespaceURI.equals(LS_NS)) {
      events.add(new EndElementEvent(localName, location));
    }
  }

  public void replay(final ContentHandler handler) throws SAXException {
    if (fatalError != null) {
      throw fatalError;
    }
    LocatorImpl replayLocation = new LocatorImpl();
    handler.setDocumentLocator(replayLocation);
    for (Iterator iter = events.iterator(); iter.hasNext();) {
      ElementEvent event = (ElementEvent) iter.next();
      event.replay(handler, replayLocation);
    }
  }

  public InputSource resolveEntity(
    final String publicId, final String systemId) throws SAXException {
    //
    //   if log4j.dtd is requested then
    //       return an empty input source.
    //   We aren't validating and do not need anything from
    //       the dtd and do not want a failure if it isn't present.
    if ((systemId != null) && systemId.endsWith("log4j.dtd")) {
      getLogger().warn("The 'log4j.dtd' is no longer used nor needed.");
      getLogger().warn("See {}#log4j_dtd for more details.", Constants.CODES_HREF);
      return new InputSource(new ByteArrayInputStream(new byte[0]));
    }

    // If the systemId is not for us to handle, we delegate to our super
    // class, at leasts that's the basic idea. However, the code below
    // needs to be more complicated.
    // Due to inexplicable voodoo, the original resolveEntity method in 
    // org.xml.sax.helpers.DefaultHandler declares throwing an IOException, 
    // whereas the org.xml.sax.helpers.DefaultHandler class included in
    // JDK 1.4 masks this exception. In JDK 1.5, the IOException has been
    // put back...
    // In order to compile under JDK 1.4, we are forced to mask the IOException
    // as well. Since its signatures varies, we cannot call our super class' 
    // resolveEntity method. We are forced to implement the default behavior 
    // ourselves, which in this case, is just returning null.
    try {
      return super.resolveEntity(publicId, systemId);
    } catch (Exception e) {
      if (e instanceof SAXException) {
        throw (SAXException) e;
      } else if (e instanceof java.io.IOException) {
        // fall back to the default "implementation"
        getLogger().error("Default entity resolver threw an IOException", e);
        return null;
      } else {
        // This point should can never be reached.
        return null;
      }
    }
  }

  public void setDocumentLocator(Locator location) {
    this.location = location;
  }

  protected ULogger getLogger() {
    if (repository != null) {
      return repository.getLogger(this.getClass().getName());
    } else {
      return SimpleULogger.getLogger(this.getClass().getName());
    }
  }

  private abstract static class ElementEvent {
    private String localName;
    private Locator location;

    ElementEvent(final String localName, final Locator location) {
      this.localName = localName;
      if (location != null) {
        this.location = new LocatorImpl(location);
      }
    }

    public final String getLocalName() {
      return localName;
    }

    public void replay(
      final ContentHandler handler, final LocatorImpl replayLocation)
      throws SAXException {
      if (location != null) {
        replayLocation.setPublicId(location.getPublicId());
        replayLocation.setColumnNumber(location.getColumnNumber());
        replayLocation.setLineNumber(location.getLineNumber());
        replayLocation.setSystemId(location.getSystemId());
      }
    }
  }

  private static class EndElementEvent extends ElementEvent {
    public EndElementEvent(final String localName, final Locator location) {
      super(localName, location);
    }

    public void replay(
      final ContentHandler handler, final LocatorImpl replayLocation)
      throws SAXException {
      super.replay(handler, replayLocation);
      handler.endElement(
        JoranDocument.LOG4J_NS, getLocalName(), getLocalName());
    }
  }

  private static class StartElementEvent extends ElementEvent {
    private Attributes attributes;

    public StartElementEvent(
      final String localName, final Locator location,
      final Attributes attributes) {
      super(localName, location);
      this.attributes = new AttributesImpl(attributes);
    }

    public void replay(
      final ContentHandler handler, final LocatorImpl replayLocation)
      throws SAXException {
      super.replay(handler, replayLocation);
      handler.startElement(
        JoranDocument.LOG4J_NS, getLocalName(), getLocalName(), attributes);
    }
  }
}
