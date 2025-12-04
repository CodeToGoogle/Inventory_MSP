-- V3__sales_purchase_views.sql
-- SalesPerformance & PurchaseSummary views

CREATE OR REPLACE VIEW SalesPerformance AS
SELECT
 so.OrderID,
 so.OrderNumber,
 so.OrderDate,
 c.CustomerName,
 SUM(sol.LineTotal) AS TotalAmount,
 SUM(sol.OrderedQty) AS TotalQuantity,
 so.OrderStatus,
 so.DeliveryStatus,
 u.UserName AS CreatedBy
FROM SalesOrders so
JOIN SalesOrderLines sol ON so.OrderID = sol.OrderID
JOIN Customers c ON so.CustomerID = c.CustomerID
LEFT JOIN Users u ON so.CreatedBy = u.UserID
GROUP BY so.OrderID, so.OrderNumber, so.OrderDate, c.CustomerName, so.OrderStatus, so.DeliveryStatus, u.UserName;

CREATE OR REPLACE VIEW PurchaseSummary AS
SELECT
 po.OrderID,
 po.OrderNumber,
 po.OrderDate,
 v.VendorName,
 SUM(pol.LineTotal) AS TotalAmount,
 SUM(pol.OrderedQty) AS TotalQuantity,
 po.OrderStatus,
 po.DeliveryStatus
FROM PurchaseOrders po
JOIN PurchaseOrderLines pol ON po.OrderID = pol.OrderID
JOIN Vendors v ON po.VendorID = v.VendorID
GROUP BY po.OrderID, po.OrderNumber, po.OrderDate, v.VendorName, po.OrderStatus, po.DeliveryStatus;
