/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */



package org.apache.log4j;

import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ErrorCode;

/**
   DailyRollingFileAppender extends FileAppender to use filenames formatted
   with date/time information. The filename is recomputed every day at
   midnight.  Note that the filename doesn't have to change every day,
   making it possible to have logfiles which are per-week or
   per-month.

   The appender computes the proper filename using the formats
   specified in {@link SimpleDateFormat.html}. The format requires
   that most static text is enclosed in single quotes, which are
   removed. The examples below show how quotes are used to embed
   static information in the format.

   Sample filenames:

<code>
     Filename pattern                     Filename
     "'/logs/trace-'yyyy-MM-dd'.log'"     /logs/trace-2000-12-31.log
     "'/logs/trace-'yyyy-ww'.log'"        /logs/trace-2000-52.log
</code>

   @author <a HREF="mailto:eirik.lygre@evita.no">Eirik Lygre</a> */
public class DailyRollingFileAppender extends FileAppender {


  // The code assumes that the following constants are in a increasing
  // sequence.
  static final int TOP_OF_ZONK   =-1;
  static final int TOP_OF_MINUTE = 0;
  static final int TOP_OF_HOUR   = 1;
  static final int HALF_DAY      = 2;
  static final int TOP_OF_DAY    = 3;
  static final int TOP_OF_WEEK   = 4;
  static final int TOP_OF_MONTH  = 5;


  /**
     A string constant used in naming the option for setting the
     filename pattern. Current value of this string constant is
     <strong>datePattern</strong>.
   */
  static final public String DATE_PATTERN_OPTION = "DatePattern";
  
  /**
     The date pattern. By default, the rolled file extension is
     the date folloed by .log. The rollover occurs at midnight. '
  */
  private String datePattern = ".YYYY-MM-dd.log";

  /**
     The actual formatted filename that is currently being written to
  */
  private String lastDatedFilename;

  /**
     The timestamp when we shall next recompute the filename
  */
  private long nextCheck = System.currentTimeMillis () - 1;

  Date now = new Date();

  SimpleDateFormat sdf;

  RollingCalendar rc = new RollingCalendar();

  int checkPeriod = TOP_OF_ZONK;

  /**
     The default constructor does nothing. */
  public
  DailyRollingFileAppender() {
  }

  /**
    Instantiate a <code>DailyRollingFileAppender</code> and open the
    file designated by <code>filename</code>. The opened filename will
    become the ouput destination for this appender.

    */
  public DailyRollingFileAppender (Layout layout, String filename, 
				   String datePattern) throws IOException {
    super(layout, filename, true);
    this.datePattern = datePattern;
    sdf = new SimpleDateFormat(datePattern);
    int type = computeCheckPeriod();
    printPeriodicity(type);
    rc.setType(type);
  }


  public
  void activateOptions() {
    super.activateOptions();
    if(datePattern != null && fileName != null) {
      lastDatedFilename = fileName+sdf.format(now);
    } else {
      LogLog.error("Either Filename or DatePattern options are not set for ["+
		   name+"].");
    }
  }
  
  void printPeriodicity(int type) {
    switch(type) {
    case TOP_OF_MINUTE:
      LogLog.debug("Appender ["+name+"] to be rolled every minute.");
      break;
    case TOP_OF_HOUR:
      LogLog.debug("Appender ["+name
		   +"] to be rolled on top of every hour.");
      break;
    case HALF_DAY:
      LogLog.debug("Appender ["+name
		   +"] to be rolled at midday and midnight.");
      break;
    case TOP_OF_DAY:
      LogLog.debug("Appender ["+name
		   +"] to be rolled at midnight.");
      break;
    case TOP_OF_WEEK:
      LogLog.debug("Appender ["+name
		   +"] to be rolled at start of week.");
      break;
    case TOP_OF_MONTH:
      LogLog.debug("Appender ["+name
		   +"] to be rolled at start of every month.");
      break;	    
    default:
      LogLog.warn("Unknown periodicity for appender ["+name+"].");
    }
  }
  
  
  int computeCheckPeriod() {
    RollingCalendar c = new RollingCalendar();
    // set sate to 1970-01-01 00:00:00 GMT
    Date epoch = new Date(0);
    
    
    if(datePattern != null) {
      for(int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
	
	String r0 = sdf.format(epoch);
	c.setType(i);
	Date next = new Date(c.getNextCheckMillis(epoch));
	String r1 = sdf.format(next);
	//LogLog.debug("Type = "+i+", r0 = "+r0+", r1 = "+r1);
	if(r0 != null && r1 != null && !r0.equals(r1)) {
	  return i;
	}
      }
    }
    // Deliberately head for trouble...
    return TOP_OF_ZONK;
  }

  /**
     Set the current output file.

     The function will compute a new filename, and open a new file only
     when the name has changed.

     The function is automatically called once a day, to allow for
     daily rolling files -- the purpose of this class.  */

  public
  synchronized
  void rollOver() throws IOException {

    LogLog.debug("RollOver called-------------------------");

    /* Compute filename, but only if datePattern is specified */
    if (datePattern == null) {
      errorHandler.error("Missing "+DATE_PATTERN_OPTION+
			 " option in rollOver().");
      return;
    }

    String datedFilename = fileName+sdf.format(now);
    if (lastDatedFilename.equals(datedFilename)) {
      return;
    }

    LogLog.debug("NexCheck="+nextCheck);    

    LogLog.debug("Target = "+datedFilename);

    
    
    // close current file, and rename it to datedFilename
    this.closeFile(); 
    
    File target  = new File(lastDatedFilename);    
    if (target.exists()) {
      target.delete();
    }

    File file = new File(fileName);
    file.renameTo(target);    
    LogLog.debug(fileName +" -> "+ lastDatedFilename);    

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false);
    }
    catch(IOException e) {
      LogLog.error("setFile("+fileName+", false) call failed., e");
    }

    lastDatedFilename = datedFilename;    
  }

  /**
     This method differentiates DailyRollingFileAppender from its
     super class.
  */
  protected
  void subAppend(LoggingEvent event) {
    
    long n = System.currentTimeMillis();
    if (n >= nextCheck) {
      now.setTime(n);
      nextCheck = rc.getNextCheckMillis(now);
      try {
        rollOver();
      } catch(IOException e) {
        LogLog.error("setFile(null, false) call failed.", e);
      }
    }
    super.subAppend(event);
  } 

  /**
     Retuns the option names for this component, namely {@link
     #FILE_NAME_PATTERN_OPTION} in
     addition to the options of {@link FileAppender#getOptionStrings
     FileAppender}.
  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
		 new String[] {DATE_PATTERN_OPTION});
  }

  /**
     Set the options for the appender
  */
  public
  void setOption(String key, String value) {
    super.setOption(key, value);    
    if(key.equalsIgnoreCase(DATE_PATTERN_OPTION)) {
      datePattern = value;
      now.setTime(System.currentTimeMillis());
      sdf = new SimpleDateFormat(datePattern);
      int type = computeCheckPeriod();
      printPeriodicity(type);
      rc.setType(type);
    }
  }
}  

/**
   RollingCalendar is a helper class to
   DailyRollingFileAppender. Using this class, it is easy to compute
   and access the next Millis()
 
   It subclasses the standard {@link GregorianCalendar}-object, to
   allow access to the protected function getTimeInMillis(), which it
   then exports.

   @author <a HREF="mailto:eirik.lygre@evita.no">Eirik Lygre</a> */

class RollingCalendar extends GregorianCalendar {
  
  int type;

  void setType(int type) {
    this.type = type;
  }

  public
  long getNextCheckMillis(Date now) {
    return getNextCheckDate(now).getTime();
  }

  public 
  Date getNextCheckDate(Date now) {
    this.setTime(now);

    switch(type) {
    case DailyRollingFileAppender.TOP_OF_MINUTE:
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.MINUTE, +1); 
	break;
    case DailyRollingFileAppender.TOP_OF_HOUR:
	this.set(Calendar.MINUTE, 0); 
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.HOUR_OF_DAY, +1); 
	break;
    case DailyRollingFileAppender.HALF_DAY:
	this.set(Calendar.MINUTE, 0); 
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	int hour = get(Calendar.HOUR_OF_DAY);
	if(hour < 12) {
	  this.set(Calendar.HOUR_OF_DAY, 12);
	} else {
	  this.set(Calendar.HOUR_OF_DAY, 0);
	  this.add(Calendar.DAY_OF_MONTH, +1);       
	}
	break;
    case DailyRollingFileAppender.TOP_OF_DAY:
	this.set(Calendar.HOUR_OF_DAY, 0); 
	this.set(Calendar.MINUTE, 0); 
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.DATE, +1);       
	break;
    case DailyRollingFileAppender.TOP_OF_WEEK:
	this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
	this.set(Calendar.HOUR_OF_DAY, 0);
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.WEEK_OF_YEAR, 1);
	break;
    case DailyRollingFileAppender.TOP_OF_MONTH:
	this.set(Calendar.DATE, 1);
	this.set(Calendar.HOUR_OF_DAY, 0);
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.MONTH, +1); 
	break;
    default:
	throw new IllegalStateException("Unknown periodicity type.");
    }      
    return getTime();
  }
}  

