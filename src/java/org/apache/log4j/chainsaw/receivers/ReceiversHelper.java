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
package org.apache.log4j.chainsaw.receivers;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        URL url = this.getClass().getClassLoader().getResource(
            this.getClass().getPackage().getName().replace('.','/') + "/known.receivers");

        LineNumberReader stream = null;
        try {

            stream = new LineNumberReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = stream.readLine()) != null) {

                try {
                    if (line.startsWith("#") || (line.length() == 0)) {
                        continue;
                    }
                    Class receiverClass = Class.forName(line);
                    receiverClassList.add(receiverClass);
                    LogLog.debug("Located known Receiver class " + receiverClass.getName());
                } catch (Exception e) {
                    LogLog.error("Failed to locate Receiver class:" + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
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
