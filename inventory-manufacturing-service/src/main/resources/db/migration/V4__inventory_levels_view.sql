-- V4__inventory_levels_view.sql
-- InventoryLevels view EXACT AS IS

CREATE OR REPLACE VIEW InventoryLevels AS
SELECT
 p.ProductID,
 p.ProductName,
 p.SKU,
 c.CategoryName,
 SUM(
   CASE
     WHEN ot.Effect = 'Increase' THEN itl.Qty
     WHEN ot.Effect = 'Decrease' THEN -itl.Qty
     ELSE 0
   END
 ) AS CurrentStock,
 p.UnitID,
 u.UnitName,
 p.ReorderLevel,
 p.SafetyStock
FROM ProductMaster p
LEFT JOIN InventoryTransactionsLine itl ON p.ProductID = itl.ProductID
LEFT JOIN InventoryTransactions it ON itl.TransactionID = it.TransactionID
LEFT JOIN InventoryOperationTypes ot ON it.OperationTypeID = ot.OperationTypeID
LEFT JOIN ProductCategories c ON p.ProductCategoryID = c.CategoryID
LEFT JOIN ProductUnits u ON p.UnitID = u.UnitID
GROUP BY
 p.ProductID, p.ProductName, p.SKU, c.CategoryName,
 p.UnitID, u.UnitName, p.ReorderLevel, p.SafetyStock;
