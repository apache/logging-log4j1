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
   DailyRollingFileAppender extends {@link FileAppender} so that the
   underlying file is rolled over at a user chosen frequency.

   <p>The rolling schedule is specified by the <b>DatePattern</b>
   option. This pattern should follow the {@link SimpleDateFormat}
   conventions. In particular, you <em>must</em> escape literal text
   within a pair of single quotes. A formatted version of the date
   pattern is used as the suffix for the rolled file name.

   <p>For example, if the <b>File</b> option is set to
   <code>/foo/bar.log</code> and the <b>DatePattern</b> set to
   <code>'.'yyyy-MM-dd</code>, on 2001-02-16 at midnight, the logging
   file <code>/foo/bar.log</code> will be copied to
   <code>/foo/bar.log.2001-02-16</code> and logging for 2001-02-17
   will continue in <code>/foo/bar.log</code> until it is rolled over
   itself the next day.
   
   <p>Is is possible to specify monthly, weekly, half-daily, daily,
   hourly, or minutely rollover schedules.

   <p><table border="1">
   <tr>
   <th>DatePatten</th> 
   <th>rollover schdule</th>
   <th>example</th>

   <tr>
   <td><code>'.'yyyy-MM</code>   
   <td>Rollover at the beginning of each month</td>   

   <td>Assuming the first day of the week is Sunday, at Sunday 00:00,
   March 25th, 2001, <code>/foo/bar.log</code> will be copied to
   <code>/foo/bar.log.2001-03</code>. Logging for the month of April
   will be output to <code>/foo/bar.log</code> until it is rolled over
   itself at the beginning of May.

   <tr>
   <td><code>'.'yyyy-ww</code>   
   
   <td>Rollover at the first day of each week. The first day of the
   week depends on the locale.</td>
   
   <td>At midnight, on March 31st, 2001, <code>/foo/bar.log</code>
   will be copied to <code>/foo/bar.log.2001-08</code>. Logging for
   the 9th week of 2001 will be output to <code>/foo/bar.log</code>
   until it is rolled over the next week.

   <tr>
   <td><code>'.'yyyy-MM-dd</code>   
   
   <td>Rollover at midnight each day.</td>
   
   <td>At midnight, on March 9th, 2001, <code>/foo/bar.log</code> will
   be copied to <code>/foo/bar.log.2001-03-08</code>. Logging for the
   9th day of March will be output to <code>/foo/bar.log</code> until
   it is rolled over the next day.

   <tr>
   <td><code>'.'yyyy-MM-dd-a</code>   
   
   <td>Rollover at midnight and midday of each day.</td>
   
   <td>At noon, on March 9th, 2001, <code>/foo/bar.log</code> will be
   copied to <code>/foo/bar.log.2001-03-09-AM</code>. Logging for the
   afternoon of the 9th will be output to <code>/foo/bar.log</code>
   until it is rolled over the next morning, i.e at midnight 00:00.

   <tr>
   <td><code>'.'yyyy-MM-dd-HH</code>   
   
   <td>Rollover at the top of every hour.</td>
   
   <td>At approximately 11:00,000, on March 9th, 2001,
   <code>/foo/bar.log</code> will be copied to
   <code>/foo/bar.log.2001-03-09-10</code>. Logging for the 11th hour
   of of the 9th of March will be output to <code>/foo/bar.log</code>
   until it is rolled over at the beginning of the next hour.


   <tr>
   <td><code>'.'yyyy-MM-dd-HH-mm</code>   
   
   <td>Rollover at the beginning of every minutue.</td>
   
   <td>At approximately 11:23,000, on March 9th, 2001,
   <code>/foo/bar.log</code> will be copied to
   <code>/foo/bar.log.2001-03-09-10-22</code>. Logging for the minutue
   of 11:23 (9th of March) will be output to
   <code>/foo/bar.log</code> untill it is rolled over the next minute.
      
   </table>

   <p>Do not use the colon ":" character in anywhere in the
   <b>DatePattern</b> option. The text before the colon is interpeted
   as the protocol specificaion of a URL which is probably not what
   you want.


   @author Eirik Lygre
   @author Ceki G&uuml;lc&uuml; */
public class DailyRollingFileAppender extends FileAppender {


  // The code assumes that the following constants are in a increasing
  // sequence.
  static final int TOP_OF_TROUBLE=-1;
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
     The date pattern. By default, the pattern is set to
     "'.'YYYY-MM-dd" meaning daily rollover.  */
  private String datePattern = "'.'YYYY-MM-dd";

  /**
     The actual formatted filename that is currently being written to
  */
  private String scheduledFilename;

  /**
     The timestamp when we shall next recompute the filename
  */
  private long nextCheck = System.currentTimeMillis () - 1;

  Date now = new Date();

  SimpleDateFormat sdf;

  RollingCalendar rc = new RollingCalendar();

  int checkPeriod = TOP_OF_TROUBLE;

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
    activateOptions();
  }


  public
  void activateOptions() {
    super.activateOptions();
    if(datePattern != null && fileName != null) {
      now.setTime(System.currentTimeMillis());
      sdf = new SimpleDateFormat(datePattern);
      int type = computeCheckPeriod();
      printPeriodicity(type);
      rc.setType(type);
      scheduledFilename = fileName+sdf.format(now);
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
    return TOP_OF_TROUBLE; // Deliberately head for trouble...
  }


  /**
     Retuns the option names for this component, namely {@link
     #DATE_PATTERN_OPTION} in
     addition to the options of {@link FileAppender#getOptionStrings
     FileAppender}.
  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
		 new String[] {DATE_PATTERN_OPTION});
  }

  /**
     Rollover the current file to a new file.
  */  
  void rollOver() throws IOException {

    /* Compute filename, but only if datePattern is specified */
    if (datePattern == null) {
      errorHandler.error("Missing "+DATE_PATTERN_OPTION+
			 " option in rollOver().");
      return;
    }

    String datedFilename = fileName+sdf.format(now);
    if (scheduledFilename.equals(datedFilename)) 
      return;

    // close current file, and rename it to datedFilename
    this.closeFile(); 
    
    File target  = new File(scheduledFilename);    
    if (target.exists()) {
      target.delete();
    }

    File file = new File(fileName);
    file.renameTo(target);    
    LogLog.debug(fileName +" -> "+ scheduledFilename);    

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false);
    }
    catch(IOException e) {
      errorHandler.error("setFile("+fileName+", false) call failed.");
    }
    scheduledFilename = datedFilename;    
  }

  /**
     Set the options for the {@link DailyRollingFileAppender}
     instance.

     <p>The <b>DatePattern</b> takes a string in the same format as
     expected by {@link SimpleDateFormat}. This options determines the
     rollover schedule.

     <p>Be sure to refer to the options in the super classes {@link
     FileAppender}, {@link WriterAppender} and in particular the
     <b>Threshold</b> option in {@link AppenderSkeleton}.
     
     </ul> */
  public
  void setOption(String key, String value) {
    if(value == null) return;
    super.setOption(key, value);    
    if(key.equalsIgnoreCase(DATE_PATTERN_OPTION)) {
      datePattern = value;
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
  
  int type = DailyRollingFileAppender.TOP_OF_TROUBLE;

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
	this.add(Calendar.MINUTE, 1); 
	break;
    case DailyRollingFileAppender.TOP_OF_HOUR:
	this.set(Calendar.MINUTE, 0); 
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.HOUR_OF_DAY, 1); 
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
	  this.add(Calendar.DAY_OF_MONTH, 1);       
	}
	break;
    case DailyRollingFileAppender.TOP_OF_DAY:
	this.set(Calendar.HOUR_OF_DAY, 0); 
	this.set(Calendar.MINUTE, 0); 
	this.set(Calendar.SECOND, 0);
	this.set(Calendar.MILLISECOND, 0);
	this.add(Calendar.DATE, 1);       
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
	this.add(Calendar.MONTH, 1); 
	break;
    default:
	throw new IllegalStateException("Unknown periodicity type.");
    }      
    return getTime();
  }
}  

