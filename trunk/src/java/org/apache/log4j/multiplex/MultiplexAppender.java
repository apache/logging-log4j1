package org.apache.log4j.multiplex;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * TODO discuss how the Selector and AppenderFactory works
 * 
 * 
 * @author psmith
 *  
 */
public class MultiplexAppender extends AppenderSkeleton {

	private MultiplexSelector selector;

    public MultiplexAppender() {
        super(true);
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.OptionHandler#activateOptions()
	 */
	public void activateOptions() {


		// check that we have a selector of something at this point
		if (getSelector() == null) {
			throw new IllegalStateException(
					"Should have had a Selector defined at this point");
		}

    // TODO work out how the Selector has it's AppenderFactory configured by Joran
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
		getSelector().close();

	}

	/**
	 * @return Returns the selector.
	 */
	public final MultiplexSelector getSelector() {
		return selector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		//        TODO check this is correct
		return true;
	}

	/**
	 * @param selector
	 *            The selector to set.
	 */
	public final void setSelector(MultiplexSelector selector) {
		this.selector = selector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.WriterAppender#subAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent event) {

		// we assume appropriate syncronization has occured on this appender

		// determinge the key to lookup the Appender to use
		Appender appender = getSelector().select(event);
		appender.doAppend(event);

	}
}