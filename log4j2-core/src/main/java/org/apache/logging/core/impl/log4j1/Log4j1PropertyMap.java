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
package org.apache.logging.core.impl.log4j1;

import org.apache.log4j.pattern.LogEvent;
import org.apache.log4j.spi.LoggingEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This class implements a Map<String, Object> backed by
 * the MDC contents of a LoggingEvent.  log4j 1.2 versions
 * prior to log4j 1.2.15 did not provide a mechanism to
 * get all MDC values.  This class implements a brute-force
 * mechanism to extract those values when an operation that
 * requires access to the whole set.  Single value retrievals
 * are done by a direct call to LoggingEvent.getMDC().
 */
public final class Log4j1PropertyMap
        implements Map<String, Object>, Serializable {

    /**
     * Reflective method to call MDC contents added in log4j 1.2.15.
     */
    private static final Method getPropertiesMethod;
    static {
        Method getProps;
            try {
               getProps = LoggingEvent.class.getMethod(
                          "getProperties");
            } catch(Exception ex) {
               getProps = null;
            }
        getPropertiesMethod = getProps;
    }

    /**
     * Underlying instance.
     */
    private final LoggingEvent base;

    private transient Map mdc;

    /**
     * Create new instance.
     * @param source underlying instance, may not be null.
     */
    private Log4j1PropertyMap(final LoggingEvent source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        base = source;
    }

    /**
     * Create new instance.
     * @param source underlying instance, may be null.
     * @return new instance.
     */
    public static Log4j1PropertyMap getInstance(
            final LoggingEvent source) {
        if (source == null) {
            return null;
        }
        return new Log4j1PropertyMap(source);
    }

    /**
     * If access to all the MDC properties are required
     * we have to go to pretty extreme lengths to
     * get them.
     *
     * @return
     */
    private synchronized Map<String, Object> getMDC() {
        if (mdc == null) {
            mdc = new HashMap<String, Object>();
            if (getPropertiesMethod != null) {
                try {
                    mdc.putAll((Map) getPropertiesMethod.invoke(base));
                } catch(Exception ex) {
                    assert false;
                }
            } else {
                //
                //  for 1.2.14 and earlier could serialize and
                //    extract MDC content
                try {
                  ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                  ObjectOutputStream os = new ObjectOutputStream(outBytes);
                  os.writeObject(base);
                  os.close();

                  byte[] raw = outBytes.toByteArray();
                  //
                  //   bytes 6 and 7 should be the length of the original classname
                  //     should be the same as our substitute class name
                  final String subClassName = LogEvent.class.getName();
                  if (raw[6] == 0 || raw[7] == subClassName.length()) {
                      //
                      //  manipulate stream to use our class name
                      //
                      for (int i = 0; i < subClassName.length(); i++) {
                          raw[8 + i] = (byte) subClassName.charAt(i);
                      }
                      ByteArrayInputStream inBytes = new ByteArrayInputStream(raw);
                      ObjectInputStream is = new ObjectInputStream(inBytes);
                      Object cracked = is.readObject();
                      assert cracked instanceof LogEvent;
                      if (cracked instanceof LogEvent) {
                          mdc.putAll(((LogEvent) cracked).getProperties());
                      }
                      is.close();
                  } else {
                      assert false;
                  }
                } catch(Exception ex) {
                    assert false;
                }
            }
        }
        return mdc;

    }

    /** {@inheritDoc} */
    public int size() {
        return getMDC().size();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return getMDC().isEmpty();
    }

    /** {@inheritDoc} */
    public boolean containsKey(final Object key) {
        return base.getMDC((String) key) != null;
    }

    /** {@inheritDoc} */
    public boolean containsValue(final Object value) {
        return getMDC().containsValue(value);
    }

    /** {@inheritDoc} */
    public Object get(final Object key) {
        return base.getMDC((String) key);
    }

    /** {@inheritDoc} */
    public Object put(final String key, final Object value) {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc} */
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends String,? extends Object> t) {
        throw new UnsupportedOperationException();
        
    }

    /** {@inheritDoc} */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Set<String> keySet() {
        return getMDC().keySet();
    }

    /** {@inheritDoc} */
    public Collection<Object> values() {
        return getMDC().values();
    }

    /** {@inheritDoc} */
    public Set<Map.Entry<String,Object>> entrySet() {
        return getMDC().entrySet();
    }
    
}
