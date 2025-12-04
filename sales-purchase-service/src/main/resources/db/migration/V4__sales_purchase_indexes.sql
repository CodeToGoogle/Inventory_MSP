-- V4__sales_purchase_indexes.sql
-- EXACT indexes from V14, only sales/purchase related

CREATE INDEX IF NOT EXISTS idx_salesorders_customer ON SalesOrders(CustomerID);
CREATE INDEX IF NOT EXISTS idx_salesorders_status ON SalesOrders(OrderStatus);
CREATE INDEX IF NOT EXISTS idx_salesorders_date ON SalesOrders(OrderDate);

CREATE INDEX IF NOT EXISTS idx_salesorderlines_order ON SalesOrderLines(OrderID);
CREATE INDEX IF NOT EXISTS idx_salesorderlines_product ON SalesOrderLines(ProductID);

CREATE INDEX IF NOT EXISTS idx_purchaseorders_vendor ON PurchaseOrders(VendorID);
CREATE INDEX IF NOT EXISTS idx_purchaseorders_status ON PurchaseOrders(OrderStatus);
