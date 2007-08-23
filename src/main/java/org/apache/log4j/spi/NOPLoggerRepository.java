package org.apache.log4j.spi;

import org.apache.log4j.Level;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Appender;

import java.util.Enumeration;
import java.util.Vector;

/**
 *  No-operation implementation of LoggerRepository which is used when
 *  LogManager.repositorySelector is erroneously nulled during class reloading.
 *  @since 1.2.15
 */
public final class NOPLoggerRepository implements LoggerRepository {
    /**
     * {@inheritDoc}
    */
    public void addHierarchyEventListener(final HierarchyEventListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDisabled(final int level) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setThreshold(final Level level) {
    }

    /**
     * {@inheritDoc}
     */
    public void setThreshold(final String val) {
    }

    /**
     * {@inheritDoc}
     */
    public void emitNoAppenderWarning(final Category cat) {
    }

    /**
     * @{inheritDoc}
     */
    public Level getThreshold() {
        return Level.OFF;
    }

    /**
     * {@inheritDoc}
     */
    public Logger getLogger(final String name) {
        return new NOPLogger(this, name);
    }

    /**
     * {@inheritDoc}
     */
    public Logger getLogger(final String name, final LoggerFactory factory) {
        return new NOPLogger(this, name);
    }

    /**
     * {@inheritDoc}
     */
    public Logger getRootLogger() {
        return new NOPLogger(this, "root");
    }

    /**
     * {@inheritDoc}
     */
    public Logger exists(final String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getCurrentLoggers() {
        return new Vector().elements();
    }

    /**
     * {@inheritDoc}
     */
    public Enumeration getCurrentCategories() {
        return getCurrentLoggers();
    }


    /**
     * {@inheritDoc}
     */
    public  void fireAddAppenderEvent(Category logger, Appender appender) {
    }

    /**
     * {@inheritDoc}
     */
    public void resetConfiguration() {
    }
}
