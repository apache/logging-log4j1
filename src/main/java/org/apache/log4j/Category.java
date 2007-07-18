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


// Contibutors: Alex Blewitt <Alex.Blewitt@ioshq.com>
//              Markus Oestreicher <oes@zurich.ibm.com>
//              Frank Hoering <fhr@zurich.ibm.com>
//              Nelson Minar <nelson@media.mit.edu>
//              Jim Cakalic <jim_cakalic@na.biomerieux.com>
//              Avy Sharell <asharell@club-internet.fr>
//              Ciaran Treanor <ciaran@xelector.com>
//              Jeff Turner <jeff@socialchange.net.au>
//              Michael Horwitz <MHorwitz@siemens.co.za>
//              Calvin Chan <calvin.chan@hic.gov.au>
//              Aaron Greenhouse <aarong@cs.cmu.edu>
//              Beat Meier <bmeier@infovia.com.ar>
//              Colin Sampaleanu <colinml1@exis.com>
//              Andy McBride <andy.mcbride@pcmsgroup.com> 
package org.apache.log4j;

import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.helpers.ReaderWriterLock;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.MessageFormatter;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * <font color="#AA2222"><b>This class has been deprecated and replaced by the
 * {@link Logger}<em>subclass</em></b></font>. It will be kept around to
 * preserve backward compatibility until such time as the Log4j team sees fit
 * to remove it.
 *
 * <p>
 * <code>Logger</code> is a subclass of Category, i.e. it extends Category. In
 * other words, a logger <em>is</em> a category. Thus, all operations that
 * can be performed on a category can be performed on a logger. Whenever
 * log4j is asked to produce a Category object, it will instead produce a
 * Logger object. However, methods that previously accepted category objects
 * still continue to accept category objects.
 * </p>
 *
 * <p>
 * For example, the following are all legal and will work as expected.
 * <pre>
 *  &nbsp;&nbsp;&nbsp;// Deprecated form:
 *  &nbsp;&nbsp;&nbsp;Category cat = Category.getInstance("foo.bar")
 *  &nbsp;&nbsp;&nbsp;// Preferred form for retrieving loggers:
 *  &nbsp;&nbsp;&nbsp;Logger logger = Logger.getLogger("foo.bar")
 *  </pre>
 * </p>
 *
 * <p>
 * The first form is deprecated and should be avoided.
 * </p>
 *
 * <p>
 * <b>There is absolutely no need for new client code to use or refer to the
 * <code>Category</code> class.</b> Whenever possible, please avoid referring
 * to it or using it.
 * </p>
 *
 * <p>
 * See the <a href="../../../../manual.html">Short Manual</a> for an
 * introduction on this class.
 * </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 * @author Yoav Shapira
 */
public class Category implements ULogger, AppenderAttachable {
  /**
   * The fully qualified name of the Category class. See also the getFQCN
   * method.
   */
  private static final String FQCN = Category.class.getName();

  /**
   * The hierarchy where categories are attached to by default.
   */

  /**
   * The name of this category.
   */
  protected String name;

  /**
   * The assigned level of this category.  The <code>level</code> variable
   * need not be assigned a value in which case it is inherited form the
   * hierarchy.
   */
  protected volatile Level level;

  /**
   * The parent of this category. All categories have at least one ancestor
   * which is the root category.
   */
  protected volatile Category parent;
  protected ResourceBundle resourceBundle;
  protected ReaderWriterLock lock;

  // Categories need to know what Hierarchy they are in
  protected LoggerRepository repository;
  AppenderAttachableImpl aai;

  /**
   * Additivity is set to true by default, that is children inherit the
   * appenders of their ancestors by default. If this variable is set to
   * <code>false</code> then the appenders found in the ancestors of this
   * category are not used. However, the children of this category will
   * inherit its appenders, unless the children have their additivity flag
   * set to <code>false</code> too. See the user manual for more details.
   */
  protected boolean additive = true;

  /**
   * This constructor created a new <code>Category</code> instance and sets
   * its name.
   *
   * <p>
   * It is intended to be used by sub-classes only. You should not create
   * categories directly.
   * </p>
   *
   * @param name The name of the category.
   */
  protected Category(String name) {
    this.name = name;
    lock = new ReaderWriterLock();
  }

  /**
   * Add <code>newAppender</code> to the list of appenders of this Category
   * instance.
   *
   * <p>
   * If <code>newAppender</code> is already in the list of appenders, then it
   * won't be added again.
   * </p>
   */
  public void addAppender(Appender newAppender) {
    // BEGIN - WRITE LOCK
    lock.getWriteLock();
    try {
    if (aai == null) {
      aai = new AppenderAttachableImpl();
    }
    aai.addAppender(newAppender);
    } finally {
    lock.releaseWriteLock();
    }
    //	END - WRITE LOCK

    repository.fireAddAppenderEvent((Logger) this, newAppender);
  }

  /**
   * If <code>assertion</code> parameter is <code>false</code>, then logs
   * <code>msg</code> as an {@link #error(Object) error} statement.
   *
   * <p>
   * The <code>assert</code> method has been renamed to <code>assertLog</code>
   * because <code>assert</code> is a language reserved word in JDK 1.4.
   * </p>
   *
   * @param assertion
   * @param msg The message to print if <code>assertion</code> is false.
   *
   * @since 1.2
   */
  public void assertLog(boolean assertion, String msg) {
    if (!assertion) {
      this.error(msg);
    }
  }

  /**
   * Call the appenders in the hierrachy starting at <code>this</code>.  If no
   * appenders could be found, emit a warning.
   *
   * <p>
   * This method calls all the appenders inherited from the hierarchy
   * circumventing any evaluation of whether to log or not to log the
   * particular log request.
   * </p>
   *
   * @param event the event to log.
   */
  public void callAppenders(LoggingEvent event) {
    int writes = 0;

    for (Category c = this; c != null; c = c.parent) {
  	  	// Protect against simultaneous writes operations such as 
	   	  // addAppender, removeAppender,...
        c.lock.getReadLock();
    	try {
        if (c.aai != null) {
          writes += c.aai.appendLoopOnAppenders(event);
        }
	     } finally {
			   c.lock.releaseReadLock();
			 }
	    
       if (!c.additive) {
         break;
       }      
    }

    if (writes == 0) {
      repository.emitNoAppenderWarning((Logger) this);
    }
  }

  /**
   * Close all attached appenders implementing the AppenderAttachable
   * interface.
   *
   * @since 1.0
   */
  void closeNestedAppenders() {
    Enumeration enumeration = this.getAllAppenders();

    if (enumeration != null) {
      while (enumeration.hasMoreElements()) {
        Appender a = (Appender) enumeration.nextElement();

        if (a instanceof AppenderAttachable) {
          a.close();
        }
      }
    }
  }


  /**
   * Log a message object with the {@link Level#DEBUG DEBUG} level.
   *
   * <p>
   * This method first checks if this category is <code>DEBUG</code> enabled
   * by comparing the level of this category with the {@link Level#DEBUG
   * DEBUG} level. If this category is <code>DEBUG</code> enabled, then it
   * converts the message object (passed as parameter) to a string by
   * invoking the appropriate {@link org.apache.log4j.or.ObjectRenderer}. It
   * then proceeds to call all the registered appenders in this category and
   * also higher in the hierarchy depending on the value of the additivity
   * flag.
   * </p>
   *
   * <p>
   * <b>WARNING</b> Note that passing a {@link Throwable} to this method will
   * print the name of the <code>Throwable</code> but no stack trace. To
   * print a stack trace use the {@link #debug(Object, Throwable)} form
   * instead.
   * </p>
   *
   * @param message the message object to log.
   */
  public void debug(Object message) {
    if (repository.isDisabled(Level.DEBUG_INT)) {
      return;
    }

    if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.DEBUG, message, null);
    }
  }

  /**
   * Log a message object with the <code>DEBUG</code> level including the
   * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
   *
   * <p>
   * See {@link #debug(Object)} form for more detailed information.
   * </p>
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   */
  public void debug(Object message, Throwable t) {
    if (repository.isDisabled(Level.DEBUG_INT)) {
      return;
    }

    if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.DEBUG, message, t);
    }
  }
  
  /**
   * Log a message with the <code>DEBUG</code> level with message formatting
   * done according to the value of <code>messagePattern</code> and 
   * <code>arg</code> parameters.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg The argument to replace the formatting element, i,e, 
   * the '{}' pair within <code>messagePattern</code>.
   * @since 1.3
   */
  public void debug(Object messagePattern, Object arg) {
    if (repository.isDisabled(Level.DEBUG_INT)) {
      return;
    }
    
    if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
      if (messagePattern instanceof String){
        String msgStr = (String) messagePattern;
        msgStr = MessageFormatter.format(msgStr, arg);
        forcedLog(FQCN, Level.DEBUG, msgStr, null);
      } else {
        // To be failsafe, we handle the case where 'messagePattern' is not
        // a String. Unless the user makes a mistake, this should never happen.
        forcedLog(FQCN, Level.DEBUG, messagePattern, null);
      }
    }
  }
  
  /**
   * Log a message with the <code>DEBUG</code> level with message formatting
   * done according to the messagePattern and the arguments arg1 and arg2.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg1 The first argument to replace the first formatting element
   * @param arg2 The second argument to replace the second formatting element
   * @since 1.3
   */
  public void debug(String messagePattern, Object arg1, Object arg2) {
    if (repository.isDisabled(Level.DEBUG_INT)) {
      return;
    }
    if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
      messagePattern = MessageFormatter.format(messagePattern, arg1, arg2);
      forcedLog(FQCN, Level.DEBUG, messagePattern, null);
    }
  }
  
  /**
   * Check whether this category is enabled for the ERROR Level. See also
   * {@link #isDebugEnabled()}.
   *
   * @return boolean - <code>true</code> if this category is enabled for level
   *         ERROR, <code>false</code> otherwise.
   */
  public boolean isErrorEnabled() {
    if (repository.isDisabled(Level.ERROR_INT)) {
      return false;
    }

    return Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel());
  }

  /**
   * Log a message object with the {@link Level#ERROR ERROR} Level.
   *
   * <p>
   * This method first checks if this category is <code>ERROR</code> enabled
   * by comparing the level of this category with {@link Level#ERROR ERROR}
   * Level. If this category is <code>ERROR</code> enabled, then it converts
   * the message object passed as parameter to a string by invoking the
   * appropriate {@link org.apache.log4j.or.ObjectRenderer}. It proceeds to
   * call all the registered appenders in this category and also higher in
   * the hierarchy depending on the value of the additivity flag.
   * </p>
   *
   * <p>
   * <b>WARNING</b> Note that passing a {@link Throwable} to this method will
   * print the name of the <code>Throwable</code> but no stack trace. To
   * print a stack trace use the {@link #error(Object, Throwable)} form
   * instead.
   * </p>
   *
   * @param message the message object to log
   */
  public void error(Object message) {
    if (repository.isDisabled(Level.ERROR_INT)) {
      return;
    }

    if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.ERROR, message, null);
    }
  }

  /**
   * Log a message object with the <code>ERROR</code> level including the
   * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
   *
   * <p>
   * See {@link #error(Object)} form for more detailed information.
   * </p>
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   */
  public void error(Object message, Throwable t) {
    if (repository.isDisabled(Level.ERROR_INT)) {
      return;
    }

    if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.ERROR, message, t);
    }
  }

  /**
   * Log a message with the <code>ERROR</code> level with message formatting
   * done according to the value of <code>messagePattern</code> and 
   * <code>arg</code> parameters.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg The argument to replace the formatting element, i,e, 
   * the '{}' pair within <code>messagePattern</code>.
   * @since 1.3
   */
  public void error(Object messagePattern, Object arg) {
    if (repository.isDisabled(Level.ERROR_INT)) {
      return;
    }

    if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
      if (messagePattern instanceof String){
        String msgStr = (String) messagePattern;
        msgStr = MessageFormatter.format(msgStr, arg);
        forcedLog(FQCN, Level.ERROR, msgStr, null);
      } else {
        // To be failsafe, we handle the case where 'messagePattern' is not
        // a String. Unless the user makes a mistake, this should never happen.
        forcedLog(FQCN, Level.ERROR, messagePattern, null);
      }
    }
  }
  
  /**
   * Log a message with the <code>ERROR</code> level with message formatting
   * done according to the messagePattern and the arguments arg1 and arg2.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   *
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg1 The first argument to replace the first formatting element
   * @param arg2 The second argument to replace the second formatting element
   * @since 1.3
   */
  public void error(String messagePattern, Object arg1, Object arg2) {
    if (repository.isDisabled(Level.ERROR_INT)) {
      return;
    }
    if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
      messagePattern = MessageFormatter.format(messagePattern, arg1, arg2);
      forcedLog(FQCN, Level.ERROR, messagePattern, null);
    }
  }
  
  /**
   * If the named category exists (in the default hierarchy) then it returns a
   * reference to the category, otherwise it returns <code>null</code>.
   *
   * @since 0.8.5
   * @deprecated Please use {@link LogManager#exists} instead.
   */
  public static Logger exists(String name) {
    return LogManager.exists(name);
  }

  /**
   * Log a message object with the {@link Level#FATAL FATAL} Level.
   *
   * <p>
   * This method first checks if this category is <code>FATAL</code> enabled
   * by comparing the level of this category with {@link Level#FATAL FATAL}
   * Level. If the category is <code>FATAL</code> enabled, then it converts
   * the message object passed as parameter to a string by invoking the
   * appropriate {@link org.apache.log4j.or.ObjectRenderer}. It proceeds to
   * call all the registered appenders in this category and also higher in
   * the hierarchy depending on the value of the additivity flag.
   * </p>
   *
   * <p>
   * <b>WARNING</b> Note that passing a {@link Throwable} to this method will
   * print the name of the Throwable but no stack trace. To print a stack
   * trace use the {@link #fatal(Object, Throwable)} form instead.
   * </p>
   *
   * @param message the message object to log
   */
  public void fatal(Object message) {
    if (repository.isDisabled(Level.FATAL_INT)) {
      return;
    }

    if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.FATAL, message, null);
    }
  }
  
  /**
   * Log a message with the <code>FATAL</code> level with message formatting
   * done according to the value of <code>messagePattern</code> and 
   * <code>arg</code> parameters.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg The argument to replace the formatting element, i,e, 
   * the '{}' pair within <code>messagePattern</code>.
   * @since 1.3
   */
  public void fatal(Object messagePattern, Object arg) {
    if (repository.isDisabled(Level.FATAL_INT)) {
      return;
    }

    if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
      if (messagePattern instanceof String){
        String msgStr = (String) messagePattern;
        msgStr = MessageFormatter.format(msgStr, arg);
        forcedLog(FQCN, Level.FATAL, msgStr, null);
      } else {
        // To be failsafe, we handle the case where 'messagePattern' is not
        // a String. Unless the user makes a mistake, this should never happen.
        forcedLog(FQCN, Level.FATAL, messagePattern, null);
      }
    }
  }

  /**
   * Log a message object with the <code>FATAL</code> level including the
   * stack trace of the {@link Throwable}<code>t</code> passed as parameter.
   *
   * <p>
   * See {@link #fatal(Object)} for more detailed information.
   * </p>
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   */
  public void fatal(Object message, Throwable t) {
    if (repository.isDisabled(Level.FATAL_INT)) {
      return;
    }

    if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.FATAL, message, t);
    }
  }

  /**
   * This method creates a new logging event and logs the event without
   * further checks.
   */
  protected void forcedLog(
    String fqcn, Priority level, Object message, Throwable t) {
    callAppenders(new LoggingEvent(fqcn, (Logger) this, level, message, t));
  }

  /**
   * Get the additivity flag for this Category instance.
   */
  public boolean getAdditivity() {
    return additive;
  }

  /**
   * Get the appenders contained in this category as an {@link Enumeration}.
   * If no appenders can be found, then a {@link NullEnumeration} is
   * returned.
   *
   * @return Enumeration An enumeration of the appenders in this category.
   */
  public Enumeration getAllAppenders() {
	  Enumeration result;
  	lock.getReadLock();
    try {
    if (aai == null) {
      result = NullEnumeration.getInstance();
    } else {
      result = aai.getAllAppenders();
    }
    } finally {
    lock.releaseReadLock();
    }
    return result;
  }

  /**
   * Look for the appender named as <code>name</code>.
   *
   * <p>
   * Return the appender with that name if in the list. Return
   * <code>null</code> otherwise.
   * </p>
   */
  public Appender getAppender(String name) {
  	Appender result;
  	
  	lock.getReadLock();
  	try {
    if ((aai == null) || (name == null)) {
      result = null;
    } else {	
	    result = aai.getAppender(name);
    }	  
  	} finally {
	  lock.releaseReadLock();
  	}
	
    return result;
  }

  /**
   * Starting from this category, search the category hierarchy for a non-null
   * level and return it. Otherwise, return the level of the root category.
   *
   * <p>
   * The Category class is designed so that this method executes as quickly as
   * possible.
   * </p>
   */
  public Level getEffectiveLevel() {
    for (Category c = this; c != null; c = c.parent) {
      if (c.level != null) {
        return c.level;
      }
    }

    return null; // If reached will cause an NullPointerException.
  }

  /**
   * @deprecated Please use the the {@link #getEffectiveLevel} method instead.
   */
  public Priority getChainedPriority() {
    for (Category c = this; c != null; c = c.parent) {
      if (c.level != null) {
        return c.level;
      }
    }

    return null; // If reached will cause an NullPointerException.
  }

  /**
   * Returns all the currently defined categories in the default hierarchy as
   * an {@link java.util.Enumeration Enumeration}.
   *
   * <p>
   * The root category is <em>not</em> included in the returned {@link
   * Enumeration}.
   * </p>
   *
   * @deprecated Please use {@link LogManager#getCurrentLoggers()} instead.
   */
  public static Enumeration getCurrentCategories() {
    return LogManager.getCurrentLoggers();
  }

  /**
   * Return the default Hierarchy instance.
   *
   * @since 1.0
   * @deprecated Please use {@link LogManager#getLoggerRepository()} instead.
   */
  public static LoggerRepository getDefaultHierarchy() {
    return LogManager.getLoggerRepository();
  }

  /**
   * Return the the {@link Hierarchy} where this <code>Category</code>
   * instance is attached.
   *
   * @since 1.1
   * @deprecated Please use {@link #getLoggerRepository} instead.
   */
  public LoggerRepository getHierarchy() {
    return repository;
  }

  /**
   * Return the the {@link LoggerRepository} where this <code>Category</code>
   * is attached.
   *
   * @since 1.2
   */
  public LoggerRepository getLoggerRepository() {
    return repository;
  }

  /**
   * @deprecated Please use the {@link Logger#getLogger(String)} method instead.
   */
  public static Category getInstance(String name) {
    return LogManager.getLogger(name);
  }

  /**
   * @deprecated Please use {@link Logger#getLogger(Class)} instead.
   */
  public static Category getInstance(Class clazz) {
    return LogManager.getLogger(clazz);
  }

  /**
   * Return the category name.
   */
  public final String getName() {
    return name;
  }

  /**
   * Returns the parent of this category. Note that the parent of a given
   * category may change during the lifetime of the category.
   *
   * <p>
   * The root category will return <code>null</code>.
   * </p>
   *
   * @since 1.2
   */
  public final Category getParent() {
    return this.parent;
  }

  /**
   * Returns the assigned {@link Level}, if any, for this Category.
   *
   * @return Level - the assigned Level, can be <code>null</code>.
   */
  public final Level getLevel() {
    return this.level;
  }

  /**
   * @deprecated Please use {@link #getLevel} instead.
   */
  public final Level getPriority() {
    return this.level;
  }

  /**
   * @deprecated Please use the {@link Logger#getRootLogger()} method instead.
   */
  public static final Category getRoot() {
    return LogManager.getRootLogger();
  }

  /**
   * Return the <em>inherited</em>{@link ResourceBundle} for this category.
   *
   * <p>
   * This method walks the hierarchy to find the appropriate resource bundle.
   * It will return the resource bundle attached to the closest ancestor of
   * this category, much like the way priorities are searched. In case there
   * is no bundle in the hierarchy then <code>null</code> is returned.
   * </p>
   *
   * @since 0.9.0
   */
  public ResourceBundle getResourceBundle() {
    for (Category c = this; c != null; c = c.parent) {
      if (c.resourceBundle != null) {
        return c.resourceBundle;
      }
    }

    // It might be the case that there is no resource bundle
    return null;
  }

  /**
   * Returns the string resource coresponding to <code>key</code> in this
   * category's inherited resource bundle. See also {@link
   * #getResourceBundle}.
   *
   * <p>
   * If the resource cannot be found, then an {@link #error error} message
   * will be logged complaining about the missing resource.
   * </p>
   */
  protected String getResourceBundleString(String key) {
    ResourceBundle rb = getResourceBundle();

    // This is one of the rare cases where we can use logging in order
    // to report errors from within log4j.
    if (rb == null) {
      //if(!hierarchy.emittedNoResourceBundleWarning) {
      //error("No resource bundle has been set for category "+name);
      //hierarchy.emittedNoResourceBundleWarning = true;
      //}
      return null;
    } else {
      try {
        return rb.getString(key);
      } catch (MissingResourceException mre) {
        error("No resource is associated with key \"" + key + "\".");

        return null;
      }
    }
  }

  /**
   * Log a message object with the {@link Level#INFO INFO} Level.
   *
   * <p>
   * This method first checks if this category is <code>INFO</code> enabled by
   * comparing the level of this category with {@link Level#INFO INFO} Level.
   * If the category is <code>INFO</code> enabled, then it converts the
   * message object passed as parameter to a string by invoking the
   * appropriate {@link org.apache.log4j.or.ObjectRenderer}. It proceeds to
   * call all the registered appenders in this category and also higher in
   * the hierarchy depending on the value of the additivity flag.
   * </p>
   *
   * <p>
   * <b>WARNING</b> Note that passing a {@link Throwable} to this method will
   * print the name of the Throwable but no stack trace. To print a stack
   * trace use the {@link #info(Object, Throwable)} form instead.
   * </p>
   *
   * @param message the message object to log
   */
  public void info(Object message) {
    if (repository.isDisabled(Level.INFO_INT)) {
      return;
    }

    if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.INFO, message, null);
    }
  }

  /**
   * Log a message with the <code>INFO</code> level with message formatting
   * done according to the value of <code>messagePattern</code> and 
   * <code>arg</code> parameters.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg The argument to replace the formatting element, i,e, 
   * the '{}' pair within <code>messagePattern</code>.
   * @since 1.3
   */
  public void info(Object messagePattern, Object arg) {
    if (repository.isDisabled(Level.INFO_INT)) {
      return;
    }

    if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
      if (messagePattern instanceof String){
        String msgStr = (String) messagePattern;
        msgStr = MessageFormatter.format(msgStr, arg);
        forcedLog(FQCN, Level.INFO, msgStr, null);
      } else {
        // To be failsafe, we handle the case where 'messagePattern' is not
        // a String. Unless the user makes a mistake, this should never happen.
        forcedLog(FQCN, Level.INFO, messagePattern, null);
      }
    }
  }
  
  /**
   * Log a message with the <code>INFO</code> level with message formatting
   * done according to the messagePattern and the arguments arg1 and arg2.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   *
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg1 The first argument to replace the first formatting element
   * @param arg2 The second argument to replace the second formatting element
   * @since 1.3
   */
  public void info(String messagePattern, Object arg1, Object arg2) {
    if (repository.isDisabled(Level.INFO_INT)) {
      return;
    }
    if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
      messagePattern = MessageFormatter.format(messagePattern, arg1, arg2);
      forcedLog(FQCN, Level.INFO, messagePattern, null);
    }
  }
  
  
  /**
   * Log a message object with the <code>INFO</code> level including the stack
   * trace of the {@link Throwable}<code>t</code> passed as parameter.
   *
   * <p>
   * See {@link #info(Object)} for more detailed information.
   * </p>
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   */
  public void info(Object message, Throwable t) {
    if (repository.isDisabled(Level.INFO_INT)) {
      return;
    }

    if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.INFO, message, t);
    }
  }

  /**
   * Is the appender passed as parameter attached to this category?
   */
  public boolean isAttached(Appender appender) {
  	boolean result;

    lock.getReadLock();
    try {
    if ((appender == null) || (aai == null)) {
      result = false;
    } else {
      result = aai.isAttached(appender);
    }
    } finally {
    	lock.releaseReadLock();
    }
    
    return result;
  }

  /**
   * Check whether this category is enabled for the <code>DEBUG</code> Level.
   *
   * <p>
   * This function is intended to lessen the computational cost of disabled
   * log debug statements.
   * </p>
   *
   * <p>
   * For some <code>cat</code> Category object, when you write,
   * <pre>
   *      cat.debug("This is entry number: " + i );
   *  </pre>
   * </p>
   *
   * <p>
   * You incur the cost constructing the message, concatenatiion in this case,
   * regardless of whether the message is logged or not.
   * </p>
   *
   * <p>
   * If you are worried about speed, then you should write
   * <pre>
   *          if(cat.isDebugEnabled()) {
   *            cat.debug("This is entry number: " + i );
   *          }
   *  </pre>
   * </p>
   *
   * <p>
   * This way you will not incur the cost of parameter construction if
   * debugging is disabled for <code>cat</code>. On the other hand, if the
   * <code>cat</code> is debug enabled, you will incur the cost of evaluating
   * whether the category is debug enabled twice. Once in
   * <code>isDebugEnabled</code> and once in the <code>debug</code>.  This is
   * an insignificant overhead since evaluating a category takes about 1%% of
   * the time it takes to actually log.
   * </p>
   *
   * @return boolean - <code>true</code> if this category is debug enabled,
   *         <code>false</code> otherwise.
   */
  public boolean isDebugEnabled() {
    if (repository.isDisabled(Level.DEBUG_INT)) {
      return false;
    }

    return Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel());
  }

  /**
   * Check whether this category is enabled for the TRACE  Level. See also
   * {@link #isDebugEnabled()}.
   *
   * @return boolean - <code>true</code> if this category is enabled for level
   *         TRACE, <code>false</code> otherwise.
   */
  public boolean isTraceEnabled() {
      if (repository.isDisabled(Level.TRACE_INT)) {
          return false;
        }

        return Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel());
  }

  /**
   * Check whether this category is enabled for a given {@link Level} passed
   * as parameter. See also {@link #isDebugEnabled()}.
   *
   * @return boolean True if this logger is enabled for <code>level</code>.
   */
  public boolean isEnabledFor(Level level) {
    if (repository.isDisabled(level.level)) {
      return false;
    }

    return level.isGreaterOrEqual(this.getEffectiveLevel());
  }

  /**
   * @deprecated Use the alternate form taking a parameter of type Level.
   */
  public boolean isEnabledFor(Priority level) {
    return isEnabledFor((Level) level);
  }
  
  /**
   * Check whether this category is enabled for the info Level. See also
   * {@link #isDebugEnabled()}.
   *
   * @return boolean - <code>true</code> if this category is enabled for level
   *         info, <code>false</code> otherwise.
   */
  public boolean isInfoEnabled() {
    if (repository.isDisabled(Level.INFO_INT)) {
      return false;
    }

    return Level.INFO.isGreaterOrEqual(this.getEffectiveLevel());
  }
  
  /**
   * Log a localized message. The user supplied parameter <code>key</code> is
   * replaced by its localized version from the resource bundle.
   *
   * @see #setResourceBundle
   * @since 0.8.4
   */
  public void l7dlog(final Priority level, String key, Throwable t) {
    l7dlog(level, key, null, t);
  }

  /**
   * Log a localized message. The user supplied parameter <code>key</code> is
   * replaced by its localized version from the resource bundle.
   *
   * @see #setResourceBundle
   * @since 1.3
   */
  public void l7dlog(final Priority level, String key) {
    l7dlog(level, key, null, null);
  }

  /**
   * Log a localized and parameterized message. First, the user supplied
   * <code>key</code> is searched in the resource bundle. Next, the resulting
   * pattern is formatted using {@link
   * java.text.MessageFormat#format(String,Object[])} method with the user
   * supplied object array <code>params</code>.
   *
   * @since 0.8.4
   */
  public void l7dlog(Priority level, String key, Object[] params, Throwable t) {
    l7dlog(FQCN, level, key, params, t);    
  }

  /**
   * Log a localized and parameterized message. First, the user supplied
   * <code>key</code> is searched in the resource bundle. Next, the resulting
   * pattern is formatted using {@link
   * java.text.MessageFormat#format(String,Object[])} method with the user
   * supplied object array <code>params</code>.
   *
   * @since 1.3
   */
  public void l7dlog(Priority level, String key, Object[] params) {
    l7dlog(FQCN, level, key, params, null);
  }

  /**
   * @deprecated Use the form taking in a Level as a parameter.
   */
  public void log(Priority level, Object message, Throwable t) {
    log((Level) level, message, t);
  }
  
  /**
   * This generic form is intended to be used by wrappers.
   */
  public void log(Level level, Object message, Throwable t) {
    if (repository.isDisabled(level.level)) {
      return;
    }

    if (level.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, level, message, t);
    }
  }

  /**
   * @deprecated Use the form taking in a Level as a parameter.
   */
  public void log(Priority level, Object message) {
    log((Level) level, message);
   }
  
  /**
   * This generic form is intended to be used by wrappers. For the extraction
   * of caller information, use the most generic form {@link #log(
    String callerFQCN, Level level, Object message, Throwable t)}.
   */
   public void log(Level level, Object message) {
    if (repository.isDisabled(level.level)) {
      return;
    }

    if (level.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, level, message, null);
    }
  }

  /**
   * This is the most generic printing method. It is intended to be invoked by
   * <b>wrapper</b> classes.
   *
   * @param callerFQCN The wrapper class' fully qualified class name.
   * @param level The level of the logging request.
   * @param message The message of the logging request.
   * @param t The throwable of the logging request, may be null.
   */
  public void log(
    String callerFQCN, Level level, Object message, Throwable t) {
    if (repository.isDisabled(level.level)) {
      return;
    }

    if (level.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(callerFQCN, level, message, t);
    }
  }

  /**
   * This is the most generic localized printing method. It is intended to be invoked by
   * <b>wrapper</b> classes.
   *
   * @param callerFQCN The wrapper class' fully qualified class name.
   * @param level The level of the logging request.
   * @param key The key of the resource bundle message.
   * @param param Format parameteres, if null will not be used.
   * @param t The throwable of the logging request, may be null.
   * 
   * @since 1.3
   */
  public void l7dlog(   
    String callerFQCN, Priority level, String key, Object[] params, Throwable t)
  {
    if (repository.isDisabled(level.level)) {
      return;
    }

    if (level.isGreaterOrEqual(this.getEffectiveLevel())) {
      String pattern = getResourceBundleString(key);
      String msg;

      if (pattern == null) {
        msg = key;
      } else {
        if (params != null)
          msg = java.text.MessageFormat.format(pattern, params);
        else
          msg = pattern;
      }

      forcedLog(callerFQCN, level, msg, t);
    }
  }
  
  /**
   * @deprecated Use the form taking in a Level as a parameter.
   */
  public void log(
      String callerFQCN, Priority level, Object message, Throwable t) {
    log(callerFQCN, (Level) level, message, t);
  }
  

  
  /**
   * Remove all previously added appenders from this Category instance.
   * <p>Removed appenders are closed.</p>
   * <p>This is useful when re-reading configuration information.</p>
   */
  public void removeAllAppenders() {
    lock.getWriteLock();
    try {
    if (aai != null) {
      aai.removeAllAppenders();
      aai = null;
    }
    } finally {
    lock.releaseWriteLock();
  }
  }

  /**
   * Remove the appender passed as parameter form the list of appenders.
   *
   * <p>Does <em>not</em> close the appender.</p>
   *
   * @since 0.8.2
   */
  public void removeAppender(Appender appender) {
	  lock.getWriteLock();
	  try {
    if ((appender == null) || (aai == null)) {
      // Nothing to do
    } else {
  		aai.removeAppender(appender);
    }
	  } finally {
    lock.releaseWriteLock();
  }
  }

  /**
   * Remove the appender with the name passed as parameter form the list of
   * appenders.
   *
   *<p>Does <em>not</em> close the appender.</p>
   *
   * @since 0.8.2
   */
  public void removeAppender(String name) {
	  lock.getWriteLock();
	  try {
    if ((name == null) || (aai == null)) {
      // nothing to do
    } else {
      aai.removeAppender(name);
    }
	  } finally {
    lock.releaseWriteLock();
  }
  }

  /**
   * Set the additivity flag for this Category instance.
   *
   * @since 0.8.1
   */
  public void setAdditivity(boolean additive) {
    this.additive = additive;
  }

  /**
   * Only the Hiearchy class can set the hiearchy of a category. Default
   * package access is MANDATORY here.
   */
  final void setHierarchy(LoggerRepository repository) {
    this.repository = repository;
  }

  /**
   * Set the level of this Category. If you are passing any of
   * <code>Level.TRACE</code>, <code>Level.DEBUG</code>, 
   * <code>Level.INFO</code>, <code>Level.WARN</code>,
   * <code>Level.ERROR</code>, or <code>Level.FATAL</code>
   *  as a parameter, you need to case them as Level.
   *
   * <p>
   * As in
   * <pre> &nbsp;&nbsp;&nbsp;logger.setLevel((Level) Level.DEBUG); </pre>
   * </p>
   *
   * <p>
   * Null values are admitted.
   * </p>
   */
  public void setLevel(Level level) {
    this.level = level;
  }

  /**
   * Set the level of this Category.
   *
   * <p>
   * Null values are admitted.
   * </p>
   *
   * @deprecated Please use {@link #setLevel} instead.
   */
  public void setPriority(Priority priority) {
    this.level = (Level) priority;
  }

  /**
   * Set the resource bundle to be used with localized logging methods {@link
   * #l7dlog(Priority,String,Throwable)} and {@link
   * #l7dlog(Priority,String,Object[],Throwable)}.
   *
   * @since 0.8.4
   */
  public void setResourceBundle(ResourceBundle bundle) {
    resourceBundle = bundle;
  }

  /**
   * Calling this method will <em>safely</em> close and remove all appenders
   * in all the categories including root contained in the default hierachy.
   *
   * <p>
   * Some appenders such as {@link org.apache.log4j.net.SocketAppender} and
   * {@link AsyncAppender} need to be closed before the application exists.
   * Otherwise, pending logging events might be lost.
   * </p>
   *
   * <p>
   * The <code>shutdown</code> method is careful to close nested appenders
   * before closing regular appenders. This is allows configurations where a
   * regular appender is attached to a category and again to a nested
   * appender.
   * </p>
   *
   * @since 1.0
   * @deprecated Please use {@link LogManager#shutdown()} instead.
   */
  public static void shutdown() {
    LogManager.shutdown();
  }

  
  /**
   * Check whether this category is enabled for the WARN Level. See also
   * {@link #isDebugEnabled()}.
   *
   * @return boolean - <code>true</code> if this category is enabled for level
   *         WARN, <code>false</code> otherwise.
   */
  public boolean isWarnEnabled() {
    if (repository.isDisabled(Level.WARN_INT)) {
      return false;
    }

    return Level.WARN.isGreaterOrEqual(this.getEffectiveLevel());
  }

  
  /**
   * Log a message object with the {@link Level#WARN WARN} Level.
   *
   * <p>
   * This method first checks if this category is <code>WARN</code> enabled by
   * comparing the level of this category with {@link Level#WARN WARN} Level.
   * If the category is <code>WARN</code> enabled, then it converts the
   * message object passed as parameter to a string by invoking the
   * appropriate {@link org.apache.log4j.or.ObjectRenderer}. It proceeds to
   * call all the registered appenders in this category and also higher in
   * the hieararchy depending on the value of the additivity flag.
   * </p>
   *
   * <p>
   * <b>WARNING</b> Note that passing a {@link Throwable} to this method will
   * print the name of the Throwable but no stack trace. To print a stack
   * trace use the {@link #warn(Object, Throwable)} form instead.
   * </p>
   *
   * <p></p>
   *
   * @param message the message object to log.
   */
  public void warn(Object message) {
    if (repository.isDisabled(Level.WARN_INT)) {
      return;
    }

    if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.WARN, message, null);
    }
  }

  /**
   * Log a message with the <code>WARN</code> level including the stack trace
   * of the {@link Throwable}<code>t</code> passed as parameter.
   *
   * <p>
   * See {@link #warn(Object)} for more detailed information.
   * </p>
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   */
  public void warn(Object message, Throwable t) {
    if (repository.isDisabled(Level.WARN_INT)) {
      return;
    }

    if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
      forcedLog(FQCN, Level.WARN, message, t);
    }
  }
  
  /**
   * Log a message with the <code>WARN</code> level with message formatting
   * done according to the value of <code>messagePattern</code> and 
   * <code>arg</code> parameters.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   * 
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg The argument to replace the formatting element, i,e, 
   * the '{}' pair within <code>messagePattern</code>.
   * @since 1.3
   */
  public void warn(Object messagePattern, Object arg) {
    if (repository.isDisabled(Level.WARN_INT)) {
      return;
    }

    if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
      if (messagePattern instanceof String){
        String msgStr = (String) messagePattern;
        msgStr = MessageFormatter.format(msgStr, arg);
        forcedLog(FQCN, Level.WARN, msgStr, null);
      } else {
        // To be failsafe, we handle the case where 'messagePattern' is not
        // a String. Unless the user makes a mistake, this should never happen.
        forcedLog(FQCN, Level.WARN, messagePattern, null);
      }
    }
  }
  /**
   * Log a message with the <code>WARN</code> level with message formatting
   * done according to the messagePattern and the arguments arg1 and arg2.
   * <p>
   * This form avoids superflous parameter construction. Whenever possible,
   * you should use this form instead of constructing the message parameter 
   * using string concatenation.
   *
   * @param messagePattern The message pattern which will be parsed and formatted
   * @param arg1 The first argument to replace the first formatting element
   * @param arg2 The second argument to replace the second formatting element
   * @since 1.3
   */
  public void warn(String messagePattern, Object arg1, Object arg2) {
    if (repository.isDisabled(Level.WARN_INT)) {
      return;
    }
    if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
      messagePattern = MessageFormatter.format(messagePattern, arg1, arg2);
      forcedLog(FQCN, Level.WARN, messagePattern, null);
    }
  }
}
// End of class: Category.java
