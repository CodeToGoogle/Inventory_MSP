-- V11__add_product_type_to_productmaster.sql
-- Add ProductType column to ProductMaster table

ALTER TABLE ProductMaster
ADD COLUMN ProductType VARCHAR(50) AFTER SKU;
