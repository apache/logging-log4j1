package org.apache.log4j.config;

import java.util.Date;

import junit.framework.TestCase;

public class PropertySetterTest extends TestCase {

  public void testSetter() {
    /*
    Properties p = new Properties();
    p.put("log4j.debug", "true");
    p.put("log4j.appender.A", "");
    p.put("log4j.rootLogger", "DEBUG,A");
    PropertyConfigurator.configure(p);
    */
    
    Date d = new Date();
    PropertySetter ps = new PropertySetter(d);
    ps.setProperty("time", "0");
    assertEquals(0L, d.getTime());

    // no properties to set, warn
    PropertySetter ps2 = new PropertySetter(new Object());
    ps2.setProperty("class", "abc");
  }
  
}
