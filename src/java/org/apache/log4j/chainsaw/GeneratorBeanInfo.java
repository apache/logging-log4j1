/*
 */
package org.apache.log4j.chainsaw;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


/**
 * A BeanInfo class to be used as meta-data about the
 * Generator plugin
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class GeneratorBeanInfo extends SimpleBeanInfo {

    /* (non-Javadoc)
     * @see java.beans.BeanInfo#getPropertyDescriptors()
     */
    public PropertyDescriptor[] getPropertyDescriptors() {

        try {

            return new PropertyDescriptor[] {
                new PropertyDescriptor("name", Generator.class),
            };
        } catch (Exception e) {

        }

        return null;
    }
}
