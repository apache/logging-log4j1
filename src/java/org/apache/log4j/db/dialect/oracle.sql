# This SQL script creates the required tables by org.apache.log4j.db.DBAppender and 
# org.apache.log4j.db.DBReceiver.
#
# It is intended for Oracle databases.

# WARNING WARNING WARNING 
# The following SQL script is untested in the sense that it has not
# been executed against an Oracle database. Thus, it can contain typos
# or may even contain incorrect SQL statements.


CREATE SEQUENCE logging_event_id_seq MINVALUE 1 START WITH 1;

CREATE TABLE logging_event 
  (
    sequence_number   BIGINT NOT NULL,
    timestamp         BIGINT NOT NULL,
    rendered_message  TEXT NOT NULL,
    logger_name       VARCHAR(254) NOT NULL,
    level_string      VARCHAR(254) NOT NULL,
    ndc               TEXT,
    thread_name       VARCHAR(254),
    reference_flag    SMALLINT,
    id                INT PRIMARY KEY
  );

CREATE TRIGGER logging_event_id_seq_trig
  BEFORE INSERT ON logging_event
  FOR EACH ROW  
  BEGIN  
    SELECT logging_event_id_seq.NEXTVAL 
    INTO   :NEW.id 
    FROM   DUAL;  
  END logging_event_id_seq_trig;

CREATE TABLE logging_event_property
  (
    event_id	      INT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      VARCHAR(254),
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(id)
  );
  
CREATE TABLE logging_event_exception
  (
    event_id         INT NOT NULL,
    i                SMALLINT NOT NULL,
    trace_line       VARCHAR(254) NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(id)
  );
  



