/*
 */
package org.apache.log4j.chainsaw.prefs;

import java.io.File;
import java.util.EventObject;

/**
 * @author psmith
 *
 */
class AbstractSettingsEvent extends EventObject {
  
  private final File settingsLocation;
  
  
  /**
   * @param source
   */
  public AbstractSettingsEvent(Object source, File settingsLocation) {
    super(source);
    this.settingsLocation = settingsLocation;
    
  }

  public File getSettingsLocation() {
    return this.settingsLocation;
  }
}
