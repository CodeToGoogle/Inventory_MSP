-- V3__calculate_product_cost_function.sql
-- EXACT SQL from your V11 file

DELIMITER $$
CREATE FUNCTION CalculateProductCost(p_product_id INT)
RETURNS DECIMAL(15,2)
DETERMINISTIC
READS SQL DATA
BEGIN
 DECLARE total_cost DECIMAL(15,2);

 SELECT SUM(itl.LineTotal) INTO total_cost
 FROM InventoryTransactionsLine itl
 JOIN InventoryTransactions it ON itl.TransactionID = it.TransactionID
 JOIN InventoryOperationTypes iot ON it.OperationTypeID = iot.OperationTypeID
 WHERE itl.ProductID = p_product_id
   AND iot.Effect = 'Increase'
   AND it.TransactionDate >= DATE_SUB(NOW(), INTERVAL 1 YEAR);

 RETURN COALESCE(total_cost, 0);
END$$
DELIMITER ;
