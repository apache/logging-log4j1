/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

/*
 */
package org.apache.log4j.chainsaw;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 *  Used to encapsulate all the preferences for a given LogPanel
 * @author Paul Smith
 */
public class LogPanelPreferenceModel {
	private final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
	
	private boolean useISO8601Format = true;
	private String alternateDateFormatPattern = "HH:mm:ss"; 
	/**
	 * Returns the Date Pattern string for the alternate date formatter.
	 * @return
	 */
	public final String getAlternateDateFormatPattern() {
		return alternateDateFormatPattern;
	}

	/**
	 * Configures the Date pattern to use when using the alternate
	 * pattern
	 * @param alternateDateFormatPattern
	 */
	public final void setAlternateDateFormatPattern(String alternateDateFormatPattern) {
		String oldVal = this.alternateDateFormatPattern;
		this.alternateDateFormatPattern = alternateDateFormatPattern;
		propertySupport.firePropertyChange("alternateDateFormatPattern", oldVal, this.alternateDateFormatPattern);
	}

	/**
	 * Whether to use the faster ISO8601Format object for
	 * renderring dates, or not.
	 * @return
	 */
	public boolean isUseISO8601Format() {
		return useISO8601Format;
	}

	/**
	 * Sets whether to use the ISO8601Format object for rendering 
	 * dates.
	 * @param useISO8601Format
	 */
	public void setUseISO8601Format(boolean useISO8601Format) {
		boolean oldVal = this.useISO8601Format;
		this.useISO8601Format = useISO8601Format;
		propertySupport.firePropertyChange("useISO8601Format", oldVal, this.useISO8601Format);
	}

	/**
	 * @param listener
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public synchronized void addPropertyChangeListener(
		String propertyName,
		PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param listener
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public synchronized void removePropertyChangeListener(
		String propertyName,
		PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

}
