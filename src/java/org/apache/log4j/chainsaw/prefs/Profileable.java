/*
 * @author Paul Smith <psmith@apache.org>
 *
*/
package org.apache.log4j.chainsaw.prefs;

/**
 * A component implementing this interface is interested in being able to 
 * configure itself.
 * 
 * Since this interface extends SettingsListener, the component
 * will receive Load and Save settings events as described
 * in SettingsManager
 * 
 * @see org.apache.log4j.chainsaw.prefs.SettingsManager
 * @author Paul Smith <psmith@apache.org>
 *
 */
public interface Profileable extends SettingsListener {

	/**
	 * Must be able to provide a name which is used to determine at a minimum, 
	 * the default profile name prefix for this component.
	*/
	public String getNamespace();
	

}
