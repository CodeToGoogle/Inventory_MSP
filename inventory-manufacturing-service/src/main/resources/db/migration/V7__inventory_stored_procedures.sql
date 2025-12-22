-- V7__inventory_stored_procedures.sql
-- EXACT SQL from V12

DELIMITER $$

CREATE PROCEDURE UpdateProductQuantity(
 IN p_product_id INT,
 IN p_quantity_change DECIMAL(15,3),
 IN p_operation_type VARCHAR(50),
 IN p_user_id INT,
 IN p_reference_type VARCHAR(20),
 IN p_reference_id INT
)
BEGIN
 DECLARE v_operation_type_id INT;
 DECLARE v_transaction_id INT;

 SELECT OperationTypeID INTO v_operation_type_id
 FROM InventoryOperationTypes
 WHERE OTName = p_operation_type
 LIMIT 1;

 INSERT INTO InventoryTransactions (
  TransactionNumber,
  TransactionDate,
  OperationTypeID,
  ReferenceType,
  ReferenceNumber,
  TotalQty,
  CreatedBy
 ) VALUES (
  CONCAT('TRX-', DATE_FORMAT(NOW(), '%Y%m%d-'), LPAD(FLOOR(RAND() * 10000),4, '0')),
  NOW(),
  v_operation_type_id,
  p_reference_type,
  p_reference_id,
  ABS(p_quantity_change),
  p_user_id
 );

 SET v_transaction_id = LAST_INSERT_ID();

 INSERT INTO InventoryTransactionsLine (
  TransactionID,
  ProductID,
  Qty,
  UnitID
 ) VALUES (
  v_transaction_id,
  p_product_id,
  p_quantity_change,
  (SELECT UnitID FROM ProductMaster WHERE ProductID = p_product_id LIMIT 1)
 );

 UPDATE ProductMaster
 SET QtyOnHand = QtyOnHand + p_quantity_change,
     UpdatedAt = NOW()
 WHERE ProductID = p_product_id;

 SELECT v_transaction_id AS TransactionID, 'Success' AS Status;
END$$


CREATE PROCEDURE CheckReplenishmentRules()
BEGIN
 DECLARE done INT DEFAULT FALSE;
 DECLARE v_rule_id INT;
 DECLARE v_product_id INT;
 DECLARE v_low_threshold DECIMAL(15,3);
 DECLARE v_high_threshold DECIMAL(15,3);
 DECLARE v_order_type VARCHAR(10);
 DECLARE v_current_stock DECIMAL(15,3);

 DECLARE cur CURSOR FOR
  SELECT rr.RuleID, rr.ProductID, rr.LowThreshold, rr.HighThreshold, rr.OrderType,
         COALESCE(il.CurrentStock,0) AS CurrentStock
  FROM ReplenishmentRules rr
  LEFT JOIN InventoryLevels il ON rr.ProductID = il.ProductID
  WHERE rr.IsActive = TRUE;

 DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

 OPEN cur;
 read_loop: LOOP
  FETCH cur INTO v_rule_id, v_product_id, v_low_threshold, v_high_threshold, v_order_type, v_current_stock;
  IF done THEN LEAVE read_loop; END IF;

  IF v_current_stock <= v_low_threshold THEN
    INSERT INTO AuditLog (TableName, RecordID, Operation, NewData, ChangedBy)
    VALUES (
      'ReplenishmentRules',
      v_rule_id,
      'TRIGGER',
      JSON_OBJECT(
        'ProductID', v_product_id,
        'CurrentStock', v_current_stock,
        'LowThreshold', v_low_threshold,
        'Action', CONCAT('Create ', v_order_type, ' order'),
        'Timestamp', NOW()
      ),
      0
    );
  END IF;

 END LOOP;
 CLOSE cur;
END$$

DELIMITER ;
