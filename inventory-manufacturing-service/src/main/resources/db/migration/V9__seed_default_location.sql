-- V9__seed_default_location.sql
-- Seed default inventory locations and operation types

-- Insert default Warehouse
INSERT IGNORE INTO InventoryLocations (LocationID, LocationName, LocationType, IsActive)
VALUES (1, 'Main Warehouse', 'Warehouse', TRUE);

-- Insert default Operation Types if they don't exist (V3 creates the table but might not seed it)
INSERT IGNORE INTO InventoryOperationTypes (OperationTypeID, OTName, Effect, Description) VALUES 
(1, 'Purchase Receipt', 'Increase', 'Receiving goods from vendors'),
(2, 'Sales Delivery', 'Decrease', 'Shipping goods to customers'),
(3, 'Manufacturing Consumption', 'Decrease', 'Consuming raw materials for production'),
(4, 'Manufacturing Production', 'Increase', 'Producing finished goods'),
(5, 'Stock Adjustment', 'Adjustment', 'Manual stock correction'),
(6, 'Stock Transfer', 'Transfer', 'Moving stock between locations');

-- Insert default Delivery Methods
INSERT IGNORE INTO InventoryDeliveryMethods (DeliveryMethodID, DMName, Description) VALUES
(1, 'Standard Shipping', 'Standard ground shipping'),
(2, 'Express Shipping', 'Next day delivery'),
(3, 'Pickup', 'Customer pickup');
