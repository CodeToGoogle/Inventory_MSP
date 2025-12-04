-- V7__seed_admin_user.sql
-- Seed base roles and an admin user placeholder (update password/hash in secure flow)

INSERT IGNORE INTO Roles (RoleName, Description) VALUES ('Admin','System administrator'), ('Sales','Sales user'), ('Procurement','Procurement user'), ('Production','Production user'), ('Viewer','Read only');

-- create an admin user placeholder (you should update password via secure flow)
INSERT IGNORE INTO Users (UserName, EncryptedPassword, UserType, IsActive)
VALUES ('admin', 'CHANGE_ME_HASH', 'Admin', TRUE);

-- assign admin role to admin user
INSERT IGNORE INTO UserRoles (UserID, RoleID)
SELECT u.UserID, r.RoleID FROM Users u CROSS JOIN Roles r
WHERE u.UserName='admin' AND r.RoleName='Admin'
ON DUPLICATE KEY UPDATE RecID=RecID;
