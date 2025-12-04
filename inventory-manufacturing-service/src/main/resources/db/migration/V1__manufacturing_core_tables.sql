-- V1__manufacturing_core_tables.sql
-- Manufacturing Workcenters, Operations, BOM, MO, WO

CREATE TABLE IF NOT EXISTS ManufacturingWorkCenters (
    WorkcenterID INT PRIMARY KEY AUTO_INCREMENT,
    WorkcenterName VARCHAR(100) NOT NULL UNIQUE,
    Description TEXT,
    Capacity DECIMAL(10,2),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ManufacturingOperations (
    OperationID INT PRIMARY KEY AUTO_INCREMENT,
    OperationName VARCHAR(100) NOT NULL UNIQUE,
    Description TEXT,
    StandardTime DECIMAL(10,2),
    SetupTime DECIMAL(10,2),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS BillOfMaterials (
    BomID INT PRIMARY KEY AUTO_INCREMENT,
    BomName VARCHAR(100) NOT NULL,
    ProductID INT NOT NULL,
    Version VARCHAR(20),
    IsActive BOOLEAN DEFAULT TRUE,
    EffectiveDate DATE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    UNIQUE KEY unique_bom_product (BomName, ProductID, Version)
);

CREATE TABLE IF NOT EXISTS BillOfMaterialsLine (
    BomLineID INT PRIMARY KEY AUTO_INCREMENT,
    BomID INT NOT NULL,
    ProductID INT NOT NULL,
    Qty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    ScrapFactor DECIMAL(5,2) DEFAULT 0,
    FOREIGN KEY (BomID) REFERENCES BillOfMaterials(BomID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);

CREATE TABLE IF NOT EXISTS ManufacturingOrders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    ReferenceSO INT NULL,
    OrderDate DATE NOT NULL,
    ProductID INT NOT NULL,
    TotalQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    OrderStatus ENUM('Draft','Planned','Released','In Progress','Partially Completed','Completed','Cancelled') DEFAULT 'Draft',
    DeliveryStatus ENUM('Pending','Ready','Delivered') DEFAULT 'Pending',
    StartDate DATE,
    CompletionDate DATE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ReferenceSO) REFERENCES SalesOrders(OrderID),
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);

CREATE TABLE IF NOT EXISTS ManufacturingOrderLines (
    LineID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    OrderedQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    DeliveredQty DECIMAL(15,3) DEFAULT 0,
    FOREIGN KEY (OrderID) REFERENCES ManufacturingOrders(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);

CREATE TABLE IF NOT EXISTS WorkOrders (
    WorkOrderID INT PRIMARY KEY AUTO_INCREMENT,
    WorkOrderNumber VARCHAR(50) UNIQUE NOT NULL,
    ReferenceMO INT NOT NULL,
    WorkcenterID INT NOT NULL,
    OperationID INT NOT NULL,
    ProductID INT NOT NULL,
    OrderedQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    DeliveredQty DECIMAL(15,3) DEFAULT 0,
    WcStatus ENUM('Pending','In Progress','Completed','On Hold','Cancelled') DEFAULT 'Pending',
    ScheduledStart DATETIME,
    ScheduledEnd DATETIME,
    ActualStart DATETIME,
    ActualEnd DATETIME,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ReferenceMO) REFERENCES ManufacturingOrders(OrderID),
    FOREIGN KEY (WorkcenterID) REFERENCES ManufacturingWorkCenters(WorkcenterID),
    FOREIGN KEY (OperationID) REFERENCES ManufacturingOperations(OperationID),
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);
