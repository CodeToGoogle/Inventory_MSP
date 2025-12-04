-- V6__inventory_manufacturing_indexes.sql
-- EXACT indexes from V14, only inventory + manufacturing

CREATE INDEX IF NOT EXISTS idx_inventorytransactions_date
 ON InventoryTransactions(TransactionDate);

CREATE INDEX IF NOT EXISTS idx_inventorytransactions_type
 ON InventoryTransactions(OperationTypeID);

CREATE INDEX IF NOT EXISTS idx_inventorytransactionsline_product
 ON InventoryTransactionsLine(ProductID);

CREATE INDEX IF NOT EXISTS idx_manufacturingorders_status
 ON ManufacturingOrders(OrderStatus);

CREATE INDEX IF NOT EXISTS idx_workorders_status
 ON WorkOrders(WcStatus);

CREATE INDEX IF NOT EXISTS idx_workorders_mo
 ON WorkOrders(ReferenceMO);
