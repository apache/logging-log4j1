# This SQL script creates the required tables by
# org.apache.log4j.db.DBAppender and org.apache.log4j.db.DBReceiver.
#
#
# It is intended for HSQLDB. 
#

DROP TABLE logging_event_exception;
DROP TABLE logging_event_property;
DROP TABLE logging_event;


CREATE TABLE logging_event 
  (
    sequence_number   BIGINT NOT NULL,
    timestamp         BIGINT NOT NULL,
    rendered_message  LONGVARCHAR NOT NULL,
    logger_name       VARCHAR NOT NULL,
    level_string      VARCHAR NOT NULL,
    ndc               LONGVARCHAR,
    thread_name       VARCHAR,
    reference_flag    SMALLINT,
    event_id          INT NOT NULL IDENTITY
  );


CREATE TABLE logging_event_property
  (
    event_id	      INT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      LONGVARCHAR,
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );

CREATE TABLE logging_event_exception
  (
    event_id         INT NOT NULL,
    i                SMALLINT NOT NULL,
    trace_line       VARCHAR NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );
