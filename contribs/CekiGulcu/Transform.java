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

import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import org.apache.serialize.OutputFormat;
import org.apache.serialize.Serializer;
import org.apache.serialize.SerializerFactory;

import org.apache.trax.Processor;
import org.apache.trax.ProcessorException;
import org.apache.trax.ProcessorFactoryException;
import org.apache.trax.Result;
import org.apache.trax.Templates;
import org.apache.trax.TemplatesBuilder;
import org.apache.trax.TransformException;
import org.apache.trax.Transformer;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileOutputStream;
import java.io.IOException;


public class Transform {
  public static void main(String[] args) throws Exception {
    PropertyConfigurator.disableAll();
    PropertyConfigurator.configure("x.lcf");

    // I. Instantiate  a stylesheet processor.
    Processor processor = Processor.newInstance("xslt");

    // II. Process the stylesheet. producing a Templates object.
    // Get the XMLReader.
    XMLReader reader = XMLReaderFactory.createXMLReader();

    // Set the ContentHandler.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();
    reader.setContentHandler(templatesBuilder);

    // Set the ContentHandler to also function as a LexicalHandler, which
    // includes "lexical" (e.g., comments and CDATA) events. The Xalan
    // TemplatesBuilder -- org.apache.xalan.processor.StylesheetHandler -- is
    // also a LexicalHandler).
    if (templatesBuilder instanceof LexicalHandler) {
      reader.setProperty(
        "http://xml.org/sax/properties/lexical-handler", templatesBuilder);
    }

    // Parse the stylesheet.                       
    reader.parse(args[0]);

    //Get the Templates object from the ContentHandler.
    Templates templates = templatesBuilder.getTemplates();

    // III. Use the Templates object to instantiate a Transformer.
    Transformer transformer = templates.newTransformer();

    // IV. Perform the transformation.
    // Set up the ContentHandler for the output.
    FileOutputStream fos = new FileOutputStream(args[2]);
    Result result = new Result(fos);
    Serializer serializer = SerializerFactory.getSerializer("xml");
    serializer.setOutputStream(fos);

    transformer.setContentHandler(serializer.asContentHandler());

    // Set up the ContentHandler for the input.
    org.xml.sax.ContentHandler chandler = transformer.getInputContentHandler();
    DC dc = new DC(chandler);
    reader.setContentHandler(dc);

    if (chandler instanceof LexicalHandler) {
      reader.setProperty(
        "http://xml.org/sax/properties/lexical-handler", chandler);
    } else {
      reader.setProperty(
        "http://xml.org/sax/properties/lexical-handler", null);
    }

    // Parse the XML input document. The input ContentHandler and
    // output ContentHandler work in separate threads to optimize
    // performance.
    reader.parse(args[1]);
  }
}


class DC implements ContentHandler {
  static Category cat = Category.getInstance("DC");
  ContentHandler chandler;

  DC(ContentHandler chandler) {
    this.chandler = chandler;
  }

  public void characters(char[] ch, int start, int length)
    throws org.xml.sax.SAXException {
    cat.debug("characters: [" + new String(ch, start, length) + "] called");
    chandler.characters(ch, start, length);
  }

  public void endDocument() throws org.xml.sax.SAXException {
    cat.debug("endDocument called.");
    chandler.endDocument();
  }

  public void endElement(String namespaceURI, String localName, String qName)
    throws org.xml.sax.SAXException {
    cat.debug(
      "endElement(" + namespaceURI + ", " + localName + ", " + qName
      + ") called");
    chandler.endElement(namespaceURI, localName, qName);
  }

  public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException {
    cat.debug("endPrefixMapping(" + prefix + ") called");
    chandler.endPrefixMapping(prefix);
  }

  public void ignorableWhitespace(char[] ch, int start, int length)
    throws org.xml.sax.SAXException {
    cat.debug("ignorableWhitespace called");
    chandler.ignorableWhitespace(ch, start, length);
  }

  public void processingInstruction(
    java.lang.String target, java.lang.String data)
    throws org.xml.sax.SAXException {
    cat.debug("processingInstruction called");
    chandler.processingInstruction(target, data);
  }

  public void setDocumentLocator(Locator locator) {
    cat.debug("setDocumentLocator called");
    chandler.setDocumentLocator(locator);
  }

  public void skippedEntity(String name) throws org.xml.sax.SAXException {
    cat.debug("skippedEntity(" + name + ")  called");
    chandler.skippedEntity(name);
  }

  public void startDocument() throws org.xml.sax.SAXException {
    cat.debug("startDocument called");
    chandler.startDocument();
  }

  public void startElement(
    String namespaceURI, String localName, String qName, Attributes atts)
    throws org.xml.sax.SAXException {
    cat.debug(
      "startElement(" + namespaceURI + ", " + localName + ", " + qName
      + ")called");

    if ("log4j:event".equals(qName)) {
      cat.debug("-------------");

      if (atts instanceof org.xml.sax.helpers.AttributesImpl) {
        AttributesImpl ai = (AttributesImpl) atts;
        int i = atts.getIndex("timestamp");
        ai.setValue(i, "hello");
      }

      String ts = atts.getValue("timestamp");
      cat.debug("New timestamp is " + ts);
    }

    chandler.startElement(namespaceURI, localName, qName, atts);
  }

  public void startPrefixMapping(String prefix, String uri)
    throws org.xml.sax.SAXException {
    cat.debug("startPrefixMapping(" + prefix + ", " + uri + ") called");
    chandler.startPrefixMapping(prefix, uri);
  }
}
