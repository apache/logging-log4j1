/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.performance;
import org.apache.log4j.Logger;

/**

   This program evaluates the performance of getLogger
      when creating and retrieving loggers.


*/
public class GetLoggerTest {

  static
  public
  void main(String[] args) {
    System.out.println("Usage: java org.apache.log4j.GetLoggerTest passes [nameCount nameLength]\n");
    int passes = 5;
    String[] names = new String[] { "org.apache.log4j.Alpha",
                                    "com.example.acme.Bravo",
                                    "com.example.acme.Charlie",
                                    "net.example.acme.Delta",
                                    "org.apache.log4j.Echo",
                                    "com.example.acme.Foxtrot",
                                    "com.example.acme.Google",
                                    "net.example.acme.Hotel",
                                    "com.example.acme.Indigo",
                                    "com.example.acme.Jakarta",
                                    "net.example.acme.Kaffe",
                                    "com.example.acme.Lima",
                                    "com.example.acme.Mommy",
                                    "net.example.acme.Nancy",
                                    "com.example.acme.Opera",
                                    "com.example.acme.Picasso",
                                    "net.example.acme.Quebec",
                                    "com.example.acme.Romeo",
                                    "com.example.acme.Sierra",
                                    "net.example.acme.Tango",
                                    "com.example.acme.Umlat",
                                    "com.example.acme.Victor",
                                    "net.example.acme.Widget",
                                    "com.example.acme.Xray",
                                    "com.example.acme.Yellow",
                                    "net.example.acme.Zulu"};
    if (args.length > 0) {
        passes = Integer.parseInt(args[0]);
        if (args.length > 1) {
            String[] newNames = new String[Integer.parseInt(args[1])];
            for (int i = 0; i < newNames.length; i++) {
                newNames[i] = names[i % names.length] + i;
            }
            if (args.length > 2) {
                int nameLength = Integer.parseInt(args[2]);
                StringBuffer buf = new StringBuffer(nameLength);
                for (int i = 0; i < newNames.length; i++) {
                    buf.insert(0, newNames[i]);
                    buf.setLength(nameLength);
                    newNames[i] = buf.toString();
                }
            }
            names = newNames;
        }
     }
     int sum = 0;
     for (int i = 0; i < passes; i++) {
         long start = System.currentTimeMillis();
         for (int j = 0; j < names.length; j++) {
             Logger.getLogger(names[j]);
         }
         long end = System.currentTimeMillis();
         if (i != 0) {
             sum += (end - start);
         }
         System.out.println("Pass " + i + ": " + (end - start) + " ms.\n");
     }
     if (passes != 1) {
        System.out.println("Average non-initial pass: " + sum / (passes - 1) + " ms.\n");
     }
  }
}
