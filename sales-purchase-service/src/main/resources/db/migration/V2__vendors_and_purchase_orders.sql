-- V2__vendors_and_purchase_orders.sql
-- Vendors, PurchaseOrders, PurchaseOrderLines

CREATE TABLE IF NOT EXISTS Vendors (
    VendorID INT PRIMARY KEY AUTO_INCREMENT,
    VendorName VARCHAR(200) NOT NULL,
    VendorCode VARCHAR(50) UNIQUE,
    ContactPerson VARCHAR(100),
    Email VARCHAR(100),
    Phone VARCHAR(20),
    Address TEXT,
    PaymentTerms INT,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS PurchaseOrders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    ReferenceSO INT NULL,
    OrderDate DATE NOT NULL,
    VendorID INT NOT NULL,
    TotalAmount DECIMAL(15,2) DEFAULT 0,
    TotalQty DECIMAL(15,3) DEFAULT 0,
    OrderStatus ENUM('Draft','Sent','Confirmed','Partially Received','Received','Invoiced','Completed','Cancelled') DEFAULT 'Draft',
    DeliveryStatus ENUM('Pending','Scheduled','In Transit','Delivered','Returned') DEFAULT 'Pending',
    ExpectedDeliveryDate DATE,
    CreatedBy INT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (VendorID) REFERENCES Vendors(VendorID),
    FOREIGN KEY (ReferenceSO) REFERENCES SalesOrders(OrderID),
    FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
);

CREATE TABLE IF NOT EXISTS PurchaseOrderLines (
    LineID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    PurchaseUnitPrice DECIMAL(15,2) NOT NULL,
    OrderedQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    DeliveredQty DECIMAL(15,3) DEFAULT 0,
    LineTotal DECIMAL(15,2) GENERATED ALWAYS AS (PurchaseUnitPrice * OrderedQty) STORED,
    FOREIGN KEY (OrderID) REFERENCES PurchaseOrders(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES ProductMaster(ProductID),
    FOREIGN KEY (UnitID) REFERENCES ProductUnits(UnitID)
);
