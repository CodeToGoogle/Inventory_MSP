-- V5__user_permissions_view.sql
-- View for user permissions

CREATE VIEW UserPermissions AS
SELECT
 u.UserID,
 u.UserName,
 r.RoleName,
 o.ObjectName,
 o.ObjectType,
 op.Access,
 op.Edit,
 op.Delete,
 op.Print,
 op.Attach,
 op.AddNotes,
 op.Approve,
 op.Reject,
 op.Reset
FROM Users u
JOIN UserRoles ur ON u.UserID = ur.UserID
JOIN Roles r ON ur.RoleID = r.RoleID
JOIN ObjectPermissions op ON r.RoleID = op.RoleID
JOIN Objects o ON op.ObjectID = o.ObjectID
WHERE u.IsActive = TRUE;
