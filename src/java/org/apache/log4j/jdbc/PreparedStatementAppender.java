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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;


/**
 *  The <code>PreparedStatementAppender</code> class provides a JDBC-based
 *  appender that uses {@link java.sql.PreparedStatement}s for efficiency.
 *  Note that this appender takes advantage of the advanced capabilities of the
 *  {@link org.apache.log4j.xml.DOMConfigurator} and therefore must be
 *  configured with an XML configuration file.
 *  <p>
 *  This appender has a number of configurable options.  First and foremost is
 *  the <code>connectionSource</code>, an implementation of
 *  {@link ConnectionSource}.  For example, {@link UrlConnectionSource} and
 *  {@link JNDIConnectionSource} are provider by log4j.  It is recommended that
 *  you use {@link JNDIConnectionSource} whenever possible and create your own
 *  implementation of {@link ConnectionSource} otherwise.  Use
 *  {@link UrlConnectionSource} if you just want to get something working fast.
 *  <p>
 *  Second is the optional <code>preparedStatementSource</code>, an
 *  implementation of {@link PreparedStatementSource}.  Only specify this if
 *  the default statement
 *  provider by <code>PreparedStatementAppender</code> is not sufficient.
 *  By default a statement similar to &quot;<code>INSERT INTO LOG4J (message,
 *  logger, level) VALUES (?, ?, ?)</code>&quot; will be generated based on the
 *  other parameters (see the example below).
 *  <p>
 *  Next, there is a parameter for each part of the logging event you might
 *  want to include in database:<br>
 *  <table border="1" rules="all" cellpadding="3">
 *    <tr>
 *      <th>Parameter</th>
 *      <th>Description</th>
 *      <th>Default JDBC Setter</th>
 *    </tr>
 *    <tr>
 *      <td>messageParameter</td>
 *      <td>The raw log message</td>
 *      <td>setString</td>
 *    </tr>
 *    <tr>
 *      <td>formattedMessageParameter</td>
 *      <td>The formatted log message</td>
 *      <td>setString</td>
 *    </tr>
 *    <tr>
 *      <td>loggerParameter</td>
 *      <td>The logger</td>
 *      <td>setString</td>
 *    </tr>
 *    <tr>
 *      <td>levelParameter</td>
 *      <td>The log message level</td>
 *      <td>setString</td>
 *    </tr>
 *    <tr>
 *      <td>ndcParameter</td>
 *      <td>The NDC (nested diagnostic context) of the log message</td>
 *      <td>setString</td>
 *    </tr>
 *    <tr>
 *      <td>exceptionParameter</td>
 *      <td>The message of the exception, i.e. the value of
 *          <code>Exception.getMessage()</code>, for the exception passed to the
 *          logger</td>
 *      <td>setString</td>
 *    </tr>
 *    <tr>
 *      <td>stackParameter</td>
 *      <td>The stack trace of the exception passed to the logger.  Beware that
 *          no effort is made to ensure that the stack trace will fit inside the
 *          database column provided.</td>
 *      <td>setCharacterStream</td>
 *    </tr>
 *    <tr>
 *      <td>timeStampParameter</td>
 *      <td>The time stamp of the log message</td>
 *      <td>setTimerStamp</td>
 *    </tr>
 *  </table><br>
 *  Define whichever subset of these you wish to include in your database table.
 *  If the JDBC setter referenced above is sufficient for the column in
 *  question, you may omit the setter value in the configuration.
 *  <p>
 *  Finally, the table parameter is used to indicate the table into which to
 *  insert the rows for the default prepared statement.
 *
 *  <h3>Example Configurations</h3>
 *  All of the example configurations assume that the database vendor is MySQL,
 *  the instance is test, the user is myUser and the password is myPassword.
 *  The database table is LOG4J and looks like:<br>
 *  <pre>
 *  mysql> desc LOG4J;
 *  +-----------+--------------+------+-----+---------+-------+
 *  | Field     | Type         | Null | Key | Default | Extra |
 *  +-----------+--------------+------+-----+---------+-------+
 *  | msg       | blob         | YES  |     | NULL    |       |
 *  | logger    | varchar(255) | YES  |     | NULL    |       |
 *  | level     | varchar(5)   | YES  |     | NULL    |       |
 *  | ndc       | varchar(255) | YES  |     | NULL    |       |
 *  | exception | blob         | YES  |     | NULL    |       |
 *  | stack     | blob         | YES  |     | NULL    |       |
 *  | formatted | blob         | YES  |     | NULL    |       |
 *  | stamp     | datetime     | YES  |     | NULL    |       |
 *  +-----------+--------------+------+-----+---------+-------+
 *  </pre>
 *
 *  <h4>Quick and dirty</h4>
 *  Include the following in your log4j.xml file:<br>
 *  <pre>
 *  &lt;appender name="X" class="org.apache.log4j.jdbc.PreparedStatementAppender">
 *    &lt;layout class="org.apache.log4j.PatternLayout">
 *      &lt;param name="ConversionPattern" value="%d %-5p %c - %m%n"/>
 *    &lt;/layout>
 *    &lt;connectionSource class="org.apache.log4j.jdbc.UrlConnectionSource">
 *        &lt;param name="driver" value="com.mysql.jdbc.Driver" />
 *        &lt;param name="url" value="jdbc:mysql://localhost:3306/test" />
 *        &lt;param name="username" value="myUser" />
 *        &lt;param name="password" value="myPassword" />
 *    &lt;/connectionSource>
 *    &lt;param name="table" value="LOG4J"/>
 *    &lt;messageParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="msg" />
 *    &lt;/messageParameter>
 *    &lt;loggerParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="logger" />
 *    &lt;/loggerParameter>
 *    &lt;levelParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="level" />
 *    &lt;/levelParameter>
 *    &lt;ndcParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="ndc" />
 *    &lt;/ndcParameter>
 *    &lt;exceptionParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="exception" />
 *    &lt;/exceptionParameter>
 *    &lt;stackParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="stack" />
 *    &lt;/stackParameter>
 *    &lt;formattedMessageParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="formatted" />
 *    &lt;/formattedMessageParameter>
 *    &lt;timeStampParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="stamp" />
 *    &lt;/timeStampParameter>
 *  &lt;/appender&gt;
 *  </pre>
 *  <p>
 *  This configuration will generate a prepared statement of the form
 *  <code>INSERT INTO LOG4J (msg, logger, level, ndc, exception, stack,
 *  formatted, stamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)</code>.  If you do not
 *  need one of the columns, for example <code>msg</code>, simply do not include
 *  it in the configuration.
 *
 *  <h4>JNDI-based Configuration</h4>
 *  This example demonstrates the JNDIConnectionSource:<br>
 *  <pre>
 *  &lt;appender name="X" class="org.apache.log4j.jdbc.PreparedStatementAppender">
 *    &lt;layout class="org.apache.log4j.PatternLayout">
 *      &lt;param name="ConversionPattern" value="%d %-5p %c - %m%n"/>
 *    &lt;/layout>
 *  <b>
 *    &lt;connectionSource class="org.apache.log4j.jdbc.JNDIConnectionSource">
 *        &lt;param name="jndiLocation" value="jdbc/MySQLDS" />
 *    &lt;/connectionSource>
 *  </b>
 *    &lt;param name="table" value="LOG4J"/>
 *    &lt;loggerParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="logger" />
 *    &lt;/loggerParameter>
 *    &lt;levelParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="level" />
 *    &lt;/levelParameter>
 *    &lt;exceptionParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="exception" />
 *    &lt;/exceptionParameter>
 *    &lt;stackParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="stack" />
 *    &lt;/stackParameter>
 *    &lt;formattedMessageParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="formatted" />
 *    &lt;/formattedMessageParameter>
 *    &lt;timeStampParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="stamp" />
 *    &lt;/timeStampParameter>
 *  &lt;/appender&gt;
 *  </pre>
 *  Note that this configuration will produce a slightly different prepared
 *  statement: <code>INSERT INTO LOG4J (logger, level, exception, stack,
 *  formatted, stamp) VALUES (?, ?, ?, ?, ?)</code>.  This was done purely for
 *  demonstration purposes, there is no reason <code>messageParameter</code> and
 *  <code>ndcParameter</code> could not be included.
 *
 *  <h4>Custom PreparedStatementSopurce Configuration</h4>
 *  This example demonstrates a custom {@link PreparedStatementSource}:<br>
 *  <pre>
 *  &lt;appender name="X" class="org.apache.log4j.jdbc.PreparedStatementAppender">
 *    &lt;layout class="org.apache.log4j.PatternLayout">
 *      &lt;param name="ConversionPattern" value="%d %-5p %c - %m%n"/>
 *    &lt;/layout>
 *    &lt;connectionSource class="org.apache.log4j.jdbc.JNDIConnectionSource">
 *        &lt;param name="jndiLocation" value="jdbc/MySQLDS" />
 *    &lt;/connectionSource>
 *  <b>
 *    &lt;preparedStatementSource class="my.wonderful.implmentation.of.PreparedStatementSource">
 *        &lt;param name="myParam1" value="myValue1" />
 *        &lt;param name="myParam2" value="myValue2" />
 *    &lt;/preparedStatementSource>
 *  </b>
 *    &lt;loggerParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="logger" />
 *    &lt;/loggerParameter>
 *    &lt;levelParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="level" />
 *    &lt;/levelParameter>
 *    &lt;exceptionParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="exception" />
 *    &lt;/exceptionParameter>
 *    &lt;stackParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="stack" />
 *    &lt;/stackParameter>
 *    &lt;formattedMessageParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="formatted" />
 *    &lt;/formattedMessageParameter>
 *    &lt;timeStampParameter class="org.apache.log4j.jdbc.PreparedStatementParameter">
 *        &lt;param name="columnName" value="stamp" />
 *    &lt;/timeStampParameter>
 *  &lt;/appender&gt;
 *  </pre>
 *  Use this configuration when you want to provide your own
 *  {@link PreparedStatementSource} becuase the default statements are
 *  insufficient.  For example, you want to call a stored procedure or the like.
 *  Note that you still must provide the <code>xxxParameter</code>
 *  configurations.
 *
 *  @author <a href="mailto:rdecampo@twcny.rr.com">Ray DeCampo</a>
 */
public class PreparedStatementAppender extends AppenderSkeleton {
  private String table = "LOG4J";
  private PreparedStatementParameter messageParameter = null;
  private PreparedStatementParameter loggerParameter = null;
  private PreparedStatementParameter levelParameter = null;
  private PreparedStatementParameter ndcParameter = null;
  private PreparedStatementParameter exceptionParameter = null;
  private PreparedStatementParameter stackParameter = null;
  private PreparedStatementParameter timeStampParameter = null;
  private PreparedStatementParameter formattedMessageParameter = null;
  private ConnectionSource connectionSource = null;
  private PreparedStatementSource preparedStatementSource = null;
  private StringBuffer sql = new StringBuffer();
  private final ArrayList params = new ArrayList();

  /**
   *  Default constructor.
   */
  public PreparedStatementAppender() {
  }

  public String getTable() {
    return table;
  }

  public void setTable(String newTable) {
    table = newTable;
  }

  public void activateOptions() {
    super.activateOptions();

    // Set up default setters and create the params list
    if (messageParameter != null) {
      params.add(messageParameter);
      messageParameter.setErrorHandler(errorHandler);

      if (messageParameter.getJdbcSetter() == null) {
        messageParameter.setJdbcSetter("setString");
      }
    }

    if (loggerParameter != null) {
      params.add(loggerParameter);
      loggerParameter.setErrorHandler(errorHandler);

      if (loggerParameter.getJdbcSetter() == null) {
        loggerParameter.setJdbcSetter("setString");
      }
    }

    if (levelParameter != null) {
      params.add(levelParameter);
      levelParameter.setErrorHandler(errorHandler);

      if (levelParameter.getJdbcSetter() == null) {
        levelParameter.setJdbcSetter("setString");
      }
    }

    if (ndcParameter != null) {
      params.add(ndcParameter);
      ndcParameter.setErrorHandler(errorHandler);

      if (ndcParameter.getJdbcSetter() == null) {
        ndcParameter.setJdbcSetter("setString");
      }
    }

    if (exceptionParameter != null) {
      params.add(exceptionParameter);
      exceptionParameter.setErrorHandler(errorHandler);

      if (exceptionParameter.getJdbcSetter() == null) {
        exceptionParameter.setJdbcSetter("setString");
      }
    }

    if (stackParameter != null) {
      params.add(stackParameter);
      stackParameter.setErrorHandler(errorHandler);

      if (stackParameter.getJdbcSetter() == null) {
        stackParameter.setJdbcSetter("setCharacterStream");
      }
    }

    if (timeStampParameter != null) {
      params.add(timeStampParameter);
      timeStampParameter.setErrorHandler(errorHandler);

      if (timeStampParameter.getJdbcSetter() == null) {
        timeStampParameter.setJdbcSetter("setTimestamp");
      }
    }

    if (formattedMessageParameter != null) {
      params.add(formattedMessageParameter);
      formattedMessageParameter.setErrorHandler(errorHandler);

      if (formattedMessageParameter.getJdbcSetter() == null) {
        formattedMessageParameter.setJdbcSetter("setString");
      }
    }

    params.trimToSize();

    connectionSource.setErrorHandler(errorHandler);
    connectionSource.activateOptions();

    if (preparedStatementSource == null) {
      generateSql();
    } else {
      preparedStatementSource.activateOptions();
    }
  }

  public void append(LoggingEvent event) {
    Throwable throwable = null;

    if (event.getThrowableInformation() != null) {
      throwable = event.getThrowableInformation().getThrowable();
    }

    Connection conn = null;
    PreparedStatement stmt = null;

    try {
      conn = connectionSource.getConnection();

      if (preparedStatementSource != null) {
        stmt = preparedStatementSource.generate(this, conn);
      } else {
        stmt = conn.prepareStatement(sql.toString());
      }

      // Set stmt parameters
      if (messageParameter != null) {
        messageParameter.setParameter(
          stmt, String.valueOf(event.getMessage()));
      }

      if (loggerParameter != null) {
        loggerParameter.setParameter(stmt, event.getLoggerName());
      }

      if (levelParameter != null) {
        levelParameter.setParameter(stmt, event.getLevel());
      }

      if (ndcParameter != null) {
        ndcParameter.setParameter(stmt, event.getNDC());
      }

      if (exceptionParameter != null) {
        if (throwable != null) {
          exceptionParameter.setParameter(stmt, throwable.getMessage());
        } else {
          exceptionParameter.setParameter(stmt, "");
        }
      }

      if (stackParameter != null) {
        stackParameter.setParameter(stmt, event.getThrowableInformation());
      }

      if (timeStampParameter != null) {
        timeStampParameter.setParameter(stmt, event.timeStamp);
      }

      if (formattedMessageParameter != null) {
        formattedMessageParameter.setParameter(
          stmt, getLayout().format(event));
      }

      // OK, do the dirty deed
      stmt.executeUpdate();
    } catch (final SQLException sqle) {
      errorHandler.error(
        "Error executing SQL: " + sql, sqle, ErrorCode.GENERIC_FAILURE, event);
    } finally {
      closeJdbc(stmt, conn);
    }
  }

  public boolean requiresLayout() {
    return true;
  }

  public void close() {
  }

  private void generateSql() {
    int colCount = 0;
    sql.setLength(0);
    sql.append("INSERT INTO ").append(table).append(" (");

    for (int i = 0; i < params.size(); i++) {
      colCount = appendColumn(getParameter(i), colCount);
    }

    sql.append(") VALUES (");

    for (int i = 0; i < colCount; i++) {
      sql.append((i == 0) ? "?" : ", ?");
    }

    sql.append(")");
    System.out.println(sql);
  }

  private int appendColumn(PreparedStatementParameter psp, int colCount) {
    if ((psp != null) && (psp.getColumnName() != null)) {
      sql.append((colCount == 0) ? "" : ", ").append(psp.getColumnName());
      psp.setOrder(++colCount);

      System.out.println(
        "JDBC setter for " + psp.getColumnName() + " is "
        + psp.getJdbcSetter());
    }

    return colCount;
  }

  private void closeJdbc(PreparedStatement stmt, Connection conn) {
    try {
      if (stmt != null) {
        stmt.close();
      }
    } catch (final SQLException sqle) {
      errorHandler.error(
        "Error closing prepared statement", sqle, ErrorCode.GENERIC_FAILURE);
    }

    try {
      if (conn != null) {
        conn.close();
      }
    } catch (final SQLException sqle) {
      errorHandler.error(
        "Error closing connection", sqle, ErrorCode.GENERIC_FAILURE);
    }
  }

  private PreparedStatementParameter getParameter(int i) {
    return (PreparedStatementParameter) params.get(i);
  }

  /**
   * Returns the timeStampParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getTimeStampParameter() {
    return timeStampParameter;
  }

  /**
   * Returns the exceptionParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getExceptionParameter() {
    return exceptionParameter;
  }

  /**
   * Returns the formattedMessageParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getFormattedMessageParameter() {
    return formattedMessageParameter;
  }

  /**
   * Returns the levelParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getLevelParameter() {
    return levelParameter;
  }

  /**
   * Returns the loggerParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getLoggerParameter() {
    return loggerParameter;
  }

  /**
   * Returns the messageParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getMessageParameter() {
    return messageParameter;
  }

  /**
   * Returns the ndcParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getNdcParameter() {
    return ndcParameter;
  }

  /**
   * Returns the stackParameter.
   * @return PreparedStatementParameter
   */
  public PreparedStatementParameter getStackParameter() {
    return stackParameter;
  }

  /**
   * Sets the timeStampParameter.
   * @param timeStampParameter The timeStampParameter to set
   */
  public void setTimeStampParameter(PreparedStatementParameter date) {
    this.timeStampParameter = date;
  }

  /**
   * Sets the exceptionParameter.
   * @param exceptionParameter The exceptionParameter to set
   */
  public void setExceptionParameter(PreparedStatementParameter exception) {
    this.exceptionParameter = exception;
  }

  /**
   * Sets the formattedMessageParameter.
   * @param formattedMessageParameter The formattedMessageParameter to set
   */
  public void setFormattedMessageParameter(
    PreparedStatementParameter formattedMsg) {
    this.formattedMessageParameter = formattedMsg;
  }

  /**
   * Sets the levelParameter.
   * @param levelParameter The levelParameter to set
   */
  public void setLevelParameter(PreparedStatementParameter level) {
    this.levelParameter = level;
  }

  /**
   * Sets the loggerParameter.
   * @param loggerParameter The loggerParameter to set
   */
  public void setLoggerParameter(PreparedStatementParameter logger) {
    this.loggerParameter = logger;
  }

  /**
   * Sets the messageParameter.
   * @param messageParameter The messageParameter to set
   */
  public void setMessageParameter(PreparedStatementParameter msg) {
    this.messageParameter = msg;
  }

  /**
   * Sets the ndcParameter.
   * @param ndcParameter The ndcParameter to set
   */
  public void setNdcParameter(PreparedStatementParameter ndc) {
    this.ndcParameter = ndc;
  }

  /**
   * Sets the stackParameter.
   * @param stackParameter The stackParameter to set
   */
  public void setStackParameter(PreparedStatementParameter stack) {
    this.stackParameter = stack;
  }

  /**
   * Returns the connectionSource.
   * @return ConnectionSource
   */
  public ConnectionSource getConnectionSource() {
    return connectionSource;
  }

  /**
   * Sets the connectionSource.
   * @param connectionSource The connectionSource to set
   */
  public void setConnectionSource(ConnectionSource connectionSource) {
    this.connectionSource = connectionSource;
  }

  /**
   * Returns the preparedStatementSource.
   * @return PreparedStatementSource
   */
  public PreparedStatementSource getPreparedStatementSource() {
    return preparedStatementSource;
  }

  /**
   * Sets the preparedStatementSource.
   * @param preparedStatementSource The preparedStatementSource to set
   */
  public void setPreparedStatementSource(
    PreparedStatementSource preparedStatementSource) {
    this.preparedStatementSource = preparedStatementSource;
  }
}
