package org.apache.log4j;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Properties;
import java.io.FileInputStream;

/**
 * Unit test for the Version class.
 *
 * @author Mark Womack
 */
public class VersionTest  extends TestCase {

  private static final String propsFile = "output/version/versionInfo";

  Logger logger = Logger.getLogger(VersionTest.class.getName());

  public VersionTest(String name) {
    super(name);
  }

  public void testVersion() throws Exception {
    // first read in the properties we are expecting from the file
    Properties baseProps = new Properties();
    baseProps.load(VersionTest.class.getClassLoader().
      getResourceAsStream("org/apache/log4j/versionInfo"));
    String baseVersion = baseProps.getProperty("version");
    String baseApiVersion = baseProps.getProperty("apiVersion");
    String baseMilestone = baseProps.getProperty("milestone");
    String baseMilestoneVersion = baseProps.getProperty("milestoneVersion");

    // make sure it looks like the version info was correctly set
    assertTrue("version was not replaced in versionInfo: '" + baseVersion + "'",
               baseVersion.indexOf('@') == -1);
    assertTrue("apiVersion was not replaced in versionInfo",
               baseApiVersion.indexOf('@') == -1);
    assertTrue("milestone was not replaced in versionInfo",
               baseMilestone.indexOf('@') == -1);
    assertTrue("milestoneVersion was not replaced in versionInfo",
               baseMilestoneVersion.indexOf('@') == -1);
    if (baseMilestone.equals("final")) {
      assertTrue("milestoneVersion is not empty for final milestone",
                 baseMilestoneVersion.length() == 0);
    } else {
      assertTrue("milestoneVersion is not set for milestone",
                 baseMilestoneVersion.length() != 0);
    }

    // now compare values to what the Version object returns
    assertEquals("version incorrect", baseVersion, Version.getVersion());
    assertEquals("apiVersion incorrect", baseApiVersion, Version.getApiVersion());
    assertEquals("milestone incorrect", baseMilestone, Version.getMilestone());
    assertEquals("milestoneVersion incorrect", baseMilestoneVersion, Version.getMilestoneVersion());
    String baseFullVersion = baseVersion + "-" + baseMilestone;
    if (!baseMilestone.equals("final")) {
      baseFullVersion += "-" + baseMilestoneVersion;
    }
    assertEquals("fullVersion incorrect", baseFullVersion, Version.getFullVersion());

    System.out.println("fullVersion is: " + Version.getFullVersion());
    System.out.println("apiVersion is: " + Version.getApiVersion());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new VersionTest("testVersion"));

    return suite;
  }
}
