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

package org.apache.log4j.rule;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.helpers.Constants;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggingEventFieldResolver;

/**
 * A Rule class implementing inequality evaluation for Levels (log4j and util.logging) using the toInt method.
 * 
 * @author Scott Deboy <sdeboy@apache.org>
 */
public class TimestampInequalityRule extends AbstractRule {

  private static final LoggingEventFieldResolver resolver = LoggingEventFieldResolver.getInstance();
  private static final DateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_RULE_FORMAT);
  private transient String inequalitySymbol;
  private long timeStamp;
  
  private TimestampInequalityRule(
    String inequalitySymbol, String value) {

    this.inequalitySymbol = inequalitySymbol;
    try {
    	timeStamp = dateFormat.parse(value).getTime();
    } catch (ParseException pe) {
    	throw new IllegalArgumentException("Could not parse date: " + value);
    }
  } 

  public static Rule getRule(String inequalitySymbol, String value) {
      return new TimestampInequalityRule(inequalitySymbol, value);
  }
  
  public boolean evaluate(LoggingEvent event) {
    long eventTimeStamp = Long.parseLong(resolver.getValue("TIMESTAMP", event).toString()) / 1000 * 1000; 
    boolean result = false;
    long first = eventTimeStamp;
    long second = timeStamp;

    if ("<".equals(inequalitySymbol)) {
      result = first < second;
    } else if (">".equals(inequalitySymbol)) {
      result = first > second;
    } else if ("<=".equals(inequalitySymbol)) {
      result = first <= second;
    } else if (">=".equals(inequalitySymbol)) {
      result = first >= second;
    }

    return result;
  }
  
  /**
    * Deserialize the state of the object
    *
    * @param in 
    *
    * @throws IOException 
    * @throws ClassNotFoundException 
    */
   private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException {
     inequalitySymbol = (String)in.readObject();
     timeStamp = in.readLong(); 
   }

   /**
    * Serialize the state of the object
    *
    * @param out 
    *
    * @throws IOException 
    */
   private void writeObject(java.io.ObjectOutputStream out)
     throws IOException {
     out.writeObject(inequalitySymbol);
     out.writeLong(timeStamp);
   }
}
