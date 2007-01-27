package org.apache.log4j.jmx;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.varia.NullAppender;

public class HierarchyMBeanTest extends TestCase {
  
  public void testConf() throws Exception {
    Properties p = new Properties();
    p.put("log4j.rootLogger","DEBUG,C");
    p.put("log4j.appender.C","org.apache.log4j.ConsoleAppender");
    p.put("log4j.appender.C","layout=org.apache.log4j.SimpleLayout");
    PropertyConfigurator.configure(p);
    HierarchyDynamicMBean h = new HierarchyDynamicMBean();
    
    Logger l = Logger.getLogger(getClass());
    l.info("hi");

    MBeanServer server = MBeanServerFactory.createMBeanServer();
    ObjectName ho = new ObjectName("foo:type=Hierarchy");
    server.registerMBean(h, ho);
    assertTrue(server.isRegistered(new ObjectName("foo:logger=root")));
    
    NullAppender a = new NullAppender();
    a.setName("na");
    l.addAppender(a);
    // Use MBeanServer ...
    // h.addLoggerMBean(l);
    server.invoke(ho, "addLoggerMBean", new Object[] { l.getName() }, null);
    assertTrue(server.isRegistered(new ObjectName("foo:logger=" + l.getName())));
    assertTrue(server.isRegistered(new ObjectName("foo:appender=na")));
    
    server.invoke(ho, "addLoggerMBeans", null, null);
    
    Set set = server.queryNames(null, null);
    Iterator i = set.iterator();
    while (i.hasNext())
      System.out.println(i.next());
  }
  
  public void testInfo() { 
    // Some JMX implementations do not allow spaces in parameter names
    MBeanParameterInfo info = new MBeanParameterInfo("spa ce", TestCase.class.toString(), "desc");
  }
  
}
