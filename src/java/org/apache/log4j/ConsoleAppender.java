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
   ConsoleAppender appends log events to <code>System.err</code> or
   <code>System.out</code> using a layout specified by the
   user. The default target is <code>System.out</code>.

   @author Ceki G&uuml;lc&uuml;
   @since 1.1 */
public class ConsoleAppender extends WriterAppender {

  public static final String SYSTEM_OUT = "System.out";
  public static final String SYSTEM_ERR = "System.err";

  /**
    @deprecated We now use JavaBeans introspection to configure
    components. Options strings are no longer needed.  */
  public static final String TARGET_OPTION = "Target";

  protected String target = SYSTEM_OUT;

  /**
     The default constructor does nothing.
   */
  public ConsoleAppender() {    
  }

  public ConsoleAppender(Layout layout) {
    this(layout, SYSTEM_OUT);
  }

  public ConsoleAppender(Layout layout, String target) {
    this.layout = layout;
    
    if (SYSTEM_OUT.equals(target)) {
      setWriter(new OutputStreamWriter(System.out));	
    } else if (SYSTEM_ERR.equalsIgnoreCase(target)) {
      setWriter(new OutputStreamWriter(System.err));
    } else {
      targetWarn(target);
    }
  }

  /**
     Sets the value of the <b>Target</b> option.
     
     @param value String identifying a console; recognized values are
                  "System.err" (default) and "System.out"
   */
  public
  void setTarget(String value) {
    String v = value.trim();
    
    if (SYSTEM_OUT.equalsIgnoreCase(v)) {
      target = SYSTEM_OUT;
    } else if (SYSTEM_ERR.equalsIgnoreCase(v)) {
      target = SYSTEM_ERR;
    } else {
      targetWarn(value);
    }  
  }
  
  /** Returns the current value of the <b>Target</b> option. */
  public
  String getTarget() {
    return target;
  }
  
  void targetWarn(String val) {
    LogLog.warn("["+val+"] should be one of System.out or System.err.");
    LogLog.warn("Reverting to System.out.");
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
     Override the parent method to do nothing.
   */
  protected
  final 
  void closeWriter() {
  }


  /**
    Retuns the option names for this component, namely the string
    array {{@link #TARGET_OPTION} and the options of its super class
    {@link WriterAppender}.

    <b>See</b> Options of the super classes {@link WriterAppender} and
    {@link AppenderSkeleton}. In particular the <b>Threshold</b>
    option.
      
    @deprecated We now use JavaBeans introspection to configure
    components. Options strings are no longer needed.  
  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {TARGET_OPTION});
  }

  /**
     Set ConsoleAppender specific options.
          
     The <b>Target</b> option is recognized on top of options
     for the super class {@link WriterAppender}.
     
     @deprecated Use the setter method for the option directly instead
     of the generic <code>setOption</code> method. 
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

}
