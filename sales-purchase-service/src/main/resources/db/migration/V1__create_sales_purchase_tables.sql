CREATE TABLE Customers (
    CustomerID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerName VARCHAR(100) NOT NULL,
    ContactName VARCHAR(100),
    ContactEmail VARCHAR(100),
    PhoneNumber VARCHAR(20),
    Address VARCHAR(255),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Vendors (
    VendorID INT PRIMARY KEY AUTO_INCREMENT,
    VendorName VARCHAR(100) NOT NULL,
    ContactName VARCHAR(100),
    ContactEmail VARCHAR(100),
    PhoneNumber VARCHAR(20),
    Address VARCHAR(255),
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SalesOrders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    OrderDate DATE NOT NULL,
    CustomerID INT NOT NULL,
    OrderStatus ENUM('Draft','Confirmed','Invoiced','Paid','Cancelled') DEFAULT 'Draft',
    DeliveryStatus ENUM('Pending','Partial','Delivered') DEFAULT 'Pending',
    TotalAmount DECIMAL(15,2),
    TotalQty DECIMAL(15,3),
    CreatedBy VARCHAR(50),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);

CREATE TABLE SalesOrderLines (
    LineID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    OrderedQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    SaleUnitPrice DECIMAL(15,2) NOT NULL,
    LineTotal DECIMAL(15,2) GENERATED ALWAYS AS (OrderedQty * SaleUnitPrice) STORED,
    DeliveredQty DECIMAL(15,3) DEFAULT 0,
    FOREIGN KEY (OrderID) REFERENCES SalesOrders(OrderID) ON DELETE CASCADE
);

CREATE TABLE PurchaseOrders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    OrderNumber VARCHAR(50) UNIQUE NOT NULL,
    OrderDate DATE NOT NULL,
    VendorID INT NOT NULL,
    OrderStatus ENUM('Draft','Sent','Confirmed','Received','Cancelled') DEFAULT 'Draft',
    DeliveryStatus ENUM('Pending','Partially Received','Received') DEFAULT 'Pending',
    TotalAmount DECIMAL(15,2),
    TotalQty DECIMAL(15,3),
    CreatedBy VARCHAR(50),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (VendorID) REFERENCES Vendors(VendorID)
);

CREATE TABLE PurchaseOrderLines (
    LineID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    OrderedQty DECIMAL(15,3) NOT NULL,
    UnitID INT NOT NULL,
    PurchaseUnitPrice DECIMAL(15,2) NOT NULL,
    LineTotal DECIMAL(15,2) GENERATED ALWAYS AS (OrderedQty * PurchaseUnitPrice) STORED,
    DeliveredQty DECIMAL(15,3) DEFAULT 0,
    FOREIGN KEY (OrderID) REFERENCES PurchaseOrders(OrderID) ON DELETE CASCADE
);

CREATE TABLE IdempotencyKeys (
    IdempotencyID INT PRIMARY KEY AUTO_INCREMENT,
    IdempotencyKey VARCHAR(255) NOT NULL UNIQUE,
    RequestMethod VARCHAR(16),
    RequestPath VARCHAR(1000),
    RequestBody LONGTEXT,
    ResponseBody LONGTEXT,
    ResponseStatus INT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
