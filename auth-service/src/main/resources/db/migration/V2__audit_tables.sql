-- V2__audit_tables.sql
-- AuditLog and SessionContext tables + indexes

-- AuditLog Table
CREATE TABLE AuditLog (
 AuditID INT PRIMARY KEY AUTO_INCREMENT,
 TableName VARCHAR(100) NOT NULL,
 RecordID VARCHAR(100) NOT NULL,
 Operation ENUM('INSERT', 'UPDATE', 'DELETE', 'SOFT_DELETE') NOT NULL,
 OldData JSON,
 NewData JSON,
 ChangedFields JSON,
 ChangedBy INT NULL,
 ChangedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 IPAddress VARCHAR(45),
 UserAgent TEXT,
 FOREIGN KEY (ChangedBy) REFERENCES Users(UserID)
);

-- Indexes for better performance
CREATE INDEX idx_auditlog_table_record ON AuditLog(TableName, RecordID);
CREATE INDEX idx_auditlog_changedby ON AuditLog(ChangedBy);
CREATE INDEX idx_auditlog_changedat ON AuditLog(ChangedAt);

-- SessionContext Table
CREATE TABLE SessionContext (
 SessionID VARCHAR(100) PRIMARY KEY,
 UserID INT,
 IPAddress VARCHAR(45),
 LoginTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 LastActivity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
