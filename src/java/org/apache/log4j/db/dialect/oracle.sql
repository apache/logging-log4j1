
CREATE SEQUENCE event_id_seq;

CREATE OR REPLACE TRIGGER event_id_seq_trig
  BEFORE INSERT ON logging_event
  FOR EACH ROW  
  BEGIN  
    SELECT logging_event_seq.NEXTVAL 
    INTO   :NEW.id 
    FROM   DUAL;  
  END event_id_seq_trig;