# This SQL script creates the required tables by org.apache.log4j.db.DBAppender and 
# org.apache.log4j.db.DBReceiver.
#
# It is intended for MySQL databases.


BEGIN;
DROP TABLE IF EXISTS loggging_event;
DROP TABLE IF EXISTS mdc;
COMMIT;


BEGIN;
CREATE TABLE logging_event 
  (
    sequence_number BIGINT NOT NULL,
    timestamp         BIGINT NOT NULL,
    rendered_message  TEXT NOT NULL,
    logger_name       VARCHAR(254) NOT NULL,
    ndc               TEXT,
    thread_name       VARCHAR(254),
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY
  );
COMMIT;

BEGIN;
CREATE TABLE mdc
  (
    event_id	      INT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      VARCHAR(254),
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(id)
  );
COMMIT;
