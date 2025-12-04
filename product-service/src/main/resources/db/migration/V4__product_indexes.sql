-- V4__product_indexes.sql
-- EXACT product indexes from V14 file

CREATE INDEX IF NOT EXISTS idx_productmaster_category ON ProductMaster(ProductCategoryID);
CREATE INDEX IF NOT EXISTS idx_productmaster_sku ON ProductMaster(SKU);
