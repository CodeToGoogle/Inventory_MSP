-- V4__product_indexes.sql
-- Indexes for the ProductMaster table

CREATE INDEX idx_productmaster_category ON ProductMaster(ProductCategoryID);
CREATE INDEX idx_productmaster_sku ON ProductMaster(SKU);
