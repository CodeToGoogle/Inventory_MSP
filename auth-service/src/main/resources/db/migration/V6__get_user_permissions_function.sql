-- V6__get_user_permissions_function.sql
-- FUNCTION GetUserPermissions

DELIMITER $$
CREATE FUNCTION GetUserPermissions(
 p_user_id INT,
 p_object_name VARCHAR(100)
) RETURNS JSON
DETERMINISTIC
READS SQL DATA
BEGIN
 DECLARE permissions JSON;

 SELECT JSON_OBJECT(
  'Access', MAX(op.Access),
  'Edit', MAX(op.Edit),
  'Delete', MAX(op.Delete),
  'Print', MAX(op.Print),
  'Attach', MAX(op.Attach),
  'AddNotes', MAX(op.AddNotes),
  'Approve', MAX(op.Approve),
  'Reject', MAX(op.Reject),
  'Reset', MAX(op.Reset)
 ) INTO permissions
 FROM Users u
 JOIN UserRoles ur ON u.UserID = ur.UserID
 JOIN Roles r ON ur.RoleID = r.RoleID
 JOIN ObjectPermissions op ON r.RoleID = op.RoleID
 JOIN Objects o ON op.ObjectID = o.ObjectID
 WHERE u.UserID = p_user_id
   AND o.ObjectName = p_object_name
   AND u.IsActive = TRUE;
 RETURN COALESCE(permissions, JSON_OBJECT());
END$$
DELIMITER ;
