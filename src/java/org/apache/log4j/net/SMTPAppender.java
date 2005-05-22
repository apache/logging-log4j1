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

package org.apache.log4j.net;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.pattern.PatternConverter;
import org.apache.log4j.pattern.PatternParser;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;
import org.apache.log4j.PatternLayout;


/**
   Send an e-mail when a specific logging event occurs, typically on
   errors or fatal errors.

   <p>The number of logging events delivered in this e-mail depend on
   the value of <b>BufferSize</b> option. The
   <code>SMTPAppender</code> keeps only the last
   <code>BufferSize</code> logging events in its cyclic buffer. This
   keeps memory requirements at a reasonable level while still
   delivering useful application context.
   
   <p>There are three ways in which the trigger is fired, resulting in an email
   containing the buffered events:
   
   <p>* DEFAULT BEHAVIOR: relies on an internal TriggeringEventEvaluator class that 
   triggers the sending of an email when an event with a severity of ERROR or greater is received.
   <p>* Set the 'evaluatorClass' param to the fully qualified class name of a class you 
   have written that implements the TriggeringEventEvaluator interface.
   <p>* Set the 'expression' param to a valid (infix) expression supported by ExpressionRule and 
   ExpressionRule's supported operators and operands.
   
   As events are received, events are evaluated against the expression rule.  An event
   that causes the rule to evaluate to true triggers the email send.
   
   If both evaluatorClass and expression params are set, the evaluatorClass is used.
   
   See org.apache.log4j.rule.ExpressionRule for a more information.
   
   <p>
   
   @author Ceki G&uuml;lc&uuml;
   @since 1.0 */
public class SMTPAppender extends AppenderSkeleton {
  private String to;
  private String from;
  private String subjectStr = "";
  private String smtpHost;
  private String charset = "ISO-8859-1";
  private int bufferSize = 512;
  private boolean locationInfo = false;
  protected CyclicBuffer cb = new CyclicBuffer(bufferSize);
  protected MimeMessage msg;
  protected TriggeringEventEvaluator evaluator;
  private PatternLayout subjectLayout;

  /**
     The default constructor will instantiate the appender with a
     {@link TriggeringEventEvaluator} that will trigger on events with
     level ERROR or higher.*/
  public SMTPAppender() {
    this(new DefaultEvaluator());
  }

  /**
     Use <code>evaluator</code> passed as parameter as the {@link
     TriggeringEventEvaluator} for this SMTPAppender.  */
  public SMTPAppender(final TriggeringEventEvaluator evaluator) {
    super(false);
    this.evaluator = evaluator;
  }

  /**
     Activate the specified options, such as the smtp host, the
     recipient, from, etc. */
  public void activateOptions() {
    int errorCount = 0;
    Properties props = new Properties(System.getProperties());

    if (smtpHost != null) {
      props.put("mail.smtp.host", smtpHost);
    }

    Session session = Session.getInstance(props, null);

    //session.setDebug(true);
    msg = new MimeMessage(session);

    try {
      if (from != null) {
        msg.setFrom(getAddress(from));
      } else {
        msg.setFrom();
      }

      msg.setRecipients(Message.RecipientType.TO, parseAddress(to));
    } catch (MessagingException e) {
      errorCount++;
      getLogger().error("Could not activate SMTPAppender options.", e);
    }

    if (subjectStr != null) {
        subjectLayout = new PatternLayout();
        subjectLayout.setConversionPattern(subjectStr);
        subjectLayout.setLoggerRepository(this.repository);
        subjectLayout.activateOptions();
    }

    if (this.evaluator == null) {
      errorCount++;
      String errMsg = "No TriggeringEventEvaluator is set for appender ["+getName()+"].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (this.layout == null) {
      errorCount++;
      String errMsg = "No layout set for appender named [" + name + "].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }
    
    if(errorCount == 0) {
      super.activateOptions();
    }
  }

  /**
     Perform SMTPAppender specific appending actions, mainly adding
     the event to a cyclic buffer and checking if the event triggers
     an e-mail to be sent. */
  public void append(LoggingEvent event) {
    if (!checkEntryConditions()) {
      return;
    }

    event.getThreadName();
    event.getNDC();

    if (locationInfo) {
      event.getLocationInformation();
    }

    cb.add(event);

    if (evaluator.isTriggeringEvent(event)) {
      sendBuffer(event);
    }
  }

  /**
      This method determines if there is a sense in attempting to append.

      <p>It checks whether there is a set output target and also if
      there is a set layout. If these checks fail, then the boolean
      value <code>false</code> is returned. */
  protected boolean checkEntryConditions() {
    if (this.msg == null) {
      return false;
    }

    if (this.evaluator == null) {
      return false;
    }

    if (this.layout == null) {
      return false;
    }

    return true;
  }

  public synchronized void close() {
    this.closed = true;
  }

  InternetAddress getAddress(String addressStr) {
    try {
      return new InternetAddress(addressStr);
    } catch (AddressException e) {
      getLogger().error(
        "Could not parse address [" + addressStr + "].", e);

      return null;
    }
  }

  InternetAddress[] parseAddress(String addressStr) {
    try {
      return InternetAddress.parse(addressStr, true);
    } catch (AddressException e) {
      getLogger().error(
        "Could not parse address [" + addressStr + "].", e);

      return null;
    }
  }

  /**
     Returns value of the <b>To</b> option.
   */
  public String getTo() {
    return to;
  }

  /**
     The <code>SMTPAppender</code> requires a {@link
     org.apache.log4j.Layout layout}.  */
  public boolean requiresLayout() {
    return true;
  }

  /**
     Send the contents of the cyclic buffer as an e-mail message.
   */
  protected void sendBuffer(LoggingEvent triggeringEvent) {
    // Note: this code already owns the monitor for this
    // appender. This frees us from needing to synchronize on 'cb'.
    try {
      MimeBodyPart part = new MimeBodyPart();
      
      String computedSubject = computeSubject(triggeringEvent);
      msg.setSubject(computedSubject, charset);
      
      StringBuffer sbuf = new StringBuffer();
      String t = layout.getHeader();

      if (t != null) {
        sbuf.append(t);
      }

      int len = cb.length();

      for (int i = 0; i < len; i++) {
        //sbuf.append(MimeUtility.encodeText(layout.format(cb.get())));
        LoggingEvent event = cb.get();
        sbuf.append(layout.format(event));

        if (layout.ignoresThrowable()) {
          String[] s = event.getThrowableStrRep();

          if (s != null) {
            for (int j = 0; j < s.length; j++) {
              sbuf.append(s[j]);
              sbuf.append(Layout.LINE_SEP);
            }
          }
        }
      }

      t = layout.getFooter();

      if (t != null) {
        sbuf.append(t);
      }

      part.setContent(sbuf.toString(), layout.getContentType() + ";charset=" + charset);

      Multipart mp = new MimeMultipart();
      mp.addBodyPart(part);
      msg.setContent(mp);

      msg.setSentDate(new Date());
      Transport.send(msg);
    } catch (Exception e) {
      getLogger().error("Error occured while sending e-mail notification.", e);
    }
  }

  String computeSubject(LoggingEvent triggeringEvent) {
      if (subjectLayout != null) {
          return subjectLayout.format(triggeringEvent);
      }
      return null;
  }

  /**
     Returns value of the <b>EvaluatorClass</b> option.
   */
  public String getEvaluatorClass() {
    return (evaluator == null) ? null : evaluator.getClass().getName();
  }

  /**
     Returns value of the <b>From</b> option.
   */
  public String getFrom() {
    return from;
  }
  
  /**
     Returns value of the <b>Subject</b> option.
   */
  public String getSubject() {
    return subjectStr;
  }

  /**
     The <b>From</b> option takes a string value which should be a
     e-mail address of the sender.
   */
  public void setFrom(String from) {
    this.from = from;
  }

  /**
   * The <b>Subject</b> option takes a string value which will be the subject 
   * of the e-mail message. This value can be string literal or a conversion 
   * pattern in the same format as expected by 
   * {@link org.apache.log4j.PatternLayout}.
   * 
   * <p>The conversion pattern is applied on the triggering event to dynamically
   * compute the subject of the outging email message. For example, setting 
   * the <b>Subject</b> option to "%properties{host} - %m"
   * will set the subject of outgoing message to the "host" property of the 
   * triggering event followed by the message of the triggering event.
   */
  public void setSubject(String subject) {
    this.subjectStr = subject;
  }

  /**
     The <b>BufferSize</b> option takes a positive integer
     representing the maximum number of logging events to collect in a
     cyclic buffer. When the <code>BufferSize</code> is reached,
     oldest events are deleted as new events are added to the
     buffer. By default the size of the cyclic buffer is 512 events.
   */
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    cb.resize(bufferSize);
  }

  /**
     The <b>SMTPHost</b> option takes a string value which should be a
     the host name of the SMTP server that will send the e-mail message.
   */
  public void setSMTPHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  /**
     Returns value of the <b>SMTPHost</b> option.
   */
  public String getSMTPHost() {
    return smtpHost;
  }

  /**
     The <b>To</b> option takes a string value which should be a
     comma separated list of e-mail address of the recipients.
   */
  public void setTo(String to) {
    this.to = to;
  }

  /**
     Returns value of the <b>BufferSize</b> option.
   */
  public int getBufferSize() {
    return bufferSize;
  }

  /**
   * The <b>EvaluatorClass</b> option takes a string value representing the 
   * name of the class implementing the {@link TriggeringEventEvaluator} 
   * interface. A corresponding object will be instantiated and assigned as 
   * the triggering event evaluator for the SMTPAppender.
   * 
   * @deprecated replaced by {@link #setEvaluator}.
   */
  public void setEvaluatorClass(String value) {
    getLogger().warn("The SMPTAppender.setEvaluatorClass is deprecated.");
    getLogger().warn("It has been replaced with the more powerful SMPTAppender.setEvaluator method.");
    evaluator =
      (TriggeringEventEvaluator) OptionConverter.instantiateByClassName(
        value, TriggeringEventEvaluator.class, evaluator);
  }

  /**
   * Set {@link TriggeringEventEvaluator} for this instance of SMTPAppender.
   */
  public void setEvaluator(TriggeringEventEvaluator evaluator) {
    this.evaluator = evaluator;
  }


  /**
     The <b>LocationInfo</b> option takes a boolean value. By
     default, it is set to false which means there will be no effort
     to extract the location information related to the event. As a
     result, the layout that formats the events as they are sent out
     in an e-mail is likely to place the wrong location information
     (if present in the format).

     <p>Location information extraction is comparatively very slow and
     should be avoided unless performance is not a concern.
   */
  public void setLocationInfo(boolean locationInfo) {
    this.locationInfo = locationInfo;
  }

  /**
     Returns value of the <b>LocationInfo</b> option.
   */
  public boolean getLocationInfo() {
    return locationInfo;
  }

    /**
     * Set charset for messages: ensure the charset
     * you are using is available on your platform.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Returns the charset for messages.  The default
     * is "ISO-8859-1."  This method should not return
     * null.
     */
    public String getCharset() {
        return charset;
     }

}


class DefaultEvaluator extends ComponentBase implements TriggeringEventEvaluator {

  private Rule expressionRule;
  private String expression;
  
  public DefaultEvaluator() {}
  
  public void setExpression(String expression) {
    this.expression = expression;
  }
  
  public void activateOptions() {
    if(expression != null) {
      try {
        expressionRule = ExpressionRule.getRule(expression);
      } catch (IllegalArgumentException iae) {
        getLogger().error("Unable to use provided expression - falling back to default behavior (trigger on ERROR or greater severity)", iae);
      }
    }
  }
  
  /**
     Is this <code>event</code> the e-mail triggering event?

     <p>This method returns <code>true</code>, if the event level
     has ERROR level or higher. Otherwise it returns
     <code>false</code>. */
  public boolean isTriggeringEvent(LoggingEvent event) {
    if (expressionRule == null) {
      return event.getLevel().isGreaterOrEqual(Level.ERROR);
    }
    return expressionRule.evaluate(event);
  }
}
