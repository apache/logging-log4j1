/*
 * Copyright 1999,2005 The Apache Software Foundation.
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
package org.apache.log4j.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public final class BinaryCompare {
    /**
     * Class can not be constructed.
     */
    private BinaryCompare() {
    }

    public static boolean compare(final String file1, final String file2)
        throws FileNotFoundException, IOException {
        BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(
                    file1));
        BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(
                    file2));

        int byte1 = 0;
        int byte2 = 0;

        for (int pos = 0; byte1 != -1; pos++) {
            byte1 = in1.read();
            byte2 = in2.read();

            if (byte1 != byte2) {
                if (byte2 == -1) {
                    System.out.println("File [" + file2 +
                        "] longer than file [" + file1 + "].");
                } else if (byte1 == -1) {
                    System.out.println("File [" + file1 +
                        "] longer than file [" + file2 + "].");
                } else {
                    System.out.println("Files differ at offset " + pos + ": [" +
                        file1 + "] has " + Integer.toHexString(byte1) + ", [" +
                        file2 + "] has " + Integer.toHexString(byte2) + ".");
                }

                return false;
            }
        }

        return true;
    }
}
