/*
 * ============================================================================
 *                   The Apache Software License, Version 1.1
 * ============================================================================
 *
 *    Copyright (C) 1999 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "log4j" and  "Apache Software Foundation"  must not be used to
 *    endorse  or promote  products derived  from this  software without  prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made  by many individuals
 * on  behalf of the Apache Software  Foundation.  For more  information on the
 * Apache Software Foundation, please see <http://www.apache.org/>.
 *
 */

package org.apache.log4j.jdbc;

import org.apache.log4j.Level;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.ThrowableInformation;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import java.math.BigDecimal;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;


/**
 *  The <code>PreparedStatementParameter</code> class encapsulates the
 *  information needed to set a parameter on a
 *  {@link java.sql.PreparedStatement}.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
public class PreparedStatementParameter implements Comparable {
  private String columnName;
  private int order;
  private String jdbcSetter;
  private ErrorHandler errorHandler;

  /**
   *  Default constructor.
   */
  public PreparedStatementParameter() {
  }

  /**
   * Returns the columnName.
   * @return String
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * Returns the jdbcSetter.
   * @return String
   */
  public String getJdbcSetter() {
    return jdbcSetter;
  }

  /**
   * Returns the order.
   * @return int
   */
  public int getOrder() {
    return order;
  }

  /**
   * Sets the columnName.
   * @param columnName The columnName to set
   */
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  /**
   * Sets the jdbcSetter.
   * @param jdbcSetter The jdbcSetter to set
   */
  public void setJdbcSetter(String jdbcSetter) {
    this.jdbcSetter = jdbcSetter;
  }

  /**
   * Sets the order.
   * @param order The order to set
   */
  public void setOrder(int order) {
    this.order = order;
  }

  /**
   *  Compares based on the order.
   */
  public int compareTo(Object obj) {
    PreparedStatementParameter that = (PreparedStatementParameter) obj;

    if (this.order < that.order) {
      return -1;
    } else if (this.order == that.order) {
      return 0;
    } else {
      return 1;
    }
  }

  /**
   *  Set this parameter on the given statement with the given value.
   *
   *  @param stmt   the statement
   *  @param value  the value to set
   *
   *  @throws SQLException  if the parameter could not be set
   */
  public void setParameter(PreparedStatement stmt, String value)
    throws SQLException {
    if ("setString".equals(jdbcSetter)) {
      stmt.setString(order, value);
    } else if ("setCharacterStream".equals(jdbcSetter)) {
      if (value == null) {
        value = "";
      }

      stmt.setCharacterStream(order, new StringReader(value), value.length());
    } else if ("setObject".equals(jdbcSetter)) {
      stmt.setObject(order, value);
    } else {
      errorHandler.error("Unsupported setter for String: " + jdbcSetter);
    }
  }

  /**
   *  Set this parameter on the given statement with the given value.
   *
   *  @param stmt   the statement
   *  @param value  the value to set
   *
   *  @throws SQLException  if the parameter could not be set
   */
  public void setParameter(PreparedStatement stmt, ThrowableInformation value)
    throws SQLException {
    final StringWriter buf = new StringWriter();

    if ((value != null) && (value.getThrowable() != null)) {
      value.getThrowable().printStackTrace(new PrintWriter(buf));
    }

    final String str = buf.getBuffer().toString();

    if ("setString".equals(jdbcSetter)) {
      stmt.setString(order, str);
    } else if ("setCharacterStream".equals(jdbcSetter)) {
      stmt.setCharacterStream(order, new StringReader(str), str.length());
    } else if ("setObject".equals(jdbcSetter)) {
      stmt.setObject(order, str);
    } else {
      errorHandler.error("Unsupported setter for Throwable: " + jdbcSetter);
    }
  }

  /**
   *  Set this parameter on the given statement with the given value.
   *
   *  @param stmt   the statement
   *  @param value  the value to set
   *
   *  @throws SQLException  if the parameter could not be set
   */
  public void setParameter(PreparedStatement stmt, Level value)
    throws SQLException {
    if ("setString".equals(jdbcSetter)) {
      stmt.setString(order, String.valueOf(value));
    } else if ("setInt".equals(jdbcSetter)) {
      stmt.setInt(order, value.toInt());
    } else if ("setObject".equals(jdbcSetter)) {
      stmt.setObject(order, String.valueOf(value));
    } else if ("setLong".equals(jdbcSetter)) {
      stmt.setLong(order, value.toInt());
    } else if ("setDouble".equals(jdbcSetter)) {
      stmt.setDouble(order, value.toInt());
    } else if ("setBigDecimal".equals(jdbcSetter)) {
      stmt.setBigDecimal(order, new BigDecimal(value.toInt()));
    } else if ("setFloat".equals(jdbcSetter)) {
      stmt.setFloat(order, value.toInt());
    } else {
      errorHandler.error("Unsupported setter for Level: " + jdbcSetter);
    }
  }

  /**
   *  Set this parameter on the given statement with the given value.
   *
   *  @param stmt   the statement
   *  @param value  the value to set
   *
   *  @throws SQLException  if the parameter could not be set
   */
  public void setParameter(PreparedStatement stmt, long value)
    throws SQLException {
    if ("setTimestamp".equals(jdbcSetter)) {
      stmt.setTimestamp(order, new Timestamp(value));
    } else if ("setDate".equals(jdbcSetter)) {
      stmt.setDate(order, new Date(value));
    } else if ("setTime".equals(jdbcSetter)) {
      stmt.setTime(order, new Time(value));
    } else if ("setString".equals(jdbcSetter)) {
      stmt.setString(order, String.valueOf(value));
    } else if ("setObject".equals(jdbcSetter)) {
      stmt.setObject(order, new Long(value));
    } else if ("setLong".equals(jdbcSetter)) {
      stmt.setLong(order, value);
    } else if ("setDouble".equals(jdbcSetter)) {
      stmt.setDouble(order, value);
    } else if ("setBigDecimal".equals(jdbcSetter)) {
      stmt.setBigDecimal(order, new BigDecimal(value));
    } else if ("setInt".equals(jdbcSetter)) {
      stmt.setInt(order, (int) value);
    } else if ("setFloat".equals(jdbcSetter)) {
      stmt.setFloat(order, (float) value);
    } else {
      errorHandler.error("Unsupported setter for long: " + jdbcSetter);
    }
  }

  /**
   * Sets the errorHandler.
   * @param errorHandler The errorHandler to set
   */
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
}
