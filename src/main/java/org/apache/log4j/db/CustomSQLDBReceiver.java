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

package org.apache.log4j.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.scheduler.Job;
import org.apache.log4j.scheduler.Scheduler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggerRepositoryEx;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;

/**
 * Converts log data stored in a database into LoggingEvents.
 * <p>
 * <b>NOTE:</b> This receiver cannot yet be created through Chainsaw's receiver panel.  
 * It must be created through an XML configuration file.
 * <p>
 * This receiver supports database configuration via ConnectionSource, in the
 * org.apache.log4j.db package: DriverManagerConnectionSource,
 * DataSourceConnectionSource, JNDIConnectionSource
 * <p>
 * This database receiver differs from DBReceiver in that this receiver relies
 * on custom SQL to retrieve logging event data, where DBReceiver requires the
 * use of a log4j-defined schema.
 * <p>
 * A 'refreshMillis' int parameter controls SQL execution. If 'refreshMillis' is
 * zero (the default), the receiver will run only one time. If it is set to any
 * other numeric value, the SQL will be executed on a recurring basis every
 * 'refreshMillis' milliseconds.
 * <p>
 * The receiver closes the connection and acquires a new connection on each 
 * execution of the SQL (use pooled connections if possible).
 * <p>
 * If the SQL will be executing on a recurring basis, specify the IDField param -
 * the column name holding the unique identifier (int) representing the logging
 * event.
 * <p>
 * As events are retrieved, the column represented by IDField is examined and
 * the largest value is held and used by the next execution of the SQL statement
 * to avoid retrieving previously processed events.
 * <p>
 * As an example, the IDField references a 'COUNTER' (int, auto-increment,
 * unique) column. The first execution of the SQL statement returns 500 rows,
 * with a final value in the COUNTER field of 500.
 * <p>
 * The SQL statement is manipulated prior to the next execution, adding ' WHERE
 * COUNTER > 500' to the statement to avoid retrieval of previously processed
 * events.
 * <p>
 * The select statement must provide ALL fields which define a LoggingEvent.
 * <p>
 * The SQL statement MUST include the columns: LOGGER, TIMESTAMP, LEVEL, THREAD,
 * MESSAGE, NDC, MDC, CLASS, METHOD, FILE, LINE, PROPERTIES, EXCEPTION
 * <p>
 * Use ' AS ' in the SQL statement to alias the SQL's column names to match your
 * database schema. (see example below).
 * <p>
 * Include all fields in the SQL statement, even if you don't have data for the
 * field (specify an empty string as the value for columns which you don't have
 * data).
 * <p>
 * The TIMESTAMP column must be a datetime.
 * <p>
 * Both a PROPERTIES column and an MDC column are supported. These fields
 * represent Maps on the logging event, but require the use of string
 * concatenation database functions to hold the (possibly multiple) name/value
 * pairs in the column.
 * <p>
 * For example, to include both 'userid' and 'lastname' properties in the
 * logging event (from either the PROPERTIES or MDC columns), the name/value
 * pairs must be concatenated together by your database.
 * <p>
 * The resulting PROPERTIES or MDC column must have data in this format: {{name,
 * value, name2, value2}}
 * <p>
 * The resulting PROPERTIES column would contain this text: {{userid, someone,
 * lastname, mylastname}}
 * <p>
 * Here is an example of concatenating a PROPERTIES or MDC column using MySQL's
 * concat function, where the 'application' and 'hostname' parameters were fixed
 * text, but the 'log4jid' key's value is the value of the COUNTER column:
 * <p>
 * concat("{{application,databaselogs,hostname,mymachine,log4jid,", COUNTER,
 * "}}") as PROPERTIES
 * <p>
 * log4jid is a special property that is used by Chainsaw to represent an 'ID'
 * field. Specify this property to ensure you can map events in Chainsaw to
 * events in the database if you need to go back and view events at a later time
 * or save the events to XML for later analysis.
 * <p>
 * Here is a complete MySQL SQL statement which can be used to provide events to
 * Chainsaw:
 * <p>
 * select logger as LOGGER, timestamp as TIMESTAMP, level as LEVEL, thread as
 * THREAD, message as MESSAGE, ndc as NDC, mdc as MDC, class as CLASS, method as
 * METHOD, file as FILE, line as LINE,
 * concat("{{application,databaselogs,hostname,mymachine, log4jid,",
 * COUNTER,"}}") as PROPERTIES, "" as EXCEPTION from logtable
 * <p>
 * @author Scott Deboy <sdeboy@apache.org>
 * <p>
 */
public class CustomSQLDBReceiver extends Receiver implements Pauseable {

    protected volatile Connection connection = null;

    protected String sqlStatement = "";

    /**
     * By default we refresh data every 1000 milliseconds.
     * 
     * @see #setRefreshMillis
     */
    static int DEFAULT_REFRESH_MILLIS = 1000;

    int refreshMillis = DEFAULT_REFRESH_MILLIS;

    protected String idField = null;

    int lastID = -1;

    private static final String WHERE_CLAUSE = " WHERE ";

    private static final String AND_CLAUSE = " AND ";

    private boolean whereExists = false;

    private boolean paused = false;

    private ConnectionSource connectionSource;

    public static final String LOG4J_ID_KEY = "log4jid";

    private Job customReceiverJob;

    public void activateOptions() {
      
      if(connectionSource == null)  {
        throw new IllegalStateException(
          "CustomSQLDBReceiver cannot function without a connection source");
      }
      whereExists = (sqlStatement.toUpperCase().indexOf(WHERE_CLAUSE) > -1);
    
      customReceiverJob = new CustomReceiverJob();
        
      if(this.repository == null) {
        throw new IllegalStateException(
        "CustomSQLDBReceiver cannot function without a reference to its owning repository");
      }
     
    

      if (repository instanceof LoggerRepositoryEx) {
        Scheduler scheduler = ((LoggerRepositoryEx) repository).getScheduler();
      
        scheduler.schedule(
          customReceiverJob, System.currentTimeMillis() + 500, refreshMillis);
      }

    }

    void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                // LogLog.warn("closing the connection. ", new Exception("x"));
                connection.close();
            } catch (SQLException sqle) {
                // nothing we can do here
            }
        }
    }

    public void setRefreshMillis(int refreshMillis) {
        this.refreshMillis = refreshMillis;
    }

    public int getRefreshMillis() {
        return refreshMillis;
    }

    /**
     * @return Returns the connectionSource.
     */
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    /**
     * @param connectionSource
     *            The connectionSource to set.
     */
    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    public void close() {
        try {
            if ((connection != null) && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    public void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.log4j.plugins.Plugin#shutdown()
     */
    public void shutdown() {
        getLogger().info("removing receiverJob from the Scheduler.");

        if(this.repository instanceof LoggerRepositoryEx) {
          Scheduler scheduler = ((LoggerRepositoryEx) repository).getScheduler();
          scheduler.delete(customReceiverJob);
        }

        lastID = -1;
    }

    public void setSql(String s) {
        sqlStatement = s;
    }

    public String getSql() {
        return sqlStatement;
    }

    public void setIDField(String id) {
        idField = id;
    }

    public String getIDField() {
        return idField;
    }

    public synchronized void setPaused(boolean p) {
        paused = p;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    class CustomReceiverJob implements Job {
        public void execute() {
            Connection connection = null;

            int oldLastID = lastID;
            try {
                connection = connectionSource.getConnection();
                Statement statement = connection.createStatement();

                Logger eventLogger = null;
                long timeStamp = 0L;
                String level = null;
                String threadName = null;
                Object message = null;
                String ndc = null;
                Hashtable mdc = null;
                String[] exception = null;
                String className = null;
                String methodName = null;
                String fileName = null;
                String lineNumber = null;
                Hashtable properties = null;

                String currentSQLStatement = sqlStatement;
                if (whereExists) {
                    currentSQLStatement = sqlStatement + AND_CLAUSE + idField
                            + " > " + lastID;
                } else {
                    currentSQLStatement = sqlStatement + WHERE_CLAUSE + idField
                            + " > " + lastID;
                }

                ResultSet rs = statement.executeQuery(currentSQLStatement);

                int i = 0;
                while (rs.next()) {
                    // add a small break every 1000 received events
                    if (++i == 1000) {
                        synchronized (this) {
                            try {
                                // add a delay
                                wait(300);
                            } catch (InterruptedException ie) {
                            }
                            i = 0;
                        }
                    }
                    eventLogger = Logger.getLogger(rs.getString("LOGGER"));
                    timeStamp = rs.getTimestamp("TIMESTAMP").getTime();

                    level = rs.getString("LEVEL");
                    threadName = rs.getString("THREAD");
                    message = rs.getString("MESSAGE");
                    ndc = rs.getString("NDC");

                    String mdcString = rs.getString("MDC");
                    mdc = new Hashtable();

                    if (mdcString != null) {
                        // support MDC being wrapped in {{name, value}}
                        // or
                        // just name, value
                        if ((mdcString.indexOf("{{") > -1)
                                && (mdcString.indexOf("}}") > -1)) {
                            mdcString = mdcString
                                    .substring(mdcString.indexOf("{{") + 2,
                                            mdcString.indexOf("}}"));
                        }

                        StringTokenizer tok = new StringTokenizer(mdcString,
                                ",");

                        while (tok.countTokens() > 1) {
                            mdc.put(tok.nextToken(), tok.nextToken());
                        }
                    }

                    exception = new String[] { rs.getString("EXCEPTION") };
                    className = rs.getString("CLASS");
                    methodName = rs.getString("METHOD");
                    fileName = rs.getString("FILE");
                    lineNumber = rs.getString("LINE");

                    // if properties are provided in the
                    // SQL they can be used here (for example, to route
                    // events to a unique tab in
                    // Chainsaw if the machinename and/or appname
                    // property
                    // are set)
                    String propertiesString = rs.getString("PROPERTIES");
                    properties = new Hashtable();

                    if (propertiesString != null) {
                        // support properties being wrapped in {{name,
                        // value}} or just name, value
                        if ((propertiesString.indexOf("{{") > -1)
                                && (propertiesString.indexOf("}}") > -1)) {
                            propertiesString = propertiesString.substring(
                                    propertiesString.indexOf("{{") + 2,
                                    propertiesString.indexOf("}}"));
                        }

                        StringTokenizer tok2 = new StringTokenizer(
                                propertiesString, ",");
                        while (tok2.countTokens() > 1) {
                            String name = tok2.nextToken();
                            String value = tok2.nextToken();
                            if (name.equals(LOG4J_ID_KEY)) {
                                try {
                                    int thisInt = Integer.parseInt(value);
                                    value = String.valueOf(thisInt);
                                    if (thisInt > lastID) {
                                        lastID = thisInt;
                                    }
                                } catch (Exception e) {
                                }
                            }
                            properties.put(name, value);
                        }
                    }

                    Level levelImpl = Level.toLevel(level);
                    LoggingEvent event = new LoggingEvent(
                            eventLogger.getName(), eventLogger, levelImpl,
                            message, null);
                    event.setLocationInformation(new LocationInfo(fileName,
                            className, methodName, lineNumber));
                    properties.putAll(mdc);
                    event.setTimeStamp(timeStamp);
                    event.setThrowableInformation(new ThrowableInformation(
                            exception));

                    event.setProperties(properties);
                    event.setThreadName(threadName);
                    event.setNDC(ndc);
                    doPost(event);
                }
                //log when rows are retrieved
                if (lastID != oldLastID) {
                    getLogger().debug("lastID: " + lastID);
                    oldLastID = lastID;
                }

                statement.close();
                statement = null;
            } catch (SQLException sqle) {
                getLogger()
                        .error("*************Problem receiving events", sqle);
            } finally {
                closeConnection(connection);
            }

            // if paused, loop prior to executing sql query
            synchronized (this) {
                while (isPaused()) {
                    try {
                        wait(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
    }
}
