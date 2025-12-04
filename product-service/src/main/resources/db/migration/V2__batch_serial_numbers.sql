-- V2__batch_serial_numbers.sql
-- Batch / Serial numbers table (belongs to product domain)

CREATE TABLE IF NOT EXISTS BatchSerialNumbers (
    BatchID INT PRIMARY KEY AUTO_INCREMENT,
    ProductID INT NOT NULL,
    BatchNumber VARCHAR(100),
    SerialNumber VARCHAR(100),
    Barcode VARCHAR(100),
    ManufacturingDate DATE,
    ExpiryDate DATE,
    Quantity DECIMAL(15,3),
    UnitID INT,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID),
    UNIQUE KEY unique_serial (SerialNumber),
    UNIQUE KEY unique_barcode (Barcode)
);
