package org.apache.log4j.multiplex;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

/**
 * @author psmith
 *
 */
public interface MultiplexSelector extends OptionHandler{

    public void setAppenderFactory(AppenderFactory factory);
    
    public Appender select(LoggingEvent e);

	/**
	 * 
	 */
	public void close();
}
