-- V3__replenishment_rules.sql
-- EXACT SQL for Replenishment Rules

CREATE TABLE IF NOT EXISTS ReplenishmentRules (
    RuleID INT PRIMARY KEY AUTO_INCREMENT,
    RuleName VARCHAR(100) NOT NULL,
    ProductID INT NOT NULL,
    LowThreshold DECIMAL(15,3) NOT NULL,
    HighThreshold DECIMAL(15,3) NOT NULL,
    OrderType ENUM('PO','MO','Transfer') NOT NULL,
    SourceLocationID INT NULL,
    DestinationLocationID INT NULL,
    VendorID INT NULL,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (SourceLocationID) REFERENCES InventoryLocations(LocationID),
    FOREIGN KEY (DestinationLocationID) REFERENCES InventoryLocations(LocationID),
    FOREIGN KEY (VendorID) REFERENCES Vendors(VendorID)
);
