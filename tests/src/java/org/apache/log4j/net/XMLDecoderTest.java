/*
 * Created on 29/04/2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.net;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.xml.XMLDecoder;

/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class XMLDecoderTest extends TestCase {

  /**
   * Constructor for XMLDecoderTest.
   * @param arg0
   */
  public XMLDecoderTest(String arg0) {
    super(arg0);
  }

  /*
   * Test for Vector decode(File)
   */
  public void testDecodeFile() throws Exception {
    XMLDecoder xmlDecoder = new XMLDecoder();
    List events = xmlDecoder.decode(new File("tests/witness/eventSet.1.xml"));
    assertTrue("Should have returned at least 418 events: " + events.size(), events.size()==418);
    
  }
  
}
