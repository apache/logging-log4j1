package org.apache.log4j.net;

/**
 * The parent of all the Network based interfaces.
 * 
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public interface NetworkBased {

  public String getName();
  
  public boolean isActive();
}
