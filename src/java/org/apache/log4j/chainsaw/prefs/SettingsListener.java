/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.chainsaw.prefs;

import java.util.EventListener;

/**
 * 
 * Components, or objects, that are interested in being notified when
 * Settings are loaded or being saved, can implement this interface.
 * 
 * 'Settings' are Chainsaw wide preferences, and are not specific to a particular
 * tab identifer etc. See the correspoing ProfileListener and related classes
 * for a discussion on profile specific events etc.
 * 
 * The implementing class can use this event notification opportunity
 * to load setting information stored previously, or to 
 * request that setting information be stored.
 * 
 * NOTE: This contract does <b>_*NOT*_</b> dictate that the Thread invoking these
 * methods will be the Swing's Event dispatching event, it could
 * be any arbitary thread.  Having said that, it COULD be the Swing's Event
 * dispatching event, but this contract makes no guarantee.
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public interface SettingsListener extends EventListener {
	
	/**
	 * When a request to load Settings has been requested, this method
	 * will be invoked by the SettingsManager.  The implementing
	 * component can query the event for settings, and modify
	 * it's internal state based on these settings.
	 * 
	 * @param event
	 */
	public void loadSettings(LoadSettingsEvent event);
  
	public void saveSettings(SaveSettingsEvent event);
}
