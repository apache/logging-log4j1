/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.APL file.  */

package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.TriggeringEventEvaluator;
import java.util.Properties;
import java.util.Date;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeUtility;

/**
   Send an e-mail when a specific logging event occurs, typically on
   errors or fatal errors.

   <p>The number of logging events delivered in this e-mail depend on
   the value of <b>BufferSize</b> option. The
   <code>SMTPAppender</code> keeps only the last
   <code>BufferSize</code> logging events in its cyclic buffer. This
   keeps memory requirements at a reasonable level while still
   delivering useful application context.
   
   @author Ceki G&uuml;lc&uuml;
   @since 1.0 */
public class SMTPAppender extends AppenderSkeleton {


 /**
     A string constant used in naming the <em>To</em> field of
     outgoing e-mail output file. Current value of this string
     constant is <b>To</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String TO_OPTION = "To";

 /**
     A string constant used in naming the <em>From</em> field of
     outgoing e-mail output file. Current value of this string
     constant is <b>From</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String FROM_OPTION = "From";

 /**
     A string constant used in naming the <em>Subject</em> field of
     outgoing e-mail output file. Current value of this string
     constant is <b>Subject</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String SUBJECT_OPTION = "Subject";


 /**
     A string constant used in naming the SMTP host that will be
     contacted to send the e-mail. Current value of this string
     constant is <b>SMTPHost</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String SMTP_HOST_OPTION = "SMTPHost";

 /**
     A string constant used in naming the cyclic buffer size option.
     Current value of this string constant is <b>BufferSize</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String BUFFER_SIZE_OPTION = "BufferSize";


 /**
     A string constant used in naming the class of the
     TriggeringEventEvaluator that this SMTPApepdner wll use. Current
     value of this string constant is <b>EvaluatorClass</b>.

     <p>Note that all option keys are case sensitive.
     
  */
  public static final String EVALUATOR_CLASS_OPTION = "EvaluatorClass";


  /**
     A string constant used in naming the option for setting the the
     location information flag.  Current value of this string
     constant is <b>LocationInfo</b>.  

     <p>Note that all option keys are case sensitive.
  */
  public static final String LOCATION_INFO_OPTION = "LocationInfo";

  String to;
  String from;
  String subject;
  String smtpHost;
  int bufferSize = 512;

  CyclicBuffer cb = new CyclicBuffer(bufferSize);
  Session session;
  Message msg;
  boolean locationInfo = false;
  
  TriggeringEventEvaluator evaluator;



  /**
     The default constructor will instantiate the appedner with a
     {@link TriggeringEventEvaluator} that will tirgger on events with
     priority ERROR or higher.*/
  public
  SMTPAppender() {
    this(new DefaultEvaluator());
  }

  
  /**
     Use <code>evaluator</code> passed as parameter as the {@link
     TriggeringEventEvaluator} for this SMTPAppender.  */
  public 
  SMTPAppender(TriggeringEventEvaluator evaluator) {
    this.evaluator = evaluator;
  }


  public
  void activateOptions() {
    Properties props = System.getProperties();
    if (smtpHost != null)
      props.put("mail.smtp.host", smtpHost);

     session = Session.getDefaultInstance(props, null);
     //session.setDebug(true);
     msg = new MimeMessage(session);
     
     try {
       if (from != null)
	 msg.setFrom(getAddress(from));
       else
	 msg.setFrom();

       msg.setRecipients(Message.RecipientType.TO, parseAddress(to));
       if(subject != null)
	 msg.setSubject(subject);
     } catch(MessagingException e) {
       LogLog.error("Could not activate SMTPAppender options.", e );
     }
  }
  
  /**
     Perform SMTPAppender specific appending actions, mainly adding
     the event to a cyclic buffer and checking if the event triggers
     an e-mail to be sent. */
  public
  void append(LoggingEvent event) {

    if(!checkEntryConditions()) {
      return;
    }

    event.getThreadName();
    event.getNDC();
    if(locationInfo) {
      event.setLocationInformation();	
    }
    cb.add(event);    
    if(evaluator.isTriggeringEvent(event)) {
      sendBuffer();
    }
  }

 /**
     This method determines if there is a sense in attempting to append.
     
     <p>It checks whether there is a set output target and also if
     there is a set layout. If these checks fail, then the boolean
     value <code>false</code> is returned. */
  protected
  boolean checkEntryConditions() {
    if(this.msg == null) {
      errorHandler.error("Message object not configured.");
      return false;
    }

    if(this.evaluator == null) {
      errorHandler.error("No TriggeringEventEvaluator is set for appender ["+
			 name+"].");
      return false;
    }

    
    if(this.layout == null) {
      errorHandler.error("No layout set for appender named ["+name+"].");
      return false;
    }
    return true;
  }


  public
  void close() {
    this.closed = true;
  }

  InternetAddress getAddress(String addressStr) {
    try {
      return new InternetAddress(addressStr);
    } catch(AddressException e) {
      errorHandler.error("Could not parse address ["+addressStr+"].", e,
			 ErrorCode.ADDRESS_PARSE_FAILURE);
      return null;
    }
  }

 /**
     Retuns the option names for this component in addition in
     addition to the options of its super class {@link
     AppenderSkeleton}.  */
  public
  String[] getOptionStrings() {
    return OptionConverter.concatanateArrays(super.getOptionStrings(),
          new String[] {TO_OPTION, FROM_OPTION, SUBJECT_OPTION, 
			  SMTP_HOST_OPTION, BUFFER_SIZE_OPTION,  
			  EVALUATOR_CLASS_OPTION, LOCATION_INFO_OPTION });
  }
  
  InternetAddress[] parseAddress(String addressStr) {
    try {
      return InternetAddress.parse(addressStr, true);
    } catch(AddressException e) {
      errorHandler.error("Could not parse address ["+addressStr+"].", e,
			 ErrorCode.ADDRESS_PARSE_FAILURE);
      return null;
    }

  }

  /**
     The <code>SMTPAppender</code> requires a {@link Layout layout}.  
  */
  public
  boolean requiresLayout() {
    return true;
  }

  /**
     Send the contents of the cyclic buffer as an e-mail message.
   */
  protected
  void sendBuffer() {
    try {      
      MimeBodyPart part = new MimeBodyPart();

      StringBuffer sbuf = new StringBuffer();
      String t = layout.getHeader();
      if(t != null)
	sbuf.append(t);
      int len =  cb.length();
      for(int i = 0; i < len; i++) {
	//sbuf.append(MimeUtility.encodeText(layout.format(cb.get())));
	sbuf.append(layout.format(cb.get()));
      }
      t = layout.getFooter();
      if(t != null)
	sbuf.append(t);
      part.setContent(sbuf.toString(), layout.getContentType());      

      Multipart mp = new MimeMultipart();
      mp.addBodyPart(part);
      msg.setContent(mp);

      msg.setSentDate(new Date());
      Transport.send(msg);      
    } catch(Exception e) {
      LogLog.error("Error occured while sending e-mail notification.", e);
    }
  }
  
 /**
     Set SMTPAppender specific options.

     <p>On top of the options of the super class {@link
     AppenderSkeleton}, the recognized options are <b>To</b>,
     <b>From</b>, <b>Subject</b>, <b>SMTPHost</b>,
     <b>BufferSize</b>, <b>EvaluatorClass</b> and <b>LocationInfo</b>. 
     
     <p>The <b>To</b> option takes a string value which should be a
     comma separated list of e-mail address of the recipients.

     <p>The <b>From</b> option takes a string value which should be a
     e-mail address of the sender.

     <p>The <b>Subject</b> option takes a string value which should be a
     the subject of the e-mail message.

     <p>The <b>SMTPHost</b> option takes a string value which should be a
     the host name of the SMTP server that will send the e-mail message.

     <p>The <b>BufferSize</b>option takes a positive integer
     representing the maximum number of logging events to collect in a
     cyclic buffer. When the <code>BufferSize</code> is reached,
     oldest events are deleted as new events are added to the
     buffer. By default the size of the cyclic buffer is 512 events.

     <p>The <b>EvaluatorClass</b> option takes a string value
     repsenting the name of the class implementing the {@link
     TriggeringEventEvaluator} interface. A corresponding object will
     be instantiated and assigned as the triggering event evaluator
     for the SMTPAppender.

     <p>The <b>LocationInfo</b> option takes a boolean value. By
     default, it is set to false which means there will be no effort
     to extract the location information related to the event. As a
     result, the layout that formats the events as they are sent out
     in an e-mail is likely to place the wrong location information
     (if present in the format).

     <p>Location information extraction is comparatively very slow and
     should be avoided unless performance is not a concern.

 */
  public
  void setOption(String option, String value) {
    if(value == null) return;
    super.setOption(option, value);    

    if(option.equals(TO_OPTION)) 
      to = value;
    else if (option.equals(FROM_OPTION))
      from = value;
    else if (option.equals(SMTP_HOST_OPTION)) 
      smtpHost = value;
    else if (option.equals(SUBJECT_OPTION)) 
      subject = value;
    else if (option.equals(EVALUATOR_CLASS_OPTION)) {      
      evaluator = (TriggeringEventEvaluator) 
                OptionConverter.instantiateByClassName(value, 
					   TriggeringEventEvaluator.class,
						       evaluator);    
    }
    else if (option.equals(BUFFER_SIZE_OPTION)) {
      bufferSize = OptionConverter.toInt(value, bufferSize);    
      cb.resize(bufferSize);
    }
    else if (option.equals(LOCATION_INFO_OPTION))
      locationInfo = OptionConverter.toBoolean(value, locationInfo);
  }
}

class DefaultEvaluator implements TriggeringEventEvaluator {
  /**
     Is this <code>event</code> the e-mail triggering event?
     
     <p>This method returns <code>true</code>, if the event priority
     has ERROR priority or higher. Otherwisem it returns
     <code>false</code>. */
  public 
  boolean isTriggeringEvent(LoggingEvent event) {
    return event.priority.isGreaterOrEqual(Priority.ERROR); 
  }
}
