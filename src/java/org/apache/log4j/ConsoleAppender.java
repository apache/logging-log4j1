/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j;

import java.io.OutputStreamWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

/**
   ConsoleAppender appends log events to <code>Syste.errm</code> or
   m<code>System.outm</code> using a layout specified by the user.

   @author Ceki G&uuml;lc&uuml;
   @since 1.1 */
public class ConsoleAppender extends WriterAppender {

  public static final String SYSTEM_OUT = "System.out";
  public static final String SYSTEM_ERR = "System.err";

  public static final String TARGET_OPTION = "Target";


  protected String target = "SYSTEM_ERR";

  /**
     The default constructor does nothing.
   */
  public ConsoleAppender() {    
  }

  public ConsoleAppender(Layout layout) {
    this(layout, SYSTEM_ERR);
  }

  public ConsoleAppender(Layout layout, String target) {
    this.layout = layout;
    if(SYSTEM_OUT.equals(target)) {
      setWriter(new OutputStreamWriter(System.out));	
    } else {
      if(!SYSTEM_ERR.equalsIgnoreCase(target)) {
	targetWarn(target);
      }
      setWriter(new OutputStreamWriter(System.err));
    }
  }

 
  public
  void activateOptions() {
    if(target.equals(SYSTEM_OUT)) {
      setWriter(new OutputStreamWriter(System.out));
    } else {
      setWriter(new OutputStreamWriter(System.err));
    }
  }

  /**
     Retuns the option names for this component, namely the string
     array {{@link #TARGET_OPTION} and the options of its super class
     {@link WriterAppender}.  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {TARGET_OPTION});
  }

  /**
     Set ConsoleAppender specific options.
          
     The <b>Target</b> option is recognized on top of options
     for the super class {@link WriterAppender}.
     
  */
  public
  void setOption(String key, String value) {
    if(value == null) return;
    super.setOption(key, value);
    
    if (key.equalsIgnoreCase(TARGET_OPTION)) {
      String v = value.trim();
      if(SYSTEM_OUT.equalsIgnoreCase(v)) {
	target = SYSTEM_OUT;
      } else {
	if(!SYSTEM_ERR.equalsIgnoreCase(v)) {
	  targetWarn(value);
	}  
      }
    }
  }
  
  
  void targetWarn(String val) {
    LogLog.warn("["+val+"] should be one of System.out or System.err.");
    LogLog.warn("Reverting to System.err.");
  }
}
