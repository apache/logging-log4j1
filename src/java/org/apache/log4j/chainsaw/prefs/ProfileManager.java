/*
 * @author Paul Smith <psmith@apache.org>
 *
*/
package org.apache.log4j.chainsaw.prefs;

import java.util.Properties;

/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ProfileManager {
	
	private static final ProfileManager instance = new ProfileManager();
	
	public static final ProfileManager getInstance() { return instance;}
	
	public void configure(Profileable p) {
		Properties props = new Properties(SettingsManager.getInstance().getDefaultSettings());
		LoadSettingsEvent event = new LoadSettingsEvent(this, props);
		p.loadSettings(event);
	}

	public void configure(Profileable p, String profileName) {
		throw new UnsupportedOperationException("Not implemented as yet");
	}
	
	private ProfileManager() {
	
		
	}
}
