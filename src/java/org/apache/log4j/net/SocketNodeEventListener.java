/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net;

/**
  Interface used to listen for {@link SocketNode} related
  events. Clients register an instance of the interface and the
  instance is called back when the various events occur.
  
  @author Mark Womack
  @since 1.3
*/
public interface SocketNodeEventListener {
  
  /**
    Called when the socket the node was given has been closed. */
  public void socketClosedEvent(Exception e);
}