/*
 */
package org.apache.log4j.net;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * BeanInfo class for the SocketHubReceiver.
 *
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class SocketHubReceiverBeanInfo extends SimpleBeanInfo {

    /* (non-Javadoc)
     * @see java.beans.BeanInfo#getPropertyDescriptors()
     */
    public PropertyDescriptor[] getPropertyDescriptors() {

        try {

            return new PropertyDescriptor[] {
                new PropertyDescriptor("host", SocketHubReceiver.class),
                new PropertyDescriptor("port", SocketHubReceiver.class),
                new PropertyDescriptor("threshold", SocketHubReceiver.class),
                new PropertyDescriptor("reconnectionDelay",
                    SocketHubReceiver.class),
            };
        } catch (Exception e) {
        }

        return null;
    }
}
