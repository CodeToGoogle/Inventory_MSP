-- V8__inventory_manufacturing_indexes.sql
-- EXACT indexes from V14, only inventory + manufacturing

CREATE INDEX idx_inventorytransactions_date
 ON InventoryTransactions(TransactionDate);

CREATE INDEX idx_inventorytransactions_type
 ON InventoryTransactions(OperationTypeID);

CREATE INDEX idx_inventorytransactionsline_product
 ON InventoryTransactionsLine(ProductID);

CREATE INDEX idx_manufacturingorders_status
 ON ManufacturingOrders(OrderStatus);

CREATE INDEX idx_workorders_status
 ON WorkOrders(WcStatus);

CREATE INDEX idx_workorders_mo
 ON WorkOrders(ReferenceMO);
