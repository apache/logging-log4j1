package org.apache.log4j.chainsaw.receivers;

import junit.framework.TestCase;

import org.apache.log4j.net.SocketHubReceiver;
import org.apache.log4j.net.SocketReceiver;
import org.apache.log4j.net.UDPReceiver;
import org.apache.log4j.net.XMLSocketReceiver;

import java.util.List;


/**
 * Test class to ensure that all the know receiver classes can be retrieved and are recognised as
 * valid Classess
 * 
 * @author psmith
 *
 */
public class ReceiversHelperTest extends TestCase {

    /**
     * @param arg0
     */
    public ReceiversHelperTest(String test) {
        super(test);
    }

    public void testKnownReceivers() {

        List list = ReceiversHelper.getInstance().getKnownReceiverClasses();

        Class[] expectedList =
            new Class[] {
                SocketHubReceiver.class, SocketReceiver.class,
                UDPReceiver.class, XMLSocketReceiver.class
            };

        for (int i = 0; i < expectedList.length; i++) {

            Class c = expectedList[i];
            assertTrue("Should have found class " + c.getName(),
                list.contains(c));
        }
    }
}
