package org.apache.log4j.multiplex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author psmith
 *  
 */
public abstract class MultiplexSelectorSkeleton implements MultiplexSelector {

	private AppenderFactory appenderFactory;

	protected Map multiplexedAppenders = Collections
			.synchronizedMap(new HashMap());

	public void close() {
		//        TODO probable should log that we're closing each appender
		for (Iterator iter = multiplexedAppenders.values().iterator(); iter
				.hasNext();) {
			Appender appender = (Appender) iter.next();
			appender.close();
		}

	}

	/**
	 * @return Returns the appenderFactory.
	 */
	public final AppenderFactory getAppenderFactory() {
		return appenderFactory;
	}

	/**
	 * @param appenderFactory
	 *            The appenderFactory to set.
	 */
	public final void setAppenderFactory(AppenderFactory appenderFactory) {
		this.appenderFactory = appenderFactory;
	}

	protected Appender lookupOrCreateAsNeeded(Object key, LoggingEvent e) {
		Appender appender = (Appender) multiplexedAppenders.get(key);
		if (appender == null) {
			appender = getAppenderFactory().create(e);
			multiplexedAppenders.put(key, appender);
		}
		return appender;

	}

}