-- This SQL script creates the required tables by org.apache.log4j.db.DBAppender and 
-- org.apache.log4j.db.DBReceiver. 
-- 
-- It is intended for MS SQL Server databases.  This has been tested with version 7.0. 

DROP TABLE logging_event_property 
DROP TABLE logging_event_exception 
DROP TABLE logging_event 

CREATE TABLE logging_event 
  ( 
    sequence_number   DECIMAL(20) NOT NULL, 
    timestamp         DECIMAL(20) NOT NULL, 
    rendered_message  TEXT NOT NULL, 
    logger_name       VARCHAR(254) NOT NULL, 
    level_string      VARCHAR(254) NOT NULL, 
    ndc               TEXT, 
    thread_name       VARCHAR(254), 
    reference_flag    SMALLINT, 
    event_id          INT NOT NULL identity, 
    PRIMARY KEY(event_id) 
  ) 

CREATE TABLE logging_event_property 
  ( 
    event_id          INT NOT NULL, 
    mapped_key        VARCHAR(254) NOT NULL, 
    mapped_value      TEXT, 
    PRIMARY KEY(event_id, mapped_key), 
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id) 
  ) 

CREATE TABLE logging_event_exception 
  ( 
    event_id         INT NOT NULL, 
    i                SMALLINT NOT NULL, 
    trace_line       VARCHAR(254) NOT NULL, 
    PRIMARY KEY(event_id, i), 
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id) 
  ) 

