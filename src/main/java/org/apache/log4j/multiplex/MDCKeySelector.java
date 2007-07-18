package org.apache.log4j.multiplex;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author psmith
 *  
 */
public class MDCKeySelector extends MultiplexSelectorSkeleton {

	private String MDCKey;

	/**
	 * @param key
	 */
	public MDCKeySelector(String key) {
		super();
		MDCKey = key;
	}

	/**
	 * @return Returns the mDCKey.
	 */
	public final String getMDCKey() {
		return MDCKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.multiplex.MultiplexSelector#select(org.apache.log4j.spi.LoggingEvent)
	 */
	public Appender select(LoggingEvent e) {
        return lookupOrCreateAsNeeded(e.getProperty(getMDCKey()), e);
	}

	/**
	 * @param key
	 *            The mDCKey to set.
	 */
	public final void setMDCKey(String key) {
		MDCKey = key;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.spi.OptionHandler#activateOptions()
	 */
	public void activateOptions() {
        
        // TODO ?
	}

}