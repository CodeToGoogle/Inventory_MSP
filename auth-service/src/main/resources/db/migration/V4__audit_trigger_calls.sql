-- V4__audit_trigger_calls.sql
-- Inlined audit triggers to avoid unsupported CALL statements in Flyway

--
-- Triggers for Users table
--
CREATE TRIGGER audit_users_insert AFTER INSERT ON Users
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog (TableName, RecordID, Operation, NewData, ChangedBy, IPAddress)
    VALUES ('Users', NEW.UserID, 'INSERT',
            JSON_OBJECT(
                'UserID', NEW.UserID,
                'UserName', NEW.UserName,
                'UserType', NEW.UserType,
                'IsActive', NEW.IsActive
            ),
            NULL, NULL);
END;

CREATE TRIGGER audit_users_update AFTER UPDATE ON Users
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog (TableName, RecordID, Operation, OldData, NewData, ChangedBy, IPAddress)
    VALUES ('Users', NEW.UserID, 'UPDATE',
            JSON_OBJECT(
                'UserID', OLD.UserID,
                'UserName', OLD.UserName,
                'UserType', OLD.UserType,
                'IsActive', OLD.IsActive
            ),
            JSON_OBJECT(
                'UserID', NEW.UserID,
                'UserName', NEW.UserName,
                'UserType', NEW.UserType,
                'IsActive', NEW.IsActive
            ),
            NULL, NULL);
END;

CREATE TRIGGER audit_users_delete BEFORE DELETE ON Users
FOR EACH ROW
BEGIN
    INSERT INTO AuditLog (TableName, RecordID, Operation, OldData, ChangedBy, IPAddress)
    VALUES ('Users', OLD.UserID, 'DELETE',
            JSON_OBJECT(
                'UserID', OLD.UserID,
                'UserName', OLD.UserName,
                'UserType', OLD.UserType,
                'IsActive', OLD.IsActive
            ),
            NULL, NULL);
END;
