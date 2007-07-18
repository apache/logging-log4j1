package org.apache.log4j.multiplex;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

/**
 * @author psmith
 *
 */

public interface AppenderFactory extends OptionHandler{

    public Appender create(LoggingEvent e);
}
