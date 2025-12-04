-- V5__seed_product_units.sql
-- Seed units from V15

INSERT IGNORE INTO ProductUnits (UnitName, Contains, ConversionFactor, IsBaseUnit)
VALUES ('Piece', 1.000, 1.0, TRUE),
       ('Kg', 1.000, 1.0, TRUE),
       ('Meter', 1.000, 1.0, TRUE);
