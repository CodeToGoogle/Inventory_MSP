-- V3__inventory_core_tables.sql
-- Inventory Locations, Types, Delivery Methods, Transactions

CREATE TABLE InventoryLocations (
    LocationID INT PRIMARY KEY AUTO_INCREMENT,
    LocationName VARCHAR(100) NOT NULL,
    ParentLocationID INT NULL,
    LocationType ENUM('Warehouse','Zone','Aisle','Rack','Shelf','Bin') DEFAULT 'Warehouse',
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ParentLocationID) REFERENCES InventoryLocations(LocationID),
    UNIQUE KEY unique_location_path (LocationName, ParentLocationID)
);

CREATE TABLE InventoryOperationTypes (
    OperationTypeID INT PRIMARY KEY AUTO_INCREMENT,
    OTName VARCHAR(100) NOT NULL UNIQUE,
    Effect ENUM('Increase','Decrease','Transfer','Adjustment') NOT NULL,
    Description TEXT,
    IsActive BOOLEAN DEFAULT TRUE
);

CREATE TABLE InventoryDeliveryMethods (
    DeliveryMethodID INT PRIMARY KEY AUTO_INCREMENT,
    DMName VARCHAR(100) NOT NULL UNIQUE,
    Description TEXT,
    IsActive BOOLEAN DEFAULT TRUE
);

CREATE TABLE InventoryTransactions (
    TransactionID INT PRIMARY KEY AUTO_INCREMENT,
    TransactionNumber VARCHAR(50) UNIQUE NOT NULL,
    TransactionDate DATETIME NOT NULL,
    OperationTypeID INT NOT NULL,
    FromLocationID INT,
    ToLocationID INT,
    DeliveryMethodID INT,
    ReferenceNumber VARCHAR(100),
    ReferenceType ENUM('SO','MO','PO','WO','Adjustment','Transfer') NULL,
    TotalQty DECIMAL(15,3) DEFAULT 0,
    CreatedBy INT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OperationTypeID) REFERENCES InventoryOperationTypes(OperationTypeID),
    FOREIGN KEY (FromLocationID) REFERENCES InventoryLocations(LocationID),
    FOREIGN KEY (ToLocationID) REFERENCES InventoryLocations(LocationID),
    FOREIGN KEY (DeliveryMethodID) REFERENCES InventoryDeliveryMethods(DeliveryMethodID)
);

CREATE TABLE InventoryTransactionsLine (
    TransactionLineID INT PRIMARY KEY AUTO_INCREMENT,
    TransactionID INT NOT NULL,
    ProductID INT NOT NULL,
    BatchID INT NULL,
    Qty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    UnitCost DECIMAL(15,2),
    LineTotal DECIMAL(15,2) GENERATED ALWAYS AS (Qty * UnitCost) STORED,
    FOREIGN KEY (TransactionID) REFERENCES InventoryTransactions(TransactionID) ON DELETE CASCADE
);
