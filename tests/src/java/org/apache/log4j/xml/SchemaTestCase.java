/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Tests the src/schema/logging-strict.xsd and src/schema/logging.xsd
 * by checking the validation results of configuration files in
 * subdirectories of tests/input/schema.
 *
 * @author Curt Arnold
 *
 */
public class SchemaTestCase extends TestCase {
  private static final String loggingXSD = "../src/schema/logging.xsd";

  /**
   * Constructs a SchemaTestCase
   * @param test name
   */
  public SchemaTestCase(final String testName) {
    super(testName);
  }

  /**
   * Validate input/schema/strict/*.xml against
   * both logging-strict.xsd and logging.xsd
   * expecting everything to be valid.
   *
   * @throws Exception
   */
  public void testValid() throws Exception {
    validateFiles("input/schema/valid", loggingXSD, true);
  }

  /**
   * Validate input/schema/strict/*.xml against
   * both logging-strict.xsd and logging.xsd
   * expecting everything to be invalid.
   *
   * @throws Exception
   */
  public void testInvalid() throws Exception {
    validateFiles("input/schema/invalid", loggingXSD, false);
  }

  /**
   * Loads the specified schema and validates
   * files matching the pattern.
   *
   * @param testPath Directory containing test files
   * @param schemaLocation schema location
   * @param expected expected validation result
   */
  private void validateFiles(
    final String testPath, final String schemaLocation, final boolean expected)
    throws Exception {
    File testDir = new File(testPath);
    assertTrue(testDir.exists());
    assertTrue(testDir.isDirectory());

    String[] files = testDir.list();

    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(true);
    factory.setFeature(
      "http://apache.org/xml/features/validation/schema", true);

    SAXParser parser = factory.newSAXParser();

    DefaultHandler handler = new SchemaEntityResolver(schemaLocation);

    if (expected) {
      for (int i = 0; i < files.length; i++) {
        if (files[i].endsWith(".xml")) {
          File testFile = new File(testDir, files[i]);

          try {
            parser.parse(testFile, handler);
          } catch (SAXException ex) {
            fail(
              "Exception while parsing expected valid configuration "
              + testFile.toString() + ".  " + ex.toString());
          }
        }
      }
    } else {
      for (int i = 0; i < files.length; i++) {
        if (files[i].endsWith(".xml")) {
          try {
            File testFile = new File(testDir, files[i]);
            parser.parse(testFile, handler);
            fail(
              "No exception thrown on expected invalid configuration "
              + testFile.toString());
          } catch (SAXException ex) {
          }
        }
      }
    }
  }

  private static class SchemaEntityResolver extends DefaultHandler {
    private final byte[] schemaBytes = new byte[20000];
    private final int schemaLength;

    public SchemaEntityResolver(final String schemaLocation)
      throws IOException {
      FileInputStream fs = new FileInputStream(schemaLocation);
      schemaLength = fs.read(schemaBytes);

      if (schemaLength == schemaBytes.length) {
        throw new IOException("Buffer too small for schema.");
      }

      fs.close();
    }

    public InputSource resolveEntity(
      final String publicID, final String systemID) {
      return new InputSource(
        new ByteArrayInputStream(schemaBytes, 0, schemaLength));
    }

    public void error(SAXParseException ex) throws SAXParseException {
      throw ex;
    }
  }
}
