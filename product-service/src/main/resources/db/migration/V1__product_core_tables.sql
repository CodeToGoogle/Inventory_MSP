-- V1__product_core_tables.sql
-- Product Categories, Product Units, Product Master

CREATE TABLE IF NOT EXISTS ProductCategories (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(100) UNIQUE NOT NULL,
    ParentCategoryID INT NULL,
    Description TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ParentCategoryID) REFERENCES ProductCategories(CategoryID)
);

CREATE TABLE IF NOT EXISTS ProductUnits (
    UnitID INT PRIMARY KEY AUTO_INCREMENT,
    UnitName VARCHAR(50) UNIQUE NOT NULL,
    Contains DECIMAL(10,3) NOT NULL,
    ReferenceUnitID INT NULL,
    ConversionFactor DECIMAL(10,6) DEFAULT 1.0,
    IsBaseUnit BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (ReferenceUnitID) REFERENCES ProductUnits(UnitID)
);

CREATE TABLE IF NOT EXISTS ProductMaster (
    ProductID INT PRIMARY KEY AUTO_INCREMENT,
    ProductName VARCHAR(200) NOT NULL,
    ProductType ENUM('Raw Material','Finished Good','Semi-Finished','Consumable','Service') NOT NULL,
    ProductCategoryID INT NULL,
    UnitID INT NOT NULL,
    QtyOnHand DECIMAL(15,3) DEFAULT 0,
    ReorderLevel DECIMAL(15,3) DEFAULT 0,
    SafetyStock DECIMAL(15,3) DEFAULT 0,
    CostPrice DECIMAL(15,2),
    SellingPrice DECIMAL(15,2),
    SKU VARCHAR(50) UNIQUE,
    Barcode VARCHAR(100),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductCategoryID) REFERENCES ProductCategories(CategoryID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);
