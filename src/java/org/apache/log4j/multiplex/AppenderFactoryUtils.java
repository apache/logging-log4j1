package org.apache.log4j.multiplex;

import java.text.MessageFormat;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.rolling.RollingFileAppender;
import org.apache.log4j.rolling.TimeBasedRollingPolicy;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author psmith
 *  
 */
public class AppenderFactoryUtils {

	public static AppenderFactory createMDCAndDailyRollingAppenderFactory(
			final String fullFilePathAndPrefix, final String mdcKey,
			final Layout layout) {
		return new AppenderFactory() {

			public void activateOptions() {

			}

			public Appender create(LoggingEvent e) {
				String value = e.getProperty(mdcKey);

				String datePattern = "yyyy-MM-dd";

				TimeBasedRollingPolicy tbrp = new TimeBasedRollingPolicy();
				String pattern = fullFilePathAndPrefix + "-" + value + "-%d{"
						+ datePattern + "}";

				tbrp.setFileNamePattern(pattern);
				tbrp.activateOptions();
				RollingFileAppender rfa = new RollingFileAppender();
				rfa.setRollingPolicy(tbrp);
				rfa.activateOptions();
				return rfa;

			}

		};

	}

	private AppenderFactoryUtils() {
	}

	/**
	 * @param mdcKey
	 * @param layout
	 * @return
	 */
	public static AppenderFactory createSimpleMDCbasedFileAppender(
			final String fullFilePathAndPrefix, final String mdcKey,
			final PatternLayout layout) {
		return new AppenderFactory() {

			public Appender create(LoggingEvent e) {
				String value = e.getProperty(mdcKey);
				String pattern = fullFilePathAndPrefix + "_{0}.log";
				try {
					FileAppender fileAppender = new FileAppender(layout,
							MessageFormat.format(pattern,
									new Object[] { value }));
					fileAppender.activateOptions();
					return fileAppender;
				} catch (Exception ex) {
					//throw new RuntimeException(ex);
          throw new RuntimeException();

				}
			}

			public void activateOptions() {

			}
		};
	}
}