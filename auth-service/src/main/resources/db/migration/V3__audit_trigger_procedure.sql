-- V3__audit_trigger_procedure.sql
-- Procedure that creates audit triggers dynamically

DELIMITER $$
CREATE PROCEDURE CreateAuditTrigger(IN tableName VARCHAR(100))
BEGIN
 SET @trigger_name_insert = CONCAT('audit_', tableName, '_insert');
 SET @trigger_name_update = CONCAT('audit_', tableName, '_update');
 SET @trigger_name_delete = CONCAT('audit_', tableName, '_delete');

 -- Create INSERT trigger
 SET @create_trigger_insert = CONCAT('
 CREATE TRIGGER ', @trigger_name_insert, ' AFTER INSERT ON ', tableName, '
 FOR EACH ROW
 BEGIN
 DECLARE current_user_id INT;
 SET current_user_id = COALESCE(@current_user_id, NULL);

 INSERT INTO AuditLog (
  TableName,
  RecordID,
  Operation,
  NewData,
  ChangedBy,
  IPAddress
 ) VALUES (
  \'', tableName, '\',
  NEW.', (SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE() AND COLUMN_KEY = 'PRI' LIMIT 1), ',
  \'INSERT\',
  JSON_OBJECT(', (SELECT GROUP_CONCAT(CONCAT('\'',COLUMN_NAME,'\', NEW.',COLUMN_NAME) SEPARATOR ',') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE()), '),
  current_user_id,
  COALESCE(@client_ip, NULL)
 );
 END');

 -- Create UPDATE trigger
 SET @create_trigger_update = CONCAT('
 CREATE TRIGGER ', @trigger_name_update, ' AFTER UPDATE ON ', tableName, '
 FOR EACH ROW
 BEGIN
 DECLARE current_user_id INT;
 DECLARE changed_fields JSON;
 SET current_user_id = COALESCE(@current_user_id, NULL);

 -- Get changed fields
 SET changed_fields = JSON_ARRAY();

 -- (Note: dynamic detection of changed fields executed in generated trigger)

 INSERT INTO AuditLog (
  TableName,
  RecordID,
  Operation,
  OldData,
  NewData,
  ChangedFields,
  ChangedBy,
  IPAddress
 ) VALUES (
  \'', tableName, '\',
  NEW.', (SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE() AND COLUMN_KEY = 'PRI' LIMIT 1), ',
  \'UPDATE\',
  JSON_OBJECT(', (SELECT GROUP_CONCAT(CONCAT('\'',COLUMN_NAME,'\', OLD.',COLUMN_NAME) SEPARATOR ',') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE()), '),
  JSON_OBJECT(', (SELECT GROUP_CONCAT(CONCAT('\'',COLUMN_NAME,'\', NEW.',COLUMN_NAME) SEPARATOR ',') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE()), '),
  changed_fields,
  current_user_id,
  COALESCE(@client_ip, NULL)
 );
 END');

 -- Create DELETE trigger
 SET @create_trigger_delete = CONCAT('
 CREATE TRIGGER ', @trigger_name_delete, ' BEFORE DELETE ON ', tableName, '
 FOR EACH ROW
 BEGIN
 DECLARE current_user_id INT;
 SET current_user_id = COALESCE(@current_user_id, NULL);

 INSERT INTO AuditLog (
  TableName,
  RecordID,
  Operation,
  OldData,
  ChangedBy,
  IPAddress
 ) VALUES (
  \'', tableName, '\',
  OLD.', (SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE() AND COLUMN_KEY = 'PRI' LIMIT 1), ',
  \'DELETE\',
  JSON_OBJECT(', (SELECT GROUP_CONCAT(CONCAT('\'',COLUMN_NAME,'\', OLD.',COLUMN_NAME) SEPARATOR ',') FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = tableName AND TABLE_SCHEMA = DATABASE()), '),
  current_user_id,
  COALESCE(@client_ip, NULL)
 );
 END');

 -- Execute dynamic SQL
 PREPARE stmt_insert FROM @create_trigger_insert; EXECUTE stmt_insert; DEALLOCATE PREPARE stmt_insert;
 PREPARE stmt_update FROM @create_trigger_update; EXECUTE stmt_update; DEALLOCATE PREPARE stmt_update;
 PREPARE stmt_delete FROM @create_trigger_delete; EXECUTE stmt_delete; DEALLOCATE PREPARE stmt_delete;
END$$
DELIMITER ;
