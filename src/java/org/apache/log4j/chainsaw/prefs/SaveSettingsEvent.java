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

import java.io.File;
import java.util.Properties;

/**
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class SaveSettingsEvent extends AbstractSettingsEvent {

  
  SaveSettingsEvent(Object source, File settingsLocation) {
	super(source, settingsLocation);
  	}
	
	public void saveSetting(String key, int value) {
		saveSetting(key, "" + value);
	}

	public void saveSetting(String key, double value) {
		saveSetting(key, "" + value);
	}

	public void saveSetting(String key, Object value) {
		saveSetting(key, value.toString());
	}

	public void saveSetting(String key, String value) {
		properties.put(key, value);
	}
	
	Properties getProperties() {
		return properties;
	}
  
  private Properties properties = new Properties();
  /**
   * @param string
   * @param b
   */
  public void saveSetting(String string, boolean b)
  {
    properties.put(string, b?Boolean.TRUE.toString():Boolean.FALSE.toString());
    
  }
}
