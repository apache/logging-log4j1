package org.apache.log4j.chainsaw.messages;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public final class MessageCenterAppender extends AppenderSkeleton {


  private final DefaultListModel model = new DefaultListModel();
  
  public final ListModel getModel() {
    return model;
  }
  
  /* (non-Javadoc)
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  protected void append(LoggingEvent event) {
    model.addElement(event);
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Appender#close()
   */
  public void close() {
  }

  /* (non-Javadoc)
   * @see org.apache.log4j.Appender#requiresLayout()
   */
  public boolean requiresLayout() {
    return false;
  }
}