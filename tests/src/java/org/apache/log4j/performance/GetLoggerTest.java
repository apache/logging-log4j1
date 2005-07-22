/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

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
