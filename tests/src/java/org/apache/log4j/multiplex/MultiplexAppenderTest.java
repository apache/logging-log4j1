package org.apache.log4j.multiplex;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.multiplex.AppenderFactory;
import org.apache.log4j.multiplex.AppenderFactoryUtils;
import org.apache.log4j.multiplex.MDCKeySelector;
import org.apache.log4j.multiplex.MultiplexAppender;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.util.Compare;

/**
 * @author psmith
 *  
 */
public class MultiplexAppenderTest extends TestCase {
	private Logger root;

	private Logger logger = Logger
			.getLogger(MultiplexAppenderTest.class);

	public void setUp() {
		root = Logger.getRootLogger();
		root.addAppender(new ConsoleAppender(new PatternLayout(
				"%d{ABSOLUTE} %c{1} [%properties{User}] %m%n") ));
	}

	public void testIllegalStates() {
		MultiplexAppender appender = null;

		appender = new MultiplexAppender();
		try {
			appender.activate();
			fail("Should have thrown an IllegalStateException because it should not be configured correctly");
		} catch (Exception e) {
			// expected
		}

		appender = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		LogManager.shutdown();
	}

	public void testMDCSelector() throws Exception {

        
		MultiplexAppender appender = new MultiplexAppender();

		final String mdcKey = "User";
        MDCKeySelector selector = new MDCKeySelector(mdcKey);
		appender.setSelector(selector);
        selector.setAppenderFactory(AppenderFactoryUtils.createSimpleMDCbasedFileAppender("output/MultiplexRollingFileAppenderTestMDC", mdcKey, new PatternLayout("%m%n")));
		selector.activateOptions();
        
        
        logger.addAppender(appender);

		MDC.put(mdcKey, "Bob");

		logger.debug("Hello Bob");

		MDC.put(mdcKey, "Jane");
		logger.debug("Hello Jane");
        
        MDC.put(mdcKey, "Bob");
        logger.debug("I wonder what Jane's file looks like");
        

        MDC.put(mdcKey, "Jane");
        logger.debug("Bob.  Be quiet.");

        
        assertTrue("Bob's file does not match expected", Compare.compare("witness/multiplex/multiplex-test1_bob.txt", "output/MultiplexRollingFileAppenderTestMDC_Bob.log"));
        assertTrue("Jane's file does not match expected", Compare.compare("witness/multiplex/multiplex-test1_jane.txt", "output/MultiplexRollingFileAppenderTestMDC_Jane.log"));

	}
    
    // TODO we need a test that tests when the MDC value is null/not found 
    
    public void testcreateMDCAndDailyRollingAppenderFactory() {
     AppenderFactory factory = AppenderFactoryUtils.createMDCAndDailyRollingAppenderFactory("output/standardMDC", "User", new PatternLayout("%m%n"));
     MDC.put("User", "Bob");
     LoggingEvent e = new LoggingEvent();
     
     RollingFileAppender appender = (RollingFileAppender) factory.create(e);
     
     assertEquals(appender.getTriggeringPolicy().getClass(), TimeBasedRollingPolicy.class);
     //TimeBasedRollingPolicy policy =(TimeBasedRollingPolicy) appender.getTriggeringPolicy();
    }
}