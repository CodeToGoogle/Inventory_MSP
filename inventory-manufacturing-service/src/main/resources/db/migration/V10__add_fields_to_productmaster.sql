-- V10__add_fields_to_productmaster.sql
-- Add missing columns to the ProductMaster table to align with the entity definition.

ALTER TABLE ProductMaster
ADD COLUMN SKU VARCHAR(50) UNIQUE AFTER ProductName,
ADD COLUMN CostPrice DECIMAL(15,2) AFTER SafetyStock,
ADD COLUMN SellingPrice DECIMAL(15,2) AFTER CostPrice;
