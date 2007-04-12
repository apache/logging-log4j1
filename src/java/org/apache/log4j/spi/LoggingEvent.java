/*
 * Copyright 1999,2006 The Apache Software Foundation.
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
package org.apache.log4j.spi;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.spi.LocationInfo;


// Contributors:   Nelson Minar <nelson@monkey.org>
//                 Wolf Siberski
//                 Anders Kristensen <akristensen@dynamicsoft.com>

/**
 * The internal representation of logging events. When an affirmative decision
 * is made to log then a <code>LoggingEvent</code> instance is created. This
 * instance is passed around to the different log4j components.
 *
 * <p>
 * This class is of concern to those wishing to extend log4j.
 * </p>
 *
 * <p>Writers of log4j components such as appenders and receivers should be 
 * aware of that some of the LoggingEvent fields are initialized lazily. 
 * Therefore, an appender wishing to output data to be later correctly read
 * by a receiver, must initialize "lazy" fields prior to writing them out.   
 * See the {@link #prepareForDeferredProcessing()} method for the exact list.</p>
 * 
 * <p>Moreover, in the absence of certain fields, receivers must set the
 * values of null fields to a default non-null value. For example, in the 
 * absence of the locationInfo data, the locationInfo field should be
 * set to {@link org.apache.log4j.spi.LocationInfo#NA_LOCATION_INFO
 * LocationInfo.NA_LOCATION_INFO}.
 * 
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @author James P. Cakalic
 */
public class LoggingEvent
       implements java.io.Serializable {
  private static long startTime = System.currentTimeMillis();

  // Serialization
  static final long serialVersionUID = -868428216207166145L;
  static final Integer[] PARAM_ARRAY = new Integer[1];
  static final String TO_LEVEL = "toLevel";
  static final Class[] TO_LEVEL_PARAMS = new Class[] { int.class };
  static final Hashtable methodCache = new Hashtable(3);    // use a tiny table

  /**
   * LoggingEvent are stamped with a {@link #sequenceNumber}. The
   * <code>sequenceCount</code> static variable keeps track of the current count.
   *
   * The count starts at 1 (one).
   *
   * @since 1.3
   */
  private static long sequenceCount = 1;

  /**
   * Fully qualified name of the calling category class. This field does not
   * survive serialization. 
   * 
   * <p>Note that the getLocationInfo() method relies on this fact.
   */
  public transient String fqnOfCategoryClass;

  /**
   * The category of the logging event. This field is not serialized for
   * performance reasons.
   *
   * <p>
   * It is set by the LoggingEvent constructor or set by a remote entity after
   * deserialization.
   * </p>
   */
  private transient Category logger;

  /** 
   * The logger name.
   *
   * the 'logger name' variable name ("categoryName") must remain the same 
   * as prior versions in order to maintain serialization compatibility with 
   * log4j 1.2.8
   */
  public String categoryName;

  /**
   * Level of logging event. Level cannot be serializable because it is a
   * flyweight.  Due to its special seralization it cannot be declared final
   * either.
   *
   * <p>
   * This field should not be accessed directly. You shoud use the {@link
   * #getLevel} method instead.
   * </p>
   *
   */
  public transient Priority level;

  /**
   * The nested diagnostic context (NDC) of logging event.
   */
  private String ndc;

  /**
   * <p>The properties map is specific for this event.</p>
   *
   * <p>When serialized, it contains a copy of MDC properties as well
   * as LoggerRepository properties.
   * </p>
   *
   * <p>It survives serialization.</p>
   * @since 1.3
   */
  private Map properties;

  /**
   * Have we tried to do an NDC lookup? If we did, there is no need to do it
   * again.  Note that its value is always false when serialized. Thus, a
   * receiving SocketNode will never use it's own (incorrect) NDC. See also
   * writeObject method.
   */
  private boolean ndcLookupRequired = true;

  /**
   * The application supplied message of logging event.
   */
  private transient Object message;

  /**
   * The application supplied message rendered through the log4j objet
   * rendering mechanism.
   */
  private String renderedMessage;

  /**
   * The name of thread in which this logging event was generated.
   */
  private String threadName;

  /**
   * This variable contains information about this event's throwable
   */
  private ThrowableInformation throwableInfo;

  /**
   * The number of milliseconds elapsed from 1/1/1970 until logging event was
   * created.
   */
  public long timeStamp;

  /**
   * A unique sequence number for this logging event. 
   *
   * @since 1.3
   */
  private long sequenceNumber;

  /**
   * Location information for the caller.
   */
  private LocationInfo locationInfo;

  /**
   * Returns the sequence count for this JVM.
   * @return the current sequence count for this JVM
   */
  public static synchronized long getSequenceCount() {
    return sequenceCount;
  }
  
  /**
   * The no-argument constructor for LoggingEvent. This method is the recommended
   * constructor for creating LoggingEvent instances.
   *
   * @since 1.3
   */
  public LoggingEvent() {
  }

  /**
   * Instantiate a LoggingEvent from the supplied parameters.
   *
   * <p>Note that many of the LoggingEvent fields are initialized when actually 
   * needed. For more information please refer to the comments for the 
   * LoggingEvent class at the top of this page.
   * </p>
   *
   * @param logger The logger of this event.
   * @param level The level of this event.
   * @param message The message of this event.
   * @param throwable The throwable of this event.
   */
  public LoggingEvent(String fqnOfLoggerClass, Category logger, Priority level, Object message, Throwable throwable) {
    this(fqnOfLoggerClass, logger, System.currentTimeMillis(), level, message, throwable);
  }

  /**
   * Instantiate a LoggingEvent from the supplied parameters.
   *
   * <p>Note that many of the LoggingEvent fields are initialized lazily. For 
   * more information please refer to the comments for the LoggingEvent class 
   * at the top of this page.
   * </p>
   *
   * @param fqnOfCategoryClass The category of this event.
   * @param logger The logger
   * @param timeStamp the timestamp of this logging event
   * @param level The level of this event.
   * @param message The message of this event.
   * @param throwable The throwable of this event.
   * @deprecated Please use the no argument constructor and the setter methods
   * instead.
   */
  public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message,
    Throwable throwable) {
    this.fqnOfCategoryClass = fqnOfCategoryClass;
    this.logger = logger;
    this.categoryName = logger.getName();
    this.level = level;
    this.message = message;

    if (throwable != null) {
      this.throwableInfo = new ThrowableInformation(throwable);
    }

    this.timeStamp = timeStamp;
    synchronized(LoggingEvent.class) {
      sequenceNumber = sequenceCount++;
    }
  }

    /**
       Create new instance.
       @since 1.2.15
       @param fqnOfCategoryClass Fully qualified class name
                 of Logger implementation.
       @param logger The logger generating this event.
       @param timeStamp the timestamp of this logging event
       @param level The level of this event.
       @param message  The message of this event.
       @param threadName thread name
       @param throwable The throwable of this event.
       @param ndc Nested diagnostic context
       @param info Location info
       @param properties MDC properties
     */
    public LoggingEvent(final String fqnOfCategoryClass,
                        final Logger logger,
                        final long timeStamp,
                        final Level level,
                        final Object message,
                        final String threadName,
                        final ThrowableInformation throwable,
                        final String ndc,
                        final LocationInfo info,
                        final java.util.Map properties) {
      this();
      this.setFQNOfLoggerClass(fqnOfCategoryClass);
      this.setLogger(logger);
      this.setLevel(level);
      this.setMessage(message);
      this.setThrowableInformation(throwable);
      this.setTimeStamp(timeStamp);
      this.setThreadName(threadName);
      this.setNDC(ndc);
      this.setLocationInformation(info);
      if (properties instanceof Hashtable) {
        this.setProperties((Hashtable) properties);
      } else {
        this.setProperties(new Hashtable(properties));
      }
    }


  /**
   * Two events are considerd equal if they refer to the same instance, or if
   * both their timestamps and sequence numbers match.
   */
  public boolean equals(Object rObject) {
    if (this == rObject) {
      return true;
    }

    if (! (rObject instanceof LoggingEvent)) {
      return false;
    }

    LoggingEvent rEvent = (LoggingEvent)rObject;

    if (timeStamp != rEvent.timeStamp) {
      return false;
    }

    if (sequenceNumber != rEvent.sequenceNumber) {
      return false;
    }

    // at this point, the probability of the two events being equal is
    // extremely high. The next few test is optimized to take advantage of
    // this knowlege. (We only compare string lengths instead of invoking
    // string.equals which is much slower when the two string are equal.
    
    if(categoryName != null &&  rEvent.categoryName != null) {
      if(categoryName.length() != rEvent.categoryName.length()) {
        return false;
      }
    } else if(categoryName != rEvent.categoryName) {
      // of categoryNames is null while the other is not, they can't possibly
      // be equal
      return false;
    }

 

    
    // If timestamp, sequenceNumber and categoryName length are equal than the 
    // events are assumed to be equal.
    return true;
  }


  /**
   * The hashcode is computed as XOR of the lower 32 bits of sequenceNumber and
   * bits 21 to 53 of timeStamp;
   */
  public int hashCode() {
    // 2^20 millis corresponds to 17 minutes
    return (int)((timeStamp >> 20) ^ (sequenceNumber & 0xFFFFFFFF));
  }


  /**
   * Check for the existence of location information without creating it (a byproduct of calling
   * getLocationInformation).
   */
  public boolean locationInformationExists() {
    return (locationInfo != null);
  }


  /**
   * Get the location information for this logging event. If location 
   * information is null at the time of its invocation, this method extracts 
   * location information. The collected information is cached for future use.
   * 
   * <p>Note that after serialisation, it is impossible to correctly extract
   * location information. In that case null is returned.</p>
   */
  public LocationInfo getLocationInformation() {
    // we rely on the fact that fqnOfLoggerClass does not survive
    // serialization
    if (locationInfo == null && fqnOfCategoryClass != null) {
      locationInfo = new LocationInfo(new Throwable(), fqnOfCategoryClass);
    }
    return locationInfo;
  }


  /**
   * Set the location information for this logging event.
   * @since 1.3
   */
  public void setLocationInformation(LocationInfo li) {
    if (locationInfo != null) {
      throw new IllegalStateException("LocationInformation has been already set.");
    }
    locationInfo = li;
  }


  /**
   * Return the level of this event.
   */
  public Level getLevel() {
    return (Level)level;
  }


  /**
   * Set the level of this event. The level can be set at most once.
   *
   * @param level The level to set.
   * @throws IllegalStateException if the level has been already set.
   * @since 1.3
   */
  public void setLevel(Level level) {
    if (this.level != null) {
      throw new IllegalStateException("The level has been already set for this event.");
    }
    this.level = level;
  }


  /**
   * Returns the logger of this event. May be null because events after
   * serialization do not have a logger.
   *
   * @since 1.3
   **/
  public Logger getLogger() {
    if (logger instanceof Logger) {
        return (Logger) logger;
    }
    return null;
  }


  /**
   * Set the logger of this event. Calling this method also sets the <code>loggerName</code>
   * for the event. The logger can be set at most once.
   *
   * Moreover, if the loggerName has been already set, this method will throw
   * an {@link IllegalStateException}.
   *
   * @throws IllegalStateException
   */
  public void setLogger(Logger logger) {
    if (this.logger != null) {
      throw new IllegalStateException("logger has been already set to [" + this.logger.getName() + "].");
    }

    if (this.categoryName != null) {
      throw new IllegalStateException("loggerName has been already set to [" + this.categoryName + "], logger "
        + logger.getName() + "] is invalid");
    }

    this.logger = logger;
    this.categoryName = logger.getName();
  }


  /**
   * Return the name of the logger.
   */
  public String getLoggerName() {
    return categoryName;
  }


  /**
   * Set the loggerName for this event. The loggerName can be set at most once.
   *
   * @param loggerName The loggerName to set.
   * @throws IllegalStateException if loggerName is already set
   * @since 1.3
   */
  public void setLoggerName(String loggerName)
         throws IllegalStateException {
    if (this.categoryName != null) {
      throw new IllegalStateException("loggerName has been already set to [" + this.categoryName + "].");
    } else {
      this.categoryName = loggerName;
    }
  }


  /**
   * Return the message for this logging event.
   *
   * <p>
   * Before serialization, the returned object is the message passed by the
   * user to generate the logging event. After serialization, the returned
   * value equals the String form of the message possibly after object
   * rendering.
   * </p>
   *
   * @since 1.1
   */
  public Object getMessage() {
    if (message != null) {
      return message;
    } else {
      return getRenderedMessage();
    }
  }


  /**
   * Set the message for this event. The
   * @param message The message to set.
   * @since 1.3
   */
  public void setMessage(Object message) {
    if (this.message != null) {
      throw new IllegalStateException("The message for this event has been set alredy.");
    }

    // After serialisation, message will be null and renderedMessage will be non-null. 
    if (this.renderedMessage != null) {
      throw new IllegalStateException("The message cannot be set if the renderedMessage has been set.");
    }
    this.message = message;
  }


  /**
   * This method returns the NDC for this event. It will return the correct
   * content even if the event was generated in a different thread or even on
   * a different machine. The {@link NDC#get} method should <em>never</em> be
   * called directly.
   */
  public String getNDC() {
    if (ndcLookupRequired) {
      ndcLookupRequired = false;
      ndc = NDC.get();
    }

    return ndc;
  }


  /**
   * This method sets the NDC string for this event.
   * @throws IllegalStateException if ndc had been already set.
   * @since 1.3
   */
  public void setNDC(String ndcString) {
    if (this.ndc != null) {
      throw new IllegalStateException("The ndc has been already set.");
    }
    ndcLookupRequired = false;
    ndc = ndcString;
  }

    /**
        Returns the the context corresponding to the <code>key</code>
        parameter. If there is a local MDC copy, possibly because we are
        in a logging server or running inside AsyncAppender, then we
        search for the key in MDC copy, if a value is found it is
        returned. Otherwise, if the search in MDC copy returns a null
        result, then the current thread's <code>MDC</code> is used.

        <p>Note that <em>both</em> the local MDC copy and the current
        thread's MDC are searched.

        @deprecated use getProperty(String) instead.

    */
    public Object getMDC(final String key) {
      //
      //  could potentially return a LoggerRepository property value
      //     when there is not an MDC property value
      //     but the negative consequences should be minimal.
      if(properties != null) {
        Object r = properties.get(key);
        if(r != null) {
          return r;
        }
      }
      return MDC.get(key);
    }

    /**
     *  Obtain a copy of this thread's MDC prior to serialization or
     *  asynchronous logging.
     *
     *  @deprecated use initializeProperties().
    */
    public
    void getMDCCopy() {
        initializeProperties();
    }



  /**
   * If the properties field is null, this method creates a new properties map 
   * containing a copy of MDC context and a copy of the properites in 
   * LoggerRepository generating this event. If properties is non-null,
   * this method does nothing.
   *
   * @since 1.3
   */
  public void initializeProperties() {
    
    if(properties == null) {
      properties = new TreeMap();
      Map mdcMap = MDC.getContext();
      if (mdcMap != null) {
        properties.putAll(mdcMap);
      }

      if (logger != null) {
        LoggerRepository repo = logger.getLoggerRepository();
        if (repo instanceof LoggerRepositoryEx) {
            properties.putAll(((LoggerRepositoryEx) repo).getProperties());
        }
      }
    }
  }



  /**
   * Return a property for this event. The return value can be null.
   *
   * <p>The property is searched first in the properties map specific for this
   * event, then in the MDC, then in the logger repository containing the logger
   * of this event.
   * @since 1.3
   */
  public String getProperty(String key) {
    String value = null;

    if (properties != null) {
      value = (String)properties.get(key);

      if (value != null) {
        return value;
      }
    }

    // if the key was not found in this even't properties, try the MDC
    Object mdcvalue = MDC.get(key);

    if (mdcvalue != null) {
      return mdcvalue.toString();
    }

    // if still not found try, the properties in the logger repository
    if (logger != null) {
      LoggerRepository repo = logger.getLoggerRepository();
      if (repo instanceof LoggerRepositoryEx) {
        value = ((LoggerRepositoryEx) repo).getProperty(key);
      }
    }

    return value;
  }


  /**
   * Returns the set of of the key values in the properties
   * for the event.
   *
   * The returned set is unmodifiable by the caller.
   *
   * @return Set an unmodifiable set of the property keys.
   * @since 1.3
   */
  public Set getPropertyKeySet() {
    initializeProperties();
    return Collections.unmodifiableSet(properties.keySet());
  }


  /**
   * Returns the rendered version of the message according to the renderers
   * registered in the logger repository.
   *
   * Only the rendered version survives serialization.
   *
   */
  public String getRenderedMessage() {
    if ((renderedMessage == null) && (message != null)) {
      if (message instanceof String) {
        renderedMessage = (String)message;
      } else {
        // The logger has a back-reference to the repository containing it
        LoggerRepository repository = logger.getLoggerRepository();

        if (repository instanceof RendererSupport) {
          RendererSupport rs = (RendererSupport)repository;
          renderedMessage = rs.getRendererMap().findAndRender(message);
        } else {
          renderedMessage = message.toString();
        }
      }
    }

    return renderedMessage;
  }


  /**
   *
   * @param renderedMessage The renderedMessage to set.
   * @throws IllegalStateException if renderedMessage  has been already set.
   * @since 1.3
   */
  public void setRenderedMessage(String renderedMessage)
         throws IllegalStateException {
    if (this.renderedMessage != null) {
      throw new IllegalStateException("renderedMessage has been already set.");
    }
    this.renderedMessage = renderedMessage;
  }


  /**
   * Returns the time when the application started, in milliseconds elapsed
   * since 01.01.1970.
   */
  public static long getStartTime() {
    return startTime;
  }

  /**
   * Returns the sequence number.
   * @since 1.3
   */
  public long getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * Sets the sequence number.
   * @since 1.3
   */
  public void setSequenceNumber(long sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  /**
   * Returns the current thread name, or a past thread name returned by this method.
   */
  public String getThreadName() {
    if (threadName == null) {
      threadName = (Thread.currentThread()).getName();
    }
    return threadName;
  }

  /**
   * Sets the thread name.
   * @param threadName The threadName to set.
   * @throws IllegalStateException If threadName has been already set.
   */
  public void setThreadName(String threadName)
         throws IllegalStateException {
    if (this.threadName != null) {
      throw new IllegalStateException("threadName has been already set");
    }
    this.threadName = threadName;
  }

  /**
   * Returns the throwable information contained within this event. May be
   * <code>null</code> if there is no such information.
   *
   * <p>
   * Note that the {@link Throwable} object contained within a {@link
   * ThrowableInformation} does not survive serialization.
   * </p>
   *
   * @since 1.1
   */
  public ThrowableInformation getThrowableInformation() {
    return throwableInfo;
  }


  /**
   * Return this event's throwable's string[] representaion.
   */
  public String[] getThrowableStrRep() {
    if (throwableInfo == null) {
      return null;
    } else {
      return throwableInfo.getThrowableStrRep();
    }
  }


  /**
   * Set this event's throwable information.
   * @since 1.3
   */
  public void setThrowableInformation(ThrowableInformation ti) {
    if (throwableInfo != null) {
      throw new IllegalStateException("ThrowableInformation has been already set.");
    } else {
      throwableInfo = ti;
    }
  }


  private void readLevel(ObjectInputStream ois)
         throws java.io.IOException, ClassNotFoundException {
    int p = ois.readInt();

    try {
      String className = (String)ois.readObject();

      if (className == null) {
        level = Level.toLevel(p);
      } else {
        Method m = (Method)methodCache.get(className);

        if (m == null) {
          Class clazz = Loader.loadClass(className);

          // Note that we use Class.getDeclaredMethod instead of
          // Class.getMethod. This assumes that the Level subclass
          // implements the toLevel(int) method which is a
          // requirement. Actually, it does not make sense for Level
          // subclasses NOT to implement this method. Also note that
          // only Level can be subclassed and not Priority.
          m = clazz.getDeclaredMethod(TO_LEVEL, TO_LEVEL_PARAMS);
          methodCache.put(className, m);
        }

        PARAM_ARRAY[0] = new Integer(p);
        level = (Level)m.invoke(null, (Object[]) PARAM_ARRAY);
      }
    } catch (Exception e) {
      //LogLog.warn("Level deserialization failed, reverting to default.", e);
      level = Level.toLevel(p);
    }
  }


  private void readObject(ObjectInputStream ois)
         throws java.io.IOException, ClassNotFoundException {
    ois.defaultReadObject();
    readLevel(ois);

    // Make sure that location info instance is set.
    if (locationInfo == null) {
      locationInfo = LocationInfo.NA_LOCATION_INFO;
    }
  }


  /**
   * @return Returns the properties specific for this event. The returned
   * value can be null.
   * @since 1.3
   */
  public Map getProperties() {
    return properties;
  }


  /**
   * @param properties The properties to set.
   */
  public void setProperties(Hashtable properties) {
    this.properties = properties;
  }


  /**
   * Set a string property using a key and a string value.  since 1.3
   */
  public void setProperty(String key, String value) {
    if (properties == null) {
      // create a copy of MDC and repository properties  
      initializeProperties();  
    }

    if (value != null) {
      properties.put(key, value);
    } else {
      properties.remove(key);
    }
  }

  /**
   * This method should be called prior to serializing an event. It should also
   * be called when using asynchronous logging, before writing the event on 
   * a database, or as an XML element. 
   * 
   *
   * @since 1.3
   */
  public void prepareForDeferredProcessing() {
      // Aside from returning the current thread name the wgetThreadName
    // method sets the threadName variable.
    this.getThreadName();

    // This sets the renders the message in case it wasn't up to now.
    this.getRenderedMessage();

    // This call has a side effect of setting this.ndc and
    // setting ndcLookupRequired to false if not already false.
    this.getNDC();

    // This call has a side effect of creating a copy of MDC context information
    // as well as a copy the properties for the containing LoggerRepository.
    if(properties == null) {
      initializeProperties();
    }
    // This sets the throwable sting representation of the event throwable.
    this.getThrowableStrRep();
    
  }

  private void writeObject(ObjectOutputStream oos)
         throws java.io.IOException {
    
    prepareForDeferredProcessing();
    oos.defaultWriteObject();

    // serialize this event's level
    writeLevel(oos);
  }


  private void writeLevel(ObjectOutputStream oos)
         throws java.io.IOException {
    oos.writeInt(level.toInt());

    Class clazz = level.getClass();

    if (clazz == Level.class) {
      oos.writeObject(null);
    } else {
      // writing directly the Class object would be nicer, except that
      // serialized a Class object can not be read back by JDK
      // 1.1.x. We have to resort to this hack instead.
      oos.writeObject(clazz.getName());
    }
  }


  /**
   * Getter for the event's time stamp. The time stamp is calculated starting
   * from 1970-01-01 GMT.
   *
   * @since 1.3
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  /**
   * Setter for the even'ts time stamp.
   * See also {@link #getTimeStamp}.
   * @since 1.3
   */
  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Get the fully qualified name of the calling logger sub-class/wrapper.
   * @since 1.3
   */
  public String getFQNOfLoggerClass() {
    return fqnOfCategoryClass;
  }


  /**
   * Set the fully qualified name of the calling logger sub-class/wrapper.
   *
   * @since 1.3
   * @param fqnOfLoggerClass
   */
  public void setFQNOfLoggerClass(String fqnOfLoggerClass) {
    this.fqnOfCategoryClass = fqnOfLoggerClass;
  }

}
