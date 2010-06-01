/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apache.logging.core;

/**
 *
 */
public class UnrecoverableLoggingException extends LoggingException {
    private final Object failedComponent;
    public UnrecoverableLoggingException(final Object failedComponent) {
        this(failedComponent, null, null);
    }
    public UnrecoverableLoggingException(final Object failedComponent,
            final String message) {
        this(failedComponent, message, null);
    }
    public UnrecoverableLoggingException(final Object failedComponent,
            final String message,
            final Throwable cause) {
        super(message,cause);
        if (failedComponent == null) {
          throw new NullPointerException();
        }
        this.failedComponent = failedComponent;
    }
    public UnrecoverableLoggingException(final Object failedComponent,
            final Throwable cause) {
        this(failedComponent, null, cause);
    }

    public final Object getFailedComponent() {
        return failedComponent;
    }

}
