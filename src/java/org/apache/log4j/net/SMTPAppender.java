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

package org.apache.log4j.net;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;


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
   The JavaMail session is obtained through {@link #setSessionJNDI(String) JNDI} or by
   directly calling {@link Session#getDefaultInstance(Properties) and setting
   various addressing details on this object.
   The former method is preferred for application servers,
   the latter for stand-alone usage.
   </p>
   
   @author Ceki G&uuml;lc&uuml;
   @since 1.0 */
public class SMTPAppender extends AppenderSkeleton {
  
  /**
   * JavaMail session.
   */
  private Session session;
  
  private String to;
  /**
   * Comma separated list of cc recipients.
   */
  private String cc;  
  /**
   * Comma separated list of bcc recipients.
   */
  private String bcc;
  private String from;
  private String subjectStr = "";
  private String smtpHost;
  private int    smtpPort;
  private String smtpUsername;
  private String smtpPassword;
  private String sessionJNDI;
  private boolean smtpDebug = false;
  private String charset = "ISO-8859-1";
  private int bufferSize = 512;
  private boolean locationInfo = false;
  protected CyclicBuffer cb = new CyclicBuffer(bufferSize);
  protected Message msg;
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
    if (sessionJNDI != null) {
      try {
        session = lookupSession();
        if (session == null)
          throw new NameNotFoundException();
      } catch (NamingException e) {
        throw new IllegalStateException("Failed finding javax.mail.Session: " + sessionJNDI + " Reason: " + e);
      }
    } else {
      session = createSession();
    }
    
    msg = new MimeMessage(session);
    try {
      addressMessage(msg);
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
      String errMsg = "No TriggeringEventEvaluator is set for appender ["+getName()+"].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (this.layout == null) {
      String errMsg = "No layout set for appender named [" + name + "].";
      getLogger().error(errMsg);
      throw new IllegalStateException(errMsg);
    }
    
    if (errorCount == 0) {
      super.activateOptions();
    }
  }
  
  private Session lookupSession() throws NamingException {
    Context c = new InitialContext();
    try {
      return (Session) c.lookup(sessionJNDI);
    } finally {
      c.close();
    }
  }

  /**
   *   Address message.
   *   @param msg message, may not be null.
   *   @throws MessagingException thrown if error addressing message. 
   */
  protected void addressMessage(final Message msg) throws MessagingException {
    if (from != null) {
      msg.setFrom(getAddress(from));
    } else {
      msg.setFrom();
    }

    if (to != null && to.length() > 0) {
      msg.setRecipients(Message.RecipientType.TO, parseAddress(to));
    }

    //Add CC receipients if defined.
    if (cc != null && cc.length() > 0) {
      msg.setRecipients(Message.RecipientType.CC, parseAddress(cc));
    }

    //Add BCC receipients if defined.
    if (bcc != null && bcc.length() > 0) {
      msg.setRecipients(Message.RecipientType.BCC, parseAddress(bcc));
    }
  }

  /**
   * Returns a new mail session, using properties from the system.
   */
  protected Session createSession() {
    Properties props;
    try {
        props = new Properties (System.getProperties());
    } catch(SecurityException ex) {
        props = new Properties();
    }
    if (smtpHost != null) {
      props.put("mail.smtp.host", smtpHost);
    }
    if (smtpPort != 0) {
      props.put("mail.smtp.port", String.valueOf(smtpPort));
    }
    
    Authenticator auth = null;
    if(smtpPassword != null && smtpUsername != null) {
      props.put("mail.smtp.auth", "true");
      auth = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(smtpUsername, smtpPassword);
        }
      };
    }
    Session session = Session.getInstance(props, auth);
    if (smtpDebug) {
        session.setDebug(smtpDebug);
    }
    return session;
  }


  /**
     Perform SMTPAppender specific appending actions, mainly adding
     the event to a cyclic buffer and checking if the event triggers
     an e-mail to be sent. */
  public void append(LoggingEvent event) {
    if (!checkEntryConditions()) {
      return;
    }

    event.prepareForDeferredProcessing();
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
      
      if (msg instanceof MimeMessage) {
        String computedSubject = computeSubject(triggeringEvent);
        ((MimeMessage) msg).setSubject(computedSubject, charset);
      }
      
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

   /**
      Set the cc recipient addresses.
      @param addresses recipient addresses as comma separated string, may be null.
    */
   public void setCc(final String addresses) {
     this.cc = addresses;
   }

   /**
      Get the cc recipient addresses.
      @return recipient addresses as comma separated string, may be null.
    */
    public String getCc() {
     return cc;
    }

   /**
      Set the bcc recipient addresses.
      @param addresses recipient addresses as comma separated string, may be null.
    */
   public void setBcc(final String addresses) {
     this.bcc = addresses;
   }

   /**
      Get the bcc recipient addresses.
      @return recipient addresses as comma separated string, may be null.
    */
    public String getBcc() {
     return bcc;
    }

  /**
   * The <b>SmtpPassword</b> option takes a string value which should be the password required to authenticate against
   * the mail server.
   * @param password password, may be null.
   */
  public void setSMTPPassword(final String password) {
    this.smtpPassword = password;
  }
 
  /**
   * The <b>SmtpUsername</b> option takes a string value which should be the username required to authenticate against
   * the mail server.
   * @param username user name, may be null.
   */
  public void setSMTPUsername(final String username) {
    this.smtpUsername = username;
  }

  /**
   * Setting the <b>SmtpDebug</b> option to true will cause the mail session to log its server interaction to stdout.
   * This can be useful when debuging the appender but should not be used during production because username and
   * password information is included in the output.
   * @param debug debug flag.
   */
  public void setSMTPDebug(final boolean debug) {
    this.smtpDebug = debug;
  }
  
  /**
   * Get SMTP password.
   * @return SMTP password, may be null.
   */
  public String getSMTPPassword() {
    return smtpPassword;
  }
 
  /**
   * Get SMTP user name.
   * @return SMTP user name, may be null.
   */
  public String getSMTPUsername() {
    return smtpUsername;
  }

  /**
   * Get SMTP debug.
   * @return SMTP debug flag.
   */
  public boolean getSMTPDebug() {
    return smtpDebug;
  }

  /**
   * Returns the session JNDI entry name.
   * This is useful for application servers.
   */
  public String getSessionJNDI() {
    return sessionJNDI;
  }

  /**
   * Sets the session JNDI entry name.
   */
  public void setSessionJNDI(String sessionJndiLocation) {
    this.sessionJNDI = sessionJndiLocation;
  }

  /**
   * Returns the SMTP port to use.
   */
  public int getSMTPPort() {
    return smtpPort;
  }

  /**
   * Sets the SMTP port to use.
   */
  public void setSMTPPort(int smtpPort) {
    this.smtpPort = smtpPort;
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
