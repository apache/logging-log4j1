/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */
package org.apache.log4j.chainsaw.receivers;


import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.helpers.LogLog;


/**
 * Helper class to assisit with all the known Receivers.
 * 
 * A local resource 'known.receivers' is read in on initialization
 * with each line representing the FQN of the Class that is a recognised Receiver.
 * 
 * @author Paul Smith <psmith@apache.org>
 *
 */
public class ReceiversHelper {

    private static final ReceiversHelper instance = new ReceiversHelper();

    private List receiverClassList = new ArrayList();
    /**
     *
     */
    private ReceiversHelper() {

        Properties p = new Properties();
        URL url = this.getClass().getClassLoader().getResource(
            this.getClass().getPackage().getName().replace('.','/') + "/known.receivers");

        try {

            LineNumberReader stream = new LineNumberReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = stream.readLine()) != null) {

                try {
                    Class receiverClass = Class.forName(line);
                    receiverClassList.add(receiverClass);
                    LogLog.debug("Located known Receiver class " + receiverClass.getName());
                } catch (Exception e) {
                    LogLog.error("Failed to locate Receiver class:" + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static ReceiversHelper getInstance() {
        return instance;
    }


    /**
     * Returns an unmodifiable list of Class objects which represent all the 'known'
     * Receiver classes.
     * @return
     */
    public List getKnownReceiverClasses() {
      return Collections.unmodifiableList(receiverClassList);
    }
    

}
